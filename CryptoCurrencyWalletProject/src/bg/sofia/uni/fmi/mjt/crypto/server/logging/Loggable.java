package bg.sofia.uni.fmi.mjt.crypto.server.logging;

import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;

public interface Loggable {
     /**
      *  Logs a given message.
      *
      * @param message The message to be logged.
      * @throws LoggerException If an error occurs during the logging process.
    */
    void log(String message) throws LoggerException;
}
