import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateReportParserTest {
    private static final String VALID_XML_FILE_PATH = "src/main/resources/Exchange_Rate_Report.xml";
    private static final String INVALID_XML_FILE_PATH = "src/nonexistent_file.xml";

    @BeforeEach
    void setUp() {
        ExchangeRateReportParser.getIsoCodeToRateMap().clear();
    }

    @Test
    void parseExchangeRateReport_validXmlFile() {
        try {
            ExchangeRateReportParser.parseExchangeRateReport(VALID_XML_FILE_PATH);

            Map<String, Double> isoCodeToRateMap = ExchangeRateReportParser.getIsoCodeToRateMap();
            assertFalse(isoCodeToRateMap.isEmpty());
            assertEquals(38, isoCodeToRateMap.size());
            assertEquals(1.00, isoCodeToRateMap.get("USD"));
            assertEquals(1.0931, isoCodeToRateMap.get("EUR"));
            assertEquals(146.98, isoCodeToRateMap.get("JPY"));
            assertEquals(1.26405, isoCodeToRateMap.get("GBP"));

        } catch (IOException e) {
            fail("IOException should not be thrown during a successful parseExchangeRateReport operation");
        }
    }

    @Test
    void parseExchangeRateReport_invalidXmlFile() {
        assertThrows(IOException.class, () -> ExchangeRateReportParser.parseExchangeRateReport(INVALID_XML_FILE_PATH));
    }

    @Test
    void loadExchangeRatesFromFile_validXmlFile() {
        try {
            ExchangeRateReportParser.loadExchangeRatesFromFile(VALID_XML_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, Double> isoCodeToRateMap = ExchangeRateReportParser.getIsoCodeToRateMap();

        assertFalse(isoCodeToRateMap.isEmpty());
    }

    @Test
    void loadExchangeRatesFromFile_invalidXmlFile() {
        try {
            ExchangeRateReportParser.loadExchangeRatesFromFile(INVALID_XML_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, Double> isoCodeToRateMap = ExchangeRateReportParser.getIsoCodeToRateMap();

        assertTrue(isoCodeToRateMap.isEmpty());
    }

    @Test
    void getIsoCodeToRateMap() {
        Map<String, Double> isoCodeToRateMap = ExchangeRateReportParser.getIsoCodeToRateMap();

        assertNotNull(isoCodeToRateMap);
        assertTrue(isoCodeToRateMap.isEmpty());
    }

    @Test
    void isIsoCodeToRateMapEmpty_emptyMap() {
        assertTrue(ExchangeRateReportParser.isIsoCodeToRateMapEmpty());
    }

    @Test
    void isIsoCodeToRateMapEmpty_nonEmptyMap() {
        try {
            ExchangeRateReportParser.loadExchangeRatesFromFile(VALID_XML_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertFalse(ExchangeRateReportParser.isIsoCodeToRateMapEmpty());
    }

    @Test
    void convertCurrencyToUSD_withValidExchangeRate() {
        ExchangeRateReportParser.getIsoCodeToRateMap().put("EUR", 0.8);
        double convertedAmount = ExchangeRateReportParser.convertCurrencyToUSD("EUR", 100.0);

        assertEquals(125.0, convertedAmount, 0.0001);
    }

    @Test
    void convertCurrencyToUSD_withMissingExchangeRate() {
        double convertedAmount = ExchangeRateReportParser.convertCurrencyToUSD("JMD", 17.37);

        assertEquals(0.0, convertedAmount, 0.0001);
    }

    @Test
    void convertCurrencyToUSD_withEmptyExchangeRateMap() {
        double convertedAmount = ExchangeRateReportParser.convertCurrencyToUSD("USD", 100.0);

        assertEquals(0.0, convertedAmount, 0.0001);
    }

    @Test
    void convertCurrencyToUSD_withIOException() {
        try {
            ExchangeRateReportParser.loadExchangeRatesFromFile("nonexistentfile.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        double convertedAmount = ExchangeRateReportParser.convertCurrencyToUSD("EUR", 100.0);

        assertEquals(0.0, convertedAmount, 0.0001);
    }
}
