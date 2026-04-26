package MessingAround;

import dev.xpple.cubiomes.Cubiomes;
import dev.xpple.cubiomes.CubiomesInit;
import dev.xpple.cubiomes.Generator;
import dev.xpple.cubiomes.Range;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

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

        for (int i = 0; i < Workers.length; i++) {
            SeedJob job = new SeedJob(version, arena, seedAmount);
            Thread thread = new Thread(job);
            out.println(thread.getName());
            thread.start();
            Workers[i] = thread;
            jobs[i] = job;
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


}
