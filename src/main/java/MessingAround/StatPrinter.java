package MessingAround;

import static MessingAround.Utils.*;

public class StatPrinter extends Thread {

    int sleepInterval = 1000;
    SeedJob[] jobs;

    public StatPrinter(int sleepInterval, SeedJob[] jobs) {
    this.sleepInterval = sleepInterval;
    this.jobs = jobs;}


    @Override
    public void run() {
        super.run();

        long lastTotal = 0;
        long lastTime = System.nanoTime();

        while (isJobsRunning()) {
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
}

    private boolean isJobsRunning() {
        for (SeedJob job : jobs) {
            if (job.isRunning) {
                return true;
            }
        }
        return false;
    }


}
