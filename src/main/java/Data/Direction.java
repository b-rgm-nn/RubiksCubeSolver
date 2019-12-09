package Data;

public class Direction {
    private int x;
    private int y;
    private int z;

    public Direction(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void rotateX() {
        int temp = y;
        y = z;
        z = -temp;
    }

    public void rotateY() {
        int temp = z;
        z = x;
        x = -temp;
    }

    public void rotateZ() {
        int temp = x;
        x = y;
        y = -temp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
    
    
}
