package bg.sofia.uni.fmi.mjt.crypto.server.session;

import bg.sofia.uni.fmi.mjt.crypto.entity.User;

import java.nio.channels.SocketChannel;

public record Session(SocketChannel key, User user) {
}
