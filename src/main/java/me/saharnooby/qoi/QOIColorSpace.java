package me.saharnooby.qoi;

/**
 * A color space that can be specified in a QOI file.
 */
public enum QOIColorSpace {

	/**
	 * sRGB color space.
	 */
	SRGB,
	/**
	 * sRGB color space with linear alpha channel.
	 */
	SRGB_LINEAR_ALPHA,
	/**
	 * All channels are linear.
	 */
	LINEAR

}
