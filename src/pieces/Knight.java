package pieces;

import board.Board;
import board.Position;
import game.Game;
import utils.Color;

import java.util.ArrayList;
import java.util.List;
import static utils.Directions.KNIGHT_MOVES;

public class Knight extends Piece{

    public Knight(Position position, Color color, String symbol) {
        super(position, color, symbol);
    }

    public Knight(Knight other) {
        super(other);
    }

    // clone this piece
    @Override
    public Piece clone() {
        return new Knight(this);
    }

    @Override
    public List<Position> getLegalMoves(Board board, Game game) {
        List<Position> legalMoves = new ArrayList<>();

        final int curX = pos.getXPos(); // current x
        final int curY = pos.getYPos(); // current y

        for (int[] dir: KNIGHT_MOVES) {
            int newX = curX + dir[0], newY = curY + dir[1];
            Position newPos = new Position(newX, newY);

            if (board.boundsCheck(newPos)) { // check if the position is safe
                if (board.isEmptyPosition(newPos)) {
                    legalMoves.add(newPos);
                }
                else {
                    if (board.getPieceAt(newPos).getColor() != this.color) {
                        legalMoves.add(newPos); // capture
                    }
                }
            }
        }

        return legalMoves;
    }
}
