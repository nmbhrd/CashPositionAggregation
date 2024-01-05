import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            TradeProcessor tradeProcessor = new TradeProcessor(reader);
            TradeAggregator tradeAggregator = new TradeAggregator(reader);
            PnLCalculator pnLCalculator = new PnLCalculator();
            tradeProcessor.initialiseTradesAndExchangeRates();

            boolean exitApplication = tradeProcessor.promptForProcessing();

            if (exitApplication){
                System.out.println("Exiting the application.");
            }else {
                tradeAggregator.setTradeProcessor(tradeProcessor);
                pnLCalculator.setTradeAggregator(tradeAggregator);
                pnLCalculator.displayCashPosition();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
