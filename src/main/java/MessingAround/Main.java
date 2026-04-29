package MessingAround;

import dev.xpple.cubiomes.Cubiomes;
import dev.xpple.cubiomes.CubiomesInit;

import java.util.Scanner;

import static MessingAround.Utils.*;
import static java.lang.System.out;

public class Main {

    static int version;
    static long startTime;

    static long seedAmount;
    static Thread[] Workers;
    static SeedJob[] jobs;
    static long[] offsets;
    static ResultHandler[]  resultHandlers;

    static void main(String[] args) {


        Scanner sc = new Scanner(System.in);

        out.println("Welcome to Swift seed!" + "\n" + "\n");
        seedAmount = ReadSeedAmount(sc, "Enter number of seeds to search through: ");
        Workers = new Thread[readInt(sc, "Enter number of threads to use ")];

        out.println("\n" + "Great, Swift seed will search through: " + seedAmount + " seeds, with " +  Workers.length + " threads" );
        seedAmount = seedAmount/Workers.length;


        Init();

        PrepareWorkers(args);
        CreateWorkers();

        startMainManager(resultHandlers);
        startStatsPrinter(jobs);

        WaitUntilComplete();
        ViewStats(jobs, startTime);
    }

    private static long ReadSeedAmount(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);

            if (sc.hasNextLong()) {
                return sc.nextLong();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.next();
            }
        }

    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            } else {
                System.out.println("Invalid input. Please enter a whole number.");
                scanner.next();
            }
        }
    }

    private static void WaitUntilComplete() {
        for (Thread thread : Workers) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void CreateWorkers() {
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
    }

    private static void PrepareWorkers(String[] args) {
        jobs = new SeedJob[Workers.length];
        offsets = generateStartOffsets(Workers.length,  seedAmount);
        resultHandlers = new ResultHandler[Workers.length];
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
