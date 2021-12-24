<img src="https://qoiformat.org/qoi-logo.svg" alt="QoiSharp" width="256"/>

# qoi-java

A pure Java 8 implementation of [Quite OK Image Format](https://github.com/phoboslab/qoi).

This library has no runtime dependencies, including Java AWT. `BufferedImage` support is provided using separate module [qoi-java-awt](https://github.com/saharNooby/qoi-java-awt).

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
    <version>1.0.0</version>
</dependency>
```

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

### Use with `BufferedImage`

To convert QOI images to BufferedImages and back, you need to also add [qoi-java-awt](https://github.com/saharNooby/qoi-java-awt) dependency.

Use methods in class `me.saharnooby.qoi.QOIUtilAWT`.

Usage example:

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

This project uses semantic versioning. Until QOI file format is finalized, major version will remain 0.