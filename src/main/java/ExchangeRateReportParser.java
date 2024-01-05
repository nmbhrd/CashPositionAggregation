import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExchangeRateReportParser {
    private static final Map<String, Double> isoCodeToRateMap = new ConcurrentHashMap<>();

    public static void parseExchangeRateReport(String filePath) throws IOException {
        Path xmlFilePath = Paths.get(filePath);

        try (InputStream inputStream = Files.newInputStream(xmlFilePath)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(inputStream);

            Element exchangeRateReportElement = document.getDocumentElement();
            NodeList rateValueList = exchangeRateReportElement.getElementsByTagName("RATE_VALUE");

            for (int i = 0; i < rateValueList.getLength(); i++) {
                Element rateValueElement = (Element) rateValueList.item(i);
                String isoCharCode = rateValueElement.getAttribute("ISO_CHAR_CODE");
                double rate = Double.parseDouble(rateValueElement.getTextContent());

                isoCodeToRateMap.put(isoCharCode, rate);
            }

        } catch (Exception e) {
            throw new IOException("Error parsing exchange rate report: " + e.getMessage(), e);
        }
    }

    public static void loadExchangeRatesFromFile(String filePath) throws IOException {
        try {
            parseExchangeRateReport(filePath);
        } catch (IOException e) {
            System.err.println("Failed to load exchange rates: " + e.getMessage());
        }
    }

    public static Map<String, Double> getIsoCodeToRateMap() {
        return isoCodeToRateMap;
    }

    public static boolean isIsoCodeToRateMapEmpty() {
        return isoCodeToRateMap.isEmpty();
    }

    public static double convertCurrencyToUSD(String fromCurrency, double amount) {
        Map<String, Double> isoCodeToRateMap = ExchangeRateReportParser.getIsoCodeToRateMap();

        if (isoCodeToRateMap != null && !isoCodeToRateMap.isEmpty()) {
            double fromRate = isoCodeToRateMap.computeIfAbsent(fromCurrency, k -> 1.0);
            return amount / fromRate;
        } else {
            System.out.println("ISO code to rate map is null or empty. Unable to perform currency conversion.");
            return 0.0;
        }
    }
}
