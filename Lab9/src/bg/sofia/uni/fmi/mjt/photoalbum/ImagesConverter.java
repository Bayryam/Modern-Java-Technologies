package bg.sofia.uni.fmi.mjt.photoalbum;

import bg.sofia.uni.fmi.mjt.photoalbum.image.Image;
import bg.sofia.uni.fmi.mjt.photoalbum.threads.ImageConsumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ImagesConverter {

    private final Queue<Image> imagesForConverting = new LinkedList<>();
    private final List<ImageConsumer> imageConsumers = new ArrayList<>();
    private boolean areAllImagesAdded = false;

    public ImagesConverter(int numberOfConsumers) {
        for (int i = 0; i < numberOfConsumers; i++) {
            ImageConsumer imageConsumer = new ImageConsumer(this);
            imageConsumers.add(imageConsumer);
            imageConsumer.start();
        }
    }

    public synchronized void addImage(Image image) {
        imagesForConverting.add(image);
        this.notifyAll();
    }

    public synchronized Image getImage() {
        while (imagesForConverting.isEmpty() && !areAllImagesAdded) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                System.err.print("Unexpected exception was thrown: " + e.getMessage());
            }
        }

        return imagesForConverting.poll();
    }

    public List<ImageConsumer> getConsumers() {
        return Collections.unmodifiableList(imageConsumers);
    }

    public synchronized void endConsumers() {
        areAllImagesAdded = true;
        this.notifyAll();
    }
}
