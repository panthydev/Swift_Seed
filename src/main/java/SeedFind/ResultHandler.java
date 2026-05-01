package SeedFind;

import java.util.ArrayDeque;


public class ResultHandler {

    private ArrayDeque<Result> results;
    public ResultHandler(){
        results = new ArrayDeque<Result>(); {};
    }

    public int GetResultCount(){
        return results.size();
    }
public void handleResult(Result result){
        results.add(result);
}

public Result pollResult() {
        return results.poll();
    }
}
