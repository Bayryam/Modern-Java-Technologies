package bg.sofia.uni.fmi.mjt.photoalbum.image;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.UncheckedIOException;

import java.nio.file.Path;

public class Image {
    private static final int EXTENSION_LENGTH = 3;
    private static final String PNG = "png";
    private static final String JPG = "jpg";
    private static final String JPEG = "jpeg";
    private final String name;
    private final BufferedImage data;

    public Image(String name, BufferedImage data) {
        this.name = name;
        this.data = data;
    }

    public static Image loadImage(Path imagePath) {
        try {
            BufferedImage imageData = ImageIO.read(imagePath.toFile());
            return new Image(imagePath.getFileName().toString(), imageData);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to load image %s", imagePath), e);
        }
    }

    public static Image convertToBlackAndWhite(Image image) {
        BufferedImage processedData =
                new BufferedImage(image.data.getWidth(),
                        image.data.getHeight(),
                        BufferedImage.TYPE_BYTE_GRAY);
        processedData.getGraphics().drawImage(image.data, 0, 0, null);

        return new Image(image.name, processedData);
    }

    public static void saveImage(Image img, String directory) {
        String extension = img.name.substring(img.name.length() - EXTENSION_LENGTH);
        if (!extension.equals(PNG) && !extension.equals(JPG)) {
            extension = JPEG;
        }

        Path filePath = Path.of(directory, img.name);

        try {
            ImageIO.write(img.data, extension, filePath.toFile());
        } catch (IOException exception) {
            throw new UncheckedIOException(String.format("Unable to save %s!", img.name), exception);
        }
    }
}