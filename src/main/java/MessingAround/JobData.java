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
    MemorySegment memStrongholdPos;
    Position spawnPos;
    Position structurePos;
    Position strongholdPos;
    int structure = Cubiomes.Village();

    public JobData(long seedAmount, int version ) {
        this.seedAmount = seedAmount;
        this.version = version;
        this.spawnPos = new Position(0, 0);
        this.structurePos = new Position(0, 0);
        this.strongholdPos = new Position(0, 0);
    }


    public void init() {
        arena = Arena.ofConfined();
        generator = Generator.allocate(arena);
        this.pos = arena.allocate(2 * Integer.BYTES);

        Cubiomes.setupGenerator(generator, version, 0);

    }



}
