package bg.sofia.uni.fmi.mjt.photoalbum;

import bg.sofia.uni.fmi.mjt.photoalbum.threads.ImageConsumer;
import bg.sofia.uni.fmi.mjt.photoalbum.threads.ImageProducer;

import java.io.IOException;
import java.io.UncheckedIOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

public class ParallelMonochromeAlbumCreator implements MonochromeAlbumCreator {

    private static final String GLOB = "{*.jpeg,*.jpg,*.png}";
    private final ImagesConverter converter;
    private final List<Thread> producers;

    public ParallelMonochromeAlbumCreator(int imageProcessorsCount) {
        converter = new ImagesConverter(imageProcessorsCount);
        producers = new ArrayList<>();
    }

    private void setDirectoryToAllConsumers(String directory) {
        for (ImageConsumer imageConsumer : converter.getConsumers()) {
            imageConsumer.setDirectory(directory);
        }
    }

    @Override
    public void processImages(String sourceDirectory, String outputDirectory) {
        Path sourceDirectoryPath = Path.of(sourceDirectory);
        Path outputDirectoryPath = Path.of(outputDirectory);

        if (!Files.exists(outputDirectoryPath)) {
            try {
                Files.createDirectories(outputDirectoryPath);
            } catch (IOException exception) {
                throw new UncheckedIOException("Unable to create directory!", exception);
            }
        }

        setDirectoryToAllConsumers(outputDirectory);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDirectoryPath, GLOB)) {
            for (Path filePath : stream) {
                Thread imageProducer = new Thread(new ImageProducer(converter, filePath));
                producers.add(imageProducer);
                imageProducer.start();
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("There is an issue loading the files!", exception);
        }

        try {
            joinProducers(producers);
            joinConsumers();
        } catch (InterruptedException exception) {
            System.err.print("Unexpected exception was thrown: " + exception.getMessage());
        }
    }

    private void joinConsumers() throws InterruptedException {
        for (Thread imageConsumer : converter.getConsumers()) {
            imageConsumer.join();
        }
    }

    private void joinProducers(List<Thread> producers) throws InterruptedException {
        for (Thread producer : producers) {
            producer.join();
        }
        converter.endConsumers();
    }

}
