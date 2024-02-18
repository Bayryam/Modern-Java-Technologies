package bg.sofia.uni.fmi.mjt.crypto.server;

import bg.sofia.uni.fmi.mjt.crypto.cache.Cache;
import bg.sofia.uni.fmi.mjt.crypto.cache.CacheHandler;
import bg.sofia.uni.fmi.mjt.crypto.cache.CryptoApiClient;
import bg.sofia.uni.fmi.mjt.crypto.cache.EquitiesCache;
import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.exception.ApiClientException;
import bg.sofia.uni.fmi.mjt.crypto.exception.InternalServerException;
import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;
import bg.sofia.uni.fmi.mjt.crypto.exception.UnableToSaveDataException;
import bg.sofia.uni.fmi.mjt.crypto.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.crypto.server.command.executor.CommandExecutor;
import bg.sofia.uni.fmi.mjt.crypto.server.logging.SystemLogger;
import bg.sofia.uni.fmi.mjt.crypto.server.session.ServerSessionOrganiser;
import bg.sofia.uni.fmi.mjt.crypto.server.session.Session;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private static final String API_KEY_FILE_PATH = "config.txt";
    private static final String USERS_FILE_PATH = "users.json";
    private static final int UPDATE_INTERVAL = 30;
    public static final int SERVER_PORT = 8080;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 2048;

    private final UserRepository userRepository = new UserRepository();
    private final Cache<Equity> equitiesCache = new EquitiesCache();
    private final CacheHandler handler =
        new CacheHandler(equitiesCache, UPDATE_INTERVAL, new CryptoApiClient(getApiKey()));
    private final SystemLogger logger = SystemLogger.setup().build();
    private final CommandExecutor commandExecutor =
        CommandExecutor.configure(userRepository, new ServerSessionOrganiser(), equitiesCache, logger);

    public void start() throws LoggerException, UnableToSaveDataException, ApiClientException, InternalServerException {
        logger.logInfo("Server started");
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            setUpServer();

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            handleClients(selector, buffer);

        } catch (IOException exception) {
            try {
                userRepository.saveRepository(new BufferedWriter(new FileWriter(USERS_FILE_PATH)));
            } catch (UnableToSaveDataException | IOException savingException) {
                logger.logError(
                    "There is a problem with the server socket: " + savingException.getMessage());
                throw new UnableToSaveDataException("Problem while saving the users!", savingException);
            }
            logger.logError(
                "There is a problem with the server socket: " + exception.getMessage());
            throw new InternalServerException("There is a problem with the server socket", exception);
        }
    }

    private void handleClients(Selector selector, ByteBuffer buffer)
        throws IOException, ApiClientException, LoggerException, UnableToSaveDataException {
        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            handleSelectedKeys(selectedKeys, selector, buffer);
        }
    }

    private void handleSelectedKeys(Set<SelectionKey> selectedKeys, Selector selector, ByteBuffer buffer)
        throws IOException, ApiClientException, LoggerException, UnableToSaveDataException {
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (key.isReadable()) {
                handleReadableKey(key, buffer);
            } else if (key.isAcceptable()) {
                handleAcceptableKey(key, selector);
            }
            keyIterator.remove();
            userRepository.saveRepository(new BufferedWriter(new FileWriter("users.json")));
        }
    }

    private void handleReadableKey(SelectionKey key, ByteBuffer buffer)
        throws IOException, ApiClientException, LoggerException {
        handler.updateCache();
        SocketChannel sc = (SocketChannel) key.channel();
        buffer.clear();
        int r = -1;
        try {
            r = sc.read(buffer);
        } catch (SocketException exception) {
            logger.logInfo("Client disconnected: " + sc.getRemoteAddress());
            key.cancel();
            try {
                key.channel().close();
            } catch (IOException ioException) {
                System.out.println("Error closing channel: " + ioException.getMessage());
            }
        }

        if (r < 0) {
            sc.close();
            return;
        }

        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        handleReadKey(key, buffer);
    }

    private void handleAcceptableKey(SelectionKey key, Selector selector) throws IOException, LoggerException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
        logger.logInfo("Accepted connection from client: " + accept.getRemoteAddress());
    }

    private void setUpServer() throws FileNotFoundException, ApiClientException {
        userRepository.setUpRepository(new BufferedReader(new FileReader(USERS_FILE_PATH)));
        handler.updateCache();
        System.out.println("Server is ready to operate!");
    }

    private String getApiKey() {
        try (BufferedReader reader = new BufferedReader(new FileReader(API_KEY_FILE_PATH))) {
            return reader.readLine();
        } catch (IOException exception) {
            throw new RuntimeException("There is a problem with the loading of the api key!", exception);
        }
    }

    private void handleReadKey(SelectionKey key, ByteBuffer buffer) throws IOException, LoggerException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.flip();
        String message = new String(buffer.array(), 0, buffer.limit());
        logger.logInfo("Message received from client " + socketChannel.getRemoteAddress() + " : " + message);
        String[] commands = message.split(System.lineSeparator());
        for (String command : commands) {
            String result;
            try {
                result = commandExecutor.execute(command, new Session(socketChannel, null));
            } catch (LoggerException exception) {
                logger.logError(
                    "There is a problem with the server socket: " + exception.getMessage());
                result = "Internal server error. Please try again later.";
            }

            buffer.clear();
            buffer.put((result + System.lineSeparator()).getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            logger.logInfo(
                "Response sent to client " + socketChannel.getRemoteAddress() + " : " +
                    result);
        }
    }
}