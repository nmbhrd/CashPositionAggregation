import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TradeTest {
    private Trade trade1;
    private Trade trade2;

    @BeforeEach
    void setUp() {
        trade1 = new Trade("ID1", "BBG1", "USD", "Buy", 100.0, 10.0, "Portfolio1", "Add", "Account1",
                "Strategy1", "User1", "2023-01-01T12:00:00", "2023-01-02");
        trade2 = new Trade("ID1", "BBG1", "USD", "Buy", 100.0, 10.0, "Portfolio1", "Add", "Account1",
                "Strategy1", "User1", "2023-01-01T12:00:00", "2023-01-02");
    }

    @Test
    void testEqualsAndHashCode() {
        assertEquals(trade1, trade2, "Trades with the same attributes should be equal");
        assertEquals(trade1.hashCode(), trade2.hashCode(), "Hash codes of equal trades should be the same");
    }

    @Test
    void testNotEquals() {
        Trade differentTrade = new Trade("ID2", "BBG2", "EUR", "Sell", 120.0, 8.0, "Portfolio2", "Remove", "Account2",
                "Strategy2", "User2", "2023-01-03T14:30:00", "2023-01-04");

        assertNotEquals(trade1, differentTrade, "Trades with different attributes should not be equal");
    }

    @Test
    void testToString() {
        String expectedString = "Trade{tradeId='ID1', bbgCode='BBG1', currency='USD', side='Buy', " +
                "price=100.0, volume=10.0, portfolio='Portfolio1', action='Add', account='Account1', " +
                "strategy='Strategy1', user='User1', tradeTimeUTC='2023-01-01T12:00:00', valueDate='2023-01-02'}";

        assertEquals(expectedString, trade1.toString(), "toString should return a readable representation of the trade");
    }

    @Test
    void testCreateNewTrade() {
        Trade newTrade = Trade.createNewTrade("ID3", "BBG3", "GBP", "Sell", 90.0, 15.0, "Portfolio3", "Update",
                "Account3", "Strategy3", "User3", "2023-01-05T10:45:00", "2023-01-06");

        assertEquals("ID3", newTrade.tradeId(), "New trade should have the specified trade ID");
        assertEquals("BBG3", newTrade.bbgCode(), "New trade should have the specified BBG code");
        assertEquals("GBP", newTrade.currencyISOCode(), "New trade should have the specified currency ISO code");
        assertEquals("Sell", newTrade.side(), "New trade should have the specified side");
        assertEquals(90.0, newTrade.price(), 0.0001, "New trade should have the specified price");
        assertEquals(15.0, newTrade.volume(), 0.0001, "New trade should have the specified volume");
        assertEquals("Portfolio3", newTrade.portfolio(), "New trade should have the specified portfolio");
        assertEquals("Update", newTrade.action(), "New trade should have the specified action");
        assertEquals("Account3", newTrade.account(), "New trade should have the specified account");
        assertEquals("Strategy3", newTrade.strategy(), "New trade should have the specified strategy");
        assertEquals("User3", newTrade.user(), "New trade should have the specified user");
        assertEquals("2023-01-05T10:45:00", newTrade.tradeTimeUTC(), "New trade should have the specified trade time UTC");
        assertEquals("2023-01-06", newTrade.valueDate(), "New trade should have the specified value date");
    }

    @Test
    void testIndividualProperties() {
        assertEquals("ID1", trade1.tradeId(), "Trade ID should match");
        assertEquals("BBG1", trade1.bbgCode(), "BBG code should match");
        assertEquals("USD", trade1.currencyISOCode(), "Currency ISO code should match");
        assertEquals("Buy", trade1.side(), "Side should match");
        assertEquals(100.0, trade1.price(), 0.0001, "Price should match");
        assertEquals(10.0, trade1.volume(), 0.0001, "Volume should match");
        assertEquals("Portfolio1", trade1.portfolio(), "Portfolio should match");
        assertEquals("Add", trade1.action(), "Action should match");
        assertEquals("Account1", trade1.account(), "Account should match");
        assertEquals("Strategy1", trade1.strategy(), "Strategy should match");
        assertEquals("User1", trade1.user(), "User should match");
        assertEquals("2023-01-01T12:00:00", trade1.tradeTimeUTC(), "Trade time UTC should match");
        assertEquals("2023-01-02", trade1.valueDate(), "Value date should match");
    }
}
