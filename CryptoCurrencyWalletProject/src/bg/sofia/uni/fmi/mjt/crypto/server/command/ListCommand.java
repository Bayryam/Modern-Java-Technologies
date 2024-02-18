package bg.sofia.uni.fmi.mjt.crypto.server.command;

import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandType;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.VerifiedCommand;

import java.util.stream.Collectors;

public class ListCommand extends VerifiedCommand {
    @Override
    protected String verifiedExecute() {
        return equitiesCache.getCachedValues().stream()
            .map(Object::toString)
            .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public CommandType getType() {
        return CommandType.LIST_OFFERINGS;
    }
}
