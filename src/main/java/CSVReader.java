import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVReader {
    private final Path csvFilePath;

    public CSVReader(Path csvFilePath) {
        if (csvFilePath == null) {
            throw new IllegalArgumentException("CSV file path cannot be null");
        }
        this.csvFilePath = csvFilePath;
    }

    public List<String[]> readTrades() throws IOException {
        try (Stream<String> lines = Files.lines(csvFilePath)) {
            return lines.skip(0).map(line -> line.split(",")).collect(Collectors.toList());
        }
    }
}
