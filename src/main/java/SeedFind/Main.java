package SeedFind;

import dev.xpple.cubiomes.Cubiomes;
import dev.xpple.cubiomes.CubiomesInit;

import java.util.Scanner;

import static SeedFind.Utils.*;
import static java.lang.System.out;

public class Main {

    static int version;
    static long startTime;

    static long seedAmount;
    static Thread[] Workers;
    static SeedJob[] jobs;
    static long[] offsets;
    static ResultHandler[] resultHandlers;

    public static void main(String[] args) {

        GetInput();
        Init();

        PrepareWorkers(args);
        CreateWorkers();

        StartMainManager(resultHandlers);
        StartStatsPrinter(jobs);

        WaitUntilComplete();
        ViewStats(jobs, startTime);

        EndProgram();
    }


    //Gets amount of seeds to search and number of threads to use
    private static void GetInput() {
        Scanner sc = new Scanner(System.in);

        out.println("Welcome to Swift seed!" + "\n" + "\n");
        seedAmount = ReadSeedAmount(sc, "Enter number of seeds to search through: ");
        Workers = new Thread[ReadInt(sc, "Enter number of threads to use ")];

        out.println("\n" + "Great, Swift seed will search through: " + seedAmount + " seeds, with " + Workers.length + " threads");
        seedAmount = seedAmount / Workers.length;
    }


    private static void EndProgram() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (Thread w : Workers) {
            w.interrupt();
        }
        System.exit(0);
    }


    //Gets input from user, until a positive long is given
    private static long ReadSeedAmount(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);

            if (sc.hasNextLong()) {
                long value = sc.nextLong();
                if (value > 0) {
                    return value;
                } else {
                    System.out.println("Invalid input. Please enter a positive number.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.next();
            }
        }
    }

    //Gets input from user, until a positive int is given within range
    public static int ReadInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                if (value > 0 && value <= 16) {
                    return value;
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and 16.");
                }
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
        for (int i = 1; i < Workers.length + 1; i++) {
            ResultHandler resultHandler = new ResultHandler();
            resultHandlers[i - 1] = resultHandler;
            SeedJob job = new SeedJob(version, seedAmount, i, offsets[i - 1], resultHandlers[i - 1]);
            Thread thread = new Thread(job);

            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
            Workers[i - 1] = thread;
            jobs[i - 1] = job;
        }
    }

    private static void PrepareWorkers(String[] args) {
        jobs = new SeedJob[Workers.length];
        offsets = generateStartOffsets(Workers.length, seedAmount);
        resultHandlers = new ResultHandler[Workers.length];
    }


    //CubiomesInit.Load MUST be called before any cubiomes usage
    public static void Init() {
        CubiomesInit.load();
        startTime = System.nanoTime();
        version = Cubiomes.MC_1_21();
    }

    public static void StartMainManager(ResultHandler[] resultHandlers) {
        Thread mainManager = new MainManager(1000, resultHandlers);
        mainManager.start();
    }

    public static void StartStatsPrinter(SeedJob[] jobs) {
        Thread statsPrinter = new StatPrinter(1000, jobs);
        statsPrinter.start();
    }
}
