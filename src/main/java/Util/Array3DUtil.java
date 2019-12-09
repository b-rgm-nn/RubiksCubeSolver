package Util;

import Data.Turn;

public class Array3DUtil {

    public static <T extends Rotatable> T[][][] rotateArray(T[][][] arr, Turn turn, int size) {
        // anticlockwise is equivalent to 3 clockwise turns
        if (!turn.clockwise) {
            Turn clockwise = new Turn(turn.axis, turn.idx, true);
            arr = rotateArray(arr, clockwise, size);
            arr = rotateArray(arr, clockwise, size);
            arr = rotateArray(arr, clockwise, size);
            return arr;
        }
        T[][][] newArray = (T[][][]) new Rotatable[size][size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    newArray[i][j][k] = arr[i][j][k];
                }
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                switch (turn.axis) {
                    case 'X':
                        newArray[turn.idx][i][j] = arr[turn.idx][j][size - 1 - i];
                        newArray[turn.idx][i][j].rotate(turn.axis);
                        newArray[turn.idx][i][j].rotate(turn.axis);
                        newArray[turn.idx][i][j].rotate(turn.axis);
                        break;
                    case 'Y':
                        newArray[j][turn.idx][size - 1 - i] = arr[i][turn.idx][j];
                        newArray[j][turn.idx][size - 1 - i].rotate(turn.axis);
                        newArray[j][turn.idx][size - 1 - i].rotate(turn.axis);
                        newArray[j][turn.idx][size - 1 - i].rotate(turn.axis);
                        break;
                    case 'Z':
                        newArray[i][j][turn.idx] = arr[j][size - 1 - i][turn.idx];
                        newArray[i][j][turn.idx].rotate(turn.axis);
                        newArray[i][j][turn.idx].rotate(turn.axis);
                        newArray[i][j][turn.idx].rotate(turn.axis);
                        break;
                }
            }
        }
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    arr[x][y][z] = newArray[x][y][z];
                }
            }
        }
        return arr;
    }
}
