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
	 * <p>This method does buffering by itself, for optimal performance the output stream should not be buffered.</p>
	 * @param image QOI image.
	 * @param outputStream Output stream.
	 * @throws IOException On any IO error.
	 */
	public static void encode(@NonNull QOIImage image, @NonNull OutputStream outputStream) throws IOException {
		int channels = image.getChannels();

		byte[] pixelData = image.getPixelData();

		// This custom buffering class is slightly faster than BufferedOutputStream
		Output out = new Output(outputStream);

		out.writeInt(QOI_MAGIC);
		out.writeInt(image.getWidth());
		out.writeInt(image.getHeight());
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

		// Duplicating encoder for two specific cases improves performance almost by 20% for RGBA images
		if (channels == 3) {
			encode3(out, pixelData);
		} else {
			encode4(out, pixelData);
		}

		for (byte b : QOI_PADDING) {
			out.write(b);
		}

		out.flush();
	}

	// Encode 3-channel RGB buffer
	private static void encode3(@NonNull Output out, byte @NonNull [] pixelData) throws IOException {
		byte[] index = createHashTableRGB();

		int run = 0;

		byte prevR = 0;
		byte prevG = 0;
		byte prevB = 0;

		byte pixelR;
		byte pixelG;
		byte pixelB;

		for (int pixelPos = 0; pixelPos < pixelData.length; pixelPos += 3) {
			pixelR = pixelData[pixelPos];
			pixelG = pixelData[pixelPos + 1];
			pixelB = pixelData[pixelPos + 2];

			if (equals(prevR, prevG, prevB, pixelR, pixelG, pixelB)) {
				run++;

				if (run == 62) {
					out.write(QOI_OP_RUN | (run - 1));

					run = 0;
				}
			} else {
				if (run > 0) {
					out.write(QOI_OP_RUN | (run - 1));

					run = 0;
				}

				int indexPos = getHashTableIndexRGB(pixelR, pixelG, pixelB);

				if (equals(pixelR, pixelG, pixelB, index[indexPos], index[indexPos + 1], index[indexPos + 2])) {
					out.write(QOI_OP_INDEX | (indexPos / 3));
				} else {
					index[indexPos] = pixelR;
					index[indexPos + 1] = pixelG;
					index[indexPos + 2] = pixelB;

					int dr = (pixelR & 0xFF) - (prevR & 0xFF);
					int dg = (pixelG & 0xFF) - (prevG & 0xFF);
					int db = (pixelB & 0xFF) - (prevB & 0xFF);

					if (smallestDiff(dr) && smallestDiff(dg) && smallestDiff(db)) {
						out.write(QOI_OP_DIFF | (dr + 2) << 4 | (dg + 2) << 2 | (db + 2));
					} else {
						int dgr = dr - dg;
						int dgb = db - dg;

						if (smallerDiff(dgr) && smallDiff(dg) && smallerDiff(dgb)) {
							out.write(QOI_OP_LUMA | (dg + 32));
							out.write((dgr + 8) << 4 | (dgb + 8));
						} else {
							out.write(QOI_OP_RGB);
							out.write(pixelR, pixelG, pixelB);
						}
					}
				}

				prevR = pixelR;
				prevG = pixelG;
				prevB = pixelB;
			}
		}

		if (run > 0) {
			out.write(QOI_OP_RUN | (run - 1));
		}
	}

	// Encode 4-channel RGBA buffer
	private static void encode4(@NonNull Output out, byte @NonNull [] pixelData) throws IOException {
		byte[] index = createHashTableRGBA();

		int run = 0;

		byte prevR = 0;
		byte prevG = 0;
		byte prevB = 0;
		byte prevA = (byte) 0xFF;

		byte pixelR;
		byte pixelG;
		byte pixelB;
		byte pixelA;

		for (int pixelPos = 0; pixelPos < pixelData.length; pixelPos += 4) {
			pixelR = pixelData[pixelPos];
			pixelG = pixelData[pixelPos + 1];
			pixelB = pixelData[pixelPos + 2];
			pixelA = pixelData[pixelPos + 3];

			if (equals(prevR, prevG, prevB, prevA, pixelR, pixelG, pixelB, pixelA)) {
				run++;

				if (run == 62) {
					out.write(QOI_OP_RUN | (run - 1));

					run = 0;
				}
			} else {
				if (run > 0) {
					out.write(QOI_OP_RUN | (run - 1));

					run = 0;
				}

				int indexPos = getHashTableIndexRGBA(pixelR, pixelG, pixelB, pixelA);

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

						if (smallestDiff(dr) && smallestDiff(dg) && smallestDiff(db)) {
							out.write(QOI_OP_DIFF | (dr + 2) << 4 | (dg + 2) << 2 | (db + 2));
						} else {
							int dgr = dr - dg;
							int dgb = db - dg;

							if (smallerDiff(dgr) && smallDiff(dg) && smallerDiff(dgb)) {
								out.write(QOI_OP_LUMA | (dg + 32));
								out.write((dgr + 8) << 4 | (dgb + 8));
							} else {
								out.write(QOI_OP_RGB);
								out.write(pixelR, pixelG, pixelB);
							}
						}
					} else {
						out.write(QOI_OP_RGBA);
						out.write(pixelR, pixelG, pixelB, pixelA);
					}
				}

				prevR = pixelR;
				prevG = pixelG;
				prevB = pixelB;
				prevA = pixelA;
			}
		}

		if (run > 0) {
			out.write(QOI_OP_RUN | (run - 1));
		}
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

	private static boolean equals(byte r1, byte g1, byte b1, byte r2, byte g2, byte b2) {
		return r1 == r2 &&
				g1 == g2 &&
				b1 == b2;
	}

	private static final class Output {

		private static final int BUFFER_SIZE = 8192;

		private final OutputStream out;
		private final byte[] buffer = new byte[BUFFER_SIZE];
		private int written;

		private Output(@NonNull OutputStream out) {
			this.out = out;
		}

		public void write(byte value) throws IOException {
			if (this.written == BUFFER_SIZE) {
				doFlush();
			}

			this.buffer[this.written++] = value;
		}

		public void write(int value) throws IOException {
			write((byte) value);
		}

		public void write(byte a, byte b, byte c) throws IOException {
			if (this.written > BUFFER_SIZE - 3) {
				doFlush();
			}

			this.buffer[this.written] = a;
			this.buffer[this.written + 1] = b;
			this.buffer[this.written + 2] = c;
			this.written += 3;
		}

		public void write(byte a, byte b, byte c, byte d) throws IOException {
			if (this.written > BUFFER_SIZE - 4) {
				doFlush();
			}

			this.buffer[this.written] = a;
			this.buffer[this.written + 1] = b;
			this.buffer[this.written + 2] = c;
			this.buffer[this.written + 3] = d;
			this.written += 4;
		}

		private void writeInt(int value) throws IOException {
			write(value >> 24);
			write(value >> 16);
			write(value >> 8);
			write(value);
		}

		public void flush() throws IOException {
			if (this.written == 0) {
				return;
			}

			doFlush();
		}

		private void doFlush() throws IOException {
			this.out.write(this.buffer, 0, this.written);

			this.written = 0;
		}

	}

}
