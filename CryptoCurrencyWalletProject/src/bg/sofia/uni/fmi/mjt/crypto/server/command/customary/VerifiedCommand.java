package bg.sofia.uni.fmi.mjt.crypto.server.command.customary;

import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.entity.User;
import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;

public abstract class VerifiedCommand extends CommandBase {
    protected User user;

    protected abstract String verifiedExecute() throws LoggerException;

    @Override
    public final String execute() throws LoggerException {
        if (!organiser.isActiveSession(currentSession)) {
            logger.logInfo("Unauthorised access attempt detected. User is not logged in");
            return "You are not logged into your account. So you are unable to perform commands!";
        }
        user = organiser.getUserFromSession(currentSession);
        return verifiedExecute();
    }

    protected boolean isEquityCodeValid(Equity equityToBeChecked, String code) throws LoggerException {
        if (equityToBeChecked == null) {
            String message = String.format("User %s tried to buy equity with code %s, but it does not exist!",
                user.getUsername(), code);
            logger.logInfo(message);
            return false;
        }
        return true;
    }
}
