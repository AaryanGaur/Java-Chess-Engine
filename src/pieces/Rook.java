package pieces;

import board.Board;
import board.Position;
import game.Game;
import utils.Color;
import java.util.ArrayList;
import java.util.List;
import static utils.Directions.ORTHOGONAL_DIRECTIONS; // has to be static

public class Rook extends Piece {

    public Rook(Position pos, Color color, String symbol) {
        super(pos,color, symbol);
    }

    public Rook(Rook other) {
        super(other);
    }

    // clone
    public Piece clone() {
        return new Rook(this);
    }

    @Override
    public List<Position> getLegalMoves(Board board, Game game) {
        List<Position> legalMoves = new ArrayList<>();
        final int curX = pos.getXPos();
        final int curY = pos.getYPos();

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
