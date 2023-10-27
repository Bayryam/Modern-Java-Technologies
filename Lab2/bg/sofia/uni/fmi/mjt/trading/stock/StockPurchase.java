package bg.sofia.uni.fmi.mjt.trading.stock;


import java.time.LocalDateTime;

public interface StockPurchase {

    int getQuantity();
    double getPurchasePricePerUnit();

    LocalDateTime getPurchaseTimestamp();

    double getTotalPurchasePrice();

    String getStockTicker();
}
