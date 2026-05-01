package SeedFind;

import java.util.ArrayList;

import static SeedFind.Utils.*;

public class StatPrinter extends Thread {

    int sleepInterval = 1000;
    long lastTotal = 0;
    long lastTime;
    SeedJob[] jobs;
    ArrayList<ResultHandler> resultHandlers;
    int totalViableResults;

    public StatPrinter(int sleepInterval, SeedJob[] jobs) {
    this.sleepInterval = sleepInterval;
    this.jobs = jobs;
    init();}


    @Override
    public void run() {
        super.run();


        lastTime = System.nanoTime();

        while (isJobsRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
            CheckViableResults();
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


            clearConsole();
            System.out.println("SPS: " + formatSPS(sps) +
                    " | total: " + total +
                    " | viable results: " + totalViableResults +
                    " | RAM: " + usedMB + " MB");

            lastTotal = total;
            lastTime = now;

    }
}

    private boolean isJobsRunning() {
        for (SeedJob job : jobs) {
            if (job.isRunning) {
                return true;
            }
        }
        return false;
    }

    private void init(){
        resultHandlers = new ArrayList<>();
        for (SeedJob job : jobs) {
            resultHandlers.add(job.resultHandler);
        }
    }

    private void CheckViableResults() {
        for (ResultHandler resultHandler : resultHandlers) {
            totalViableResults += resultHandler.GetResultCount();
        }
    }

    /**
     * Clears the console with cmd magic or by printing 50 empty lines as a fallback lmao
     */
    public static void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            // fallback
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }




}
