<img src="https://qoiformat.org/qoi-logo.svg" alt="QoiSharp" width="256"/>

# qoi-java

A pure Java 8 implementation of [Quite OK Image Format](https://github.com/phoboslab/qoi).

This library has no runtime dependencies, including Java AWT. `ImageIO`/`BufferedImage` support is provided using separate module [qoi-java-awt](https://github.com/saharNooby/qoi-java-awt).

## How to Use

### Build and Install

You will need Git, Maven and JDK 8 or higher.

```shell
git clone https://github.com/saharNooby/qoi-java.git
cd qoi-java
mvn clean install
```

Add this library as a dependency to your build system. Maven example:

```xml
<dependency>
    <groupId>me.saharnooby</groupId>
    <artifactId>qoi-java</artifactId>
    <version>1.2.0</version>
</dependency>
```

Additionally, if you want to use QOI with `ImageIO` API, you need to install [qoi-java-awt](https://github.com/saharNooby/qoi-java-awt). 

### Usage

Use methods in class `me.saharnooby.qoi.QOIUtil`.

Usage example:

```java
// Read image from a file
QOIImage image = QOIUtil.readFile(new File("image.qoi"));

System.out.println("Image size: " + image.getWidth() + " x " + image.getHeight());

// Access pixel data
System.out.println("Red channel is " + image.getPixelData()[0]);

// Create new 1x1 RGBA image from raw pixel data
byte[] pixelData = {(byte) 255, 127, 0, (byte) 255};

QOIImage orangeImage = QOIUtil.createFromPixelData(pixelData, 1, 1, 4);

// Write image to a file
QOIUtil.writeImage(orangeImage, new File("orange.qoi"));
```

### Use with `ImageIO`

To use QOI with `ImageIO`, you need to also install and add [qoi-java-awt](https://github.com/saharNooby/qoi-java-awt) dependency. It provides an [ImageIO plugin](https://docs.oracle.com/javase/8/docs/technotes/guides/imageio/spec/extending.fm1.html), which installs automatically using [service provider mechanism](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html).

To read and write QOI images, use standard `ImageIO` methods:

```java
// Read QOI image from a file
BufferedImage image = ImageIO.read(new File("image.qoi"));

Objects.requireNonNull(image, "Invalid QOI image");

// Write QOI image into a file
if (!ImageIO.write(image, "QOI", new File("copy.qoi"))) {
	throw new IllegalStateException("Image type is not supported");
}
```

To convert QOI images into BufferedImages and back, use methods in class `me.saharnooby.qoi.QOIUtilAWT`:

```java
// Convert PNG to QOI
BufferedImage image = ImageIO.read(new File("image.png"));
QOIImage qoi = QOIUtilAWT.createFromBufferedImage(image);
QOIUtil.writeImage(qoi, new File("image.qoi"));

// Convert QOI to PNG
QOIImage secondImage = QOIUtil.readFile(new File("second-image.qoi"));
BufferedImage convertedImage = QOIUtilAWT.convertToBufferedImage(secondImage);
ImageIO.write(convertedImage, "PNG", new File("second-image.png"));
```

## Compatibility

No AWT classes are used, so it should be compatible with Android. Please report compatibility issues, if they arise.

## Versioning

This project uses semantic versioning.
