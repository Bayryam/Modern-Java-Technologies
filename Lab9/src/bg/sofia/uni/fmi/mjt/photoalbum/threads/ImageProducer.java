package bg.sofia.uni.fmi.mjt.photoalbum.threads;

import bg.sofia.uni.fmi.mjt.photoalbum.ImagesConverter;
import bg.sofia.uni.fmi.mjt.photoalbum.image.Image;

import java.nio.file.Path;

public class ImageProducer implements Runnable {

    private final ImagesConverter converter;
    private final Image image;

    public ImageProducer(ImagesConverter converter, Path imagePath) {
        this.converter = converter;
        image = Image.loadImage(imagePath);
    }

    @Override
    public void run() {
        converter.addImage(image);
    }

}
