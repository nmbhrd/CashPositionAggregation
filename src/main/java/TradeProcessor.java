import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TradeProcessor {
    private final Map<String, Trade> trades = new ConcurrentHashMap<>();
    private final Map<String, Trade> convertedTrades = new ConcurrentHashMap<>();
    private final AtomicInteger totalTradesAdded = new AtomicInteger(0);
    private final AtomicInteger amendedTradesCount = new AtomicInteger(0);
    private final AtomicInteger canceledTradesCount = new AtomicInteger(0);
    final BufferedReader reader;

    public TradeProcessor(BufferedReader reader) {
        this.reader = reader;
    }

    public void initialiseTradesAndExchangeRates() {
        try {
            String exchangeRatesPath = "src/main/resources/Exchange_Rate_Report.xml";
            ExchangeRateReportParser.loadExchangeRatesFromFile(exchangeRatesPath);

            String tradesFilePath = "src/sample_trades (2).csv";
            CSVReader tradesCSVReader = new CSVReader(Path.of(tradesFilePath));
            List<String[]> tradeDataList = tradesCSVReader.readTrades();
            processTradesFromAList(tradeDataList);

        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean promptForProcessing() {
        try {
            boolean exitApplication = false;
            boolean continueLoop = true;

            while (continueLoop) {
                System.out.println("Would you like to process additional new trades or make any amendments or cancellations to existing trades?");
                System.out.println("1. Yes");
                System.out.println("2. No");
                System.out.println("3. Exit");

                String input = reader.readLine();

                if (input != null) {
                    String choiceInput = input.trim().toLowerCase().replaceAll("\\s+", " ");

                    switch (choiceInput) {
                        case "1", "yes" -> {
                            promptForNewTradesOrAmendments();
                            continueLoop = false;
                        }
                        case "2", "no" -> continueLoop = false;
                        case "3", "exit" -> {
                            exitApplication = true;
                            continueLoop = false;
                        }
                        default ->
                                System.out.println("Invalid choice. Enter '1' or '2' or 'yes' or 'no' or 'exit'. Please enter a valid input.");
                    }
                }
            }
            return exitApplication;
        } catch (IOException e) {
            System.out.println("Error: An exception has occurred.");
            e.printStackTrace();
            return true;
        }
    }

    private void promptForNewTradesOrAmendments() {
        boolean exitRequested = false;

        while (!exitRequested) {
            try {
                System.out.println("Would you like to:");
                System.out.println("1. Process new trades");
                System.out.println("2. Amend an existing trade");
                System.out.println("3. Cancel an existing trade");
                System.out.println("4. Return (to return to aggregation)");

                String input = reader.readLine();

                if (input != null) {
                    String choiceInput = input.trim().toLowerCase().replaceAll("\\s+", " ");
                    switch (choiceInput) {
                        case "1", "process new trade(s)" -> promptForNewTrades();
                        case "2", "amend an existing trade", "3", "cancel an existing trade" -> promptForManualTrade();
                        case "4", "return" -> exitRequested = true;
                        default ->
                                System.out.println("Invalid choice. Enter '1' or '2' or 'process new trade(s)' or 'amend an existing trade' or 'cancel an existing trade' or '4' or 'return'. Please enter a valid input.");
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("Error: An exception has occurred.");
                e.printStackTrace();
                exitRequested = true;
            }
        }
    }

    private void promptForManualTrade() {
        Trade manualTrade = null;

        do {
            try {
                System.out.println("Enter the trade data, please provide 13 comma-separated values or type return to return previous prompt:");
                System.out.println("Format: tradeId, bbgCode, currencyISOCode, side, price, volume, portfolio, action, account, strategy, user, tradeTimeUTC, valueDate");

                String manualTradeDataInput = reader.readLine().replaceAll("\\s", "");

                if ("return".equalsIgnoreCase(manualTradeDataInput)) {
                    System.out.println("Returning to previous prompt.");
                    break;
                }

                String[] manualTradeDataArray = manualTradeDataInput.split(",");

                if (manualTradeDataArray.length >= 13) {
                    manualTrade = Trade.createNewTrade(
                            manualTradeDataArray[0], manualTradeDataArray[1], manualTradeDataArray[2],
                            manualTradeDataArray[3], Double.parseDouble(manualTradeDataArray[4]),
                            Double.parseDouble(manualTradeDataArray[5]), manualTradeDataArray[6],
                            manualTradeDataArray[7], manualTradeDataArray[8], manualTradeDataArray[9],
                            manualTradeDataArray[10], manualTradeDataArray[11], manualTradeDataArray[12]);

                    processTrade(manualTrade);
                    System.out.println("Manual trade processed successfully.");
                } else {
                    System.out.println("Invalid input. Please provide exactly 13 comma-separated values.");
                    System.out.println("Format: tradeId, bbgCode, currencyISOCode, side, price, volume, portfolio, action, account, strategy, user, tradeTimeUTC, valueDate");
                }

            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        } while (manualTrade == null);
    }

    private void promptForNewTrades() {
        try {
            System.out.println("Choose the source type of new trades to add:");
            System.out.println("1. A CSV file");
            System.out.println("2. A List of manual trades");
            System.out.println("3. A Manual trade");

            String input = reader.readLine();
            if (input != null) {
                String choiceInput = input.trim().toLowerCase().replaceAll("\\s+", " ");

                switch (choiceInput) {
                    case "1", "csv file" -> promptForNewCSVTrades();
                    case "2", "list of manual trades" -> promptForListOfTrades();
                    case "3", "manual trade" -> promptForManualTrade();
                    default -> System.out.println("Invalid choice.");
                }
            }
        } catch (IOException | NumberFormatException e) {
            handleInputMismatchException();
            System.out.println("Exiting the application. Please run again.");
        }
    }

    private void handleInputMismatchException() {
        System.out.println("Invalid input. Please enter a valid input.");
        try {
            reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processTradesFromAList(List<String[]> tradeDataList) {
        if (tradeDataList == null || tradeDataList.isEmpty()) {
            System.out.println("No trade data provided. Please provide valid trade data.");
            return;
        }

        for (String[] tradeDataArray : tradeDataList) {
            try {
                Trade trade = Trade.createNewTrade(
                        tradeDataArray[0], tradeDataArray[1], tradeDataArray[2],
                        tradeDataArray[3], Double.parseDouble(tradeDataArray[4]),
                        Double.parseDouble(tradeDataArray[5]), tradeDataArray[6],
                        tradeDataArray[7], tradeDataArray[8], tradeDataArray[9],
                        tradeDataArray[10], tradeDataArray[11], tradeDataArray[12]);

                processTrade(trade);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                if (e.getMessage().contains("For input string: \"Price\"") || e.getMessage().contains("For input string: \"Volume\"")) {
                    continue;
                }
                System.out.println("Error processing trade data: " + String.join(", ", tradeDataArray) + ". Exception: " + e.getMessage());
            }
        }
    }

    public void processTradesFromCSV(List<String> csvFiles) throws IOException {
        for (String csvFile : csvFiles) {
            CSVReader csvReader = new CSVReader(Path.of(csvFile));
            List<String[]> tradeDataList = csvReader.readTrades();
            processTradesFromAList(tradeDataList);
        }
    }

    private void promptForNewCSVTrades() {
        try {
            System.out.println("Enter the new CSV file path(s) (comma-separated):");
            String csvFilesInput = reader.readLine();
            List<String> csvFile = List.of(csvFilesInput.split(","));
            processTradesFromCSV(csvFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void promptForListOfTrades() {
        try {
            System.out.println("Enter the list of trades (one trade per line, press the Enter key twice to finish):");
            List<String[]> tradeDataList = new ArrayList<>();
            while (true) {
                String line = reader.readLine();
                if (line.trim().isEmpty()) {
                    break;
                }
                tradeDataList.add(line.split(","));
            }
            processTradesFromAList(tradeDataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processTrade(Trade trade) {
        if ("CANCEL".equalsIgnoreCase(trade.action())) {
            canceledTradesCount.incrementAndGet();
            trades.remove(trade.tradeId());
        } else {
            totalTradesAdded.incrementAndGet();
            if ("AMEND".equalsIgnoreCase(trade.action())) {
                amendedTradesCount.incrementAndGet();
                trades.put(trade.tradeId(), trade);
            } else {
                trades.putIfAbsent(trade.tradeId(), trade);
            }
        }
    }

    public void applyCurrencyConversion(Map<String, Trade> originalTrades) {
        originalTrades.forEach((tradeId, originalTrade) -> convertedTrades.compute(tradeId, (key, existingTrade) -> {
            if (!"USD".equalsIgnoreCase(originalTrade.currencyISOCode())) {
                double convertedPrice = ExchangeRateReportParser.convertCurrencyToUSD(originalTrade.currencyISOCode(), originalTrade.price());
                return Trade.createNewTrade(originalTrade.tradeId(), originalTrade.bbgCode(), originalTrade.currencyISOCode(), originalTrade.side(), convertedPrice, originalTrade.volume(), originalTrade.portfolio(), originalTrade.action(), originalTrade.account(), originalTrade.strategy(), originalTrade.user(), originalTrade.tradeTimeUTC(), originalTrade.valueDate());
            } else {
                return originalTrade;
            }
        }));
    }

    public Map<String, Trade> getConvertedTrades() {
        applyCurrencyConversion(trades);
        return convertedTrades;
    }
}
