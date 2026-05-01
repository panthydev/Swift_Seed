package SeedFind;

import java.util.Random;

import static java.lang.System.out;

// typical abomination of class with a bunch of misc, utilities
public class Utils {
    //used for formatting

    /**
     * formats the given value into thousands and millions, into a string
     * @param value value to format
     * @return string of the formatted value
     */
    public static String formatSPS(double value) {
        if (value >= 1_000_000) {
            return String.format("%.2f million", value / 1_000_000);
        } else if (value >= 1_000) {
            return String.format("%.2f k", value / 1_000);
        } else {
            return String.format("%.0f", value);
        }
    }

    //generates the offsets that each seedjob will start on, ensures we dont search the same seeds again and again

    /**
     *
     * @param threadCount How many threads to make offsets for ?
     * @param seedAmount How many seeds are we searching ?
     * @return an array of type long, contains each offset position
     */
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

    /**
     * Prints the final stats of the seed searching process directly to the console
     ** @param Jobs The seedJobs that contain the individual stats
     ** @param startTime When the program first began running
     *
     */
    public static void ViewStats(SeedJob[] Jobs, long startTime) {

        //delay so it doesn't print inside SPS prints
        try {
            Thread.sleep(1250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
