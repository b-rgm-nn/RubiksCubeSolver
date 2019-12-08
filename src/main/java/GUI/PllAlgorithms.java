package GUI;

// Ja Perm
/**
 *
 * @author Matthias
 */
public interface PllAlgorithms {
    int[][] RECOGNITION = {
        // edge only
        {0, 0,  2, 1,  2, 0,  1, 0,  1, 1,  0, 1,  0, 2,  1, 2,  2, 2}, // Ua Perm
        {0, 0,  0, 1,  2, 0,  2, 1,  1, 1,  1, 0,  0, 2,  1, 2,  2, 2}, // Ub Perm
        {0, 0,  0, 1,  2, 0,  1, 0,  1, 1,  1, 2,  0, 2,  2, 1,  2, 2}, // Z Perm
        {0, 0,  1, 2,  2, 0,  2, 1,  1, 1,  0, 1,  0, 2,  1, 0,  2, 2}, // H Perm
        // corner only
        {0, 0,  1, 0,  0, 2,  0, 1,  1, 1,  2, 1,  2, 2,  1, 2,  2, 0}, // Aa Perm
        {0, 0,  1, 0,  2, 2,  0, 1,  1, 1,  2, 1,  2, 0,  1, 2,  0, 2}, // Ab perm
        {0, 2,  1, 0,  2, 2,  0, 1,  1, 1,  2, 1,  0, 0,  1, 2,  2, 0}, // E Perm
        // corner edge swaps
        {0, 0,  1, 0,  2, 2,  2, 1,  1, 1,  0, 1,  0, 2,  1, 2,  2, 0}, // T Perm
        {2, 0,  1, 0,  0, 0,  2, 1,  1, 1,  0, 1,  0, 2,  1, 2,  2, 2}, // F Perm
        {2, 0,  0, 1,  0, 0,  1, 0,  1, 1,  2, 1,  0, 2,  1, 2,  2, 2}, // Ja Perm
        {0, 0,  1, 0,  2, 2,  0, 1,  1, 1,  1, 2,  0, 2,  2, 1,  2, 0}, // Jb Perm
        {2, 0,  1, 0,  0, 0,  1, 2,  1, 1,  2, 1,  0, 2,  0, 1,  2, 2}, // Ra Perm
        {2, 0,  1, 0,  0, 0,  0, 1,  1, 1,  1, 2,  0, 2,  2, 1,  2, 2}, // Rb perm
        {0, 0,  0, 1,  0, 2,  1, 0,  1, 1,  2, 1,  2, 0,  1, 2,  2, 2}, // V Perm
        {2, 2,  0, 1,  2, 0,  1, 0,  1, 1,  2, 1,  0, 2,  1, 2,  0, 0}, // Y Perm
        {2, 2,  1, 2,  2, 0,  0, 1,  1, 1,  2, 1,  0, 2,  1, 0,  0, 0}, // Na Perm
        {0, 0,  1, 2,  0, 2,  0, 1,  1, 1,  2, 1,  2, 0,  1, 0,  2, 2}, // Nb Perm
        // corner edge cycles
        {2, 0,  0, 1,  0, 2,  2, 1,  1, 1,  1, 0,  0, 0,  1, 2,  2, 2}, // Ga perm
        {0, 2,  1, 2,  2, 0,  1, 0,  1, 1,  2, 1,  2, 2,  0, 1,  0, 0}, // Gb perm
        {0, 2,  1, 0,  2, 0,  2, 1,  1, 1,  1, 2,  2, 2,  0, 1,  0, 0}, // Gc perm
        {2, 0,  0, 1,  0, 2,  1, 2,  1, 1,  2, 1,  0, 0,  1, 0,  2, 2}, // Gd perm
    };
    
    String[] ALGORITHM = {
        // edge only
        "R' U R' U' R' U' R' U R U R2",                                 // Ua Perm
        "R2 U' R' U' R U R U R U' R",                                   // Ub Perm
        "[U R' U'] R U' R U R U' R' U R U [R2 U' R' U]",                // Z Perm
        "R2 L2 D R2 L2 U2 R2 L2 D R2 L2",                               // H Perm
        // corner only
        "R B' R F2 R' B R F2 R2",                                       // Aa Perm
        "R2 F2 R' B' R F2 R' B R'",                                     // Ab Perm
        "R B' R' F R B R' F' R B R' F R B' R' F'",                      // E Perm
        // corner edge swaps
        "[R U R' U'] R' F R2 U' R' U' [R U R' F']",                     // T Perm
        "L U2 L U2 R' U L U L U' L' U2 R U2 L' U' L'",                  // F Perm
        "[R' U L'] U2 [R U' R' U2] [L R U']",                           // Ja Perm
        "[R U R' F'] [R U R' U' R' F] [R2 U' R' U']",                   // Jb Perm
        "[L U2' L' U2'] L F' L' U' L U L F L2' U",                      // Ra perm
        "[R' U2 R U2] R' F R U R' U' R' F' R2 U'",                      // Rb perm
        "R' U L U' R U R' U L' U' R U2 L U2 L'",                        // V Perm
        "[F R U' R' U' R U R' F'] [R U R' U'] [R' F R F']",             // Y Perm
        "(L U' L' U L) (F U F') (L' U' L) (F' L F) (L' U L') U2",       // Na Perm
        "U R' U R' F R F' R U' R' F' U F R U R' U' R U'",               // Nb Perm
        // corner edge cycles
        "D' R2 U R' U R' U' R U' R2 U' D R' U R",                       // Ga perm
        "R' U' R U D' R2 U R' U R U' R U' R2 D",                        // Gb perm
        "R2 U' R U' R U R' U R2 D' U R U' R' D",                        // Gc perm
        "R U R' U' D R2 U' R U' R' U R' U R2 D'",                       // Gd perm
    };
}
