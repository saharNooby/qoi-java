package me.saharnooby.qoi;

import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Objects;
import java.util.Random;

/**
 * @author saharNooby
 * @since 16:38 01.12.2021
 */
class QOITest {

	private final ByteArrayOutputStream out = new ByteArrayOutputStream();

	@Test
	void testEmptyImageRGB() throws Exception {
		int size = 128;

		testEncodingDecoding(new byte[3 * size * size], size, size, 3);
	}

	@Test
	void testEmptyImageRGBA() throws Exception {
		int size = 128;

		testEncodingDecoding(new byte[4 * size * size], size, size, 4);
	}

	@Test
	void testRandomImage() throws Exception {
		int width = 800;
		int height = 600;

		Random random = new Random("seed".hashCode());

		for (int channels : new int[] {3, 4}) {
			byte[] data = new byte[width * height * channels];

			random.nextBytes(data);

			testEncodingDecoding(data, width, height, channels);
		}
	}

	@Test
	void testNormalImage() throws Exception {
		InputStream in = Objects.requireNonNull(getClass().getResourceAsStream("/orange-cross.qoi"), "Test image not found");

		QOIImage rgb = QOIDecoder.decode(new BufferedInputStream(in), 3);

		in = Objects.requireNonNull(getClass().getResourceAsStream("/orange-cross.qoi"), "Test image not found");

		QOIImage rgba = QOIDecoder.decode(new BufferedInputStream(in), 4);

		testEncodingDecoding(rgb.getPixelData(), rgb.getWidth(), rgb.getHeight(), 3);
		testEncodingDecoding(rgba.getPixelData(), rgba.getWidth(), rgba.getHeight(), 4);
	}

	/**
	 * Tests that after encoding and decoding back data stays the same.
	 */
	private void testEncodingDecoding(byte @NonNull [] pixelData, int width, int height, int channels) throws Exception {
		this.out.reset();

		QOIEncoder.encode(new QOIImage(width, height, channels, QOIColorSpace.SRGB, pixelData), this.out);

		QOIImage decoded = decode(channels);

		Assertions.assertArrayEquals(pixelData, decoded.getPixelData());

		// Try read with different channel count
		int differentChannels = channels == 3 ? 4 : 3;

		decoded = decode(differentChannels);

		for (int i = 0; i < width * height; i++) {
			Assertions.assertEquals(pixelData[i * channels], decoded.getPixelData()[i * differentChannels]);
			Assertions.assertEquals(pixelData[i * channels + 1], decoded.getPixelData()[i * differentChannels + 1]);
			Assertions.assertEquals(pixelData[i * channels + 2], decoded.getPixelData()[i * differentChannels + 2]);

			if (differentChannels == 4) {
				Assertions.assertEquals(0xFF, decoded.getPixelData()[i * differentChannels + 3] & 0xFF);
			}
		}
	}

	/**
	 * Decodes current buffer into raw pixel data.
	 */
	private QOIImage decode(int channels) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(this.out.toByteArray());

		QOIImage decoded = QOIDecoder.decode(in, channels);

		Assertions.assertEquals(0, in.available(), "Expected input stream to be read fully");

		return decoded;
	}

}