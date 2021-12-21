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
			case LINEAR:
				out.write(QOI_LINEAR);
				break;
			default:
				throw new IllegalStateException("Unsupported color space");
		}

		byte[] index = createHashTable();

		int run = 0;

		byte prevR = 0;
		byte prevG = 0;
		byte prevB = 0;
		byte prevA = (byte) 0xFF;

		byte pixelR;
		byte pixelG;
		byte pixelB;
		byte pixelA = (byte) 0xFF;

		boolean hasAlpha = channels == 4;

		for (int pixelPos = 0; pixelPos < pixelData.length; pixelPos += channels) {
			pixelR = pixelData[pixelPos];
			pixelG = pixelData[pixelPos + 1];
			pixelB = pixelData[pixelPos + 2];

			if (hasAlpha) {
				pixelA = pixelData[pixelPos + 3];
			}

			boolean prevEqualsCurrent = equals(prevR, prevG, prevB, prevA, pixelR, pixelG, pixelB, pixelA);

			if (prevEqualsCurrent) {
				run++;

				if (run == 62 || pixelPos + channels == pixelData.length) {
					out.write(QOI_OP_RUN | (run - 1));

					run = 0;
				}
			} else {
				if (run > 0) {
					out.write(QOI_OP_RUN | (run - 1));

					run = 0;
				}

				int indexPos = getHashTableIndex(pixelR, pixelG, pixelB, pixelA);

				if (equals(pixelR, pixelG, pixelB, pixelA, index[indexPos], index[indexPos + 1], index[indexPos + 2], index[indexPos + 3])) {
					out.write(QOI_OP_INDEX | (indexPos / 4));
				} else {
					index[indexPos] = pixelR;
					index[indexPos + 1] = pixelG;
					index[indexPos + 2] = pixelB;
					index[indexPos + 3] = pixelA;

					if (prevA == pixelA) {
						int dr = (pixelR & 0xFF) - (prevR & 0xFF);
						int dg = (pixelG & 0xFF) - (prevG & 0xFF);
						int db = (pixelB & 0xFF) - (prevB & 0xFF);

						int dgr = dr - dg;
						int dgb = db - dg;

						if (smallestDiff(dr) && smallestDiff(dg) && smallestDiff(db)) {
							out.write(QOI_OP_DIFF | (dr + 2) << 4 | (dg + 2) << 2 | (db + 2));
						} else if (smallerDiff(dgr) && smallDiff(dg) && smallerDiff(dgb)) {
							out.write(QOI_OP_LUMA | (dg + 32));
							out.write((dgr + 8) << 4 | (dgb + 8));
						} else {
							out.write(QOI_OP_RGB);
							out.write(pixelR);
							out.write(pixelG);
							out.write(pixelB);
						}
					} else {
						out.write(QOI_OP_RGBA);
						out.write(pixelR);
						out.write(pixelG);
						out.write(pixelB);
						out.write(pixelA);
					}
				}
			}

			prevR = pixelR;
			prevG = pixelG;
			prevB = pixelB;
			prevA = pixelA;
		}

		out.write(QOI_PADDING);
	}

	private static void write32(@NonNull OutputStream out, int value) throws IOException {
		out.write(value >> 24);
		out.write(value >> 16);
		out.write(value >> 8);
		out.write(value);
	}

	private static boolean smallDiff(int i) {
		return i > -33 && i < 32;
	}

	private static boolean smallerDiff(int i) {
		return i > -9 && i < 8;
	}

	private static boolean smallestDiff(int i) {
		return i > -3 && i < 2;
	}

	private static boolean equals(byte r1, byte g1, byte b1, byte a1, byte r2, byte g2, byte b2, byte a2) {
		return r1 == r2 &&
				g1 == g2 &&
				b1 == b2 &&
				a1 == a2;
	}

}
