package bg.sofia.uni.fmi.mjt.crypto.server.command.customary;

public class UnsupportedCommand extends CommandBase {
    private static final String UNSUPPORTED_COMMAND_MESSAGE =
        "Unknown command specified. Use command 'help' for more info.";
    private final String commandMessage;

    public UnsupportedCommand() {
        this(UNSUPPORTED_COMMAND_MESSAGE);
    }

    public UnsupportedCommand(String message) {
        this.commandMessage = message;
    }

    @Override
    public String execute() {
        return commandMessage;
    }

    @Override
    public CommandType getType() {
        return CommandType.UNKNOWN;
    }
}
