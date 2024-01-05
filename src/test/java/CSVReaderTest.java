import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CSVReaderTest {
    private CSVReader csvReader;

    @BeforeEach
    void setUp() {
        Path csvFilePath = Paths.get("src/sample_trades (2).csv");
        csvReader = new CSVReader(csvFilePath);
    }
    @Test
    void constructorShouldThrowExceptionForNullFilePath() {
        assertThrows(IllegalArgumentException.class, () -> new CSVReader(null));
    }
    @Test
    void readTrades() {
        try {
            List<String[]> trades = csvReader.readTrades();
            assertNotNull(trades);
            assertFalse(trades.isEmpty());
            assertEquals(100027, trades.size());
            assertArrayEquals(new String[]{"TradeID","BBGCode","Currency","Side","Price","Volume","Portfolio","Action","Account","Strategy","User","TradeTimeUTC","ValueDate"}, trades.get(0));

        } catch (IOException e) {
            fail("IOException should not be thrown during a successful readTrades operation");
        }
    }

    @Test
    void readTradesWithIOException() {
        Path nonExistentFilePath = Paths.get("nonexistentfile.csv");
        CSVReader readerWithIOException = new CSVReader(nonExistentFilePath);

        assertThrows(IOException.class, readerWithIOException::readTrades);
    }
}
