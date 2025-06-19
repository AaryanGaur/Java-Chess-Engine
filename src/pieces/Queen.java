package pieces;

import board.Board;
import board.Position;
import game.Game;
import utils.Color;
import static utils.Directions.ORTHOGONAL_DIRECTIONS;
import static utils.Directions.DIAGONAL_DIRECTIONS;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece{

    public Queen(Position position, Color color, String symbol) {
        super(position, color, symbol);
    }

    @Override
    public Piece clone() {
        return new Queen(this);
    }

    public Queen(Queen other) {
        super(other);
    }


    @Override
    /*
    The queen is just a combination of the rook and the bishop so we will copy both
    loops here and add the legal moves accordingly.
     */
    public List<Position> getLegalMoves(Board board, Game game) {
        List<Position> legalMoves = new ArrayList<>();

        final int curX = pos.getXPos(); // current x
        final int curY = pos.getYPos(); // current y

        // check for each direction in the vertical and horizontal direction and add the moves to the list
        for (int[] dir: ORTHOGONAL_DIRECTIONS) {
            int dx = dir[0], dy = dir[1]; // get x and y changes
            int newX = curX + dx, newY = curY + dy;

            // loop through all possibilities in that particular direction
            while (0<=newX && newX<8 && 0<=newY && newY<8) {
                Position checkPos = new Position(newX,newY);

                if (board.isEmptyPosition(checkPos)) {
                    legalMoves.add(checkPos);
                }
                else {
                    if (board.getPieceAt(checkPos).getColor() != this.color) {
                        legalMoves.add(checkPos); // capture
                    }
                    break;
                }
                newX += dx;
                newY += dy;
            }
        }

        // also do a check for diagonal directions
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
                    if (board.getPieceAt(checkPos).getColor() != this.color) {
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
