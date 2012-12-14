package pl.shockah.terraria.xnb;

import pl.shockah.BinBuffer;

//region HEADER
/* This file was derived from libmspack
 * (C) 2003-2004 Stuart Caie.
 * (C) 2011 Ali Scissons.
 *
 * The LZX method was created by Jonathan Forbes and Tomi Poutanen, adapted
 * by Microsoft Corporation.
 *
 * This source file is Dual licensed; meaning the end-user of this source file
 * may redistribute/modify it under the LGPL 2.1 or MS-PL licenses.
 */
//region LGPL License
/* GNU LESSER GENERAL PUBLIC LICENSE version 2.1
 * LzxDecoder is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (LGPL) version 2.1
 */
//endregion LGPL License
//region MS-PL License
/*
 * MICROSOFT PUBLIC LICENSE
 * This source code is subject to the terms of the Microsoft Public License (Ms-PL).
 *  
 * Redistribution and use in source and binary forms, with or without modification,
 * is permitted provided that redistributions of the source code retain the above
 * copyright notices and this file header.
 *  
 * Additional copyright notices should be appended to the list above.
 *
 * For details, see <http://www.opensource.org/licenses/ms-pl.html>.
 */
//endregion MS-PL License
/*
 * DETAILS
 * This file is a pure C# port of the lzxd.c file from libmspack, with minor
 * changes towards the decompression of XNB files. The original decompression
 * software of LZX encoded data was written by Suart Caie in his
 * libmspack/cabextract projects, which can be located at
 * http://http://www.cabextract.org.uk/
 */
//endregion HEADER

public class LZXDecoder {
	public static long[] position_base = null; // uint[]
	public static int[] extra_bits = null; // byte[]

	private LZXState m_state;

	public LZXDecoder(int window) throws LZXException {
		long wndsize = 1 << window; // uint
		int posn_slots;

		if (window < 15 || window > 21) throw new LZXException("UnsupportedWindowSizeRange");

		m_state = new LZXState();
		m_state.actual_size = 0;
		m_state.window = new int[(int)wndsize]; // byte[]
		for (int i = 0; i < wndsize; i++)
			m_state.window[i] = 0xDC;
		m_state.actual_size = wndsize;
		m_state.window_size = wndsize;
		m_state.window_posn = 0;

		/* initialize static tables */
		if (extra_bits == null) {
			extra_bits = new int[52]; // byte[]
			for (int i = 0, j = 0; i <= 50; i += 2) {
				extra_bits[i] = extra_bits[i + 1] = j; // byte
				if ((i != 0) && (j < 17)) j++;
			}
		}
		if (position_base == null) {
			position_base = new long[51]; // uint
			for (int i = 0, j = 0; i <= 50; i++) {
				position_base[i] = j; // uint
				j += 1 << extra_bits[i];
			}
		}

		/* calculate required position slots */
		if (window == 20) posn_slots = 42;
		else if (window == 21) posn_slots = 50;
		else posn_slots = window << 1;

		m_state.R0 = m_state.R1 = m_state.R2 = 1;
		m_state.main_elements = LZXConstants.NUM_CHARS + (posn_slots << 3); // ushort
		m_state.header_read = 0;
		m_state.frames_read = 0;
		m_state.block_remaining = 0;
		m_state.block_type = LZXConstants.BLOCKTYPE.INVALID;
		m_state.intel_curpos = 0;
		m_state.intel_started = 0;

		// yo dawg i herd u liek arrays so we put arrays in ur arrays so u can array while u array
		m_state.PRETREE_table = new int[(1 << LZXConstants.PRETREE_TABLEBITS) + (LZXConstants.PRETREE_MAXSYMBOLS << 1)]; // ushort[]
		m_state.PRETREE_len = new int[LZXConstants.PRETREE_MAXSYMBOLS + LZXConstants.LENTABLE_SAFETY]; // byte[]
		m_state.MAINTREE_table = new int[(1 << LZXConstants.MAINTREE_TABLEBITS) + (LZXConstants.MAINTREE_MAXSYMBOLS << 1)]; // ushort[]
		m_state.MAINTREE_len = new int[LZXConstants.MAINTREE_MAXSYMBOLS + LZXConstants.LENTABLE_SAFETY]; // byte[]
		m_state.LENGTH_table = new int[(1 << LZXConstants.LENGTH_TABLEBITS) + (LZXConstants.LENGTH_MAXSYMBOLS << 1)]; // ushort[]
		m_state.LENGTH_len = new int[LZXConstants.LENGTH_MAXSYMBOLS + LZXConstants.LENTABLE_SAFETY]; // byte[]
		m_state.ALIGNED_table = new int[(1 << LZXConstants.ALIGNED_TABLEBITS) + (LZXConstants.ALIGNED_MAXSYMBOLS << 1)]; // ushort[]
		m_state.ALIGNED_len = new int[LZXConstants.ALIGNED_MAXSYMBOLS + LZXConstants.LENTABLE_SAFETY]; // byte[]
		/* initialise tables to 0 (because deltas will be applied to them) */
		for (int i = 0; i < LZXConstants.MAINTREE_MAXSYMBOLS; i++)
			m_state.MAINTREE_len[i] = 0;
		for (int i = 0; i < LZXConstants.LENGTH_MAXSYMBOLS; i++)
			m_state.LENGTH_len[i] = 0;
	}

	@SuppressWarnings({"fallthrough","unused"}) public int Decompress(BinBuffer inData, int inLen, BinBuffer outData, int outLen) {
		BitBuffer bitbuf = new BitBuffer(inData);
		long startpos = inData.getPos();
		long endpos = inData.getPos() + inLen;

		int[] window = m_state.window;

		long window_posn = m_state.window_posn;
		long window_size = m_state.window_size;
		long R0 = m_state.R0;
		long R1 = m_state.R1;
		long R2 = m_state.R2;
		long i, j;

		int togo = outLen, this_run, main_element, match_length, match_offset, length_footer, extra, verbatim_bits;
		int rundest, runsrc, copy_length, aligned_bits;

		bitbuf.InitBitStream();

		/* read header if necessary */
		if (m_state.header_read == 0) {
			long intel = bitbuf.ReadBits(1);
			if (intel != 0) {
				// read the filesize
				i = bitbuf.ReadBits(16);
				j = bitbuf.ReadBits(16);
				m_state.intel_filesize = (int)((i << 16) | j);
			}
			m_state.header_read = 1;
		}

		/* main decoding loop */
		while (togo > 0) {
			/* last block finished, new block expected */
			if (m_state.block_remaining == 0) {
				// T O D O may screw something up here
				if (m_state.block_type == LZXConstants.BLOCKTYPE.UNCOMPRESSED) {
					if ((m_state.block_length & 1) == 1) inData.readByte(); /* realign bitstream to word */
					bitbuf.InitBitStream();
				}

				m_state.block_type = LZXConstants.BLOCKTYPE.values()[(int)bitbuf.ReadBits(3)];
				i = bitbuf.ReadBits(16);
				j = bitbuf.ReadBits(8);
				m_state.block_remaining = m_state.block_length = (i << 8) | j;

				switch (m_state.block_type) {
					case ALIGNED:
						for (i = 0, j = 0; i < 8; i++) {
							j = bitbuf.ReadBits(3);
							m_state.ALIGNED_len[(int)i] = (byte)j;
						}
						MakeDecodeTable(LZXConstants.ALIGNED_MAXSYMBOLS,LZXConstants.ALIGNED_TABLEBITS,m_state.ALIGNED_len,m_state.ALIGNED_table);
					case VERBATIM:
						ReadLengths(m_state.MAINTREE_len,0,256,bitbuf);
						ReadLengths(m_state.MAINTREE_len,256,m_state.main_elements,bitbuf);
						MakeDecodeTable(LZXConstants.MAINTREE_MAXSYMBOLS,LZXConstants.MAINTREE_TABLEBITS,m_state.MAINTREE_len,m_state.MAINTREE_table);
						if (m_state.MAINTREE_len[0xE8] != 0) m_state.intel_started = 1;

						ReadLengths(m_state.LENGTH_len,0,LZXConstants.NUM_SECONDARY_LENGTHS,bitbuf);
						MakeDecodeTable(LZXConstants.LENGTH_MAXSYMBOLS,LZXConstants.LENGTH_TABLEBITS,m_state.LENGTH_len,m_state.LENGTH_table);
					break;
					case UNCOMPRESSED:
						m_state.intel_started = 1; /* because we can't assume otherwise */
						bitbuf.EnsureBits(16); /* get up to 16 pad bits into the buffer */
						if (bitbuf.GetBitsLeft() > 16) inData.setPos(inData.getPos() - 2); /* and align the bitstream! */
						byte hi,
						mh,
						ml,
						lo;
						lo = (byte)inData.readByte();
						ml = (byte)inData.readByte();
						mh = (byte)inData.readByte();
						hi = (byte)inData.readByte();
						R0 = (lo | ml << 8 | mh << 16 | hi << 24);
						lo = (byte)inData.readByte();
						ml = (byte)inData.readByte();
						mh = (byte)inData.readByte();
						hi = (byte)inData.readByte();
						R1 = (lo | ml << 8 | mh << 16 | hi << 24);
						lo = (byte)inData.readByte();
						ml = (byte)inData.readByte();
						mh = (byte)inData.readByte();
						hi = (byte)inData.readByte();
						R2 = (lo | ml << 8 | mh << 16 | hi << 24);
					break;
					default:
						return -1; // T O D O throw proper exception
				}
			}

			/* buffer exhaustion check */
			if (inData.getPos() > (startpos + inLen)) {
				/*
				 * it's possible to have a file where the next run is less than
				 * 16 bits in size. In this case, the READ_HUFFSYM() macro used
				 * in building the tables will exhaust the buffer, so we should
				 * allow for this, but not allow those accidentally read bits to
				 * be used (so we check that there are at least 16 bits
				 * remaining - in this boundary case they aren't really part of
				 * the compressed data)
				 */
				System.out.println("WTF");
				if (inData.getPos() > (startpos + inLen + 2) || bitbuf.GetBitsLeft() < 16) return -1; // T O D O throw proper exception
			}

			while ((this_run = (int)m_state.block_remaining) > 0 && togo > 0) {
				if (this_run > togo) this_run = togo;
				togo -= this_run;
				m_state.block_remaining -= this_run;

				/* apply 2^x-1 mask */
				window_posn &= window_size - 1;
				/* runs can't straddle the window wraparound */
				if ((window_posn + this_run) > window_size) return -1; // T O D O throw proper exception

				switch (m_state.block_type) {
					case VERBATIM:
						while (this_run > 0) {
							main_element = (int)ReadHuffSym(m_state.MAINTREE_table,m_state.MAINTREE_len,LZXConstants.MAINTREE_MAXSYMBOLS,
									LZXConstants.MAINTREE_TABLEBITS,bitbuf);
							if (main_element < LZXConstants.NUM_CHARS) {
								/* literal: 0 to NUM_CHARS-1 */
								window[(int)window_posn++] = (byte)main_element;
								this_run--;
							} else {
								/* match: NUM_CHARS + ((slot<<3) | length_header (3 bits)) */
								main_element -= LZXConstants.NUM_CHARS;

								match_length = main_element & LZXConstants.NUM_PRIMARY_LENGTHS;
								if (match_length == LZXConstants.NUM_PRIMARY_LENGTHS) {
									length_footer = (int)ReadHuffSym(m_state.LENGTH_table,m_state.LENGTH_len,LZXConstants.LENGTH_MAXSYMBOLS,
											LZXConstants.LENGTH_TABLEBITS,bitbuf);
									match_length += length_footer;
								}
								match_length += LZXConstants.MIN_MATCH;

								match_offset = main_element >> 3;

								if (match_offset > 2) {
									/* not repeated offset */
									if (match_offset != 3) {
										extra = extra_bits[match_offset];
										verbatim_bits = (int)bitbuf.ReadBits((byte)extra);
										match_offset = (int)position_base[match_offset] - 2 + verbatim_bits;
									} else {
										match_offset = 1;
									}

									/* update repeated offset LRU queue */
									R2 = R1;
									R1 = R0;
									R0 = match_offset;
								} else if (match_offset == 0) {
									match_offset = (int)R0;
								} else if (match_offset == 1) {
									match_offset = (int)R1;
									R1 = R0;
									R0 = match_offset;
								} else /* match_offset == 2 */
								{
									match_offset = (int)R2;
									R2 = R0;
									R0 = match_offset;
								}

								rundest = (int)window_posn;
								this_run -= match_length;

								/* copy any wrapped around source data */
								if (window_posn >= match_offset) {
									/* no wrap */
									runsrc = rundest - match_offset;
								} else {
									runsrc = rundest + ((int)window_size - match_offset);
									copy_length = match_offset - (int)window_posn;
									if (copy_length < match_length) {
										match_length -= copy_length;
										window_posn += copy_length;
										while (copy_length-- > 0)
											window[rundest++] = window[runsrc++];
										runsrc = 0;
									}
								}
								window_posn += match_length;

								/* copy match data - no worries about destination wraps */
								while (match_length-- > 0)
									window[rundest++] = window[runsrc++];
							}
						}
					break;

					case ALIGNED:
						while (this_run > 0) {
							main_element = (int)ReadHuffSym(m_state.MAINTREE_table,m_state.MAINTREE_len,LZXConstants.MAINTREE_MAXSYMBOLS,
									LZXConstants.MAINTREE_TABLEBITS,bitbuf);

							if (main_element < LZXConstants.NUM_CHARS) {
								/* literal 0 to NUM_CHARS-1 */
								window[(int)window_posn++] = (byte)main_element;
								this_run--;
							} else {
								/* match: NUM_CHARS + ((slot<<3) | length_header (3 bits)) */
								main_element -= LZXConstants.NUM_CHARS;

								match_length = main_element & LZXConstants.NUM_PRIMARY_LENGTHS;
								if (match_length == LZXConstants.NUM_PRIMARY_LENGTHS) {
									length_footer = (int)ReadHuffSym(m_state.LENGTH_table,m_state.LENGTH_len,LZXConstants.LENGTH_MAXSYMBOLS,
											LZXConstants.LENGTH_TABLEBITS,bitbuf);
									match_length += length_footer;
								}
								match_length += LZXConstants.MIN_MATCH;

								match_offset = main_element >> 3;

								if (match_offset > 2) {
									/* not repeated offset */
									extra = extra_bits[match_offset];
									match_offset = (int)position_base[match_offset] - 2;
									if (extra > 3) {
										/* verbatim and aligned bits */
										extra -= 3;
										verbatim_bits = (int)bitbuf.ReadBits((byte)extra);
										match_offset += (verbatim_bits << 3);
										aligned_bits = (int)ReadHuffSym(m_state.ALIGNED_table,m_state.ALIGNED_len,LZXConstants.ALIGNED_MAXSYMBOLS,
												LZXConstants.ALIGNED_TABLEBITS,bitbuf);
										match_offset += aligned_bits;
									} else if (extra == 3) {
										/* aligned bits only */
										aligned_bits = (int)ReadHuffSym(m_state.ALIGNED_table,m_state.ALIGNED_len,LZXConstants.ALIGNED_MAXSYMBOLS,
												LZXConstants.ALIGNED_TABLEBITS,bitbuf);
										match_offset += aligned_bits;
									} else if (extra > 0) /* extra==1, extra==2 */
									{
										/* verbatim bits only */
										verbatim_bits = (int)bitbuf.ReadBits((byte)extra);
										match_offset += verbatim_bits;
									} else /* extra == 0 */
									{
										/* ??? */
										match_offset = 1;
									}

									/* update repeated offset LRU queue */
									R2 = R1;
									R1 = R0;
									R0 = match_offset;
								} else if (match_offset == 0) {
									match_offset = (int)R0;
								} else if (match_offset == 1) {
									match_offset = (int)R1;
									R1 = R0;
									R0 = match_offset;
								} else /* match_offset == 2 */
								{
									match_offset = (int)R2;
									R2 = R0;
									R0 = match_offset;
								}

								rundest = (int)window_posn;
								this_run -= match_length;

								/* copy any wrapped around source data */
								if (window_posn >= match_offset) {
									/* no wrap */
									runsrc = rundest - match_offset;
								} else {
									runsrc = rundest + ((int)window_size - match_offset);
									copy_length = match_offset - (int)window_posn;
									if (copy_length < match_length) {
										match_length -= copy_length;
										window_posn += copy_length;
										while (copy_length-- > 0)
											window[rundest++] = window[runsrc++];
										runsrc = 0;
									}
								}
								window_posn += match_length;

								/* copy match data - no worries about destination wraps */
								while (match_length-- > 0)
									window[rundest++] = window[runsrc++];
							}
						}
					break;
					case UNCOMPRESSED:
						if ((inData.getPos() + this_run) > endpos) return -1; // T O D O throw proper exception
						byte[] temp_buffer = inData.readBytes(this_run);
						for (int k = 0; k < temp_buffer.length; k++)
							window[(int)(window_posn + k)] = temp_buffer[k];
						window_posn += this_run;
					break;

					default:
						return -1; // T O D O throw proper exception
				}
			}
		}

		if (togo != 0) return -1; // T O D O throw proper exception
		int start_window_pos = (int)window_posn;
		if (start_window_pos == 0) start_window_pos = (int)window_size;
		start_window_pos -= outLen;
		for (int k = 0; k < outLen; k++)
			outData.writeByte(window[start_window_pos + k]);

		m_state.window_posn = window_posn;
		m_state.R0 = R0;
		m_state.R1 = R1;
		m_state.R2 = R2;

		// T O D O finish intel E8 decoding
		/* intel E8 decoding */
		if ((m_state.frames_read++ < 32768) && m_state.intel_filesize != 0) {
			if (outLen <= 6 || m_state.intel_started == 0) {
				m_state.intel_curpos += outLen;
			} else {
				int dataend = outLen - 10;
				long curpos = m_state.intel_curpos;
				long filesize = m_state.intel_filesize;
				long abs_off, rel_off;

				m_state.intel_curpos = (int)curpos + outLen;

				while (outData.getPos() < dataend) {
					if (outData.readByte() != 0xE8) {
						curpos++;
						continue;
					}
					// abs_off =
				}
			}
			return -1;
		}
		return 0;
	}

	// READ_LENGTHS(table, first, last)
	// if(lzx_read_lens(LENTABLE(table), first, last, bitsleft))
	// return ERROR (ILLEGAL_DATA)
	//

	// T O D O make returns throw exceptions
	private int MakeDecodeTable(long nsyms, long nbits, int[] length, int[] table) {
		int sym;
		long leaf;
		int bit_num = 1;
		long fill;
		long pos = 0; /* the current position in the decode table */
		long table_mask = (1 << (int)nbits);
		long bit_mask = table_mask >> 1; /* don't do 0 length codes */
		long next_symbol = bit_mask; /* base of allocation for long codes */

		/* fill entries for codes short enough for a direct mapping */
		while (bit_num <= nbits) {
			for (sym = 0; sym < nsyms; sym++) {
				if (length[sym] == bit_num) {
					leaf = pos;

					if ((pos += bit_mask) > table_mask) return 1; /* table overrun */

					/* fill all possible lookups of this symbol with the symbol itself */
					fill = bit_mask;
					while (fill-- > 0)
						table[(int)leaf++] = sym;
				}
			}
			bit_mask >>= 1;
			bit_num++;
		}

		/* if there are any codes longer than nbits */
		if (pos != table_mask) {
			/* clear the remainder of the table */
			for (sym = (int)pos; sym < table_mask; sym++)
				table[sym] = 0;

			/* give ourselves room for codes to grow by up to 16 more bits */
			pos <<= 16;
			table_mask <<= 16;
			bit_mask = 1 << 15;

			while (bit_num <= 16) {
				for (sym = 0; sym < nsyms; sym++) {
					if (length[sym] == bit_num) {
						leaf = pos >> 16;
						for (fill = 0; fill < bit_num - nbits; fill++) {
							/* if this path hasn't been taken yet, 'allocate' two entries */
							if (table[(int)leaf] == 0) {
								table[(int)(next_symbol << 1)] = 0;
								table[(int)((next_symbol << 1) + 1)] = 0;
								table[(int)leaf] = (int)(next_symbol++);
							}
							/* follow the path and select either left or right for next bit */
							leaf = (table[(int)leaf] << 1);
							if (((pos >> (int)(15 - fill)) & 1) == 1) leaf++;
						}
						table[(int)leaf] = sym;

						if ((pos += bit_mask) > table_mask) return 1;
					}
				}
				bit_mask >>= 1;
				bit_num++;
			}
		}

		/* full talbe? */
		if (pos == table_mask) return 0;

		/* either erroneous table, or all elements are 0 - let's find out. */
		for (sym = 0; sym < nsyms; sym++)
			if (length[sym] != 0) return 1;
		return 0;
	}

	// T O D O throw exceptions instead of returns
	private void ReadLengths(int[] lens, long first, long last, BitBuffer bitbuf) {
		long x, y;
		int z;

		// hufftbl pointer here?

		for (x = 0; x < 20; x++) {
			y = bitbuf.ReadBits(4);
			m_state.PRETREE_len[(int)x] = (byte)y;
		}
		MakeDecodeTable(LZXConstants.PRETREE_MAXSYMBOLS,LZXConstants.PRETREE_TABLEBITS,m_state.PRETREE_len,m_state.PRETREE_table);

		for (x = first; x < last;) {
			z = (int)ReadHuffSym(m_state.PRETREE_table,m_state.PRETREE_len,LZXConstants.PRETREE_MAXSYMBOLS,LZXConstants.PRETREE_TABLEBITS,bitbuf);
			if (z == 17) {
				y = bitbuf.ReadBits(4);
				y += 4;
				while (y-- != 0)
					lens[(int)x++] = 0;
			} else if (z == 18) {
				y = bitbuf.ReadBits(5);
				y += 20;
				while (y-- != 0)
					lens[(int)x++] = 0;
			} else if (z == 19) {
				y = bitbuf.ReadBits(1);
				y += 4;
				z = (int)ReadHuffSym(m_state.PRETREE_table,m_state.PRETREE_len,LZXConstants.PRETREE_MAXSYMBOLS,LZXConstants.PRETREE_TABLEBITS,bitbuf);
				z = lens[(int)x] - z;
				if (z < 0) z += 17;
				while (y-- != 0)
					lens[(int)x++] = (byte)z;
			} else {
				z = lens[(int)x] - z;
				if (z < 0) z += 17;
				lens[(int)x++] = (byte)z;
			}
		}
	}

	private long ReadHuffSym(int[] table, int[] lengths, long nsyms, long nbits, BitBuffer bitbuf) {
		long i, j;
		bitbuf.EnsureBits(16);
		if ((i = table[(int)bitbuf.PeekBits((int)nbits)]) >= nsyms) {
			j = (1 << (int)((((4)) * 8) - nbits));
			do {
				j >>= 1;
				i <<= 1;
				i |= (bitbuf.GetBuffer() & j) != 0 ? (long)1 : 0;
				if (j == 0) return 0; // T O D O throw proper exception
			} while ((i = table[(int)i]) >= nsyms);
		}
		j = lengths[(int)i];
		bitbuf.RemoveBits((byte)j);

		return i;
	}

	private class BitBuffer {
		long buffer;
		int bitsleft;
		BinBuffer byteStream;

		public BitBuffer(BinBuffer stream) {
			byteStream = stream;
			InitBitStream();
		}

		public void InitBitStream() {
			buffer = 0;
			bitsleft = 0;
		}

		@SuppressWarnings("unused") public void EnsureBits(int bits) {
			while (bitsleft < bits) {
				int lo = (byte)byteStream.readByte();
				int hi = (byte)byteStream.readByte();
				int amount2shift = ((4)) * 8 - 16 - bitsleft;
				buffer |= ((hi << 8) | lo) << (((4)) * 8 - 16 - bitsleft);
				bitsleft += 16;
			}
		}

		public long PeekBits(int bits) {
			return (buffer >> ((((4)) * 8) - bits));
		}

		public void RemoveBits(int bits) {
			buffer <<= bits;
			bitsleft -= bits;
		}

		public long ReadBits(int bits) {
			long ret = 0;

			if (bits > 0) {
				EnsureBits(bits);
				ret = PeekBits(bits);
				RemoveBits(bits);
			}

			return ret;
		}

		public long GetBuffer() {
			return buffer;
		}

		public int GetBitsLeft() {
			return bitsleft;
		}
	}

	private class LZXState {
		public long R0, R1, R2; /* for the LRU offset system */
		public int main_elements; /* number of main tree elements */
		public int header_read; /* have we started decoding at all yet? */
		public LZXConstants.BLOCKTYPE block_type; /* type of this block */
		public long block_length; /* uncompressed length of this block */
		public long block_remaining; /* uncompressed bytes still left to decode */
		public long frames_read; /* the number of CFDATA blocks processed */
		public int intel_filesize; /* magic header value used for transform */
		public int intel_curpos; /* current offset in transform space */
		public int intel_started; /* have we seen any translateable data yet? */

		public int[] PRETREE_table;
		public int[] PRETREE_len;
		public int[] MAINTREE_table;
		public int[] MAINTREE_len;
		public int[] LENGTH_table;
		public int[] LENGTH_len;
		public int[] ALIGNED_table;
		public int[] ALIGNED_len;

		// NEEDED MEMBERS
		// CAB actualsize
		// CAB window
		// CAB window_size
		// CAB window_posn
		@SuppressWarnings("unused") public long actual_size;
		public int[] window;
		public long window_size;
		public long window_posn;
	}

	/* CONSTANTS */
	private static class LZXConstants {
		public enum BLOCKTYPE {
			INVALID(), VERBATIM(), ALIGNED(), UNCOMPRESSED();
		}

		public static final int MIN_MATCH = 2;
		@SuppressWarnings("unused") public static final int MAX_MATCH = 257;
		public static final int NUM_CHARS = 256;

		public static final int PRETREE_NUM_ELEMENTS = 20;
		public static final int ALIGNED_NUM_ELEMENTS = 8;
		public static final int NUM_PRIMARY_LENGTHS = 7;
		public static final int NUM_SECONDARY_LENGTHS = 249;

		public static final int PRETREE_MAXSYMBOLS = PRETREE_NUM_ELEMENTS;
		public static final int PRETREE_TABLEBITS = 6;
		public static final int MAINTREE_MAXSYMBOLS = NUM_CHARS + 50 * 8;
		public static final int MAINTREE_TABLEBITS = 12;
		public static final int LENGTH_MAXSYMBOLS = NUM_SECONDARY_LENGTHS + 1;
		public static final int LENGTH_TABLEBITS = 12;
		public static final int ALIGNED_MAXSYMBOLS = ALIGNED_NUM_ELEMENTS;
		public static final int ALIGNED_TABLEBITS = 7;

		public static final int LENTABLE_SAFETY = 64;
	}
}