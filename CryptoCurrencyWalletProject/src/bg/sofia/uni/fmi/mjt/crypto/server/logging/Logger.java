package bg.sofia.uni.fmi.mjt.crypto.server.logging;

import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;

public interface Logger {
    /**
     * Logs a message with log level INFO.
     *
     * @param message The message to be logged.
     * @throws LoggerException If an error occurs during the logging process.
     */
    void logInfo(String message) throws LoggerException;

    /**
     * Logs a message with log level WARN.
     *
     * @param message The message to be logged.
     * @throws LoggerException If an error occurs during the logging process.
     */
    void logWarning(String message) throws LoggerException;

    /**
     * Logs a message with log level ERROR.
     *
     * @param message The message to be logged.
     * @throws LoggerException If an error occurs during the logging process.
     */
    void logError(String message) throws LoggerException;

    /**
     * Logs a message with a specified log level.
     *
     * @param level The level of the log.
     * @param message The message to be logged.
     * @throws LoggerException If an error occurs during the logging process.
     */
    void log(LogLevel level, String message) throws LoggerException;
}
