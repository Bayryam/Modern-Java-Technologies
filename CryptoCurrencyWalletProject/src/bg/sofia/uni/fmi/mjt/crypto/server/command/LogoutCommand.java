package bg.sofia.uni.fmi.mjt.crypto.server.command;

import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandBase;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandType;

public class LogoutCommand extends CommandBase {
    @Override
    public String execute() throws LoggerException {
        if (!organiser.isActiveSession(currentSession)) {
            logger.logInfo("Logout attempt for not logged user.");
            return "User not logged in.";
        }
        logger.logInfo("User logged out: " + organiser.getUserFromSession(currentSession).getUsername());
        organiser.removeSession(currentSession);
        return "Logged out successfully.";
    }

    @Override
    public CommandType getType() {
        return CommandType.LOGOUT;
    }
}
