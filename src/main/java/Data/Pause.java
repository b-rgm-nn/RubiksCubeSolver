
package Data;

public class Pause extends Action {
    public final long ms;

    public Pause(long ms) {
        super(Actions.PAUSE);
        this.ms = ms;
    }
}
