package me.saharnooby.qoi;

import lombok.NonNull;

import java.io.*;

/**
 * Contains public API methods of the library.
 */
@SuppressWarnings("unused")
public final class QOIUtil {

	/**
	 * Creates a QOI image from raw pixel data.
	 * Channel count is detected automatically.
	 * Data array is not copied.
	 * @param pixelData Pixel data array in the form of [R, G, B, (A,) ...].
	 * @param width Image width, must be positive.
	 * @param height Image height, must be positive.
	 * @return QOI image.
	 * @throws IllegalArgumentException If any arguments are invalid.
	 */
	public static QOIImage createFromPixelData(byte @NonNull [] pixelData, int width, int height) {
		return createFromPixelData(pixelData, width, height, pixelData.length / width / height);
	}

	/**
	 * Creates a QOI image from raw pixel data.
	 * Data array is not copied.
	 * @param pixelData Pixel data array in the form of [R, G, B, (A,) ...]. Alpha must be present only if channel count is 4.
	 * @param width Image width, must be positive.
	 * @param height Image height, must be positive.
	 * @param channels Channel count, must be 3 of 4.
	 * @return QOI image.
	 * @throws IllegalArgumentException If any arguments are invalid.
	 */
	public static QOIImage createFromPixelData(byte @NonNull [] pixelData, int width, int height, int channels) {
		return createFromPixelData(pixelData, width, height, channels, QOIColorSpace.SRGB);
	}

	/**
	 * Creates a QOI image from raw pixel data.
	 * Data array is not copied.
	 * @param pixelData Pixel data array in the form of [R, G, B, (A,) ...]. Alpha must be present only if channel count is 4.
	 * @param width Image width, must be positive.
	 * @param height Image height, must be positive.
	 * @param channels Channel count, must be 3 or 4.
	 * @param colorSpace Color space.
	 * @return QOI image.
	 * @throws IllegalArgumentException If any arguments are invalid.
	 */
	public static QOIImage createFromPixelData(byte @NonNull [] pixelData, int width, int height, int channels, @NonNull QOIColorSpace colorSpace) {
		if (width < 1) {
			throw new IllegalArgumentException("Width must be positive");
		}

		if (height < 1) {
			throw new IllegalArgumentException("Height must be positive");
		}

		if (channels != 3 && channels != 4) {
			throw new IllegalArgumentException("3 or 4 channels are supported");
		}

		if (pixelData.length != width * height * channels) {
			throw new IllegalArgumentException("Unexpected pixel data array length, must match width * height * channels");
		}

		return new QOIImage(width, height, channels, colorSpace, pixelData);
	}

	/**
	 * Reads a QOI image from an input stream.
	 * @param in Input stream, should be buffered for optimal performance.
	 * @return QOI image.
	 * @throws InvalidQOIStreamException If provided data does not represent a valid QOI image.
	 * @throws IOException On any IO error.
	 */
	public static QOIImage readImage(@NonNull InputStream in) throws IOException {
		return readImage(in, 0);
	}

	/**
	 * Reads a QOI image from an input stream.
	 * @param in Input stream, should be buffered for optimal performance.
	 * @param channels Channel count, must be 0 (auto), 3 or 4.
	 * @return QOI image.
	 * @throws InvalidQOIStreamException If provided data does not represent a valid QOI image.
	 * @throws IOException On any IO error.
	 */
	public static QOIImage readImage(@NonNull InputStream in, int channels) throws IOException {
		return QOIDecoder.decode(in, channels);
	}

	/**
	 * Reads a QOI image from a file.
	 * @param file File.
	 * @return QOI image.
	 * @throws InvalidQOIStreamException If provided file does not represent a valid QOI image.
	 * @throws IOException On any IO error.
	 */
	public static QOIImage readFile(@NonNull File file) throws IOException {
		return readFile(file, 0);
	}

	/**
	 * Reads a QOI image from a file.
	 * @param file File.
	 * @param channels Channel count, must be 0 (auto), 3 or 4.
	 * @return QOI image.
	 * @throws InvalidQOIStreamException If provided file does not represent a valid QOI image.
	 * @throws IOException On any IO error.
	 */
	public static QOIImage readFile(@NonNull File file, int channels) throws IOException {
		try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
			return readImage(in, channels);
		}
	}

	/**
	 * Writes a QOI image into an output stream.
	 * @param image Image.
	 * @param out Output stream.
	 * @throws IOException On any IO error.
	 */
	public static void writeImage(@NonNull QOIImage image, @NonNull OutputStream out) throws IOException {
		if (out instanceof BufferedOutputStream || out instanceof ByteArrayOutputStream) {
			QOIEncoder.encode(image, out);
		} else {
			out = new BufferedOutputStream(out);

			QOIEncoder.encode(image, out);

			out.flush();
		}
	}

	/**
	 * Writes a QOI image into a file.
	 * @param image Image.
	 * @param file File.
	 * @throws IOException On any IO error.
	 */
	public static void writeImage(@NonNull QOIImage image, @NonNull File file) throws IOException {
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
			writeImage(image, out);
		}
	}

	/**
	 * Removes alpha channel from a 4-channel image, converting it to a 3-channel image.
	 * If provided image is already 3-channel, this method will return the provided image.
	 * @param image Source image.
	 * @return Converted image.
	 */
	public static QOIImage removeAlpha(@NonNull QOIImage image) {
		if (image.getChannels() != 4) {
			return image;
		}

		int width = image.getWidth();
		int height = image.getHeight();

		byte[] pixelData = image.getPixelData();
		byte[] newData = new byte[width * height * 3];

		for (int i = 0, j = 0; i < pixelData.length; i += 4, j += 3) {
			newData[j] = pixelData[i];
			newData[j + 1] = pixelData[i + 1];
			newData[j + 2] = pixelData[i + 2];
		}

		return new QOIImage(width, height, 3, image.getColorSpace(), newData);
	}

	/**
	 * Adds alpha channel to a 3-channel image, converting it to a 4-channel image.
	 * If provided image is already 4-channel, this method will return the provided image.
	 * @param image Source image.
	 * @param alpha Alpha channel value. Bits higher than 8 will be ignored.
	 * @return Converted image.
	 */
	public static QOIImage addAlpha(@NonNull QOIImage image, int alpha) {
		if (image.getChannels() != 3) {
			return image;
		}

		int width = image.getWidth();
		int height = image.getHeight();

		byte[] pixelData = image.getPixelData();
		byte[] newData = new byte[width * height * 4];

		byte alphaByte = (byte) alpha;

		for (int i = 0, j = 0; i < pixelData.length; i += 3, j += 4) {
			newData[j] = pixelData[i];
			newData[j + 1] = pixelData[i + 1];
			newData[j + 2] = pixelData[i + 2];
			newData[j + 3] = alphaByte;
		}

		return new QOIImage(width, height, 4, image.getColorSpace(), newData);
	}

}
