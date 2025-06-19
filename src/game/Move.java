package game;
import board.Position;
import pieces.Piece;


public class Move {
    // positional knowledge
    private final Position from;
    private final Position to;

    // piece knowledge
    private final Piece movedPiece;
    private final Piece capturedPiece;

    // important flags for game state
    private final boolean isEnPassantMove;
    private final boolean isCastlingMove;
    private final boolean isPromotion;
    private final Piece promotionChoice;


    public Move(Position from, Position to,
                Piece movedPiece, Piece capturedPiece,
                boolean isEnPassantMove,
                boolean isPromotion,
                boolean isCastlingMove,
                Piece promotionChoice) {
        this.capturedPiece = capturedPiece;
        this.movedPiece = movedPiece;
        this.from = from;
        this.to = to;
        this.isEnPassantMove = isEnPassantMove;
        this.isCastlingMove = isCastlingMove;
        this.isPromotion = isPromotion;
        this.promotionChoice = promotionChoice;

    }

    // this class only has getters since it has no other functionality
    public Position getFrom() { return from; }
    public Position getTo() { return to; }
    public Piece getMovedPiece() { return movedPiece; }
    public Piece getCapturedPiece() { return capturedPiece; }
    public boolean isCastlingMove() { return isCastlingMove; }
    public boolean isEnPassant() { return isEnPassantMove; }
    public boolean isPromotion() { return isPromotion; }
    public Piece getPromotionResult() { return promotionChoice; }
}
