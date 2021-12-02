package me.saharnooby.qoi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author saharNooby
 * @since 18:46 02.12.2021
 */
class QOIUtilTest {

	@Test
	void testRemoveAlpha() {
		QOIImage image = QOIUtil.createFromPixelData(new byte[] {1, 2, 3, 4, 5, 6, 7, 8}, 2, 1, 4);

		QOIImage removed = QOIUtil.removeAlpha(image);
		Assertions.assertEquals(2, removed.getWidth());
		Assertions.assertEquals(1, removed.getHeight());
		Assertions.assertEquals(3, removed.getChannels());
		Assertions.assertArrayEquals(new byte[] {1, 2, 3, 5, 6, 7}, removed.getPixelData());

		Assertions.assertSame(removed, QOIUtil.removeAlpha(removed));
	}

	@Test
	void testAddAlpha() {
		QOIImage image = QOIUtil.createFromPixelData(new byte[] {1, 2, 3, 4, 5, 6}, 2, 1, 3);

		QOIImage added = QOIUtil.addAlpha(image, 99);
		Assertions.assertEquals(2, added.getWidth());
		Assertions.assertEquals(1, added.getHeight());
		Assertions.assertEquals(4, added.getChannels());
		Assertions.assertArrayEquals(new byte[] {1, 2, 3, 99, 4, 5, 6, 99}, added.getPixelData());

		Assertions.assertSame(added, QOIUtil.addAlpha(added, 123));
	}

}