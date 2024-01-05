import java.util.Objects;

public record Trade(
        String tradeId,
        String bbgCode,
        String currencyISOCode,
        String side,
        double price,
        double volume,
        String portfolio,
        String action,
        String account,
        String strategy,
        String user,
        String tradeTimeUTC,
        String valueDate
) {
    @Override
    public String toString() {
        return "Trade{" +
                "tradeId='" + tradeId + '\'' +
                ", bbgCode='" + bbgCode + '\'' +
                ", currency='" + currencyISOCode + '\'' +
                ", side='" + side + '\'' +
                ", price=" + price +
                ", volume=" + volume +
                ", portfolio='" + portfolio + '\'' +
                ", action='" + action + '\'' +
                ", account='" + account + '\'' +
                ", strategy='" + strategy + '\'' +
                ", user='" + user + '\'' +
                ", tradeTimeUTC='" + tradeTimeUTC + '\'' +
                ", valueDate='" + valueDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return Double.compare(trade.price, price) == 0 &&
                Double.compare(trade.volume, volume) == 0 &&
                tradeId.equals(trade.tradeId) &&
                bbgCode.equals(trade.bbgCode) &&
                currencyISOCode.equals(trade.currencyISOCode) &&
                side.equals(trade.side) &&
                portfolio.equals(trade.portfolio) &&
                action.equals(trade.action) &&
                account.equals(trade.account) &&
                strategy.equals(trade.strategy) &&
                user.equals(trade.user) &&
                tradeTimeUTC.equals(trade.tradeTimeUTC) &&
                valueDate.equals(trade.valueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                tradeId, bbgCode, currencyISOCode, side, price, volume,
                portfolio, action, account, strategy, user, tradeTimeUTC, valueDate
        );
    }

    public static Trade createNewTrade(String tradeId, String bbgCode, String currencyISOCode, String side,
                                       double price, double volume, String portfolio, String action, String account,
                                       String strategy, String user, String tradeTimeUTC, String valueDate) {

        return new Trade(tradeId, bbgCode, currencyISOCode, side, price, volume, portfolio, action, account, strategy, user, tradeTimeUTC, valueDate);
    }
}
