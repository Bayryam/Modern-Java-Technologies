package bg.sofia.uni.fmi.mjt.crypto.server.command;

import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandBase;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandType;

public class HelpCommand extends CommandBase {
    private static final String HELP_COMMAND_MESSAGE = """
        You can use the following commands:
        <--> register {username} {password} -> register new user
        <--> login {username} {password} -> login existing user
        <--> logout -> logout current user
        <--> buy --offering={offering_code} --money={amount} -> buy offering with code(offering_code) for money(amount)
        <--> sell --offering=<offering_code> -> sell offering with code(offering_code)
        <--> deposit-money {amount} -> deposit money to your account
        <--> list-offerings - list all offerings
        <--> get-wallet-summary - give information about the active offerings and the money in the wallet
        <--> get-wallet-overall-summary - give information about the all time profit/loss of the user""";
    @Override
    public String execute() {
        return HELP_COMMAND_MESSAGE;
    }

    @Override
    public CommandType getType() {
        return CommandType.HELP;
    }
}
