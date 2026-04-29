package MessingAround;

import dev.xpple.cubiomes.Cubiomes;
import dev.xpple.cubiomes.CubiomesInit;
import static MessingAround.Utils.*;
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

        ViewStats(jobs, startTime);
    }


    public static void Init(){
        CubiomesInit.load();
        startTime = System.nanoTime();
        version = Cubiomes.MC_1_21();


    }

    public static void startMainManager(ResultHandler[] resultHandlers) {
       Thread mainManager = new MainManager(1000,  resultHandlers);
       mainManager.start();
    }
    public static void startStatsPrinter(SeedJob[] jobs) {
        Thread statsPrinter = new StatPrinter(1000, jobs);
        statsPrinter.start();
    }
}
