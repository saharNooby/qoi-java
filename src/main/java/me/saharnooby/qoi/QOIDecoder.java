package me.saharnooby.qoi;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

import static me.saharnooby.qoi.QOICodec.*;

/**
 * Contains method that decodes data stream into raw pixel data.
 */
public final class QOIDecoder {

	/**
	 * Decodes data in the input stream into raw pixel data.
	 * <p>This method does buffering by itself, for optimal performance the input stream should not be buffered.</p>
	 * <p>This method may read past a single valid QOI image, if more data is available in the stream. This is
	 * usually not a concern when reading regular image files. To prevent this behaviour, use overload with
	 * <code>doNotTouchDataAfterImage</code> set to <code>true</code>.</p>
	 * @param inputStream Input stream.
	 * @param channels Channel count. Allowed values are 3, 4 and 0 (read as many channels as actually stored).
	 * @return QOI image.
	 * @throws IllegalArgumentException If channel count is invalid.
	 * @throws InvalidQOIStreamException If provided data does not represent a valid QOI image.
	 * @throws IOException On any IO error.
	 */
	public static QOIImage decode(@NonNull InputStream inputStream, int channels) throws IOException {
		return decode(inputStream, channels, false);
	}

	/**
	 * Decodes data in the input stream into raw pixel data.
	 * <p>If <code>doNotTouchDataAfterImage</code> is set to <code>false</code>, this method does
	 * buffering by itself, for optimal performance the input stream should not be buffered.</p>
	 * @param inputStream Input stream.
	 * @param channels Channel count. Allowed values are 3, 4 and 0 (read as many channels as actually stored).
	 * @param doNotTouchDataAfterImage If set to true, decoder will not read past a single valid QOI image.
	 *                                 You then need to provide a {@link java.io.BufferedInputStream} for optimal performance.
	 * @return QOI image.
	 * @throws IllegalArgumentException If channel count is invalid.
	 * @throws InvalidQOIStreamException If provided data does not represent a valid QOI image.
	 * @throws IOException On any IO error.
	 */
	public static QOIImage decode(@NonNull InputStream inputStream, int channels, boolean doNotTouchDataAfterImage) throws IOException {
		if (channels != 0 && channels != 3 && channels != 4) {
			throw new IllegalArgumentException("Invalid channel count, must be 0, 3 or 4");
		}

		// This custom buffering class is faster than BufferedInputStream and allows for controlled buffering
		Input in = new Input(inputStream, !doNotTouchDataAfterImage);

		int headerMagic = in.readInt();

		if (headerMagic != QOI_MAGIC) {
			throw new InvalidQOIStreamException("Invalid magic value, probably not a QOI image");
		}

		int width = in.readInt();

		if (width < 1) {
			throw new InvalidQOIStreamException("Invalid image width");
		}

		int height = in.readInt();

		if (height < 1) {
			throw new InvalidQOIStreamException("Invalid image height");
		}

		int storedChannels = in.read() & 0xFF;

		if (storedChannels != 3 && storedChannels != 4) {
			throw new InvalidQOIStreamException("Invalid stored channel count");
		}

		if (channels == 0) {
			channels = storedChannels;
		}

		QOIColorSpace colorSpace = in.readColorSpace();

		// Duplicating decoder for two specific cases improves performance almost by 26%
		byte[] pixelData = channels == 3 ?
				read3(in, width, height) :
				read4(in, width, height);

		for (int i = 0; i < 8; i++) {
			if (QOI_PADDING[i] != in.readSkipBuffer()) {
				throw new InvalidQOIStreamException("Invalid padding");
			}
		}

		return new QOIImage(width, height, channels, colorSpace, pixelData);
	}

	// Read into 3-channel RGB buffer
	private static byte[] read3(@NonNull Input in, int width, int height) throws IOException {
		// Check for overflow on big images
		int pixelDataLength = Math.multiplyExact(Math.multiplyExact(width, height), 3);

		byte[] pixelData = new byte[pixelDataLength];

		byte[] index = createHashTableRGBA();

		byte pixelR = 0;
		byte pixelG = 0;
		byte pixelB = 0;
		byte pixelA = (byte) 0xFF;

		for (int pixelPos = 0; pixelPos < pixelDataLength; pixelPos += 3) {
			int b1 = in.read() & 0xFF;

			if (b1 == QOI_OP_RGB) {
				pixelR = in.read();
				pixelG = in.read();
				pixelB = in.read();
			} else if (b1 == QOI_OP_RGBA) {
				pixelR = in.read();
				pixelG = in.read();
				pixelB = in.read();
				pixelA = in.read();
			} else {
				switch (b1 & QOI_MASK_2) {
					case QOI_OP_INDEX:
						int indexPos = (b1 & ~QOI_MASK_2) << 2;

						pixelR = index[indexPos];
						pixelG = index[indexPos + 1];
						pixelB = index[indexPos + 2];
						pixelA = index[indexPos + 3];

						break;
					case QOI_OP_DIFF:
						pixelR += ((b1 >> 4) & 0x03) - 2;
						pixelG += ((b1 >> 2) & 0x03) - 2;
						pixelB += (b1 & 0x03) - 2;

						break;
					case QOI_OP_LUMA:
						// Safe widening conversion
						int b2 = in.read();
						int vg = (b1 & 0x3F) - 32;
						pixelR += vg - 8 + ((b2 >> 4) & 0x0F);
						pixelG += vg;
						pixelB += vg - 8 + (b2 & 0x0F);

						break;
					case QOI_OP_RUN:
						int run = b1 & 0x3F;

						for (int i = 0; i < run; i++) {
							pixelData[pixelPos] = pixelR;
							pixelData[pixelPos + 1] = pixelG;
							pixelData[pixelPos + 2] = pixelB;

							pixelPos += 3;
						}

						break;
				}
			}

			int indexPos = getHashTableIndexRGBA(pixelR, pixelG, pixelB, pixelA);
			index[indexPos] = pixelR;
			index[indexPos + 1] = pixelG;
			index[indexPos + 2] = pixelB;
			index[indexPos + 3] = pixelA;

			pixelData[pixelPos] = pixelR;
			pixelData[pixelPos + 1] = pixelG;
			pixelData[pixelPos + 2] = pixelB;
		}

		return pixelData;
	}

	// Read into 4-channel RGBA buffer
	private static byte[] read4(@NonNull Input in, int width, int height) throws IOException {
		// Check for overflow on big images
		int pixelDataLength = Math.multiplyExact(Math.multiplyExact(width, height), 4);

		byte[] pixelData = new byte[pixelDataLength];

		byte[] index = createHashTableRGBA();

		byte pixelR = 0;
		byte pixelG = 0;
		byte pixelB = 0;
		byte pixelA = (byte) 0xFF;

		for (int pixelPos = 0; pixelPos < pixelDataLength; pixelPos += 4) {
			int b1 = in.read() & 0xFF;

			if (b1 == QOI_OP_RGB) {
				pixelR = in.read();
				pixelG = in.read();
				pixelB = in.read();
			} else if (b1 == QOI_OP_RGBA) {
				pixelR = in.read();
				pixelG = in.read();
				pixelB = in.read();
				pixelA = in.read();
			} else {
				switch (b1 & QOI_MASK_2) {
					case QOI_OP_INDEX:
						int indexPos = (b1 & ~QOI_MASK_2) << 2;

						pixelR = index[indexPos];
						pixelG = index[indexPos + 1];
						pixelB = index[indexPos + 2];
						pixelA = index[indexPos + 3];

						break;
					case QOI_OP_DIFF:
						pixelR += ((b1 >> 4) & 0x03) - 2;
						pixelG += ((b1 >> 2) & 0x03) - 2;
						pixelB += (b1 & 0x03) - 2;

						break;
					case QOI_OP_LUMA:
						// Safe widening conversion
						int b2 = in.read();
						int vg = (b1 & 0x3F) - 32;
						pixelR += vg - 8 + ((b2 >> 4) & 0x0F);
						pixelG += vg;
						pixelB += vg - 8 + (b2 & 0x0F);

						break;
					case QOI_OP_RUN:
						int run = b1 & 0x3F;

						for (int i = 0; i < run; i++) {
							pixelData[pixelPos] = pixelR;
							pixelData[pixelPos + 1] = pixelG;
							pixelData[pixelPos + 2] = pixelB;
							pixelData[pixelPos + 3] = pixelA;

							pixelPos += 4;
						}

						break;
				}
			}

			int indexPos = getHashTableIndexRGBA(pixelR, pixelG, pixelB, pixelA);
			index[indexPos] = pixelR;
			index[indexPos + 1] = pixelG;
			index[indexPos + 2] = pixelB;
			index[indexPos + 3] = pixelA;

			pixelData[pixelPos] = pixelR;
			pixelData[pixelPos + 1] = pixelG;
			pixelData[pixelPos + 2] = pixelB;
			pixelData[pixelPos + 3] = pixelA;
		}

		return pixelData;
	}

	private static final class Input {

		private final InputStream in;
		private final byte[] buffer;
		private int position;
		private int read;

		private Input(@NonNull InputStream in, boolean useBuffer) {
			this.in = in;

			if (useBuffer) {
				this.buffer = new byte[8192];
			} else {
				this.buffer = null;
			}
		}

		public byte read() throws IOException {
			if (this.buffer == null) {
				return readSingleByte();
			}

			if (this.position == this.read) {
				this.read = this.in.read(this.buffer);

				if (this.read == -1) {
					throw new InvalidQOIStreamException("Unexpected end of stream");
				}

				this.position = 0;
			}

			return this.buffer[this.position++];
		}

		public byte readSkipBuffer() throws IOException {
			if (this.buffer == null) {
				return readSingleByte();
			}

			if (this.position == this.read) {
				return readSingleByte();
			}

			return this.buffer[this.position++];
		}

		private byte readSingleByte() throws IOException {
			int read = this.in.read();

			if (read == -1) {
				throw new InvalidQOIStreamException("Unexpected end of stream");
			}

			return (byte) read;
		}

		public int readInt() throws IOException {
			int a = read() & 0xFF;
			int b = read() & 0xFF;
			int c = read() & 0xFF;
			int d = read() & 0xFF;
			return (a << 24) | (b << 16) | (c << 8) | d;
		}

		public QOIColorSpace readColorSpace() throws IOException {
			int value = read() & 0xFF;

			switch (value) {
				case QOI_SRGB:
					return QOIColorSpace.SRGB;
				case QOI_LINEAR:
					return QOIColorSpace.LINEAR;
			}

			throw new InvalidQOIStreamException("Invalid color space value " + value);
		}

	}

}
