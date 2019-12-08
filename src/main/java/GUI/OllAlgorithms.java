package GUI;


/**
 *
 * @author Matthias
 */
public interface OllAlgorithms {
    /**
     * Algorithms for 2 look oll
     * 
     * first String: recognition without edges
     * second String: notation
     */ 
    
    String[][] algorithms = {
        {"L U F R", "R U2 R' U' R U' R'"},
        {"B R U F", "R U R' U R U2' R'"},
        {"B B F F", "(R U2 R') (U' R U R') (U' R U' R')"},
        {"L B L F", "R U2' R2' U' R2 U' R2' U2' R "},
        {"U B U F", "R' F' L F R F' L' F"},
        {"U B L U", "R' F' L' F R F' L F"},
        {"U U F F", "R2 D (R' U2 R) D' (R' U2 R')"},
        {"U U U U", ""},
    };
}
