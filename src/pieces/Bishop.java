package pieces;

import board.Board;
import board.Position;
import game.Game;
import utils.Color;
import java.util.ArrayList;
import java.util.List;
import static utils.Directions.DIAGONAL_DIRECTIONS;

public class Bishop extends Piece {

    public Bishop(Position pos, Color color, String symbol) {
        super(pos,color, symbol);
    }

    // copy constructor
    public Bishop (Bishop other) {
        super(other);
    }

    @Override
    public Piece clone() {
        return new Bishop(this);
    }

    @Override
    /*
    For Bishop, it can move only diagonally.
    There are 4 total diagonals - up left, up right, down left and down right

     */
    public List<Position> getLegalMoves(Board board, Game game) {
        List<Position> legalMoves = new ArrayList<>(); // create arraylist object similar to rook

        final int curX = pos.getXPos(); // current x
        final int curY = pos.getYPos(); // current y

        // for each diagonal direction, check if position is valid
        for (int[] dir : DIAGONAL_DIRECTIONS) {
            int dx = dir[0], dy = dir[1]; // get x and y changes
            int newX = curX + dx, newY = curY + dy;

            // loop through all possibilities in that particular direction
            while (0<=newX && newX<8 && 0<=newY && newY<8) {
                Position checkPos = new Position(newX,newY);

                if (board.isEmptyPosition(checkPos)) {
                    legalMoves.add(checkPos);
                }
                else {
                    if (board.getPieceAt(checkPos).color != this.color) {
                        legalMoves.add(checkPos); // capture
                    }
                    break;
                }
                newX += dx;
                newY += dy;
            }
        }
        return legalMoves;
    }
}
