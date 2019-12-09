package GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Group;

/**
 *
 * @author Matthias
 */
public class RubiksCube extends Group {

    private final double turnTime = 256;

    public static final double SCALE = 30.0;

    private static final boolean GRAPHIC = true;
    private static final boolean LOGICAL = false;

    private int size = 3;
    private double offset = size / 2.0 - 0.5;

    private Thread graphicThread;
    private ArrayList<Action> turnQueue = new ArrayList<>();

    private final Cubie[][][] cubies = new Cubie[size][size][size];
    // The graphical representation is always behind the logical position
    // so to avoid problems there's an extra array
    private final Cubie[][][] graphicCubies = new Cubie[size][size][size];

    // represents how many clockwise turns the cube made 
    // (for setFrontFace() and resetFrontFace())
    // the number represents the clockwise quaterturns (R = 1, B = 2, L = 3)
    private int frontfaceOffset = 0;

    private static final HashMap<String, Character> FACE_TO_AXIS = new HashMap<>();
    private static final HashMap<String, Integer> FACE_TO_IDX = new HashMap<>();

    private List<Cubie> highlighted = new ArrayList<>();

    private int nbTurns = 0;

    static {
        FACE_TO_AXIS.put("R", 'X');
        FACE_TO_AXIS.put("L", 'X');
        FACE_TO_AXIS.put("U", 'Y');
        FACE_TO_AXIS.put("D", 'Y');
        FACE_TO_AXIS.put("F", 'Z');
        FACE_TO_AXIS.put("B", 'Z');

        FACE_TO_IDX.put("R", 2);
        FACE_TO_IDX.put("L", 0);
        FACE_TO_IDX.put("U", 2);
        FACE_TO_IDX.put("D", 0);
        FACE_TO_IDX.put("F", 2);
        FACE_TO_IDX.put("B", 0);
    }

    public class Action {

        public final static int TURN = 0;
        public final static int PAUSE = 1;
        public final static int HIGHLIGHT = 2;
        public final static int UNSET_HIGHLIGHT = 3;

        public int type;
        public char axis;
        public int idx;
        public boolean clockwise;
        public long ms = 1;
        public int x, y, z;

        public Action(char axis, int idx, boolean clockwise) {
            this.axis = axis;
            this.idx = idx;
            this.clockwise = clockwise;
        }

        public Action(long ms) {
            type = PAUSE;
            this.ms = ms;
        }

        public Action(int type) {
            this.type = type;
        }

        public Action(int x, int y, int z) {
            type = HIGHLIGHT;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public RubiksCube(int size) {
        this.size = size;
        offset = size / 2.0 - 0.5;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    cubies[i][j][k] = new Cubie(i, j, k, offset);
                    graphicCubies[i][j][k] = new Cubie(i, j, k, offset);
                    getChildren().add(graphicCubies[i][j][k]);
                }
            }
        }
    }

    /**
     * execute the turns passed as cubing notation
     *
     * @param notation the notation as a String array
     */
    public void performNotation(String notation) throws Exception {

        notation = notation.toUpperCase().trim();
        String[] turns = notation.split("[\\(\\)\\[\\]\\s]+");

        for (String turn : turns) {
            // clockwise
            // nbTurns
            // face

            boolean clockwise = !turn.contains("'");
            turn = turn.replace("'", "");

            int nbTurns = turn.contains("2") ? 2 : 1;
            turn = turn.replace("2", "");

            Integer idx = FACE_TO_IDX.get(turn);
            Character axis = FACE_TO_AXIS.get(turn);
            if (idx == null || axis == null) {
                continue;
            }
            // clockwise refers to when that face is up, so rotating the
            // bottom side clockwise is actually anticlockwise from the
            // perspective of the Array
            if (idx >= getSize() / 2) {
                clockwise = !clockwise;
            }

            for (int i = 0; i < nbTurns; i++) {
                rotate(axis, idx, clockwise);
            }
        }
    }

    /**
     * rotate one layer of the rubiks cube either clockwise or anticlockwise
     * with animation
     *
     * @param axis Which axis to turn around
     * @param idx Which layer along that axis to turn (e.g. 0 = top layer, 2 =
     * bottom layer)
     * @param clockwise true = turn clockwise, false = turn anticlockwise (from
     * a cubing perspective, so clockwise for the top layer is anticlockwise for
     * the bottom layer)
     */
    private void rotate(char axis, int idx, boolean clockwise) {

        for (int i = 0; i < frontfaceOffset; i++) {
            if (axis == 'X' && idx == 0) {
                axis = 'Z';
                idx = 2;
                clockwise = !clockwise;
            } else if (axis == 'Z' && idx == 2) {
                axis = 'X';
            } else if (axis == 'X' && idx == 2) {
                axis = 'Z';
                idx = 0;
                clockwise = !clockwise;
            } else if (axis == 'Z' && idx == 0) {
                axis = 'X';
            }
        }

        rotateArray(axis, idx, clockwise, LOGICAL);
        performAction(new Action(axis, idx, clockwise));
    }

    /**
     * The cube gets turned so the specified face thereafter functions as the
     * front face. defaults to F if the specified face is U or D
     *
     *
     * @param face that will be the new front face
     */
    public void setFrontFace(char face) {
        switch (face) {
            case 'R':
                frontfaceOffset = 1;
                break;
            case 'B':
                frontfaceOffset = 2;
                break;
            case 'L':
                frontfaceOffset = 3;
                break;
            default:
                frontfaceOffset = 0;
        }

    }

    /**
     * set the frontface back to F
     */
    public void resetFrontFace() {
        setFrontFace('F');
    }

    /**
     * set the front face so that the specified edge is the right edge does
     * nothing if the coordinates don't lie on a vertical edge
     *
     * @param x the x coordinate of the edge
     * @param z the y coordinate of the edge
     */
    public void setRightEdge(int x, int z) {
        if (x == 0 && z == 0) {
            setFrontFace('B');
        }
        if (x == 2 && z == 0) {
            setFrontFace('R');
        }
        if (x == 0 && z == 2) {
            setFrontFace('L');
        }
        if (x == 2 && z == 2) {
            setFrontFace('F');
        }
    }

    /**
     * set the front face so the cubie lies on the right edge does nothing if
     * the cubie doesn't lie on a vertical edge
     *
     * @param cubie the cubie that will be on the right edge
     */
    public void setRightEdge(Cubie cubie) {
        setRightEdge(cubie.getX(), cubie.getZ());
    }

    /**
     * set the front face so the cubie lies on the front face. the cubie has to
     * lie on a vertical center slice. If it doesn't nothing happens
     *
     * @param cubie the cubie that will lie on the front face
     */
    public void setAsFrontFace(Cubie cubie) {
        if (cubie.getX() == 1 && cubie.getZ() == 0) {
            setFrontFace('B');
        }
        if (cubie.getX() == 1 && cubie.getZ() == 2) {
            setFrontFace('F');
        }
        if (cubie.getX() == 0 && cubie.getZ() == 1) {
            setFrontFace('L');
        }
        if (cubie.getX() == 2 && cubie.getZ() == 1) {
            setFrontFace('R');
        }
    }

    public Cubie getStartCubie(int startX, int startY, int startZ)
            throws Exception {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    if (cubies[i][j][k].getStartX() == startX
                            && cubies[i][j][k].getStartY() == startY
                            && cubies[i][j][k].getStartZ() == startZ) {
                        return cubies[i][j][k];
                    }
                }
            }
        }

        throw new Exception("Cubie not found");
    }

    public Cubie getCubie(int x, int y, int z) {
        return cubies[x][y][z];
    }

    public int getSize() {
        return size;
    }

    private void performAction(Action a) {
        turnQueue.add(a);

        if (graphicThread != null) {
            return;
        }

        graphicThread = new Thread(() -> {
            while (true) {
                if (turnQueue.isEmpty()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    continue;
                }

                Action action = turnQueue.remove(0);

                switch (action.type) {
                    case Action.PAUSE:
                        try {
                            Thread.sleep(action.ms);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Action.HIGHLIGHT:
                        highlighted.add(graphicCubies[action.x][action.y][action.z]);
                        for (int i = 0; i < cubies.length; i++) {
                            for (int j = 0; j < cubies[0].length; j++) {
                                for (int k = 0; k < cubies[0][0].length; k++) {
                                    graphicCubies[i][j][k].unsetHighlight();
                                }
                            }
                        }
                        for (Cubie cubie : highlighted) {
                            cubie.highlight();
                        }
                        break;
                    case Action.UNSET_HIGHLIGHT:
                        highlighted.clear();
                        for (int i = 0; i < cubies.length; i++) {
                            for (int j = 0; j < cubies[0].length; j++) {
                                for (int k = 0; k < cubies[0][0].length; k++) {
                                    // highlight sets it to opaque
                                    graphicCubies[i][j][k].highlight();
                                }
                            }
                        }
                        break;
                    case Action.TURN:
                        nbTurns++;
//                        System.out.println(nbTurns);
                        rotateArray(action.axis, action.idx, action.clockwise, GRAPHIC);
                        // for each individual cubie, turn gradually
                        rotateGraphical(action.axis, action.idx, action.clockwise);
                        try {
                            Thread.sleep((long) turnTime + 50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(RubiksCube.class.getName()).log(Level.SEVERE, null, ex);
                        }

                }
            }
        });
        graphicThread.start();
    }

    private Cubie[][][] copyArray(Cubie[][][] arr) {
        Cubie[][][] newArray = new Cubie[size][size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.arraycopy(arr[i][j], 0, newArray[i][j], 0, size);
            }
        }

        return newArray;
    }

    private void rotateArray(char axis, int idx, boolean clockwise, boolean graphical) {
        if (!clockwise) {
            rotateArray(axis, idx, true, graphical);
            rotateArray(axis, idx, true, graphical);
            rotateArray(axis, idx, true, graphical);
            return;
        }

        Cubie[][][] arr = cubies;
        if (graphical) {
            arr = graphicCubies;
        }

        Cubie[][][] newArray = copyArray(arr);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                switch (axis) {
                    case 'X':
                        newArray[idx][i][j] = arr[idx][j][size - 1 - i];
                        newArray[idx][i][j].rotate(axis);
                        newArray[idx][i][j].rotate(axis);
                        newArray[idx][i][j].rotate(axis);
                        break;
                    case 'Y':
                        newArray[j][idx][size - 1 - i] = arr[i][idx][j];
                        newArray[j][idx][size - 1 - i].rotate(axis);
                        newArray[j][idx][size - 1 - i].rotate(axis);
                        newArray[j][idx][size - 1 - i].rotate(axis);
                        break;
                    case 'Z':
                        newArray[i][j][idx] = arr[j][size - 1 - i][idx];
                        newArray[i][j][idx].rotate(axis);
                        newArray[i][j][idx].rotate(axis);
                        newArray[i][j][idx].rotate(axis);
                        break;
                }
            }
        }

        // can't use copyArray() because then arr doesn't refer to the same
        // array so the actual array (cubies/graphicCubies) doesn't get changed
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    arr[i][j][k] = newArray[i][j][k];
                }
            }
        }
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
    private void rotateGraphical(char axis, int idx, boolean clockwise) {
        int dir = clockwise ? 1 : -1;
        double angle = dir * 90.0;
        // for each individual cubie, turn by some angle
        for (int j = 0; j < size; j++) {
            for (int k = 0; k < size; k++) {
                switch (axis) {
                    case 'X':
                        graphicCubies[idx][j][k].rotateGraphical(angle, axis, turnTime);
                        break;
                    case 'Y':
                        graphicCubies[j][idx][k].rotateGraphical(angle, axis, turnTime);
                        break;
                    case 'Z':
                        graphicCubies[j][k][idx].rotateGraphical(angle, axis, turnTime);
                        break;
                }
            }
        }
    }

    /**
     * removes any unnecessary turns (R R', R2 R R, etc)
     *
     * @return true if something has changed, otherwise false
     */
    public boolean optimizeTurns() {
        boolean changed = false;

        // create a list with only the turns
        ArrayList<Action> turns = new ArrayList<>();
        for (Action action : turnQueue) {
            if (action.type == Action.TURN) {
                turns.add(action);
            }
        }

        for (int i = 3; i < turns.size(); i++) {
            if (turns.get(i).axis != turns.get(i - 1).axis
                    || turns.get(i).idx != turns.get(i - 1).idx) {
                continue;
            }

            // the turns cancel each other out
            if (turns.get(i).clockwise != turns.get(i - 1).clockwise) {
                turns.remove(i).type = Action.PAUSE;
                turns.remove(i - 1).type = Action.PAUSE;
                i -= 2;
                changed = true;
            } // there are 3 turns that are the same
            else if (turns.get(i).axis == turns.get(i - 2).axis
                    && turns.get(i).idx == turns.get(i - 2).idx
                    && turns.get(i).clockwise == turns.get(i - 2).clockwise) {
                // replace with one turn in the opposite direction
                turns.get(i).clockwise = !turns.get(i).clockwise;
                turns.remove(i - 1).type = Action.PAUSE;
                turns.remove(i - 2).type = Action.PAUSE;
                i -= 2;
                changed = true;
            }
        }

        return changed;
    }

    public void highlight(int x, int y, int z) {
        unsetHighlight();
        performAction(new Action(x, y, z));
    }

    public void highlightMultiple(int x, int y, int z) {
        performAction(new Action(x, y, z));
    }

    public void unsetHighlight() {
        performAction(new Action(Action.UNSET_HIGHLIGHT));
    }

    public void pause(long ms) {
        performAction(new Action(ms));
    }

    public char getFrontFace() {
        char[] faces = {'F', 'R', 'B', 'L'};
        return faces[frontfaceOffset];
    }

    public void resetTurns() {
        nbTurns = 0;
    }
}
