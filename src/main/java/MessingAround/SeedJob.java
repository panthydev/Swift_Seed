package MessingAround;

import dev.xpple.cubiomes.Cubiomes;
import dev.xpple.cubiomes.Generator;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Random;
import java.util.random.RandomGenerator;

import static java.lang.System.out;

public class SeedJob implements Runnable {
    public JobData jobData;
    public long currSeedAttempt;
    public static final int MAX_STRONGHOLD_RANGE = 3000;
    public static final int MAX_DISTANCE_FROM_STRONGHOLD = 300;
    SeedJob(int version, Arena arena, long  seedAmount ) {
        jobData = new JobData(arena, seedAmount, version);
    }
    @Override
    public void run() {


    for (currSeedAttempt = 0; currSeedAttempt <= jobData.seedAmount; currSeedAttempt++) {
        ProcessSeed(currSeedAttempt); }
    }

    public void ProcessSeed(long seed) {


        Position strongholdPos = FindStronghold(jobData, seed);
        if (IsStrongholdGood(strongholdPos)) {} else return;
        FindStructure(jobData, seed);
        if (IsStructureGood(jobData.structurePos,  strongholdPos))
        {out.println("good stronghold at: " +  strongholdPos.Print() + " with a village at: " + jobData.structurePos.Print());} else return;








        Cubiomes.applySeed(jobData.generator, Cubiomes.DIM_OVERWORLD(), seed);
        MemorySegment spawn = Cubiomes.getSpawn(jobData.arena, jobData.generator);
        jobData.spawnPos.setFromBlock(spawn.get(ValueLayout.JAVA_INT, 0), spawn.get(ValueLayout.JAVA_INT, 4));


     //   boolean validVillage = Cubiomes.isViableStructurePos(jobData.structure, jobData.generator, jobData.structurePos.getBlockX(), jobData.structurePos.getBlockZ(), 0) != 0;




/*

         if (validVillage){

        }

        if (!strongholdPos.isEmpty()){
            out.println("Seed: " + seed + " has a village near spawn at: " + jobData.structurePos.x + ", " + jobData.structurePos.z);
            out.println("stronghold pos: " + strongholdPos.x + " " + strongholdPos.z);
        }

*/

    }

    public static boolean IsStrongholdGood(Position position) {
        if (Position.isWithinRange(position, MAX_STRONGHOLD_RANGE)) {return true;} else {return false;}
    }

    public static void FindStructure(JobData jobData, long seed){

        Cubiomes.getStructurePos(jobData.structure, jobData.version, seed, jobData.spawnPos.getRegionX(), jobData.spawnPos.getRegionZ(), jobData.pos);

        jobData.structurePos.setFromBlock(jobData.pos.get(ValueLayout.JAVA_INT, 0), jobData.pos.get(ValueLayout.JAVA_INT, 4));
    }

    public static boolean IsStructureGood(Position structurePos, Position StrongholdPos) {
        if (Position.Distance(structurePos, StrongholdPos) > MAX_DISTANCE_FROM_STRONGHOLD) {return false;}
        else {return true;}
    }





    public static Position FindStronghold(JobData data, long seed){

        MemorySegment strongholdPos = Cubiomes.initFirstStronghold(data.arena, data.generator, data.version, seed);

        int x = strongholdPos.get(ValueLayout.JAVA_INT, 0);
        int z = strongholdPos.get(ValueLayout.JAVA_INT, 4);
        return new Position(x, z);
    }

}
