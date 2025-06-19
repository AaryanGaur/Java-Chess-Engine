package pieces;

import board.Board;
import board.Position;
import game.Game;
import utils.Color;
import static utils.Directions.KING_MOVES;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece{

    public King(Position position, Color color, String symbol) {
        super(position, color, symbol);
    }

    public King(King other) {
        super(other);
    }

    // clone object
    public Piece clone() {
        return new King(this);
    }

    @Override
    public List<Position> getLegalMoves(Board board, Game game) {
        List<Position> legalMoves = new ArrayList<>();

        final int curX = pos.getXPos(); // current x
        final int curY = pos.getYPos(); // current y

        // normal moves
        for (int[] dir: KING_MOVES) {
            int newX = curX + dir[0], newY = curY + dir[1];
            Position newPos = new Position(newX, newY);

            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) { // check if the position is safe
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

        // castling
        if (!game.hasKingMoved(this.color)) {
            // castling is only legal if king is not in check
            if (!game.isKingInCheck(this.color)) {
                // do king side check first
                if (!game.hasKingSideRookMoved(this.color)) {
                    // check if there is empty space in between
                    if (board.isEmptyPosition(new Position(curX,curY+1)) && board.isEmptyPosition(new Position(curX,curY+2))) {
                        Position kingSideCastling = new Position(curX,curY+2);
                        legalMoves.add(kingSideCastling);
                    }
                }

                // queen side check
                if (!game.hasQueenSideRookMoved(this.color)) {
                    // check if there is empty space in between
                    if (board.isEmptyPosition(new Position(curX,curY-1)) && board.isEmptyPosition(new Position(curX,curY-2)) && board.isEmptyPosition(new Position(curX,curY-3))) {
                        Position kingSideCastling = new Position(curX,curY-2);
                        legalMoves.add(kingSideCastling);
                    }
                }
            }
        }

        return legalMoves;
    }
}
