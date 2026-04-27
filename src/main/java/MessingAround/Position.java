package MessingAround;

public class Position {
    int x;
    int z;
    public Position(int x, int z) {
        this.x = x;
        this.z = z;
    }

 public static boolean isWithinRange(Position position, long range) {
        long rangeSquared = range * range;
        return (position.x * position.x + position.z * position.z <= rangeSquared);
 }

 public String Print(){
        return " {" + x + ", " + z + "}";
 }

 public String PrintTp(){
     return "/teleport @a " + x + " 200" + " " + z;
 }

 public static long Distance(Position position1, Position position2) {
        return Math.abs(position2.x - position1.x) + Math.abs(position2.z - position1.z);
 }

    public boolean isEmpty() {
        return x == 0 && z == 0;
    }

    public void setFromBlock(int x, int z) {
        this.x = x;
        this.z = z;
    }
    public void setFromChunk(int x, int z) {
        this.x = x << 4;
        this.z = z << 4;
    }
    public void setFromRegion(int x, int z) {
        this.x = x << 9;
        this.z = z << 9;
    }
    public int  getBlockX() {
        return x;
    }
    public int getBlockZ() {
        return z;
    }

    public int getChunkX() {
        return x >> 4;
    }

    public int getChunkZ() {
        return z >> 4;
    }

    public int getRegionX() {
        return x >> 9;
    }
    public int getRegionZ() {
        return z >> 9;
    }



}
