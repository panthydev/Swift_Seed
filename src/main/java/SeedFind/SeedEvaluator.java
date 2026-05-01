package SeedFind;

import dev.xpple.cubiomes.Cubiomes;

import java.lang.foreign.ValueLayout;

public class SeedEvaluator {



    public JobData jobData;
    public Conditions conditions;

    public ResultHandler resultHandler;


    public SeedEvaluator(JobData jobData, Conditions conditions, ResultHandler resultHandler) {
        this.jobData = jobData;
        this.conditions = conditions;
        this.resultHandler = resultHandler;
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
        SendResult(seed, jobData.strongholdPos, jobData.structurePos,  jobData.spawnPos);
        return;
    }

    private void FindSpawn() {
        jobData.memSpawnPos = Cubiomes.getSpawn(jobData.arena, jobData.generator);
        jobData.spawnPos.setFromChunk(jobData.memSpawnPos.get(ValueLayout.JAVA_INT, 0), jobData.memSpawnPos.get(ValueLayout.JAVA_INT, 4));
    }

    private boolean IsSpawnGood(Position spawnPos, Position strongholdPos){
        if (Position.Distance(spawnPos, strongholdPos) > conditions.maxPlayerSpawnDistFromStronghold){
            return false;
        } else return true;
    }

    private boolean IsStrongholdGood(Position position) {
        if (Position.isWithinRange(position, conditions.maxStrongholdRangeFromOrigin)) {return true;} else {return false;}
    }


    private boolean FindStructure(JobData jobData, long seed){

        Cubiomes.getStructurePos(jobData.structure, jobData.version, seed, jobData.strongholdPos.VILgetRegionX(), jobData.strongholdPos.VILgetRegionZ(), jobData.pos);

        jobData.structurePos.setFromBlock(jobData.pos.get(ValueLayout.JAVA_INT, 0), jobData.pos.get(ValueLayout.JAVA_INT, 4));
        if (jobData.structurePos.isEmpty()) return false;
        return true;
    }

    private boolean IsStructureGood(Position structurePos, Position StrongholdPos) {
        if (Position.Distance(structurePos, StrongholdPos) > conditions.maxVillageDistFromStronghold) {return false;}
        else {return true;}
    }


    private Position FindStronghold(JobData data, long seed){

        data.memStrongholdPos = Cubiomes.initFirstStronghold(data.arena, data.generator, data.version, seed);


        int x = data.memStrongholdPos.get(ValueLayout.JAVA_INT, 0);
        int z = data.memStrongholdPos.get(ValueLayout.JAVA_INT, 4);
        return new Position(x, z);
    }

    private void SendResult(long seed, Position strong, Position village, Position spawn){
        Result result =new Result(true, seed, strong, village, spawn);
        resultHandler.handleResult(result);
    }
}
