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
    public long currSeed;
    public int threadId;
    public long baseOffset;
    public static final int MAX_STRONGHOLD_RANGE = 1400;

    public static final int MAX_SPAWN_DISTANCE_FROM_STRONGHOLD = 650;
    public static final int MAX_DISTANCE_FROM_STRONGHOLD = 16;

    public void init(){
        jobData.init();
    }


    SeedJob(int version, long  seedAmount, int threadId, long baseOffset ) {
        jobData = new JobData(seedAmount, version);
        this.threadId = threadId;
        this.baseOffset = baseOffset;
    }
    @Override
    public void run() {
        init();

        final int BATCH_SIZE = 1_000_00;

        while (currSeedAttempt <= jobData.seedAmount) {

            jobData.init(); // NEW arena each batch

            for (int i = 0; i < BATCH_SIZE && currSeedAttempt <= jobData.seedAmount; i++, currSeedAttempt++) {
                long seed = baseOffset + currSeedAttempt + threadId;
                ProcessSeed(seed);
            }


            jobData.arena.close(); // FREE EVERYTHING
        }
    }

    public void ProcessSeed(long seed) {

        jobData.strongholdPos = FindStronghold(jobData, seed);
        if (IsStrongholdGood(jobData.strongholdPos)) {} else return;
        if (!FindStructure(jobData, seed)) return;
        if (!IsStructureGood(jobData.structurePos,  jobData.strongholdPos)) return;



        Cubiomes.setupGenerator(jobData.generator, Cubiomes.MC_1_21(), 0);
        Cubiomes.applySeed(jobData.generator, Cubiomes.DIM_OVERWORLD(), seed);
        boolean validVillage = Cubiomes.isViableStructurePos(jobData.structure,
                jobData.generator,
                jobData.structurePos.getBlockX(),
                jobData.structurePos.getBlockZ(),
                0) != 0;

        if (!validVillage){return;}
        FindSpawn();
        if (!IsSpawnGood(jobData.spawnPos, jobData.strongholdPos)){
            return;
        }
        out.println("seed : " + seed + " has a good stronghold at: "
                +  jobData.strongholdPos.Print()
                + " with a village at: "
                + jobData.structurePos.Print() +
                " spawn at: "
                + jobData.spawnPos.Print());

        out.println(jobData.structurePos.PrintTp());
    }

    private void FindSpawn() {
        jobData.memSpawnPos = Cubiomes.getSpawn(jobData.arena, jobData.generator);
        jobData.spawnPos.setFromBlock(jobData.memSpawnPos.get(ValueLayout.JAVA_INT, 0), jobData.memSpawnPos.get(ValueLayout.JAVA_INT, 4));
    }

    private boolean IsSpawnGood(Position spawnPos, Position strongholdPos){
       if (Position.Distance(spawnPos, strongholdPos) > MAX_SPAWN_DISTANCE_FROM_STRONGHOLD){
           return false;
       } else return true;
    }

    public static boolean IsStrongholdGood(Position position) {
        if (Position.isWithinRange(position, MAX_STRONGHOLD_RANGE)) {return true;} else {return false;}
    }


    public static boolean FindStructure(JobData jobData, long seed){

        Cubiomes.getStructurePos(jobData.structure, jobData.version, seed, jobData.strongholdPos.VILgetRegionX(), jobData.strongholdPos.VILgetRegionZ(), jobData.pos);

        jobData.structurePos.setFromBlock(jobData.pos.get(ValueLayout.JAVA_INT, 0), jobData.pos.get(ValueLayout.JAVA_INT, 4));
        if (jobData.structurePos.isEmpty()) return false;
        return true;
    }

    public static boolean IsStructureGood(Position structurePos, Position StrongholdPos) {
        if (Position.Distance(structurePos, StrongholdPos) > MAX_DISTANCE_FROM_STRONGHOLD) {return false;}
        else {return true;}
    }





    public static Position FindStronghold(JobData data, long seed){

        data.memStrongholdPos = Cubiomes.initFirstStronghold(data.arena, data.generator, data.version, seed);


        int x = data.memStrongholdPos.get(ValueLayout.JAVA_INT, 0);
        int z = data.memStrongholdPos.get(ValueLayout.JAVA_INT, 4);
        return new Position(x, z);
    }

}
