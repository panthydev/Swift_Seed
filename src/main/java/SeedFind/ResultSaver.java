package SeedFind;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ResultSaver {

    private final Path resultsDir;
    private final Path resultFile;

    public ResultSaver() {
        this.resultsDir = Paths.get(System.getProperty("user.dir"), "results");
        CreateDirIfNeeded();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        this.resultFile = resultsDir.resolve(timestamp + ".txt");
    }

    private void CreateDirIfNeeded() {
        try {
            Files.createDirectories(resultsDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create results directory", e);
        }
    }

    public void SaveResults(List<Result> results) {
        StringBuilder sb = new StringBuilder();

        for (Result r : results) {
            sb.append(r.toString()).append(System.lineSeparator());
        }

        try {
            Files.writeString(
                    resultFile,
                    sb.toString(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to write results file", e);
        }
    }
}