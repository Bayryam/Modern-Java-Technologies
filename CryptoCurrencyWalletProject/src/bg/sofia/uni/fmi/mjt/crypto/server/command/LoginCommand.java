package bg.sofia.uni.fmi.mjt.crypto.server.command;

import bg.sofia.uni.fmi.mjt.crypto.entity.User;
import bg.sofia.uni.fmi.mjt.crypto.exception.CipherException;
import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandBase;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandType;
import bg.sofia.uni.fmi.mjt.crypto.server.session.Session;

public class LoginCommand extends CommandBase {
    private final String username;
    private final String password;

    public LoginCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String execute() throws LoggerException {
        if (organiser.isActiveSession(currentSession)) {
            logger.logInfo("User already logged in: " + username);
            return "User already logged in.";
        }

        try {
            if (!userRepository.isUserRegistered(username, password)) {
                logger.logInfo("Invalid username or password when logging: " + username);
                return "Invalid username or password.";
            }
        } catch (CipherException exception) {
            logger.logInfo(exception.getMessage());
            return "Could not log in. Please try again later.";
        }

        User registeredUser = userRepository.getUsers().stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst().get();

        organiser.registerNewSession(new Session(currentSession.key(), registeredUser));

        logger.logInfo("User logged successfully: " + username);
        return String.format("User %s logged successfully", username);
    }

    @Override
    public CommandType getType() {
        return CommandType.LOGIN;
    }
}
