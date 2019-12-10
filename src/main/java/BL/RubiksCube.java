package BL;

import Data.Action;
import Data.Highlight;
import Data.Pause;
import Data.Rotate;
import Data.Turn;
import Data.UnsetHighlight;
import Exceptions.InvalidNotationException;
import Util.Array3DUtil;
import Util.AxisUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthias
 */
public class RubiksCube {

    private int size = 3;

    private ArrayList<Action> turnQueue = new ArrayList<>();

    private Cubie[][][] cubies = new Cubie[size][size][size];

    // represents how many clockwise turns the cube made 
    // (for setFrontFace() and resetFrontFace())
    // the number represents the clockwise quaterturns (R = 1, B = 2, L = 3)
    private int frontfaceOffset = 0;

    private int nbTurns = 0;

    public RubiksCube(int size) {
        this.size = size;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    cubies[i][j][k] = new Cubie(i, j, k);
                }
            }
        }
    }

    /**
     * execute the turns passed as cubing notation
     *
     * @param notation the notation as a String array
     */
    public void performNotation(String notation) throws InvalidNotationException {

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

            Integer idx = AxisUtils.FACE_TO_IDX.get(turn);
            Character axis = AxisUtils.FACE_TO_AXIS.get(turn);
            if (idx == null || axis == null) {
                continue;
            }
            if(idx == 3) {
                rotate(new Rotate(axis, clockwise));
                continue;
            }
            // clockwise refers to when that face is up, so rotating the
            // bottom side clockwise is actually anticlockwise from the
            // perspective of the Array
            if (idx >= getSize() / 2) {
                clockwise = !clockwise;
            }

            for (int i = 0; i < nbTurns; i++) {
                turn(new Turn(axis, idx, clockwise));
            }
        }
    }

    /**
     * returns the index of the first action that is a turn before the passed
     * index or -1
     *
     * @param i
     */
    private int prevTurn(int idx) {
        for (int i = idx - 1; i >= 0; i--) {
            if (turnQueue.get(i) instanceof Turn) {
                return i;
            }
        }
        return -1;
    }

    /**
     * returns the index of the first action that is a turn after the passed
     * index or -1
     *
     * @param i
     */
    private int nextTurn(int idx) {
        for (int i = idx + 1; i < turnQueue.size(); i++) {
            if (turnQueue.get(i) instanceof Turn) {
                return i;
            }
        }
        return -1;
    }

    /**
     * removes any unnecessary turns (R R', R2 R R, etc)
     */
    public void optimizeTurns() {
        int size = 0;
        while (turnQueue.size() != size) {
            size = turnQueue.size();
            for (int i = nextTurn(-1); i >= 0; i = nextTurn(i)) {
                Turn cur = (Turn) turnQueue.get(i);
                int prevIdx = prevTurn(i);
                int prevprevIdx = prevTurn(prevIdx);
                if (prevprevIdx < 0) {
                    continue;
                }
                Turn prev = (Turn) turnQueue.get(prevIdx);
                Turn prevprev = (Turn) turnQueue.get(prevprevIdx);
                if (cur.axis != prev.axis
                        || cur.idx != prev.idx) {
                    continue;
                }

                // the turns cancel each other out
                if (cur.clockwise != prev.clockwise) {
                    turnQueue.remove(i);
                    turnQueue.remove(prevIdx);
                    i -= 2;
                } // there are 3 turns that are the same
                else if (cur.axis == prevprev.axis
                        && cur.idx == prevprev.idx
                        && cur.clockwise == prevprev.clockwise) {
                    // replace with one turn in the opposite direction
                    turnQueue.set(i, new Turn(cur.axis, cur.idx, !cur.clockwise));
                    turnQueue.remove(prevIdx);
                    turnQueue.remove(prevprevIdx);
                    i -= 2;
                }
            }
        }
    }
    
    public Turn applyFrontFaceOffset(Turn turn) {
        for (int i = 0; i < frontfaceOffset; i++) {
            if (turn.axis == 'X' && turn.idx == 0) {
                turn = new Turn('Z', 2, !turn.clockwise);
            } else if (turn.axis == 'Z' && turn.idx == 2) {
                turn = new Turn('X', turn.idx, turn.clockwise);
            } else if (turn.axis == 'X' && turn.idx == 2) {
                turn = new Turn('Z', 0, !turn.clockwise);
            } else if (turn.axis == 'Z' && turn.idx == 0) {
                turn = new Turn('X', turn.idx, turn.clockwise);
            }
        }
        return turn;
    }

    public void turn(Turn turn) {
        turn = applyFrontFaceOffset(turn);
        turnQueue.add(turn);
        cubies = Array3DUtil.rotateArray(cubies, turn, size);
    }
    
    public void rotate(Rotate rotate) {
        turnQueue.add(rotate);
        cubies = Array3DUtil.rotateArray(cubies, rotate, size);
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
    }

    public void highlight(int x, int y, int z) {
        unsetHighlight();
        performAction(new Highlight(x, y, z));
    }

    public void highlightMultiple(int x, int y, int z) {
        performAction(new Highlight(x, y, z));
    }

    public void unsetHighlight() {
        performAction(new UnsetHighlight());
    }

    public void pause(long ms) {
        performAction(new Pause(ms));
    }

    public char getFrontFace() {
        char[] faces = {'F', 'R', 'B', 'L'};
        return faces[frontfaceOffset];
    }

    public void resetTurns() {
        nbTurns = 0;
    }

    public List<Action> getAndClearTurnQueue() {
        List<Action> actions = new ArrayList<>();
        actions.addAll(turnQueue);
        turnQueue.clear();
        return actions;
    }

}
