package bg.sofia.uni.fmi.mjt.crypto.server.command;

import bg.sofia.uni.fmi.mjt.crypto.exception.CipherException;
import bg.sofia.uni.fmi.mjt.crypto.exception.DuplicateUsernameException;
import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandBase;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandType;
import bg.sofia.uni.fmi.mjt.crypto.validator.PasswordValidator;

public class RegisterCommand extends CommandBase {
    private final String username;
    private final String location;
    private final String phoneNumber;
    private final String password;

    public RegisterCommand(String username, String location, String phoneNumber, String password) {
        this.username = username;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    @Override
    public String execute() throws LoggerException {
        if (organiser.isActiveSession(currentSession)) {
            logger.logInfo("User tried to register when logged: " + username);
            return "Already logged in.";
        }

        if (!PasswordValidator.isValidPassword(password)) {
            logger.logInfo("User tried to register with invalid password: " + username);
            return "The provided password do not follow the restrictions! " +
                PasswordValidator.getPasswordRestrictions();
        }

        try {
            userRepository.addUser(username, location, phoneNumber, password);
        } catch (DuplicateUsernameException e) {
            logger.logError("User with username " + username + " already exists.");
            return "Unable to register user. Username already exists.";
        } catch (CipherException e) {
            logger.logError("There was a problem with the cipher when registering user: " + username);
            return "Unable to register user.";
        }

        logger.logInfo("User registered: " + username);
        return "User registered successfully.";
    }

    @Override
    public CommandType getType() {
        return CommandType.REGISTER;
    }
}
