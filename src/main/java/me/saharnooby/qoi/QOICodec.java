package me.saharnooby.qoi;

/**
 * Contains constants and utility methods for decoder and encoder.
 */
final class QOICodec {

	static final int QOI_SRGB = 0;
	static final int QOI_LINEAR = 1;

	static final int QOI_OP_INDEX = 0x00;
	static final int QOI_OP_DIFF = 0x40;
	static final int QOI_OP_LUMA = 0x80;
	static final int QOI_OP_RUN = 0xC0;
	static final int QOI_OP_RGB = 0xFE;
	static final int QOI_OP_RGBA = 0xFF;

	static final int QOI_MASK_2 = 0xC0;

	static final int QOI_MAGIC = 'q' << 24 | 'o' << 16 | 'i' << 8 | 'f';

	// Seven 0x00 bytes followed by 0x01
	static final byte[] QOI_PADDING = new byte[] {0, 0, 0, 0, 0, 0, 0, 1};

	private static final int HASH_TABLE_SIZE = 64;

	static byte[] createHashTableRGBA() {
		return new byte[HASH_TABLE_SIZE * 4];
	}

	static int getHashTableIndexRGBA(byte r, byte g, byte b, byte a) {
		int hash = (r & 0xFF) * 3 + (g & 0xFF) * 5 + (b & 0xFF) * 7 + (a & 0xFF) * 11;

		return (hash & 0x3F) << 2;
	}

	static int getHashTableIndexRGB(byte r, byte g, byte b) {
		int hash = (r & 0xFF) * 3 + (g & 0xFF) * 5 + (b & 0xFF) * 7 + 0xFF * 11;

		return (hash & 0x3F) << 2;
	}

}
