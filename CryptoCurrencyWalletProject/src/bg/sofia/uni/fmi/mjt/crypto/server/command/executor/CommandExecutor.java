package bg.sofia.uni.fmi.mjt.crypto.server.command.executor;

import bg.sofia.uni.fmi.mjt.crypto.cache.Cache;
import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;
import bg.sofia.uni.fmi.mjt.crypto.server.logging.Logger;
import bg.sofia.uni.fmi.mjt.crypto.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.crypto.server.session.Session;
import bg.sofia.uni.fmi.mjt.crypto.server.session.SessionOrganiser;

public class CommandExecutor {
    private final UserRepository userRepository;
    private final SessionOrganiser organiser;
    private final Cache<Equity> equitiesCache;
    private final Logger logger;

    public static CommandExecutor configure(UserRepository userRepository, SessionOrganiser organiser,
                                            Cache<Equity> equitiesCache, Logger logger) {
        return new CommandExecutor(userRepository, organiser, equitiesCache, logger);
    }

    private CommandExecutor(UserRepository userRepository, SessionOrganiser organiser, Cache<Equity> equitiesCache,
                            Logger logger) {
        this.userRepository = userRepository;
        this.organiser = organiser;
        this.equitiesCache = equitiesCache;
        this.logger = logger;
    }

    public String execute(String command, Session session) throws LoggerException {
        return CommandParser.getCommandFromString(command)
            .addUserRepository(userRepository)
            .addSessionOrganiser(organiser)
            .addEquitiesCache(equitiesCache)
            .addCurrentSession(session)
            .addLogger(logger).execute();
    }
}
