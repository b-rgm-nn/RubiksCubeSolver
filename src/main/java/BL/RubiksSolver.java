package BL;

import Exceptions.InvalidNotationException;
import GUI.OllAlgorithms;
import GUI.PllAlgorithms;
import GUI.RubiksCubeGraphical;
import java.util.Random;
import javafx.scene.Group;

/**
 *
 * @author Matthias
 */
public class RubiksSolver extends Group {

    private RubiksCube cube = new RubiksCube(3);
    private RubiksCubeGraphical graphicsCube = new RubiksCubeGraphical(3);

    public RubiksSolver() {
        getChildren().add(graphicsCube);
    }

    public void solveCenters() throws Exception {
        Cubie botX = cube.getStartCubie(0, 1, 1);
        Cubie botY = cube.getStartCubie(1, 0, 1);
        if (botY.getY() == 2) {
            performNotation("x x");
        } else if (botY.getX() == 0) {
            performNotation("z");
        } else if (botY.getX() == 2) {
            performNotation("z'");
        } else if (botY.getZ() == 0) {
            performNotation("x'");
        } else if (botY.getZ() == 2) {
            performNotation("x");
        }
        
        if(botX.getX() == 2) {
            performNotation("y y");
        }
        else if(botX.getZ() == 0) {
            performNotation("y");
        }
        else if(botX.getZ() == 2) {
            performNotation("y'");
        }
    }

    /**
     * Solve the bottom cross (classically the white cross)
     *
     * @throws Exception
     */
    public void solveCross() throws Exception {
        int[][] pos = {
            {1, 0, 0},
            {1, 0, 2},
            {0, 0, 1},
            {2, 0, 1}
        };
        for (int i = 0; i < 4; i++) {
            cube.pause(100);
            Cubie cubie = cube.getStartCubie(pos[i][0], pos[i][1], pos[i][2]);
            cube.unsetHighlight();
            cube.highlight(cubie.getX(), cubie.getY(), cubie.getZ());
            if (cubie.isSolved()) {
                continue;
            }

            // rotate cubie without disturbing cross
            if (cubie.isInCorrectPosition()) {
                cube.setFrontFace(cubie.getFace('Y'));
                cube.performNotation("F L' U' L F2");
                cube.resetFrontFace();
                continue;
            }

            // get cubie into the top layer
            if (cubie.getY() == 0) {
                cube.setAsFrontFace(cubie);
                cube.performNotation("F2");
                cube.resetFrontFace();
            }
            if (cubie.getY() == 1) {
                cube.setFrontFace(cubie.getFace('Y'));
                // The cube could be in either the left or the right slot
                cube.performNotation("R U R'");
                cube.performNotation("L' U' L");
                cube.resetFrontFace();
            }

            // orient cube so the bottom side points up
            if (cubie.getFace('Y') != 'U') {
                cube.setAsFrontFace(cubie);
                cube.performNotation("L F' L' F U'");
                cube.resetFrontFace();
            }
            // move cubie over correct slot
            int count = 0;
            while ((cubie.getX() != cubie.getStartX()
                    || cubie.getZ() != cubie.getStartZ()) && count++ < 10) {
                cube.performNotation("U");
            }
            if (count > 5) {
                System.out.println("Error in solveCross(), move cubie over  correct slot");
                System.exit(0);
            }

            // insert cubie
            if (cubie.getX() == 1) {
                cube.setFrontFace(cubie.getFace('Z'));
            } else {
                cube.setFrontFace(cubie.getFace('X'));
            }
            cube.performNotation("F2");
            cube.resetFrontFace();
        }
    }

    /**
     * Solve the remaining Corners on the bottom side
     *
     * @throws Exception
     */
    public void solveCorners() throws Exception {
        int[][] pos = {
            {0, 0, 0},
            {0, 0, 2},
            {2, 0, 0},
            {2, 0, 2}
        };

        for (int i = 0; i < 4; i++) {
            cube.pause(100);
            Cubie cubie = cube.getStartCubie(pos[i][0], pos[i][1], pos[i][2]);
            cube.unsetHighlight();
            cube.highlight(cubie.getX(), cubie.getY(), cubie.getZ());

            if (cubie.isSolved()) {
                continue;
            }

            // rotate cubie
            if (cubie.isInCorrectPosition()) {
                cube.setRightEdge(cubie);
                while (!cubie.isSolved()) {
                    cube.performNotation("R U R' U' R U R' U'");
                }
                cube.resetFrontFace();
                continue;
            }

            // get cubie into top layer
            if (cubie.getY() == 0) {
                cube.setRightEdge(cubie);
                cube.performNotation("R U R'");
                cube.resetFrontFace();
            }

            // move cubie over correct slot
            int count = 0;
            while ((cubie.getX() != cubie.getStartX()
                    || cubie.getZ() != cubie.getStartZ()) && count++ < 10) {
                cube.performNotation("U");
            }
            if (count > 5) {
                System.out.println("Error in solveCorners(), move cubie over correct slot. Cubie is not in top layer");
                System.exit(0);
            }

            // insert cubie into slot
            cube.setRightEdge(cubie);
            if (cubie.getFace('Y') == 'U') {
                cube.performNotation("U R U2 R' U R U' R'");
            } else if (cubie.getFace('Y') == cube.getFrontFace()) {
                cube.performNotation("F' U' F");
            } else {
                cube.performNotation("R U R'");
            }
            cube.resetFrontFace();
        }
    }

    /**
     * Solve the edges in the central ring
     *
     * @throws Exception
     */
    public void solveEdges() throws Exception {
        int[][] pos = {
            {0, 1, 0},
            {0, 1, 2},
            {2, 1, 0},
            {2, 1, 2}
        };

        for (int i = 0; i < 4; i++) {
            cube.pause(100);
            Cubie cubie = cube.getStartCubie(pos[i][0], pos[i][1], pos[i][2]);
            cube.unsetHighlight();
            cube.highlight(cubie.getX(), cubie.getY(), cubie.getZ());

            if (cubie.isSolved()) {
                continue;
            }

            // get cubie into top layer
            if (cubie.getY() == 1) {
                cube.setRightEdge(cubie);
                cube.performNotation("U R U' R' U' F' U F");
                cube.resetFrontFace();
            }

            // line cubie up with correct face
            char axis = 'X';
            if (cubie.getFace('X') == 'U') {
                axis = 'Z';
            }
            while (cubie.getFace(axis) != cubie.getStartFace(axis)) {
                cube.performNotation("U");
            }

            // insert cubie into slot
            cube.setAsFrontFace(cubie);
            cube.performNotation("F");
            if (cubie.isSolved()) {
                cube.performNotation("F'");
                cube.performNotation("U R U' R' U' F' U F");
            } else {
                cube.performNotation("F'");
                cube.performNotation("U' L' U L U F U' F'");
            }
        }
    }

    public void orientEdges() throws Exception {
        int[] x = {1, 0, 2, 1};
        int[] z = {0, 1, 1, 2};

        int oriented = 0;
        for (int i = 0; i < 4; i++) {
            if (cube.getCubie(x[i], 2, z[i]).getFace('Y') == 'U') {
                oriented++;
            }
        }
        cube.setFrontFace('F');
        int count = 0;
        while (oriented < 4 && count++ < 50) {
            if (oriented == 0) {
                cube.performNotation("F (R U R' U') F'");
                oriented += 2;
            }

            if (oriented == 2) {
                // left and top edge oriented
                if (cube.getCubie(x[0], 2, z[0]).getFace('Y') == 'U'
                        && cube.getCubie(x[1], 2, z[1]).getFace('Y') == 'U') {
                    cube.performNotation("F (R U R' U') F'");
                } // horizontal line
                else if (cube.getCubie(x[1], 2, z[1]).getFace('Y') == 'U'
                        && cube.getCubie(x[2], 2, z[2]).getFace('Y') == 'U') {
                    cube.performNotation("F (R U R' U') F'");
                    oriented += 2;
                } else {
                    cube.performNotation("U");
                }
            }
        }
        if (count > 40) {
            throw new Exception("failed in orient Edges");
        }
    }

    public void orientCorners() throws Exception {
        int[] x = {0, 2, 0, 2};
        int[] z = {0, 0, 2, 2};

        String cornerOrientation = "";
        for (int i = 0; i < 4; i++) {
            cornerOrientation += cube.getCubie(x[i], 2, z[i]).getFace('Y');
            if (i < 3) {
                cornerOrientation += " ";
            }
        }

        boolean done = false;
        int count = 0;
        while (!done && count++ < 10) {
            for (String[] algorithm : OllAlgorithms.algorithms) {
                if (algorithm[0].equals(cornerOrientation)) {
                    cube.performNotation(algorithm[1]);
                    done = true;
                    break;

                }
            }

            cube.performNotation("U");
            cornerOrientation = "";
            for (int i = 0; i < 4; i++) {
                cornerOrientation += cube.getCubie(x[i], 2, z[i]).getFace('Y');
                if (i < 3) {
                    cornerOrientation += " ";
                }
            }
        }
        if (count > 5) {
            throw new Exception("failed in orient corners");
        }
    }

    /**
     * orient last layer (2 look oll)
     *
     * @throws Exception
     */
    public void oll() throws Exception {
        orientEdges();
        orientCorners();
    }

    /**
     * try to match an pll case for all four angles
     *
     * @param recognition The recognition array to match
     * @param idx the index of the corresponding algorithm
     * @return the algorithm necessary or an empty String of the case didn't
     * match
     */
    public String matchPll(int[] recognition, int idx) {
        int[] x = {0, 1, 2, 0, 1, 2, 0, 1, 2};
        int[] z = {0, 0, 0, 1, 1, 1, 2, 2, 2};

        String result = "";
        for (int i = 0; i < 4; i++) {

            boolean match = true;
            for (int j = 0; j < 9; j++) {
                if (recognition[2 * j] != cube.getCubie(x[j], 2, z[j]).getStartX()
                        || recognition[2 * j + 1] != cube.getCubie(x[j], 2, z[j]).getStartZ()) {
                    match = false;
                }
            }

            if (match) {
                result += PllAlgorithms.ALGORITHM[idx];
                return result;
            } else {
                result += "U ";
                // rotate recognition array
                int[] rotated = new int[recognition.length];

                for (int rotX = 0; rotX < 3; rotX++) {
                    for (int rotY = 0; rotY < 3; rotY++) {
                        int i1 = (2 - rotY) * 3 + rotX;
                        int i2 = rotX * 3 + rotY;
                        rotated[i1 * 2] = recognition[i2 * 2];
                        rotated[i1 * 2 + 1] = recognition[i2 * 2 + 1];
                    }
                }

                recognition = rotated;
            }

        }

        return "";
    }

    /**
     * permutate last layer
     *
     * @throws Exception
     */
    public void pll() throws Exception {

        for (int i = 0; i < PllAlgorithms.RECOGNITION.length; i++) {
            int[] recognition = PllAlgorithms.RECOGNITION[i];
            // every pll case has 4 orientations
            for (int j = 0; j < 4; j++) {
                String alg = matchPll(recognition, i);

                if (alg.length() > 0) {
                    cube.performNotation(alg);
                    break;
                } else {
                    // rotate recognition array
                    for (int k = 0; k < 9; k++) {
                        int x = recognition[k * 2] - 1;
                        int y = recognition[k * 2 + 1] - 1;
                        recognition[k * 2] = y + 1;
                        recognition[k * 2 + 1] = -x + 1;
                    }
                }
            }

        }

        // rotate U until cube is solved
        int count = 0;
        while (!cube.getCubie(0, 2, 0).isSolved() && count++ < 10) {
            cube.performNotation("U");
        }
        if (count > 4) {
            throw new Exception("Failed in pll");
        }

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                if (i == 1 && k == 1) {
                    continue;
                }
                if (!cube.getCubie(i, 2, k).isSolved()) {
                    throw new Exception("Failed in pll (not solved)"
                            + "x: " + i + " Y: 2 z: " + k);
                }
            }
        }
    }

    public void solve() throws Exception {
        cube.unsetHighlight();
        cube.resetTurns();
        solveCenters();
//        solveCross();
//        solveCorners();
//        solveEdges();
//        cube.unsetHighlight();
//        for (int x = 0; x < cube.getSize(); x++) {
//            for (int z = 0; z < cube.getSize(); z++) {
//                cube.highlightMultiple(x, cube.getSize()-1, z);
//            }
//        }
//        oll();
//        pll();
//        cube.unsetHighlight();
//        cube.optimizeTurns();
        graphicsCube.performActions(cube.getAndClearTurnQueue());
    }

    public void scramble(int nbTurns) throws InvalidNotationException {
        Random rand = new Random();
        String[] turns = {"F", "B", "R", "L", "U", "D", "F'", "B'", "R'", "L'", "U'", "D'"};
        String notation = "";
        for (int i = 0; i < nbTurns; i++) {
            notation += turns[rand.nextInt(turns.length)] + " ";
        }
        cube.performNotation(notation);
        cube.optimizeTurns();
        graphicsCube.performActions(cube.getAndClearTurnQueue());
    }

    public void performNotation(String notation) throws Exception {
        cube.performNotation(notation);
        graphicsCube.performActions(cube.getAndClearTurnQueue());
    }
}
