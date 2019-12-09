
package BL;

import Data.Direction;
import Util.Rotatable;

public class Cubie implements Rotatable {
    private Direction startPosition;
    private Direction position;
    private Direction faceX, faceY, faceZ;
    private char startFaceX, startFaceY, startFaceZ;

    public Cubie(int x, int y, int z) {
        init(x, y, z);
    }

    private void init(int x, int y, int z) {
        startPosition = new Direction(x, y, z);

        // shifted so the center is 0. otherwise rotation doesn't work
        position = new Direction(x - 1, y - 1, z - 1);

        faceX = x == 0 ? new Direction(-1, 0, 0) : new Direction(1, 0, 0);
        faceY = y == 0 ? new Direction(0, -1, 0) : new Direction(0, 1, 0);
        faceZ = z == 0 ? new Direction(0, 0, -1) : new Direction(0, 0, 1);
        startFaceX = x == 0 ? 'L' : 'R';
        startFaceY = y == 0 ? 'D' : 'U';
        startFaceZ = z == 0 ? 'B' : 'F';
    }

    public void rotate(char axis) {
        switch (axis) {
            case 'X':
                faceX.rotateX();
                faceY.rotateX();
                faceZ.rotateX();
                position.rotateX();
                break;
            case 'Y':
                faceX.rotateY();
                faceY.rotateY();
                faceZ.rotateY();
                position.rotateY();
                break;
            case 'Z':
                faceX.rotateZ();
                faceY.rotateZ();
                faceZ.rotateZ();
                position.rotateZ();
                break;
        }
    }

    public int getStartX() {
        return startPosition.getX();
    }

    public int getStartY() {
        return startPosition.getY();
    }

    public int getStartZ() {
        return startPosition.getZ();
    }

    public int getX() {
        return position.getX() + 1;
    }

    public int getY() {
        return position.getY() + 1;
    }

    public int getZ() {
        return position.getZ() + 1;
    }

    public boolean isInCorrectPosition() {
        return getX() == getStartX()
                && getY() == getStartY()
                && getZ() == getStartZ();
    }

    public boolean isSolved() {
        return isInCorrectPosition()
                && faceX.getX() == (startPosition.getX() == 0 ? -1 : 1)
                && faceY.getY() == (startPosition.getY() == 0 ? -1 : 1)
                && faceZ.getZ() == (startPosition.getZ() == 0 ? -1 : 1);
    }

    /**
     * What face is the cubie on, specifically the sticker that normally lies on
     * the passed axis
     *
     * @param axis
     * @return
     */
    public char getFace(char axis) {
        Direction dir = null;

        switch (axis) {
            case 'X':
                dir = faceX;
                break;
            case 'Y':
                dir = faceY;
                break;
            case 'Z':
                dir = faceZ;
                break;
        }

        if (dir.getX() == -1) {
            return 'L';
        }
        if (dir.getX() == 1) {
            return 'R';
        }
        if (dir.getY() == -1) {
            return 'D';
        }
        if (dir.getY() == 1) {
            return 'U';
        }
        if (dir.getZ() == -1) {
            return 'B';
        }
        return 'F';
    }

    /**
     * What face the cubie started on, specifically the sticker that lies on the
     * passed axis
     *
     * @param axis
     * @return
     */
    public char getStartFace(char axis) {
        switch (axis) {
            case 'X':
                return startFaceX;
            case 'Y':
                return startFaceY;
            default:
                return startFaceZ;
        }
    }
}
