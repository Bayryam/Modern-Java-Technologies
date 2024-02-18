package bg.sofia.uni.fmi.mjt.crypto.server.session;

import bg.sofia.uni.fmi.mjt.crypto.entity.User;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class ServerSessionOrganiser implements SessionOrganiser {
    private final Map<SocketChannel, User> sessions;

    public ServerSessionOrganiser() {
        sessions = new HashMap<>();
    }

    @Override
    public void registerNewSession(Session session) {
        sessions.put(session.key(), session.user());
    }

    @Override
    public boolean isActiveSession(Session session) {
        return sessions.containsKey(session.key());
    }

    @Override
    public User getUserFromSession(Session session) {
        return sessions.get(session.key());
    }

    @Override
    public void removeSession(Session session) {
        sessions.remove(session.key());
    }
}