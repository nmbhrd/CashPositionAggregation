import java.util.List;
import java.util.Objects;

public class PnLCalculator {
    private TradeAggregator tradeAggregator;

    public void setTradeAggregator(TradeAggregator tradeAggregator) {
        this.tradeAggregator = tradeAggregator;
    }

    private double calculateTradePnL(Trade trade) {
        return trade.price() * trade.volume();
    }

    public synchronized double calculateCashPosition() {
        if (tradeAggregator == null) {
            System.out.println("TradeAggregator not set. Unable to calculate PnL.");
            return 0.0;
        }
        List<Trade> aggregatedTrades = Objects.requireNonNull(tradeAggregator.aggregateTrades());
        double totalCashPosition = 0.0;
        for (Trade trade : aggregatedTrades) {
            double pnl = calculateTradePnL(trade);
            if (trade.side().equalsIgnoreCase("S")) {
                totalCashPosition += pnl;
            } else {
                totalCashPosition -= pnl;
            }
        }
        return totalCashPosition;
    }

    public void displayCashPosition() {
        double cashPosition = calculateCashPosition();
        String aggregationKey = tradeAggregator.getAggregationKey();
        System.out.printf("Cash Position (PnL) for \"%s\" = %,.2f USD%n", aggregationKey, cashPosition);
    }
}
