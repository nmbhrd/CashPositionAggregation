import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TradeAggregatorTest {
    @Test
    void getTradesList_CorrectSize() {
        BufferedReader mockReader = mock(BufferedReader.class);
        TradeAggregator tradeAggregator = new TradeAggregator(mockReader);
        Map<String, Trade> tradesData = Stream.of(
                Trade.createNewTrade("Trade1", "ABC", "USD", "B", 150.5, 1000, "Portfolio1", "NEW", "Account1", "Strategy1", "User1", "2023-12-19T10:30:00", "2023-12-20"),
                Trade.createNewTrade("Trade2", "ABC", "USD", "S", 152.0, 800, "Portfolio2", "NEW", "Account2", "Strategy2", "User2", "2023-12-19T11:30:00", "2023-12-21"),
                Trade.createNewTrade("Trade3", "ABC", "USD", "B", 149.0, 1200, "Portfolio3", "NEW", "Account3", "Strategy3", "User3", "2023-12-19T12:30:00", "2023-12-22")
        ).collect(Collectors.toMap(Trade::tradeId, Function.identity()));
        TradeProcessor tradeProcessorMock = mock(TradeProcessor.class);
        when(tradeProcessorMock.getConvertedTrades()).thenReturn(tradesData);
        tradeAggregator.setTradeProcessor(tradeProcessorMock);

        List<Trade> tradesList = tradeAggregator.getTradesList();

        assertEquals(3, tradesList.size());
    }

    @Test
    void obtainAggregationKey_invalidInput_maxAttemptsReached() throws IOException {
        BufferedReader mockReader = mock(BufferedReader.class);
        when(mockReader.readLine()).thenReturn("", "", "", "", "", "", "");
        TradeProcessor mockTradeProcessor = mock(TradeProcessor.class);
        when(mockTradeProcessor.getConvertedTrades()).thenReturn(Collections.emptyMap());
        TradeAggregator aggregatorWithMockedReader = new TradeAggregator(mockReader);
        aggregatorWithMockedReader.setTradeProcessor(mockTradeProcessor);

        String aggregationKey = aggregatorWithMockedReader.obtainAggregationKey();

        assertEquals("Maximum attempts reached. Exiting the program.", aggregationKey);
    }
}
