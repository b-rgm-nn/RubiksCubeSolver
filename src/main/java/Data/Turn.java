
package Data;

public class Turn extends Action{
    public final char axis;
    public final int idx;
    public final boolean clockwise;

    public Turn(char axis, int idx, boolean clockwise) {
        super(Actions.TURN);
        this.axis = axis;
        this.idx = idx;
        this.clockwise = clockwise;
    }
}
