package bg.sofia.uni.fmi.mjt.crypto.server.command.customary;

import bg.sofia.uni.fmi.mjt.crypto.cache.Cache;
import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.repository.Repository;
import bg.sofia.uni.fmi.mjt.crypto.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.crypto.server.session.Session;
import bg.sofia.uni.fmi.mjt.crypto.server.session.SessionOrganiser;

import bg.sofia.uni.fmi.mjt.crypto.server.logging.Logger;

public abstract class CommandBase implements Command {
    protected Repository userRepository;
    protected SessionOrganiser organiser;
    protected Session currentSession;
    protected Cache<Equity> equitiesCache;
    protected Logger logger;

    @Override
    public final Command addUserRepository(UserRepository repository) {
        this.userRepository = repository;
        return this;
    }

    @Override
    public final Command addSessionOrganiser(SessionOrganiser organiser) {
        this.organiser = organiser;
        return this;
    }

    @Override
    public final Command addCurrentSession(Session session) {
        this.currentSession = session;
        return this;
    }

    @Override
    public final Command addEquitiesCache(Cache<Equity> cache) {
        this.equitiesCache = cache;
        return this;
    }

    @Override
    public Command addLogger(Logger logger) {
        this.logger = logger;
        return this;
    }
}