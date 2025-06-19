package pieces;

import utils.Color;
import board.*;
import java.util.List;
import game.Game;

public abstract class Piece {
    protected Position pos;
    protected final Color color;
    protected final String symbol;

    // constructor
    public Piece(Position initial, Color color, String symbol) {
        this.color = color;
        pos = initial;
        this.symbol = symbol;
    }

    // copy constructor
    public Piece(Piece other) {
        this.pos = other.pos;
        this.color = other.color;
        this.symbol = other.symbol;
    }

    // getters
    public Color getColor() {
        return color;
    }

    public String getSymbol() {
        return symbol;
    }

    public Position getPosition() {
        return pos;
    }

    // set new position
    public void setPosition(Position newPosition) {
        pos = newPosition;
    }

    // create clone of Piece
    public abstract Piece clone();

    /*
     abstract method to find valid moves
     that will be implemented by every unique piece
     */
    public abstract List<Position> getLegalMoves(Board board, Game game);

}
