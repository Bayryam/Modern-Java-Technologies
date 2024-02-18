package bg.sofia.uni.fmi.mjt.crypto.server.command.executor;

import bg.sofia.uni.fmi.mjt.crypto.server.command.BuyCommand;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.Command;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandType;
import bg.sofia.uni.fmi.mjt.crypto.server.command.DepositCommand;
import bg.sofia.uni.fmi.mjt.crypto.server.command.HelpCommand;
import bg.sofia.uni.fmi.mjt.crypto.server.command.ListCommand;
import bg.sofia.uni.fmi.mjt.crypto.server.command.LoginCommand;
import bg.sofia.uni.fmi.mjt.crypto.server.command.LogoutCommand;
import bg.sofia.uni.fmi.mjt.crypto.server.command.OverallSummaryCommand;
import bg.sofia.uni.fmi.mjt.crypto.server.command.RegisterCommand;
import bg.sofia.uni.fmi.mjt.crypto.server.command.SellCommand;
import bg.sofia.uni.fmi.mjt.crypto.server.command.SummaryCommand;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.UnsupportedCommand;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class CommandParser {
    private static final int ZERO_ARGUMENTS = 0;
    private static final int THREE_ARGUMENTS = 3;
    private static final int FOUR_ARGUMENTS = 4;
    private static final int MONEY_PREFIX_LENGTH = 8;
    private static final int OFFERING_PREFIX_LENGTH = 11;

    public static Command getCommandFromString(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Provided line argument is null!");
        }

        List<String> tokens = Arrays.stream(line.split(" ")).filter(x -> !x.isBlank()).toList();

        if (tokens.isEmpty()) {
            return new UnsupportedCommand();
        }

        String keyword = tokens.getFirst();
        tokens = tokens.subList(1, tokens.size());
        CommandType currentCommandType = identifyCommand(keyword);
        return switch (currentCommandType) {
            case HELP -> help(tokens);
            case REGISTER -> register(tokens);
            case LOGIN -> login(tokens);
            case LOGOUT -> logout(tokens);
            case DEPOSIT_MONEY -> depositMoney(tokens);
            case LIST_OFFERINGS -> listOfferings(tokens);
            case BUY -> buy(tokens);
            case SELL -> sell(tokens);
            case SUMMARY -> summary(tokens);
            case OVERALL_SUMMARY -> overallSummary(tokens);
            case UNKNOWN -> new UnsupportedCommand();
        };

    }

    private static CommandType identifyCommand(String keyword) {
        return Arrays.stream(CommandType.values()).dropWhile(type -> !type.getKeyword().equals(keyword)).findFirst()
            .orElse(CommandType.UNKNOWN);
    }

    private static Command help(List<String> commandArguments) {
        int argumentsCount = commandArguments.size();
        if (argumentsCount != ZERO_ARGUMENTS) {
            return new UnsupportedCommand(
                String.format("You should not provide any extra data. You provided %d arguments!",
                    argumentsCount));
        }

        return new HelpCommand();
    }

    private static Command register(List<String> commandArguments) {
        int argumentsCount = commandArguments.size();
        if (argumentsCount != FOUR_ARGUMENTS) {
            return new UnsupportedCommand(
                String.format(
                    "You should only provide username, location, phone number and password. You provided %d arguments!",
                    argumentsCount));
        }

        return new RegisterCommand(commandArguments.getFirst(), commandArguments.get(1), commandArguments.get(2),
            commandArguments.get(THREE_ARGUMENTS));
    }

    private static Command login(List<String> commandArguments) {
        int argumentsCount = commandArguments.size();
        if (argumentsCount != 2) {
            return new UnsupportedCommand(
                String.format("You should only provide username and password. You provided %d arguments!",
                    argumentsCount));
        }

        return new LoginCommand(commandArguments.getFirst(), commandArguments.get(1));
    }

    private static Command logout(List<String> commandArguments) {
        int argumentsCount = commandArguments.size();
        if (argumentsCount != 0) {
            return new UnsupportedCommand(
                String.format("You should not provide any extra data. You provided %d arguments!",
                    argumentsCount));
        }

        return new LogoutCommand();
    }

    private static Command depositMoney(List<String> commandArguments) {
        int argumentsCount = commandArguments.size();
        if (argumentsCount != 1) {
            return new UnsupportedCommand(
                String.format("You should only provide amount of money to be deposited. You provided %d arguments!",
                    argumentsCount));
        }

        try {
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(commandArguments.getFirst()));
            return new DepositCommand(amount);
        } catch (NumberFormatException exception) {
            return new UnsupportedCommand("You should provide valid deposit amount!");
        }
    }

    private static Command listOfferings(List<String> commandArguments) {
        int argumentsCount = commandArguments.size();
        if (argumentsCount != ZERO_ARGUMENTS) {
            return new UnsupportedCommand(
                String.format("You should not provide any extra data. You provided %d arguments!",
                    argumentsCount));
        }

        return new ListCommand();
    }

    private static Command buy(List<String> commandArguments) {
        int argumentsCount = commandArguments.size();
        if (argumentsCount != 2) {
            return new UnsupportedCommand(
                String.format("You should not provide any extra data. You provided %d arguments!",
                    argumentsCount));
        }
        String offeringParameter = commandArguments.getFirst();
        String amountParameter = commandArguments.get(1);
        if (!offeringParameter.startsWith("--offering=") || !amountParameter.startsWith("--money=")
            || offeringParameter.length() == OFFERING_PREFIX_LENGTH) {
            return new UnsupportedCommand(
                "You should provide offering code and amount to be bought. You provided wrong arguments!");
        }

        String offeringCode = offeringParameter.substring(OFFERING_PREFIX_LENGTH);
        String amount = amountParameter.substring(MONEY_PREFIX_LENGTH);

        try {
            BigDecimal amountToBeBought = BigDecimal.valueOf(Double.parseDouble(amount));
            return new BuyCommand(offeringCode, amountToBeBought);
        } catch (NumberFormatException exception) {
            return new UnsupportedCommand("You should provide valid amount to be bought!");
        }
    }

    private static Command sell(List<String> commandArguments) {
        int argumentsCount = commandArguments.size();
        if (argumentsCount != 1) {
            return new UnsupportedCommand(
                String.format("You should not provide any extra data. You provided %d arguments!",
                    argumentsCount));
        }

        String offeringParameter = commandArguments.getFirst();

        if (!offeringParameter.startsWith("--offering=") || offeringParameter.length() == OFFERING_PREFIX_LENGTH) {
            return new UnsupportedCommand("You should not provide any extra data. You provided false arguments!");
        }

        return new SellCommand(offeringParameter.substring(OFFERING_PREFIX_LENGTH));
    }

    private static Command summary(List<String> commandArguments) {
        int argumentsCount = commandArguments.size();
        if (argumentsCount != ZERO_ARGUMENTS) {
            return new UnsupportedCommand(
                String.format("You should not provide any extra data. You provided %d arguments!",
                    argumentsCount));
        }

        return new SummaryCommand();
    }

    private static Command overallSummary(List<String> commandArguments) {
        int argumentsCount = commandArguments.size();
        if (argumentsCount != ZERO_ARGUMENTS) {
            return new UnsupportedCommand(
                String.format("You should not provide any extra data. You provided %d arguments!",
                    argumentsCount));
        }

        return new OverallSummaryCommand();
    }
}
