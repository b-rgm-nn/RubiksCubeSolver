
package Util;

import java.util.HashMap;

public class AxisUtils {
    public static final HashMap<String, Character> FACE_TO_AXIS = new HashMap<>();
    public static final HashMap<String, Integer> FACE_TO_IDX = new HashMap<>();

    static {
        FACE_TO_AXIS.put("R", 'X');
        FACE_TO_AXIS.put("L", 'X');
        FACE_TO_AXIS.put("U", 'Y');
        FACE_TO_AXIS.put("D", 'Y');
        FACE_TO_AXIS.put("F", 'Z');
        FACE_TO_AXIS.put("B", 'Z');

        FACE_TO_AXIS.put("X", 'X');
        FACE_TO_AXIS.put("Y", 'Y');
        FACE_TO_AXIS.put("Z", 'Z');

        FACE_TO_AXIS.put("M", 'X');
        FACE_TO_AXIS.put("E", 'Y');
        FACE_TO_AXIS.put("S", 'Z');





        FACE_TO_IDX.put("R", 2);
        FACE_TO_IDX.put("L", 0);
        FACE_TO_IDX.put("U", 2);
        FACE_TO_IDX.put("D", 0);
        FACE_TO_IDX.put("F", 2);
        FACE_TO_IDX.put("B", 0);

        FACE_TO_IDX.put("X", 3);
        FACE_TO_IDX.put("Y", 3);
        FACE_TO_IDX.put("Z", 3);

        FACE_TO_IDX.put("M", 1);
        FACE_TO_IDX.put("E", 1);
        FACE_TO_IDX.put("S", 1);
    }
}
