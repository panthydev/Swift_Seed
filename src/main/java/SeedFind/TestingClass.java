package SeedFind;

import dev.xpple.cubiomes.*;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import static java.lang.System.out;


public class TestingClass {

    // private final static Arena arena = Arena.global();

    public static void Init(){
        CubiomesInit.load();
    }

    static void main(String[] args) {
        Init();
        long start = System.nanoTime();
        long seedAmount = Long.parseLong(args[0]);


        int version = Cubiomes.MC_1_21();

        Arena arena = Arena.global();
        MemorySegment generator = Generator.allocate(arena);

        Cubiomes.setupGenerator(generator, version, 0);

        for (int i = 0; i < seedAmount +1; i++) {
            Cubiomes.applySeed(generator, Cubiomes.DIM_OVERWORLD(), i);
            FindVillage(arena, version, i, generator);
        }
        long end = System.nanoTime();
        double seconds = (end - start) / 1_000_000_000.0;
        out.println(seedAmount + " Completed in Time: " + seconds + " seconds");
        out.println("seeds per second: " + seedAmount/seconds);

    }

    private static void FindVillage(Arena arena, int version, long seed, MemorySegment generator) {
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
            Position strongholdPos = FindStronghold(arena, version, seed, generator);

            if (!strongholdPos.IsEmpty()){
                out.println("Seed: " + seed + " has a village near spawn at: " + structureX + ", " + structureZ);
                out.println("stronghold pos: " + strongholdPos.x + " " + strongholdPos.z);
            }


        }
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


}

