package bg.sofia.uni.fmi.mjt.crypto.server.command.customary;

import bg.sofia.uni.fmi.mjt.crypto.cache.Cache;
import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;
import bg.sofia.uni.fmi.mjt.crypto.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.crypto.server.session.Session;
import bg.sofia.uni.fmi.mjt.crypto.server.session.SessionOrganiser;

import bg.sofia.uni.fmi.mjt.crypto.server.logging.Logger;

public interface Command {
    /**
     * Executes the command.
     *
     * @return the result message of the command execution.
     */
    String execute() throws LoggerException;

    /**
     * Adds a user repository to the command.
     *
     * @param repository The UserRepository to be added.
     * @return the command with the added user repository.
     */
    Command addUserRepository(UserRepository repository);

    /**
     * Adds a session organiser to the command.
     *
     * @param organiser The SessionOrganiser to be added.
     * @return the command with the added session organiser.
     */
    Command addSessionOrganiser(SessionOrganiser organiser);

    /**
     * Adds the current session to the command.
     *
     * @param session The current session to be added.
     * @return the command with the added current session.
     */
    Command addCurrentSession(Session session);

    /**
     * Adds an equities cache to the command.
     *
     * @param cache The equities cache to be added.
     * @return the command with the added equities cache.
     */
    Command addEquitiesCache(Cache<Equity> cache);

    /**
     * Adds a logger to the command.
     *
     * @param logger The logger to be added.
     * @return the command with the added logger.
     */
    Command addLogger(Logger logger);

    /**
     * Retrieves the type of the command.
     *
     * @return the command type of the command.
     */
    CommandType getType();

}
