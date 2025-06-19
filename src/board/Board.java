package board;

import pieces.*;

import static utils.Color.BLACK;
import static utils.Color.WHITE;

/*
 The board class holds all the pieces. It involves some functionality like knowing if
 a position is empty and what piece is at what position. It is essentially a 2d grid
 of Piece objects.
 */
public class Board {

    private Piece[][] grid = new Piece[8][8];

    // checks if a position is empty
    public boolean isEmptyPosition(Position checkPos) {
        return grid[checkPos.getXPos()][checkPos.getYPos()] == null;
    }

    // function to remove a piece at a given position
    public void removePieceAt(Position pos) {
        if (boundsCheck(pos)) {
            grid[pos.getXPos()][pos.getYPos()] = null;
        }
    }

    // returns a piece at a particular position
    public Piece getPieceAt(Position pos) {
        // System.out.println("Looking for piece at: x = " + pos.getXPos() + ", y = " + pos.getYPos());

//        if (grid[pos.getXPos()][pos.getYPos()] == null) {
//             System.out.println("This is a null piece!");
//        }
        return grid[pos.getXPos()][pos.getYPos()];
    }

    // set piece at new position: never called externally
    public void setPieceAt(Position newPos, Piece piece) {
        grid[newPos.getXPos()][newPos.getYPos()] = piece;
    }

    // function to move a piece. this involves nullifying the older position
    public void movePiece(Position oldPos, Position newPos) {
        Piece currentPiece = getPieceAt(oldPos);
        setPieceAt(newPos, currentPiece);
        setPieceAt(oldPos, null);
        if (currentPiece != null) {
            currentPiece.setPosition(newPos);
        }
    }

    // function to check if position is in bounds of the board to avoid bad referencing
    public boolean boundsCheck(Position pos) {
        return 0 <= pos.getXPos() && pos.getXPos() < 8 && 0 <= pos.getYPos() && pos.getYPos() < 8;
    }

    // function to initialize the board
    public void initializeBoard() {
        // initialize black pieces
        grid[0][0] = new Rook(new Position(0,0), BLACK, "r");
        grid[0][1] = new Knight(new Position(0,1), BLACK, "n");
        grid[0][2] = new Bishop(new Position(0,2), BLACK, "b");
        grid[0][3] = new Queen(new Position(0,3), BLACK, "q");
        grid[0][4] = new King(new Position(0,4), BLACK, "k");
        grid[0][5] = new Bishop(new Position(0,5), BLACK, "b");
        grid[0][6] = new Knight(new Position(0,6), BLACK, "n");
        grid[0][7] = new Rook(new Position(0,7), BLACK, "r");

        // initialize black pawns
        for (int i = 0; i < 8; i++) {
            grid[1][i] = new Pawn(new Position(1, i), BLACK, "p");
        }


        // initialize white pieces
        grid[7][0] = new Rook(new Position(7,0), WHITE, "R");
        grid[7][1] = new Knight(new Position(7,1), WHITE, "N");
        grid[7][2] = new Bishop(new Position(7,2), WHITE, "B");
        grid[7][3] = new Queen(new Position(7,3), WHITE, "Q");
        grid[7][4] = new King(new Position(7,4), WHITE, "K");
        grid[7][5] = new Bishop(new Position(7,5), WHITE, "B");
        grid[7][6] = new Knight(new Position(7,6), WHITE, "N");
        grid[7][7] = new Rook(new Position(7,7), WHITE, "R");

        // initialize white pawns
        for (int i = 0; i < 8; i++) {
            grid[6][i] = new Pawn(new Position(6, i), WHITE, "P");
        }
    }

    // function to render the board on console
    public void renderBoard() {
        System.out.println("\n" + "  |" +"a" + "|" + "b" + "|" + "c" + "|" + "d" + "|" + "e" + "|" + "f" + "|" + "g" + "|" + "h" + "|");

        // render pieces
        for (int rank = 0; rank < 8; rank++) {
            System.out.print(" " + (8-rank) + "|");
            for (int file = 0; file < 8; file++) {
                Piece piece = grid[rank][file];
                if (piece != null) {
                    System.out.print(piece.getSymbol());
                }
                else {
                    System.out.print("*"); // empty space
                }
                System.out.print("|");
            }
            System.out.println();
        }
        System.out.println("  |" +"a" + "|" + "b" + "|" + "c" + "|" + "d" + "|" + "e" + "|" + "f" + "|" + "g" + "|" + "h" + "|");
    }

    // function to create a deep copy of the board meant for storing game states
    public Board deepCopy() {
        Board newBoard = new Board();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position coordinates = new Position(i,j);
                Piece piece = this.getPieceAt(coordinates);
                if (piece != null) {
                    piece = piece.clone();
                }
                newBoard.setPieceAt(coordinates, piece);
            }
        }
        return newBoard;
    }
}
