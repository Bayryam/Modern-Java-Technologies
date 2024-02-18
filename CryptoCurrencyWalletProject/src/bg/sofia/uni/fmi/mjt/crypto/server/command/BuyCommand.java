package bg.sofia.uni.fmi.mjt.crypto.server.command;

import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;
import bg.sofia.uni.fmi.mjt.crypto.exception.MoneyOperationException;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandType;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.VerifiedCommand;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BuyCommand extends VerifiedCommand {
    private static final int ROUNDING_SCALE = 10;
    private final String code;
    private final BigDecimal money;

    public BuyCommand(String code, BigDecimal money) {
        this.code = code;
        this.money = money;
    }

    @Override
    protected String verifiedExecute() throws LoggerException {
        Equity equityToBeBought = equitiesCache.getCachedValues().stream()
            .filter(equity -> equity.equityId().equals(code)).findFirst().orElse(null);
        if (!isEquityCodeValid(equityToBeBought, code)) {
            return String.format("Equity with code %s does not exist!", code);
        }

        try {
            BigDecimal currentPrice = equityToBeBought.priceInUSD();
            BigDecimal amountBought = money.divide(currentPrice, ROUNDING_SCALE, RoundingMode.HALF_UP);
            user.buyEquity(equityToBeBought, amountBought, money);
            return "Successfully bought " + amountBought + " of " + equityToBeBought.equityName();
        } catch (InsufficientBalanceException | MoneyOperationException exception) {
            logger.logInfo("Error while buying equity: " + exception.getMessage());
            return exception.getMessage();
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.BUY;
    }
}
