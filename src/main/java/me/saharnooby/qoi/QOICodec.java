package me.saharnooby.qoi;

/**
 * Contains constants and utility methods for decoder and encoder.
 */
final class QOICodec {

	static final int QOI_SRGB = 0x00;
	static final int QOI_SRGB_LINEAR_ALPHA = 0x01;
	static final int QOI_LINEAR = 0x0F;

	static final int QOI_INDEX = 0x00;
	static final int QOI_RUN_8 = 0x40;
	static final int QOI_RUN_16 = 0x60;
	static final int QOI_DIFF_8 = 0x80;
	static final int QOI_DIFF_16 = 0xC0;
	static final int QOI_DIFF_24 = 0xE0;
	static final int QOI_COLOR = 0xF0;

	static final int QOI_MASK_2 = 0xC0;
	static final int QOI_MASK_3 = 0xE0;
	static final int QOI_MASK_4 = 0xF0;

	static final int QOI_MAGIC = 'q' << 24 | 'o' << 16 | 'i' << 8 | 'f';

	static final int QOI_PADDING = 4;

	private static final int HASH_TABLE_SIZE = 64;

	static byte[] createHashTable() {
		return new byte[HASH_TABLE_SIZE * 4];
	}

	static int getHashTableIndex(int r, int g, int b, int a) {
		int hash = r ^ g ^ b ^ a;

		return (hash & (HASH_TABLE_SIZE - 1)) * 4;
	}

}
