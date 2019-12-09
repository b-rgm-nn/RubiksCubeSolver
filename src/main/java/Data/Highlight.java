
package Data;

public class Highlight extends Action {
    public final int x, y, z;

    public Highlight(int x, int y, int z) {
        super(Actions.HIGHLIGHT);
        this.x = x;
        this.y = y;
        this.z = z;
    }
}