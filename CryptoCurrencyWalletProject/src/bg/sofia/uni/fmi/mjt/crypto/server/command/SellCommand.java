package bg.sofia.uni.fmi.mjt.crypto.server.command;

import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.CommandType;
import bg.sofia.uni.fmi.mjt.crypto.server.command.customary.VerifiedCommand;

public class SellCommand extends VerifiedCommand {
    private final String code;

    public SellCommand(String code) {
        this.code = code;
    }

    @Override
    protected String verifiedExecute() throws LoggerException {
        Equity equityToBeSold = equitiesCache.getCachedValues().stream()
            .filter(equity -> equity.equityId().equals(code)).findFirst().orElse(null);
        if (!isEquityCodeValid(equityToBeSold, code)) {
            return String.format("Equity with code %s does not exist!", code);
        }

        if (!user.getEquities().stream().anyMatch(e -> e.equity().equityId().equals(code))) {
            return "You do not own any " + code + "!";
        }
        user.sellEquity(code, equityToBeSold.priceInUSD());
        return "Successfully sold " + code + "!";
    }

    @Override
    public CommandType getType() {
        return CommandType.SELL;
    }
}
