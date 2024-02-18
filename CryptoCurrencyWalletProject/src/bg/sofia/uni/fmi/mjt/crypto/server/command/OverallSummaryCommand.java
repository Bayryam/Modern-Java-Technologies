package bg.sofia.uni.fmi.mjt.crypto.server.command;

import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandType;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.VerifiedCommand;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class OverallSummaryCommand extends VerifiedCommand {
    private static final int SCALE = 3;
    private BigDecimal findProfitLoss() {
        BigDecimal profitLoss = user.getProfitLoss();
        for (var estimate : user.getEquities()) {
            BigDecimal amount = estimate.amount();
            BigDecimal newPrice = BigDecimal.ZERO;
            Optional<Equity> equityOptional = equitiesCache.getCachedValues().stream()
                .filter(equity -> equity.equityId().equals(estimate.equity().equityId()))
                .findFirst();
            if (equityOptional.isPresent()) {
                newPrice = equityOptional.get().priceInUSD();
            }
            BigDecimal currentProfitLoss = newPrice.multiply(amount);
            profitLoss = profitLoss.add(currentProfitLoss);
        }
        return profitLoss.setScale(SCALE, RoundingMode.HALF_UP);
    }

    @Override
    protected String verifiedExecute() throws LoggerException {
        logger.logInfo("Overall summary command executed");
        return "Profit/Loss: " + findProfitLoss() + '$';
    }

    @Override
    public CommandType getType() {
        return CommandType.OVERALL_SUMMARY;
    }
}
