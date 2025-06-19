package utils;

public class Directions {
    public static final int[][] DIAGONAL_DIRECTIONS = {
            {-1, -1}, // up-left
            {-1,  1}, // up-right
            { 1, -1}, // down-left
            { 1,  1}  // down-right
    };

    public static final int[][] ORTHOGONAL_DIRECTIONS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    public static final int[][] KNIGHT_MOVES = {
            {-2, 1},
            {-2, -1},
            {-1,2},
            {1,2},
            {2,-1},
            {2,1},
            {-1,-2},
            {1,-2}
    };

    public static final int[][] KING_MOVES = {
            {-1, -1}, // up-left
            {-1,  1}, // up-right
            { 1, -1}, // down-left
            { 1,  1},  // down-right
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    public static final int[][] PAWN_MOVES = {
            {-1,0},
            {-1,-1},
            {-1,1}
    };

}
