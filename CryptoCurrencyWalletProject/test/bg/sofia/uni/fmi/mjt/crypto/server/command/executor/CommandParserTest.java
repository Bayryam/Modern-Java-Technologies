package bg.sofia.uni.fmi.mjt.crypto.server.command.executor;

import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.Command;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandParserTest {
    @Test
    void testParseNullInput() {
        assertThrows(IllegalArgumentException.class, () -> CommandParser.getCommandFromString(null),
            "Null input should throw IllegalArgumentException!");
    }

    @Test
    void testParseEmptyInput() {
        Command cmd = CommandParser.getCommandFromString(" ");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Empty string should get Unknown command!");
    }

    @Test
    void testParseInvalidCommand() {
        Command cmd = CommandParser.getCommandFromString("invalid command");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Invalid command should get Unknown command!");
    }

    @Test
    void testParseHelpCommand() {
        Command cmd = CommandParser.getCommandFromString("help");
        assertEquals(CommandType.HELP, cmd.getType(), "Should map help command to Help cmd.");
    }

    @Test
    void testParseHlpCommandWithArgumentsGetUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("help money-8");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Help command with arguments should get Unknown command!");
    }

    @Test
    void testParseValidLoginCommand() {
        Command cmd = CommandParser.getCommandFromString("login success success");
        assertEquals(CommandType.LOGIN, cmd.getType(), "Login with exactly 2 paramb s should get Login command.");
    }

    @Test
    void testParseLoginCommandWithMoreArgsGetUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("login lots of arguments");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Login with more than 2 params should get Unknown command.");
    }

    @Test
    void testParseValidRegisterCommand() {
        Command cmd = CommandParser.getCommandFromString("register user USA 09933 1234");
        assertEquals(CommandType.REGISTER, cmd.getType(),
            "Register with exactly 4 params should get Register command.");
    }

    @Test
    void testParseRegisterCommandWithLessArguments() {
        Command cmd = CommandParser.getCommandFromString("register less args");
        assertEquals(CommandType.UNKNOWN, cmd.getType(),
            "Register with less than 4 params should get Unknown command.");
    }

    @Test
    void testParseValidLogoutCommand() {
        Command cmd = CommandParser.getCommandFromString("logout");
        assertEquals(CommandType.LOGOUT, cmd.getType(), "No arguments and correct keyword should get Logout command.");
    }

    @Test
    void testParseLogoutCommandWithArgsReturnUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("logout with params");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Logout with arguments should get Unknown command.");
    }

    @Test
    void testParseValidDepositMoneyCommand() {
        Command cmd = CommandParser.getCommandFromString("deposit-money 100");
        assertEquals(CommandType.DEPOSIT_MONEY, cmd.getType(),
            "Deposit-money with exactly 1 param should get DepositMoney command.");
    }

    @Test
    void testParseDepositMoneyCommandWithMoreArgsReturnUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("deposit-money 100 200");
        assertEquals(CommandType.UNKNOWN, cmd.getType(),
            "Deposit-money with more than 1 param should get Unknown command.");
    }

    @Test
    void testParseDepositMoneyCommandWithInvalidAmountReturnUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("deposit-money invalid");
        assertEquals(CommandType.UNKNOWN, cmd.getType(),
            "Deposit-money with invalid amount should get Unknown command.");
    }

    @Test
    void testParseValidListOfferingsCommand() {
        Command cmd = CommandParser.getCommandFromString("list-offerings");
        assertEquals(CommandType.LIST_OFFERINGS, cmd.getType(),
            "List-offerings with no arguments should get ListOfferings command.");
    }

    @Test
    void testParseListOfferingsCommandWithArgsReturnUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("list-offerings with params");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "List-offerings with arguments should get Unknown command.");
    }

    @Test
    void testParseValidBuyCommand() {
        Command cmd = CommandParser.getCommandFromString("buy --offering=BTC --money=100");
        assertEquals(CommandType.BUY, cmd.getType(), "Buy with exactly 2 params should get Buy command.");
    }

    @Test
    void testParseBuyCommandWithMoreArgsReturnUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("buy --offering=BTC --money=100 --extra=param");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Buy with more than 2 params should get Unknown command.");
    }

    @Test
    void testParseBuyCommandWithInvalidPrefixesReturnUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("buy --332=BTC --23=100");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Buy with invalid prefixes should get Unknown command.");
    }

    @Test
    void testParseBuyCommandWithInvalidMoneyReturnUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("buy --offering=BTC --money=sdf");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Buy with invalid money should get Unknown command.");
    }

    @Test
    void testParseValidSellCommand() {
        Command cmd = CommandParser.getCommandFromString("sell --offering=BTC");
        assertEquals(CommandType.SELL, cmd.getType(), "Sell with exactly 1 params should get Sell command.");
    }

    @Test
    void testParseSellCommandWithMoreArgsReturnUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("sell --offering=BTC --amount=100 --extra=param");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Sell with more than 2 params should get Unknown command.");
    }

    @Test
    void testParseSellCommandWithWrongPrefixesReturnUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("sell --offedwqing=BTC");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Wrong prefixes should get Unknown command.");
    }

    @Test
    void testParseValidSummaryCommand() {
        Command cmd = CommandParser.getCommandFromString("get-wallet-summary");
        assertEquals(CommandType.SUMMARY, cmd.getType(), "Summary with no arguments should get Summary command.");
    }

    @Test
    void testParseSummaryCommandWithArgsReturnUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("get-wallet-summary summary with params");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Summary with arguments should get Unknown command.");
    }

    @Test
    void testParseValidOverallSummaryCommand() {
        Command cmd = CommandParser.getCommandFromString("get-wallet-overall-summary");
        assertEquals(CommandType.OVERALL_SUMMARY, cmd.getType(),
            "Overall-summary with no arguments should get OverallSummary command.");
    }

    @Test
    void testParseOverallSummaryCommandWithArgsReturnUnknownCommand() {
        Command cmd = CommandParser.getCommandFromString("get-wallet-overall-summary with params");
        assertEquals(CommandType.UNKNOWN, cmd.getType(), "Overall-summary with arguments should get Unknown command.");
    }
}
