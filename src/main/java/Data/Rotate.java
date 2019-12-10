
package Data;

public class Rotate extends Action {
    public final char axis;
    public final boolean clockwise;

    public Rotate(char axis, boolean clockwise) {
        super(Actions.ROTATE);
        this.axis = axis;
        this.clockwise = clockwise;
    }
}
