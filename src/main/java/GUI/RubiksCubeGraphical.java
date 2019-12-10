package GUI;

import BL.RubiksCube;
import Data.Action;
import Data.Highlight;
import Data.Pause;
import Data.Rotate;
import Data.Turn;
import Util.Array3DUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Group;

public class RubiksCubeGraphical extends Group {

    private final double turnTime = 256;

    public static final double SCALE = 30.0;
    private int size;
    private double offset;
    private ArrayList<Action> turnQueue = new ArrayList<>();
    private Thread graphicThread;

    private CubieGraphical[][][] cubies;
    private List<CubieGraphical> highlighted = new ArrayList<>();

    public RubiksCubeGraphical(int size) {
        this.size = size;
        this.offset = size / 2.0 - 0.5;
        cubies = new CubieGraphical[size][size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    cubies[i][j][k] = new CubieGraphical(i, j, k, offset);
                    getChildren().add(cubies[i][j][k]);
                }
            }
        }
    }

    public void performActions(List<Action> a) {
        turnQueue.addAll(a);
        if (graphicThread != null && graphicThread.isAlive()) {
            return;
        }

        graphicThread = new Thread(() -> {
            while (!turnQueue.isEmpty()) {
                Action action = turnQueue.remove(0);

                switch (action.getAction()) {
                    case PAUSE:
                        Pause pause = (Pause) action;
                        try {
                            Thread.sleep(pause.ms);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case HIGHLIGHT:
                        Highlight h = (Highlight) action;
                        highlighted.add(cubies[h.x][h.y][h.z]);
                        for (int i = 0; i < cubies.length; i++) {
                            for (int j = 0; j < cubies[0].length; j++) {
                                for (int k = 0; k < cubies[0][0].length; k++) {
                                    cubies[i][j][k].unsetHighlight();
                                }
                            }
                        }
                        for (CubieGraphical cubie : highlighted) {
                            cubie.highlight();
                        }
                        break;
                    case UNSET_HIGHLIGHT:
                        highlighted.clear();
                        for (int i = 0; i < cubies.length; i++) {
                            for (int j = 0; j < cubies[0].length; j++) {
                                for (int k = 0; k < cubies[0][0].length; k++) {
                                    // highlight sets it to opaque
                                    cubies[i][j][k].highlight();
                                }
                            }
                        }
                        break;
                    case TURN:
                        Turn turn = (Turn) action;
                        cubies = Array3DUtil.rotateArray(cubies, turn, size);
                        // for each individual cubie, turn gradually
                        rotate(turn.axis, turn.idx, turn.clockwise);
                        try {
                            Thread.sleep((long) turnTime + 50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(RubiksCube.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case ROTATE:
                        Rotate rotate = (Rotate) action;
                        cubies = Array3DUtil.rotateArray(cubies, rotate, size);
                        for (int i = 0; i < size; i++) {
                            rotate(rotate.axis, i, rotate.clockwise);
                        }
                        try {
                            Thread.sleep((long) turnTime + 50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(RubiksCube.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                }
            }
        });
        graphicThread.start();
    }

    /**
     * rotate one layer of the rubiks cube by one step (90 / turnResolution)
     *
     * @param axis Which axis to turn around
     * @param idx Which layer along that axis to turn (e.g. 0 = top layer, 2 =
     * bottom layer)
     * @param clockwise true = turn clockwise, false = turn anticlockwise (from
     * a cubing perspective, so clockwise for the top layer is anticlockwise for
     * the bottom layer)
     */
    private void rotate(char axis, int idx, boolean clockwise) {
        int dir = clockwise ? 1 : -1;
        double angle = dir * 90.0;
        // for each individual cubie, turn by some angle
        for (int j = 0; j < size; j++) {
            for (int k = 0; k < size; k++) {
                switch (axis) {
                    case 'X':
                        cubies[idx][j][k].rotate(angle, axis, turnTime);
                        break;
                    case 'Y':
                        cubies[j][idx][k].rotate(angle, axis, turnTime);
                        break;
                    case 'Z':
                        cubies[j][k][idx].rotate(angle, axis, turnTime);
                        break;
                }
            }
        }
    }
}
