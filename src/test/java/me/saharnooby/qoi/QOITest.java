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
		int channels = 3;

		testEncodingDecoding(new byte[channels * size * size], size, size, channels);
	}

	@Test
	void testEmptyImageRGBA() throws Exception {
		int size = 128;
		int channels = 4;

		testEncodingDecoding(new byte[channels * size * size], size, size, channels);
	}

	@Test
	void testRandomImageRGB() throws Exception {
		int width = 800;
		int height = 600;
		int channels = 3;

		Random random = new Random("seed1".hashCode());

		byte[] data = new byte[width * height * channels];

		random.nextBytes(data);

		testEncodingDecoding(data, width, height, channels);
	}

	@Test
	void testRandomImageRGBA() throws Exception {
		int width = 800;
		int height = 600;
		int channels = 4;

		Random random = new Random("seed2".hashCode());

		byte[] data = new byte[width * height * channels];

		random.nextBytes(data);

		testEncodingDecoding(data, width, height, channels);
	}

	@Test
	void testOrangeRGB() throws Exception {
		testImage(3, "/orange.qoi");
	}

	@Test
	void testOrangeRGBA() throws Exception {
		testImage(4, "/orange.qoi");
	}

	@Test
	void testOrangeCrossRGB() throws Exception {
		testImage(3, "/orange-cross.qoi");
	}

	@Test
	void testOrangeCrossRGBA() throws Exception {
		testImage(4, "/orange-cross.qoi");
	}

	private void testImage(int channels, @NonNull String imagePath) throws Exception {
		InputStream in = Objects.requireNonNull(getClass().getResourceAsStream(imagePath), "Test image " + imagePath + " not found");

		QOIImage rgb = QOIDecoder.decode(new BufferedInputStream(in), channels);

		testEncodingDecoding(rgb.getPixelData(), rgb.getWidth(), rgb.getHeight(), channels);
	}

	/**
	 * Tests that data stays the same after encoding and decoding back.
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