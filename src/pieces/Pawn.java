package pieces;


import board.Board;
import board.Position;
import game.Game;
import utils.Color;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    // constructors
    public Pawn(Position position, Color color, String symbol) {
        super(position, color, symbol);
    }

    public Pawn(Pawn other) {
        super(other);
    }

    // method to clone
    @Override
    public Piece clone() {
        return new Pawn(this);
    }

    @Override
    public List<Position> getLegalMoves(Board board, Game game) {
        List<Position> legalMoves = new ArrayList<>();

        final int curX = pos.getXPos(); // current x
        final int curY = pos.getYPos(); // current y
        int direction = (color == Color.WHITE) ? -1 : 1;
        int startRow = (color == Color.WHITE) ? 6 : 1;
        int enPassantRow = (color == Color.WHITE) ? 3 : 4;

        // one ahead
        Position oneAhead = new Position(curX + direction, curY);
        if (board.isEmptyPosition(oneAhead) && board.boundsCheck(oneAhead)) {
            legalMoves.add(oneAhead);

            // do two ahead as well
            Position twoAhead = new Position(curX + 2 * direction, curY);
            if (curX == startRow && board.isEmptyPosition(twoAhead)) {
                legalMoves.add(twoAhead);
            }
        }

        // diagonal captures and en passant check
        for (int dy : new int[]{-1, 1}) {
            Position diag = new Position(curX + direction, curY + dy);
            if (!board.boundsCheck(diag)) continue;

            Piece target = board.getPieceAt(diag);
            if (target != null && target.getColor() != this.color) {
                legalMoves.add(diag); // normal capture
            }

            // en passant check
            Position sidePawnPos = new Position(curX, curY + dy);
            Piece enPassantTarget = game.getEnPassantTarget();
            if (curX == enPassantRow && enPassantTarget != null
                    && enPassantTarget.getColor() != this.color) {
                Position enPassantPosition = enPassantTarget.getPosition();

                if (enPassantPosition.getXPos() == sidePawnPos.getXPos() &&
                        enPassantPosition.getYPos() == sidePawnPos.getYPos()) {
                    // System.out.println("Adding En Passant Target here");
                    legalMoves.add(diag);
                }
            }
        }

        return legalMoves;
    }
}