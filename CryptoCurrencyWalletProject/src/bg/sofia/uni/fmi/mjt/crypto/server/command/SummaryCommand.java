package bg.sofia.uni.fmi.mjt.crypto.server.command;

import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandType;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.VerifiedCommand;

public class SummaryCommand extends VerifiedCommand {
    @Override
    protected String verifiedExecute() {
        return user.getInformationForWallet();
    }

    @Override
    public CommandType getType() {
        return CommandType.SUMMARY;
    }
}
