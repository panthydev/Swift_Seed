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

    for (currSeedAttempt = 0; currSeedAttempt <= seedAmount; currSeedAttempt++) { ProcessSeed(currSeedAttempt);}
    }

    private void init() {
        generator = Generator.allocate(arena);
        Cubiomes.setupGenerator(generator, version, 0);

    }

    public void ProcessSeed(long seed) {

        Cubiomes.applySeed(generator, Cubiomes.DIM_OVERWORLD(), seed);
        int structure = Cubiomes.Village();

        MemorySegment spawn = Cubiomes.getSpawn(arena, generator);
        MemorySegment pos = arena.allocate(2 * Integer.BYTES); // 8 bytes


        int spawnX = spawn.get(ValueLayout.JAVA_INT, 0);
        int spawnZ = spawn.get(ValueLayout.JAVA_INT, 4);

        int spawnChunkX = spawnX >> 4;
        int spawnChunkZ = spawnZ >> 4;

        int spawnRegionX = spawnChunkX >> 5;
        int spawnRegionZ = spawnChunkZ >> 5;

        Cubiomes.getStructurePos(structure, version, seed, spawnRegionX, spawnRegionZ, pos);

        int structureX = pos.get(ValueLayout.JAVA_INT, 0);
        int structureZ = pos.get(ValueLayout.JAVA_INT, 4);



        boolean validVillage = Cubiomes.isViableStructurePos(structure, generator, structureX, structureZ, 0) != 0;

        if (validVillage){
            position strongholdPos = FindStronghold(arena, version, seed, generator);

            if (!strongholdPos.isEmpty()){
                out.println("Seed: " + seed + " has a village near spawn at: " + structureX + ", " + structureZ);
                out.println("stronghold pos: " + strongholdPos.x + " " + strongholdPos.z);
            }


        }
    }

    public static position FindStronghold(Arena arena, int version, long seed, MemorySegment generator){

        MemorySegment strongholdPos = Cubiomes.initFirstStronghold(arena, generator, version, seed);
        int maxBlockRange = 1050;

        int x = strongholdPos.get(ValueLayout.JAVA_INT, 0);
        int z = strongholdPos.get(ValueLayout.JAVA_INT, 4);

        if (x > maxBlockRange || z > maxBlockRange  || x < -maxBlockRange || z < -maxBlockRange){
            return new position(0, 0);
        } else {
            return new position(x, z);
        }

    }


    private long getRandomSeed(){
        return  rng.nextLong();
    }
}
