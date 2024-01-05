import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PnLCalculatorTest {
    private PnLCalculator pnlCalculator;
    private TradeAggregator tradeAggregator;

    @BeforeEach
    void setUp() {
        pnlCalculator = new PnLCalculator();
        tradeAggregator = mock(TradeAggregator.class);
        pnlCalculator.setTradeAggregator(tradeAggregator);
    }

    @Test
    void setTradeAggregator() {
        assertNotNull(pnlCalculator);
    }

    @Test
    void calculateCashPosition_withNoTradeAggregator() {
        pnlCalculator.setTradeAggregator(null);

        assertEquals(0, pnlCalculator.calculateCashPosition());
    }

    @Test
    void calculateCashPosition_withTrades() {
        List<Trade> mockTrades = Arrays.asList(
                new Trade("Trade1", "ABC", "USD", "B", 100.0, 10, "Portfolio1", "NEW", "Account1",
                        "Strategy1", "User1", "2023-12-19T10:30:00", "2023-12-20"),
                new Trade("Trade2", "XYZ", "USD", "S", 110.0, 5, "Portfolio2", "NEW", "Account2",
                        "Strategy2", "User2", "2023-12-19T11:30:00", "2023-12-21"),
                new Trade("Trade3", "ABC", "USD", "S", 105.0, 8, "Portfolio3", "NEW", "Account3",
                        "Strategy3", "User3", "2023-12-19T12:30:00", "2023-12-22")
        );

        when(tradeAggregator.aggregateTrades()).thenReturn(mockTrades);

        assertEquals(390.0, pnlCalculator.calculateCashPosition());
    }

    @Test
    void displayCashPosition() {
        List<Trade> mockTrades = Arrays.asList(
                new Trade("Trade1", "ABC", "USD", "B", 100.0, 10, "Portfolio12", "NEW", "Account1",
                        "Strategy1", "User1", "2023-12-19T10:30:00", "2023-12-20"),
                new Trade("Trade2", "XYZ", "USD", "S", 110.0, 5, "Portfolio12", "NEW", "Account2",
                        "Strategy2", "User2", "2023-12-19T11:30:00", "2023-12-21"),
                new Trade("Trade3", "ABC", "USD", "S", 105.0, 8, "Portfolio12", "NEW", "Account3",
                        "Strategy3", "User3", "2023-12-19T12:30:00", "2023-12-22")
        );
        when(tradeAggregator.aggregateTrades()).thenReturn(mockTrades);
        when(tradeAggregator.getAggregationKey()).thenReturn("Portfolio12");
        ByteArrayOutputStream systemOutCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOutCapture));
        pnlCalculator.displayCashPosition();
        System.setOut(System.out);

        String expectedOutput = "Cash Position (PnL) for \"Portfolio12\" = 390.00 USD";
        assertEquals(expectedOutput, systemOutCapture.toString().trim());
    }
}
