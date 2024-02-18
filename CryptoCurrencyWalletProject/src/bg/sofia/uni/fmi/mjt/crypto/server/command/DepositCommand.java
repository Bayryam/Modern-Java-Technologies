package bg.sofia.uni.fmi.mjt.crypto.server.command;

import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;
import bg.sofia.uni.fmi.mjt.crypto.exception.MoneyOperationException;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandType;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.VerifiedCommand;

import java.math.BigDecimal;

public class DepositCommand extends VerifiedCommand {
    private final BigDecimal money;

    public DepositCommand(BigDecimal money) {
        this.money = money;
    }

    @Override
    protected String verifiedExecute() throws LoggerException {
        try {
            user.deposit(money);
        } catch (MoneyOperationException exception) {
            logger.logInfo("Error while buying equity: " + exception.getMessage());
            return exception.getMessage();
        }
        return "Successfully deposited: " + money + "$";
    }

    @Override
    public CommandType getType() {
        return CommandType.DEPOSIT_MONEY;
    }
}
