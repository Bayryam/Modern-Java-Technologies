package bg.sofia.uni.fmi.mjt.trading.stock;

import java.time.LocalDateTime;

public class GoogleStockPurchase implements StockPurchase{

    private final int  quantity;
    private final LocalDateTime purchaseTimestamp;
    private final double purchasePricePerUnit;
    public GoogleStockPurchase(int quantity,
                               LocalDateTime purchaseTimestamp, double purchasePricePerUnit){
        this.quantity = quantity;
        this.purchaseTimestamp = purchaseTimestamp;
        this.purchasePricePerUnit = purchasePricePerUnit;

    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public double getPurchasePricePerUnit() {
        return Double.parseDouble(String.format("%.2f", purchasePricePerUnit));
    }

    @Override
    public LocalDateTime getPurchaseTimestamp() {
        return purchaseTimestamp;
    }

    @Override
    public double getTotalPurchasePrice() {
        return Double.parseDouble(String.format("%.2f", (quantity*purchasePricePerUnit)));
    }

    @Override
    public String getStockTicker() {
        return "GOOG";
    }
}
