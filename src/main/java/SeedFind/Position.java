package SeedFind;

public class Position {
    int x;
    int z;

    public static final int VILLAGE_REGION_SIZE = 544;

    public Position(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public static boolean isWithinRange(Position position, long range) {
        long rangeSquared = range * range;
        return (position.x * position.x + position.z * position.z <= rangeSquared);
    }

    public String Print() {
        return " {" + x + ", " + z + "}";
    }

    /**
     * creates a string with the teleport command to tp to this position
     *
     * @return string teleport command
     */
    public String PrintTp() {
        return "/teleport @a " + x + " 200" + " " + z;
    }

    /**
     * Calculates distance between two points/block coordinates
     *
     * @param position1 first position
     * @param position2 second position
     * @return the block distance between two block coordinates
     */
    public static long Distance(Position position1, Position position2) {
        return Math.abs(position2.x - position1.x) + Math.abs(position2.z - position1.z);
    }

    /**
     *
     * @return true if position is (0.0) else false
     */
    public boolean IsEmpty() {
        return x == 0 && z == 0;
    }

    /**
     * sets internal block coordinates directly from block coordinates
     */
    public void SetFromBlock(int x, int z) {
        this.x = x;
        this.z = z;
    }

    /**
     * sets internal block coordinates directly from Chunk coordinates
     */
    public void SetFromChunk(int x, int z) {
        this.x = x << 4;
        this.z = z << 4;
    }

    /**
     * sets internal block coordinates directly from Region coordinates
     */
    public void SetFromRegion(int x, int z) {
        this.x = x << 9;
        this.z = z << 9;
    }

    /**
     * Gets X block coordinate directly from internal block coordinate
     */
    public int GetBlockX() {
        return x;
    }

    /**
     * Gets Z block coordinate directly from internal block coordinate
     */
    public int GetBlockZ() {
        return z;
    }

    /**
     * Gets X Chunk coordinate directly from internal block coordinate
     */
    public int GetChunkX() {
        return x >> 4;
    }

    /**
     * Gets Z Chunk coordinate directly from internal block coordinate
     */

    public int GetChunkZ() {
        return z >> 4;
    }

    /**
     * Gets X Region coordinate directly from internal block coordinate
     */

    public int GetRegionX() {
        return x >> 9;
    }

    /**
     * Gets Z Region coordinate directly from internal block coordinate
     */
    public int GetRegionZ() {
        return z >> 9;
    }


    /**
     * Sets internal block coordinates directly from village region coordinates
     *
     * @apiNote Village regions are calculated slightly different, Village regions are 34 chunks, not 32.
     */
    public void VillageSetFromRegion(int x, int z) {
        this.x = x * VILLAGE_REGION_SIZE;
        this.z = z * VILLAGE_REGION_SIZE;
    }

    /**
     * Gets Region X directly from internal block coordinates
     *
     * @apiNote Village regions are calculated slightly different, Village regions are 34 chunks, not 32.
     */

    public int VillageGetRegionX() {
        return (int) Math.floor((double) x / VILLAGE_REGION_SIZE);
    }

    /**
     * Gets Region Z directly from internal block coordinates
     *
     * @apiNote Village regions are calculated slightly different, Village regions are 34 chunks, not 32.
     */
    public int VillageGetRegionZ() {
        return (int) Math.floor((double) z / VILLAGE_REGION_SIZE);
    }

}
