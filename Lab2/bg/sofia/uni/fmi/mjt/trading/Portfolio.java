package bg.sofia.uni.fmi.mjt.trading;

import bg.sofia.uni.fmi.mjt.trading.price.PriceChartAPI;
import bg.sofia.uni.fmi.mjt.trading.stock.AmazonStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.GoogleStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.MicrosoftStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.StockPurchase;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Portfolio implements PortfolioAPI
{
    private final String owner;
    private final PriceChartAPI priceChart;
    private StockPurchase[] stockPurchases;
    private int stockPurchasesCount;
    private double budget;
    private final int maxSize;
    public Portfolio(String owner, PriceChartAPI priceChart, double budget, int maxSize){
        this.owner = owner;
        this.priceChart = priceChart;
        this.budget = budget;
        this.maxSize = maxSize;
        this.stockPurchases = new StockPurchase[maxSize];
        this.stockPurchasesCount = 0;
    }

    public Portfolio(String owner, PriceChartAPI priceChart, StockPurchase[] stockPurchases,
              double budget, int maxSize){
        this.owner = owner;
        this.priceChart = priceChart;
        this.stockPurchases = Arrays.copyOf(stockPurchases,maxSize);
        stockPurchasesCount = stockPurchases.length;
        this.budget = budget;
        this.maxSize = maxSize;
    }

    @Override
    public StockPurchase buyStock(String stockTicker, int quantity) {
        if((stockTicker == null) || (quantity <=0) || (stockPurchasesCount == maxSize)) {
            return null;
        }

        double expenses = 0;
        StockPurchase sp;

            switch (stockTicker) {
                case "MSFT" -> {
                    expenses = quantity * priceChart.getCurrentPrice("MSFT");
                    if (expenses > budget){
                        return null;
                    }
                    sp = new MicrosoftStockPurchase(quantity, LocalDateTime.now()
                            , priceChart.getCurrentPrice("MSFT"));
                    priceChart.changeStockPrice("MSFT",5);
                }
                case "AMZ" -> {
                    expenses = quantity * priceChart.getCurrentPrice("AMZ");
                    if (expenses > budget){
                        return null;
                    }
                    sp = new AmazonStockPurchase(quantity, LocalDateTime.now()
                            , priceChart.getCurrentPrice("AMZ"));
                    priceChart.changeStockPrice("AMZ",5);
                }
                case "GOOG" -> {
                    expenses = quantity * priceChart.getCurrentPrice("GOOG");
                    if (expenses > budget){
                        return null;
                    }
                    sp = new GoogleStockPurchase(quantity, LocalDateTime.now()
                            , priceChart.getCurrentPrice("GOOG"));
                    priceChart.changeStockPrice("GOOG",5);
                }
                default -> {return null;}
            };
            budget-=expenses;
            stockPurchases[stockPurchasesCount++] = sp;
            return sp;
    }

    @Override
    public StockPurchase[] getAllPurchases() {
        return stockPurchases;
    }

    @Override
    public StockPurchase[] getAllPurchases(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        StockPurchase[] wantedPurchases = new StockPurchase[stockPurchases.length];
        int index = 0;
        for(StockPurchase stockPurchase :stockPurchases){

            if(stockPurchase == null) {
                continue;
            }
            LocalDateTime purchaseMade = stockPurchase.getPurchaseTimestamp();

            if((purchaseMade.isAfter(startTimestamp) && purchaseMade.isBefore(endTimestamp))
            ||purchaseMade.equals(startTimestamp) || purchaseMade.equals(endTimestamp)){
                wantedPurchases[index++] = stockPurchase;
            }

        }
        wantedPurchases = Arrays.stream(wantedPurchases).filter(p -> p != null)
                .toArray(StockPurchase[]::new);
        return wantedPurchases;
    }

    @Override
    public double getNetWorth() {
        double netWorth = 0;
        for (StockPurchase s:stockPurchases){
            if(s == null){
                continue;
            }
            netWorth+=priceChart.getCurrentPrice(s.getStockTicker()) * s.getQuantity();
        }
        return netWorth;
    }

    @Override
    public double getRemainingBudget() {
        return Double.parseDouble(String.format("%.2f",budget));
    }

    @Override
    public String getOwner() {
        return owner;
    }
}
