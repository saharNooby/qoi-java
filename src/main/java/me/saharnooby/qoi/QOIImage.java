package me.saharnooby.qoi;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A bundle of QOI image metadata and raw pixel data.
 * Use methods in {@link QOIUtil} to create instances of this class.
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class QOIImage {

	/**
	 * Image width. Positive value.
	 */
	private final int width;
	/**
	 * Image height. Positive value.
	 */
	private final int height;
	/**
	 * Channel count. Supported values are 3 (no alpha) and 4 (with alpha).
	 */
	private final int channels;
	/**
	 * Color space of the image.
	 */
	@NonNull
	private final QOIColorSpace colorSpace;
	/**
	 * Raw pixel data in the form of [R, G, B, (A,) ...].
	 * The array has (width * height * channels) elements.
	 * Alpha is present when channel count is 4.
	 */
	private final byte @NonNull [] pixelData;

}
