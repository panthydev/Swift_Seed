package MessingAround;

import dev.xpple.cubiomes.Cubiomes;
import dev.xpple.cubiomes.Generator;
import dev.xpple.cubiomes.Pos;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Random;
import java.util.random.RandomGenerator;

import static java.lang.System.out;

public class SeedJob implements Runnable {
    public JobData jobData;
    public long currSeedAttempt;
    public int threadId;
    public long baseOffset;
    public boolean isRunning = true;

    public ResultHandler resultHandler;
    public void init(){
        jobData.init();
        isRunning = true;
    }




    SeedJob(int version, long  seedAmount, int threadId, long baseOffset, ResultHandler resultHandler ) {
        jobData = new JobData(seedAmount, version);
        this.threadId = threadId;
        this.baseOffset = baseOffset;
        this.resultHandler = resultHandler;

    }
    @Override
    public void run() {
        init();
        SeedEvaluator evaluator =  new SeedEvaluator(jobData, new Conditions(
                1416,
                50,
                32), resultHandler) ;

        final int BATCH_SIZE = 1_000_00;

        while (currSeedAttempt <= jobData.seedAmount) {

            jobData.init(); // NEW arena each batch

            for (int i = 0; i < BATCH_SIZE && currSeedAttempt <= jobData.seedAmount; i++, currSeedAttempt++) {
                long seed = baseOffset + currSeedAttempt + threadId;
                evaluator.ProcessSeed(seed);
            }


            jobData.arena.close(); // FREE EVERYTHING
        }
        isRunning = false;

    }



}
