package bg.sofia.uni.fmi.mjt.crypto.server.session;

import bg.sofia.uni.fmi.mjt.crypto.entity.User;

public interface SessionOrganiser {
    /**
     * Registers a new session.
     *
     * @param session The session to be registered.
     */
    void registerNewSession(Session session);

    /**
     * Checks if a session is active.
     *
     * @param session The session to be checked.
     * @return true if the session is active, false otherwise.
     */
    boolean isActiveSession(Session session);

    /**
     * Retrieves the user associated with a session.
     *
     * @param session The session from which to retrieve the user.
     * @return the User associated with the session.
     */
    User getUserFromSession(Session session);

    /**
     * Removes a session.
     *
     * @param session The session to be removed.
     */
    void removeSession(Session session);
}
