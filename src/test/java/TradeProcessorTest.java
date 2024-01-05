import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

class TradeProcessorTest {
    private TradeProcessor tradeProcessor;

    @BeforeEach
    void setUp() {
        BufferedReader mockReader = mock(BufferedReader.class);
        tradeProcessor = new TradeProcessor(mockReader);
    }

    @Test
    void processTradesFromAList() {
        List<String[]> tradeDataList = Arrays.asList(
                new String[]{"3", "CODE3", "EUR", "B", "90.0", "15.0", "Portfolio3", "NEW", "Account3", "Strategy3", "User3", "2023-01-05T10:45:00", "2023-01-06"},
                new String[]{"4", "CODE4", "USD", "S", "80.0", "20.0", "Portfolio4", "CANCEL", "Account4", "Strategy4", "User4", "2023-01-07T08:15:00", "2023-01-08"}
        );

        assertDoesNotThrow(() -> tradeProcessor.processTradesFromAList(tradeDataList), "Exception should not be thrown");
    }

    @Test
    void processTradesFromCSV() {
        List<String> csvFiles = Arrays.asList("src/sample_trades (2).csv", "src/sample_trades (2).csv");

        assertDoesNotThrow(() -> tradeProcessor.processTradesFromCSV(csvFiles));
    }

    @Test
    void applyCurrencyConversion() {
        Map<String, Trade> originalTrades = Map.of(
                "1", new Trade("1", "CODE1", "USD", "B", 100.0, 10.0, "Portfolio1", "NEW", "Account1", "Strategy1", "User1", "2023-01-01T12:00:00", "2023-01-02"),
                "2", new Trade("2", "CODE2", "GBP", "S", 120.0, 8.0, "Portfolio2", "CANCEL", "Account2", "Strategy2", "User2", "2023-01-03T14:30:00", "2023-01-04")
        );

        assertDoesNotThrow(() -> tradeProcessor.applyCurrencyConversion(originalTrades), "Exception should not be thrown");
    }
}
