package MessingAround;

public class position {
    int x;
    int z;
    public position(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public boolean isEmpty() {
        return x == 0 && z == 0;
    }
}
