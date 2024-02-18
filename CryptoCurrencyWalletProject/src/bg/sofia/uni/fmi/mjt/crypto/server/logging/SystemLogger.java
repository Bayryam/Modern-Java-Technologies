package bg.sofia.uni.fmi.mjt.crypto.server.logging;

import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;

import java.time.LocalDateTime;

public class SystemLogger implements Logger {
    private static final String LOG_FILE_PATH = "logs/log.txt";
    private static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.INFO;
    private final LogLevel consoleLogLevel;
    private final LogLevel fileLogLevel;
    private final FileLogger fileLogger;
    private final ConsoleLogger consoleLogger;

    private SystemLogger(SystemLoggerBuilder builder) {
        this.consoleLogLevel = builder.consoleLogLevel;
        this.consoleLogger = builder.consoleLogger == null ? new ConsoleLogger() : builder.consoleLogger;
        this.fileLogLevel = builder.fileLogLevel;
        this.fileLogger = builder.fileLogger == null ? new FileLogger(builder.logPathFile) : builder.fileLogger;
    }

    public static SystemLoggerBuilder setup() {
        return new SystemLoggerBuilder();
    }

    @Override
    public void logInfo(String message) throws LoggerException {
        log(LogLevel.INFO, message);
    }

    @Override
    public void logWarning(String message) throws LoggerException {
        log(LogLevel.WARNING, message);
    }

    @Override
    public void logError(String message) throws LoggerException {
        log(LogLevel.ERROR, message);
    }

    @Override
    public void log(LogLevel level, String message) throws LoggerException {
        LocalDateTime time = LocalDateTime.now();

        if (consoleLogLevel.getCode() <= level.getCode()) {
            logInConsole(time, level, message);
        }

        if (fileLogLevel.getCode() <= level.getCode()) {
            logInFile(time, level, message);
        }
    }

    private void logInFile(LocalDateTime time, LogLevel level, String message) throws LoggerException {
        String logMessage = "{" + level.name() + "} {" + time + "} " + message + System.lineSeparator();
        fileLogger.log(logMessage);
    }

    private void logInConsole(LocalDateTime time, LogLevel level, String message) throws LoggerException {
        String logMessage = "{" + level.name() + "} {" + time + "} " + message + System.lineSeparator();
        consoleLogger.log(logMessage);
    }

    public static class SystemLoggerBuilder {

        private LogLevel consoleLogLevel = DEFAULT_LOG_LEVEL;
        private LogLevel fileLogLevel = DEFAULT_LOG_LEVEL;
        private ConsoleLogger consoleLogger;
        private FileLogger fileLogger;
        private String logPathFile = LOG_FILE_PATH;

        private SystemLoggerBuilder() {
        }

        public SystemLoggerBuilder setLogPathFile(String path) {
            this.logPathFile = path;
            return this;
        }

        public SystemLoggerBuilder setConsoleLogger(ConsoleLogger logger) {
            consoleLogger = logger;
            return this;
        }

        public SystemLoggerBuilder setConsoleLogLevel(LogLevel level) {
            consoleLogLevel = level;
            return this;
        }

        public SystemLoggerBuilder setFileLogger(FileLogger logger) {
            fileLogger = logger;
            return this;
        }

        public SystemLoggerBuilder setFileLogLevel(LogLevel level) {
            fileLogLevel = level;
            return this;
        }

        public SystemLoggerBuilder addConsoleLoggerAndSetLevel(LogLevel level, ConsoleLogger logger) {
            consoleLogger = logger;
            consoleLogLevel = level;
            return this;
        }

        public SystemLoggerBuilder addFileLoggerAndSetLevel(LogLevel level, FileLogger logger) {
            fileLogger = logger;
            fileLogLevel = level;
            return this;
        }

        public SystemLogger build() {
            return new SystemLogger(this);
        }

    }
}
