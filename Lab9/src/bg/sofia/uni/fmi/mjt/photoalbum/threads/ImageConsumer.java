package bg.sofia.uni.fmi.mjt.photoalbum.threads;

import bg.sofia.uni.fmi.mjt.photoalbum.ImagesConverter;
import bg.sofia.uni.fmi.mjt.photoalbum.image.Image;

public class ImageConsumer extends Thread {

    private final ImagesConverter converter;
    private String directory;

    public ImageConsumer(ImagesConverter converter) {
        this.converter = converter;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public void run() {
        Image image;
        while ((image = converter.getImage()) != null) {
            Image convertedImage = Image.convertToBlackAndWhite(image);
            Image.saveImage(convertedImage, directory);
        }
    }
}