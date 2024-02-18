package bg.sofia.uni.fmi.mjt.crypto.server.logging;

import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;

import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileLogger implements Loggable {
    private final String logFilePath;

    public FileLogger(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Override
    public void log(String toBeLogged) throws LoggerException {
        logToFile(logFilePath, toBeLogged);
    }

    private void logToFile(String pathParameters, String toBeLogged) throws LoggerException {
        Path path = Path.of(pathParameters);
        createPathIfNeeded(path);

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(toBeLogged);
            writer.flush();
        } catch (IOException exception) {
            throw new LoggerException("There was a failure of IO operation!", exception);
        }
    }

    private void createPathIfNeeded(Path path) throws LoggerException {
        try {
            Files.createDirectories(path.getParent().toAbsolutePath());
            Files.createFile(path.toAbsolutePath());
        } catch (FileAlreadyExistsException exception) {
                //no need for creating the path
        } catch (IOException exception) {
            throw new LoggerException("There was a failure of IO operation!", exception);
        }
    }
}