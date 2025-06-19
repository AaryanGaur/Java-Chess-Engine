package board;

public class Position {
    // attributes
    private int xPos;
    private int yPos;

    // constructor
    public Position(int x, int y) {
        xPos = x;
        yPos = y;
    }

    // getters
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    // update position
    public void updateX(int dx) {
        xPos += dx;
    }

    public void updateY(int dy) {
        yPos += dy;
    }

    // change x and y
    public void changeX(int x) {
        xPos = x;
    }

    public void changeY(int y) {
        yPos = y;
    }

    // check if position is valid
    public boolean isValid() {
        return (xPos >= 0) && (xPos < 8) && (yPos >= 0) && (yPos < 8);
    }

    // return a new position after certain offset
    public Position newPosition(int x_offset, int y_offset) {
        return new Position(xPos + x_offset, yPos + y_offset);
    }

    // check if some position is equal
    public boolean equalPosition(Position checkPos) {
        return (xPos == checkPos.getXPos()) && (yPos == checkPos.getYPos());
    }

}
