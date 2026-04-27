package MessingAround;

import dev.xpple.cubiomes.Cubiomes;
import dev.xpple.cubiomes.CubiomesInit;
import dev.xpple.cubiomes.Generator;
import dev.xpple.cubiomes.Range;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Random;

import static java.lang.System.out;

public class Main {

    static int version;
    static long startTime;
    static Arena arena;


    static void main(String[] args) {
        Init();
        long seedAmount = Long.parseLong(args[0]);
        Thread[] Workers =  new Thread[Integer.parseInt(args[1])];
        SeedJob[] jobs = new SeedJob[Workers.length];
        long[] offsets = generateStartOffsets(Workers.length,  seedAmount);


        for (int i = 1; i < Workers.length +1; i++) {
            SeedJob job = new SeedJob(version, arena, seedAmount, i, offsets[i-1]);
            Thread thread = new Thread(job);
            out.println(thread.getName());
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
            Workers[i -1] = thread;
            jobs[i -1] = job;
        }

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
        arena = Arena.global();

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

}
