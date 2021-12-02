package me.saharnooby.qoi;

import lombok.NonNull;

import java.io.IOException;
import java.io.OutputStream;

import static me.saharnooby.qoi.QOICodec.*;

/**
 * Contains method that encodes raw pixel data into bytes.
 */
public final class QOIEncoder {

	/**
	 * Encodes raw pixel data into QOI image, which then is written into the provided output stream.
	 * @param image QOI image.
	 * @param out Output stream, should be buffered for optimal performance.
	 * @throws IOException On any IO error.
	 */
	public static void encode(@NonNull QOIImage image, @NonNull OutputStream out) throws IOException {
		int channels = image.getChannels();

		byte[] pixelData = image.getPixelData();

		write32(out, QOI_MAGIC);
		write32(out, image.getWidth());
		write32(out, image.getHeight());
		out.write(image.getChannels());

		switch (image.getColorSpace()) {
			case SRGB:
				out.write(QOI_SRGB);
				break;
			case SRGB_LINEAR_ALPHA:
				out.write(QOI_SRGB_LINEAR_ALPHA);
				break;
			case LINEAR:
				out.write(QOI_LINEAR);
				break;
			default:
				throw new IllegalStateException("Unsupported color space");
		}

		byte[] index = createHashTable();

		int run = 0;

		int prevR = 0;
		int prevG = 0;
		int prevB = 0;
		int prevA = 0xFF;

		int pixelR;
		int pixelG;
		int pixelB;
		int pixelA = 0xFF;

		for (int pixelPos = 0; pixelPos < pixelData.length; pixelPos += channels) {
			pixelR = pixelData[pixelPos] & 0xFF;
			pixelG = pixelData[pixelPos + 1] & 0xFF;
			pixelB = pixelData[pixelPos + 2] & 0xFF;

			if (channels == 4) {
				pixelA = pixelData[pixelPos + 3] & 0xFF;
			}

			boolean prevEqualsCurrent = equals(prevR, prevG, prevB, prevA, pixelR, pixelG, pixelB, pixelA);

			if (prevEqualsCurrent) {
				run++;
			}

			if (run > 0 && (run == 0x2020 || !prevEqualsCurrent || pixelPos + channels == pixelData.length)) {
				if (run < 33) {
					run -= 1;
					out.write(QOI_RUN_8 | run);
				} else {
					run -= 33;
					out.write(QOI_RUN_16 | run >> 8);
					out.write(run);
				}

				run = 0;
			}

			if (!prevEqualsCurrent) {
				int indexPos = getHashTableIndex(pixelR, pixelG, pixelB, pixelA);

				if (equals(pixelR, pixelG, pixelB, pixelA, index[indexPos], index[indexPos + 1], index[indexPos + 2], index[indexPos + 3])) {
					out.write(QOI_INDEX | (indexPos / 4));
				} else {
					index[indexPos] = (byte) pixelR;
					index[indexPos + 1] = (byte) pixelG;
					index[indexPos + 2] = (byte) pixelB;
					index[indexPos + 3] = (byte) pixelA;

					int dr = pixelR - prevR;
					int dg = pixelG - prevG;
					int db = pixelB - prevB;
					int da = pixelA - prevA;

					if (smallDiff(dr) && smallDiff(dg) && smallDiff(db) && smallDiff(da)) {
						if (da == 0 && smallestDiff(dr) && smallestDiff(dg) && smallestDiff(db)) {
							out.write(QOI_DIFF_8 | ((dr + 2) << 4) | (dg + 2) << 2 | (db + 2));
						} else if (da == 0 && smallerDiff(dg) && smallerDiff(db)) {
							out.write(QOI_DIFF_16 | (dr + 16));
							out.write((dg + 8) << 4 | (db + 8));
						} else {
							out.write(QOI_DIFF_24 | (dr + 16) >> 1);
							out.write((dr + 16) << 7 | (dg + 16) << 2 | (db + 16) >> 3);
							out.write((db + 16) << 5 | (da + 16));
						}
					} else {
						out.write(QOI_COLOR | (dr != 0 ? 8 : 0) | (dg != 0 ? 4 : 0) | (db != 0 ? 2 : 0) | (da != 0 ? 1 : 0));

						if (dr != 0) {
							out.write(pixelR);
						}

						if (dg != 0) {
							out.write(pixelG);
						}

						if (db != 0) {
							out.write(pixelB);
						}

						if (da != 0) {
							out.write(pixelA);
						}
					}
				}
			}

			prevR = pixelR;
			prevG = pixelG;
			prevB = pixelB;
			prevA = pixelA;
		}

		for (int i = 0; i < QOI_PADDING; i++) {
			out.write(0);
		}
	}

	private static void write32(@NonNull OutputStream out, int value) throws IOException {
		out.write(value >> 24);
		out.write(value >> 16);
		out.write(value >> 8);
		out.write(value);
	}

	private static boolean smallDiff(int i) {
		return i > -17 && i < 16;
	}

	private static boolean smallerDiff(int i) {
		return i > -9 && i < 8;
	}

	private static boolean smallestDiff(int i) {
		return i > -3 && i < 2;
	}

	private static boolean equals(int r1, int g1, int b1, int a1, int r2, int g2, int b2, int a2) {
		return r1 == r2 && g1 == g2 && b1 == b2 && a1 == a2;
	}

}
