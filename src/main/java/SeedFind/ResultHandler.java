package SeedFind;

import java.util.ArrayDeque;


public class ResultHandler {

    //why would they call a queue for ArrayDeque.... it sounds like a queue thats always dequeing ?? so confusing
    private ArrayDeque<Result> results;

    public ResultHandler() {
        results = new ArrayDeque<Result>();
    }

    public int GetResultCount() {
        return results.size();
    }

    public void HandleResult(Result result) {
        results.add(result);
    }

    public Result PollResult() {
        return results.poll();
    }
}
