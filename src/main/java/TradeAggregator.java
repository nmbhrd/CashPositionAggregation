import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class TradeAggregator {
    private TradeProcessor tradeProcessor;
    private List<Trade> trades;
    private String aggregationKey;
    private final BufferedReader reader;

    public TradeAggregator(BufferedReader reader) {
        this.reader = reader;
    }

    public void setTradeProcessor(TradeProcessor tradeProcessor) {
        if (tradeProcessor == null) {
            throw new IllegalArgumentException("TradeProcessor cannot be null.");
        }
        this.tradeProcessor = tradeProcessor;
    }

    public List<Trade> getTradesList() {
        trades = new ArrayList<>(tradeProcessor.getConvertedTrades().values());
        return trades;
    }

    protected String promptUserForAggregationCriteria() {
        List<Trade> tradesList = getTradesList();
        int attempts = 0;
        int maxAttempts = 6;
        String input;
        while (attempts < maxAttempts) {
            System.out.println("Enter the desired criteria for aggregation. Valid aggregations can be specific: BBGCode (Bloomberg Code), Portfolio, Strategy, User, or Currency. Type 'exit' to exit the program");
            try {
                String userInput = reader.readLine();
                input = (userInput != null) ? userInput.trim() : "";

                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Exiting the program.");
                    System.exit(0);
                } else if (aggregationKeyIsValid(input, tradesList) && !input.isEmpty() && !input.isBlank()) {
                    return input;
                } else {
                    System.out.println("Invalid input. Please provide a valid criteria.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error reading input. Please try again.");
            }
            attempts++;
        }
        System.out.println("Maximum attempts reached. Exiting the program.");
        System.exit(0);
        return "";
    }

    public synchronized String obtainAggregationKey() {
        List<Trade> tradesList = getTradesList();
        if (tradesList == null) {
            System.out.println("Error: Trades list is null.");
            return "error";
        }

        boolean isValidKey;
        do {
            aggregationKey = promptUserForAggregationCriteria();
            isValidKey = aggregationKeyIsValid(aggregationKey, tradesList);
        } while (!isValidKey);

        return aggregationKey;
    }

    protected boolean aggregationKeyIsValid(String key, List<Trade> tradesList) {
        boolean isValid = key != null && tradesList.stream().anyMatch(trade -> tradeContainsAggregationKey(trade, key));
        if (!isValid) {
            System.out.println("Invalid aggregation key. Please enter a valid key.");
        }
        return isValid;
    }

    public synchronized List<Trade> aggregateTrades() {
        String aggregationKey = obtainAggregationKey();
        List<Trade> tradesList = getTradesList();

        if (aggregationKey == null) {
            System.out.println("Error: Aggregation key not set. Please set the aggregation key first.");
            return Collections.emptyList();
        }
        return aggregateByCriteria(tradesList);
    }

    private static final List<String> FIELDS_TO_CONSIDER = Arrays.asList("bbgCode", "currencyISOCode", "portfolio", "strategy", "user");

    private synchronized boolean tradeContainsAggregationKey(Trade trade, String key) {
        return FIELDS_TO_CONSIDER.stream().anyMatch(fieldName -> {
            try {
                Field field = Trade.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(trade);
                return String.valueOf(value).equalsIgnoreCase(key);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public synchronized List<Trade> aggregateByCriteria(List<Trade> tradesList) {
        if (tradesList == null || tradesList.isEmpty()) {
            System.out.println("No trades available for aggregation. Returning an empty list.");
            return Collections.emptyList();
        }

        String lowerCaseAggregationKey = aggregationKey.toLowerCase();
        List<Trade> filteredTrades = tradesList.stream().filter(trade -> tradeContainsAggregationKey(trade, lowerCaseAggregationKey)).collect(Collectors.toList());

        if (filteredTrades.isEmpty()) {
            System.out.println("No trades match the aggregation criteria. Returning an empty list.");
            return Collections.emptyList();
        }

        System.out.println("Aggregation completed successfully for " + aggregationKey);
        return filteredTrades;
    }

    public synchronized String getAggregationKey() {
        return aggregationKey;
    }
}
