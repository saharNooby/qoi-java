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
	 * @param in Input stream, should be buffered for optimal performance.
	 * @param channels Channel count. Allowed values are 3, 4 and 0 (read as many channels as actually stored).
	 * @return QOI image.
	 * @throws IllegalArgumentException If channel count is invalid.
	 * @throws InvalidQOIStreamException If provided data does not represent a valid QOI image.
	 * @throws IOException On any IO error.
	 */
	public static QOIImage decode(@NonNull InputStream in, int channels) throws IOException {
		if (channels != 0 && channels != 3 && channels != 4) {
			throw new IllegalArgumentException("Invalid channel count, must be 0, 3 or 4");
		}

		int headerMagic = read32(in);

		if (headerMagic != QOI_MAGIC) {
			throw new InvalidQOIStreamException("Invalid magic value, probably not a QOI image");
		}

		int width = read32(in);

		if (width < 1) {
			throw new InvalidQOIStreamException("Invalid image width");
		}

		int height = read32(in);

		if (height < 1) {
			throw new InvalidQOIStreamException("Invalid image height");
		}

		int storedChannels = read(in);

		if (storedChannels != 3 && storedChannels != 4) {
			throw new InvalidQOIStreamException("Invalid stored channel count");
		}

		if (channels == 0) {
			channels = storedChannels;
		}

		QOIColorSpace colorSpace = readColorSpace(in);

		int pixelDataLength = width * height * channels;

		byte[] pixelData = new byte[pixelDataLength];

		byte[] index = createHashTable();

		byte pixelR = 0;
		byte pixelG = 0;
		byte pixelB = 0;
		byte pixelA = (byte) 0xFF;

		int run = 0;

		boolean hasAlpha = channels == 4;

		for (int pixelPos = 0; pixelPos < pixelDataLength; pixelPos += channels) {
			if (run > 0) {
				run--;
			} else {
				int b1 = read(in);

				if ((b1 & QOI_MASK_2) == QOI_INDEX) {
					int indexPos = (b1 ^ QOI_INDEX) * 4;

					pixelR = index[indexPos];
					pixelG = index[indexPos + 1];
					pixelB = index[indexPos + 2];
					pixelA = index[indexPos + 3];
				} else if ((b1 & QOI_MASK_3) == QOI_RUN_8) {
					run = b1 & 0x1F;
				} else if ((b1 & QOI_MASK_3) == QOI_RUN_16) {
					int b2 = read(in);

					run = (((b1 & 0x1F) << 8) | (b2)) + 32;
				} else if ((b1 & QOI_MASK_2) == QOI_DIFF_8) {
					pixelR += ((b1 >> 4) & 0x03) - 2;
					pixelG += ((b1 >> 2) & 0x03) - 2;
					pixelB += (b1 & 0x03) - 2;
				} else if ((b1 & QOI_MASK_3) == QOI_DIFF_16) {
					int b2 = read(in);

					pixelR += (b1 & 0x1F) - 16;
					pixelG += (b2 >> 4) - 8;
					pixelB += (b2 & 0x0F) - 8;
				} else if ((b1 & QOI_MASK_4) == QOI_DIFF_24) {
					int b2 = read(in);
					int b3 = read(in);

					pixelR += (((b1 & 0x0F) << 1) | (b2 >> 7)) - 16;
					pixelG += ((b2 & 0x7C) >> 2) - 16;
					pixelB += (((b2 & 0x03) << 3) | ((b3 & 0xE0) >> 5)) - 16;
					pixelA += (b3 & 0x1F) - 16;
				} else if ((b1 & QOI_MASK_4) == QOI_COLOR) {
					if ((b1 & 8) != 0) {
						pixelR = (byte) read(in);
					}

					if ((b1 & 4) != 0) {
						pixelG = (byte) read(in);
					}

					if ((b1 & 2) != 0) {
						pixelB = (byte) read(in);
					}

					if ((b1 & 1) != 0) {
						pixelA = (byte) read(in);
					}
				}

				int indexPos = getHashTableIndex(pixelR, pixelG, pixelB, pixelA);
				index[indexPos] = pixelR;
				index[indexPos + 1] = pixelG;
				index[indexPos + 2] = pixelB;
				index[indexPos + 3] = pixelA;
			}

			pixelData[pixelPos] = pixelR;
			pixelData[pixelPos + 1] = pixelG;
			pixelData[pixelPos + 2] = pixelB;

			if (hasAlpha) {
				pixelData[pixelPos + 3] = pixelA;
			}
		}

		for (int i = 0; i < QOI_PADDING; i++) {
			read(in);
		}

		return new QOIImage(width, height, channels, colorSpace, pixelData);
	}

	private static int read(@NonNull InputStream in) throws IOException {
		int read = in.read();

		if (read < 0) {
			throw new InvalidQOIStreamException("Unexpected end of stream");
		}

		return read;
	}

	private static int read32(@NonNull InputStream in) throws IOException {
		int a = read(in);
		int b = read(in);
		int c = read(in);
		int d = read(in);
		return (a << 24) | (b << 16) | (c << 8) | d;
	}

	private static QOIColorSpace readColorSpace(@NonNull InputStream in) throws IOException {
		int value = read(in);

		switch (value) {
			case QOI_SRGB:
				return QOIColorSpace.SRGB;
			case QOI_SRGB_LINEAR_ALPHA:
				return QOIColorSpace.SRGB_LINEAR_ALPHA;
			case QOI_LINEAR:
				return QOIColorSpace.LINEAR;
		}

		throw new InvalidQOIStreamException("Invalid color space value " + value);
	}

}
