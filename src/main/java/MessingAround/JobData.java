package MessingAround;

import dev.xpple.cubiomes.Cubiomes;
import dev.xpple.cubiomes.Generator;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Random;
import java.util.random.RandomGenerator;

public class JobData {
    MemorySegment generator;
    Arena arena;
    long seedAmount;
    int version;

    MemorySegment pos;
 //   MemorySegment spawn;
    Position spawnPos;
    Position structurePos;
    int structure = Cubiomes.Village();

    public JobData(Arena arena, long seedAmount, int version ) {
        generator = Generator.allocate(arena);
        this.arena = arena;
        this.seedAmount = seedAmount;
        this.version = version;
        this.pos = arena.allocate(2 * Integer.BYTES);
        this.spawnPos = new Position(0, 0);
        this.structurePos = new Position(0, 0);

        Cubiomes.setupGenerator(generator, version, 0);
    }


}
