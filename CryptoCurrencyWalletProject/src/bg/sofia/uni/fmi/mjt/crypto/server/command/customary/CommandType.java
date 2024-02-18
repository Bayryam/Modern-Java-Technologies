package bg.sofia.uni.fmi.mjt.crypto.server.command.customary;

public enum CommandType {
    HELP("help"),
    REGISTER("register"),
    LOGIN("login"),
    LOGOUT("logout"),
    DEPOSIT_MONEY("deposit-money"),
    LIST_OFFERINGS("list-offerings"),
    BUY("buy"),
    SELL("sell"),
    SUMMARY("get-wallet-summary"),
    OVERALL_SUMMARY("get-wallet-overall-summary"),
    UNKNOWN("unknown");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
