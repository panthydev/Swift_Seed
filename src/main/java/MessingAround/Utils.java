package MessingAround;

import java.util.Random;

import static java.lang.System.out;

public class Utils {
    public static String formatSPS(double value) {
        if (value >= 1_000_000) {
            return String.format("%.2f million", value / 1_000_000);
        } else if (value >= 1_000) {
            return String.format("%.2f k", value / 1_000);
        } else {
            return String.format("%.0f", value);
        }
    }

    public static long[] generateStartOffsets(int threadCount, long seedAmount) {
        Random rng = new Random();

        long base = rng.nextLong();
        long step = Math.max(1, seedAmount / threadCount);

        long[] starts = new long[threadCount];

        for (int i = 0; i < threadCount; i++) {
            starts[i] = base + (i * step);
        }

        return starts;
    }

    public static void ViewStats(SeedJob[] Jobs, long startTime) {
        long end = System.nanoTime();
        double seconds = (end - startTime) / 1_000_000_000.0;
        long totalSeedsAttempted = 0;
        for (SeedJob job : Jobs) {
            totalSeedsAttempted += job.currSeedAttempt;
        }

        out.println(totalSeedsAttempted + " Completed in: " + seconds + " seconds");
        out.println("seeds per second: " + totalSeedsAttempted/seconds);
        out.println("seeds per second per thread: " + totalSeedsAttempted/seconds/Jobs.length);
    }
}
