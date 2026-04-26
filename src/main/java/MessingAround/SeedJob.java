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

    MemorySegment generator;
    Arena arena;
    RandomGenerator rng = new Random();
    long seedAmount;
    int version;
    public long currSeedAttempt;
    SeedJob(int version, Arena arena, long  seedAmount ) {
        this.version = version;
        this.arena = arena;
        this.seedAmount = seedAmount;
    }
    @Override
    public void run() {
        init();
        MemorySegment pos = arena.allocate(2 * Integer.BYTES); // 8 bytes
        Position spawnPos = new Position(0, 0);
        Position structurePos = new Position(0, 0);



    for (currSeedAttempt = 0; currSeedAttempt <= seedAmount; currSeedAttempt++) { ProcessSeed(currSeedAttempt, pos, spawnPos, structurePos);}
    }

    private void init() {
        generator = Generator.allocate(arena);
        Cubiomes.setupGenerator(generator, version, 0);

    }

    public void ProcessSeed(long seed, MemorySegment pos, Position spawnPos, Position structrurePos) {

        Cubiomes.applySeed(generator, Cubiomes.DIM_OVERWORLD(), seed);
        int structure = Cubiomes.Village();

        MemorySegment spawn = Cubiomes.getSpawn(arena, generator);

        spawnPos.setFromBlock(spawn.get(ValueLayout.JAVA_INT, 0), spawn.get(ValueLayout.JAVA_INT, 4));


        Cubiomes.getStructurePos(structure, version, seed, spawnPos.getRegionX(), spawnPos.getRegionZ(), pos);

        structrurePos.setFromBlock(pos.get(ValueLayout.JAVA_INT, 0), pos.get(ValueLayout.JAVA_INT, 4));



        boolean validVillage = Cubiomes.isViableStructurePos(structure, generator, structrurePos.getBlockX(), structrurePos.getBlockZ(), 0) != 0;



        Position strongholdPos = FindStronghold(arena, version, seed, generator);
        /*

         if (validVillage){

        }
a
        if (!strongholdPos.isEmpty()){
            out.println("Seed: " + seed + " has a village near spawn at: " + structureX + ", " + structureZ);
            out.println("stronghold pos: " + strongholdPos.x + " " + strongholdPos.z);


        }
*/


    }

    public static Position FindStronghold(Arena arena, int version, long seed, MemorySegment generator){

        MemorySegment strongholdPos = Cubiomes.initFirstStronghold(arena, generator, version, seed);

        int maxBlockRange = 1050;

        int x = strongholdPos.get(ValueLayout.JAVA_INT, 0);
        int z = strongholdPos.get(ValueLayout.JAVA_INT, 4);

        if (x > maxBlockRange || z > maxBlockRange  || x < -maxBlockRange || z < -maxBlockRange){
            return new Position(0, 0);
        } else {
            return new Position(x, z);
        }

    }


    private long getRandomSeed(){
        return  rng.nextLong();
    }
}
