package SeedFind;

import java.util.ArrayList;

public class MainManager extends Thread {
    int sleepInterval;
    ResultHandler[] resultHandlers;
    ResultSaver resultSaver;
    public MainManager(int sleepInterval,  ResultHandler[] resultHandlers) {
        this.sleepInterval = sleepInterval;
        this.resultHandlers = resultHandlers;
        this.resultSaver = new ResultSaver();
    }
    @Override
    public void run() {
        super.run();
        ArrayList<Result> results = new ArrayList<>();
        while (true) {
            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException e) {return;}

            for (ResultHandler resultHandler : resultHandlers) {
                for (Result result = resultHandler.pollResult(); result != null; result = resultHandler.pollResult()) {
                    results.add(result);
                }
            }

            resultSaver.saveResults(results);
        }
    }
}
