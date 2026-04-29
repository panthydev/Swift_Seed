package MessingAround;

import java.util.ArrayList;

public class MainManager extends Thread {
    int sleepInterval;
    ResultHandler[] resultHandlers;
    public MainManager(int sleepInterval,  ResultHandler[] resultHandlers) {
        this.sleepInterval = sleepInterval;
        this.resultHandlers = resultHandlers;
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
                    result.PrintResult();
                }
            }
        }
    }
}
