package bg.sofia.uni.fmi.mjt.trading.price;

public class PriceChart implements PriceChartAPI {

    private double microsoftStockPrice;
    private double googleStockPrice;
    private double amazonStockPrice;

    public PriceChart(double microsoftStockPrice, double googleStockPrice, double amazonStockPrice) {
        this.microsoftStockPrice = microsoftStockPrice;
        this.googleStockPrice = googleStockPrice;
        this.amazonStockPrice = amazonStockPrice;
    }

    @Override
    public double getCurrentPrice(String stockTicker) {

        if (stockTicker ==null)
            return 0.0;

        return switch (stockTicker) {
            case "MSFT" -> Double.parseDouble(String.format("%.2f", microsoftStockPrice));
            case "AMZ" -> Double.parseDouble(String.format("%.2f", amazonStockPrice));
            case "GOOG" -> Double.parseDouble(String.format("%.2f", googleStockPrice));
            default -> 0.0;
        };

    }

    @Override
    public boolean changeStockPrice(String stockTicker, int percentChange) {
        if (percentChange <= 0)
            return false;

        if(stockTicker == null)
            return false;

        switch (stockTicker) {
            case "MSFT" -> {
                microsoftStockPrice += microsoftStockPrice * (percentChange / 100.0);
                return true;
            }
            case "AMZ" -> {
                amazonStockPrice += amazonStockPrice * (percentChange / 100.0);
                return true;
            }
            case "GOOG" -> {
                googleStockPrice += googleStockPrice * (percentChange / 100.0);
                return true;
            }
            default -> {return false;}
        }
    }

}