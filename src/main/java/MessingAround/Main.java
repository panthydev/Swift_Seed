package MessingAround;

import dev.xpple.cubiomes.Cubiomes;
import dev.xpple.cubiomes.CubiomesInit;
import dev.xpple.cubiomes.Generator;
import dev.xpple.cubiomes.Range;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.System.out;

public class Main {

    static int version;
    static long startTime;


    static void main(String[] args) {
        Init();
        long seedAmount = Long.parseLong(args[0]);
        Thread[] Workers =  new Thread[Integer.parseInt(args[1])];
        SeedJob[] jobs = new SeedJob[Workers.length];
        long[] offsets = generateStartOffsets(Workers.length,  seedAmount);
        ResultHandler[]  resultHandlers = new ResultHandler[Workers.length];


        for (int i = 1; i < Workers.length +1; i++) {
            ResultHandler resultHandler = new ResultHandler();
            resultHandlers[i-1] = resultHandler;
            SeedJob job = new SeedJob(version, seedAmount, i, offsets[i-1], resultHandlers[i-1]);
            Thread thread = new Thread(job);

            out.println(thread.getName());
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
            Workers[i -1] = thread;
            jobs[i -1] = job;
        }

        startMainManager(resultHandlers);
        startStatsPrinter(jobs);
        for (Thread thread : Workers) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ViewStats(jobs);
    }


    public static void Init(){
        CubiomesInit.load();
        startTime = System.nanoTime();
        version = Cubiomes.MC_1_21();


    }

    public static void ViewStats(SeedJob[] Jobs) {
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

    public static void startMainManager(ResultHandler[] resultHandlers) {
       Thread manager = new Thread(() -> {
           ArrayList<Result> results = new ArrayList<>();
           while (true) {
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {return;}

               for (ResultHandler resultHandler : resultHandlers) {
                   for (Result result = resultHandler.pollResult(); result != null; result = resultHandler.pollResult()) {
                       results.add(result);
                       result.PrintResult();
                   }
               }
           }
       });

       manager.start();
    }


    public static void startStatsPrinter(SeedJob[] jobs) {
        Thread stats = new Thread(() -> {


            long lastTotal = 0;
            long lastTime = System.nanoTime();

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }

                long total = 0;

                // sum all thread progress
                for (SeedJob job : jobs) {
                    total += job.currSeedAttempt;
                }

                long now = System.nanoTime();

                double seconds = (now - lastTime) / 1e9;
                long delta = total - lastTotal;

                double sps = delta / seconds;

                Runtime rt = Runtime.getRuntime();
                long usedMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);

                System.out.println("SPS: " + formatSPS(sps) +
                        " | total: " + total +
                        " | RAM: " + usedMB + " MB");

                lastTotal = total;
                lastTime = now;
            }
        });

        stats.setDaemon(true);
        stats.start();
    }

    public static String formatSPS(double value) {
        if (value >= 1_000_000) {
            return String.format("%.2f million", value / 1_000_000);
        } else if (value >= 1_000) {
            return String.format("%.2f k", value / 1_000);
        } else {
            return String.format("%.0f", value);
        }
    }

}
