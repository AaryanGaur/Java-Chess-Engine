package game;

/*
import necessities
 */
import board.Board;
import board.Position;
import pieces.*;
import utils.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static utils.Color.BLACK;
import static utils.Color.WHITE;

import static utils.Directions.ORTHOGONAL_DIRECTIONS;
import static utils.Directions.DIAGONAL_DIRECTIONS;
import static utils.Directions.KNIGHT_MOVES;

public class Game {
    private Board board;
    private final List<Move> moveHistory;

    private boolean gameOver;
    private Color currentTurn;

    private Player whitePlayer;
    private Player blackPlayer;

    // track kings locations
    private Position whiteKingPos;
    private Position blackKingPos;

    // to store game states
    private final List<Board> gameStates;

    // various flags for making moves
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteKingsideRookMoved = false;
    private boolean whiteQueensideRookMoved = false;
    private boolean blackKingsideRookMoved = false;
    private boolean blackQueensideRookMoved = false;
    private boolean whiteKingCheck = false;
    private boolean blackKingCheck = false;
    private Color winner = null;
    private boolean enPassantMove;
    private Piece enPassantTarget;
    private int moveCounter = 0;
    private int enPassantTracker = 0;
    private Piece squareAttackedBy;
    private Color opponentColor;
    private Piece recentCaptured;
    // castling variables
    private Position initialCastlingRook;
    private Position destinationCastlingRook;
    private boolean castlingMove;


    // constructor
    public Game() {
        board = new Board();
        moveHistory = new ArrayList<>();
        gameOver = false;
        currentTurn = WHITE;
        opponentColor = BLACK;
        whitePlayer = new Player(WHITE);
        blackPlayer = new Player(BLACK);
        gameStates = new ArrayList<>();

        board.initializeBoard(); // set up everything
        whiteKingPos = new Position(7, 4); // initial white king
        blackKingPos = new Position(0, 4); // initial black king

        // initialize gameStates
        gameStates.add(board.deepCopy());
    }

    // getters for all objects and flags
    public List<Move> getMoveHistory() {return moveHistory;}
    public boolean getEnPassantMove() {return enPassantMove;}
    public boolean getCastlingMove() {return castlingMove;}

    // move counter and en passant tracker
    public int getMoveCounter() {return moveCounter;}
    public int getEnPassantTracker() {return enPassantTracker;}

    // get current turn
    public Color getCurrentTurn() {return currentTurn;}

    // get opponent turn
    public Color getOpponent() {return opponentColor;}

    // get recent captured piece
    public Piece getRecentCaptured() {return recentCaptured;}

    // get players
    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    // get board
    public Board getBoard() {return board;}

    // switch turns
    public void switchTurns() {
        currentTurn = (currentTurn == WHITE) ? BLACK : WHITE;
        opponentColor = (opponentColor == WHITE) ? BLACK : WHITE;
    }

    // function checks if king has moved depending on color
    public boolean hasKingMoved(Color color) {
        return (color == WHITE) ? whiteKingMoved : blackKingMoved;
    }

    // function checks if queen side rook has moved for each color
    public boolean hasQueenSideRookMoved(Color color) {
        return (color == WHITE) ? whiteQueensideRookMoved : blackQueensideRookMoved;
    }

    // function will check if king side rook has moved for each color
    public boolean hasKingSideRookMoved(Color color) {
        return (color == WHITE) ? whiteKingsideRookMoved : blackKingsideRookMoved;
    }

    public Color getWinner() {return winner;}

    // checks if king is in check based on color
    public boolean isKingInCheck(Color color) {
        return (color == WHITE) ? isWhiteKingCheck() : isBlackKingCheck();
    }

    public Piece getEnPassantTarget() {return enPassantTarget;}

    // this method will check if white king is in check
    public boolean isWhiteKingCheck() {
        return isSquareUnderAttack(whiteKingPos, WHITE);
    }

    // this method will check if black king is in check
    public boolean isBlackKingCheck() {
        return isSquareUnderAttack(blackKingPos, BLACK);
    }

    // this is a common method to check if a certain square is in attack by any of the other pieces
    public boolean isSquareUnderAttack(Position pos, Color defendingColor) {
        /*
        this function will check if a square is under attack by looking in all possible directions
        i will be using the predefined directions from the Directions class for this.
         */

        // Color defendingColor = board.getPieceAt(pos).getColor(); // defending color
        // System.out.println("We are checking if the king of this color is under attack! " + defendingColor);
        int defX = pos.getXPos();
        int defY = pos.getYPos();
        Color attackingColor = (defendingColor == WHITE) ? BLACK : WHITE;

        // check for up-down directions
        for (int[] dir: ORTHOGONAL_DIRECTIONS) {
            int dx = dir[0], dy = dir[1]; // get x and y changes
            int newX = defX + dx, newY = defY + dy;
            System.out.println("Orthogonal checks being done for " + defendingColor);
            boolean kingCheck = true;

            while (0<=newX && newX<8 && 0<=newY && newY<8) {
                Position checkPos = new Position(newX,newY);
                System.out.println(checkPos.getXPos() + " " + checkPos.getYPos());
                Piece checkPiece = board.getPieceAt(checkPos); // for verification

                if (!board.isEmptyPosition(checkPos)) {
                    if (checkPiece.getColor() != defendingColor) {
                        // check immediately if its a king
                        if (kingCheck) {
                            if (checkPiece instanceof King) {
                                // make sure this king himself isn't in check
                                boolean checking = makeMove(checkPos, pos, attackingColor, true);
                                if (!checking) break;
                                squareAttackedBy = checkPiece;
                                System.out.println("Square attacked by king");
                                return true;
                            }
                            else {
                                kingCheck = false;
                                if (checkPiece instanceof Rook || checkPiece instanceof Queen) {
                                    System.out.println("Attacking piece is a rook or queen!");
                                    return true;
                                }
                            }
                        }
                        // check if piece is rook or queen
                        else if (checkPiece instanceof Rook || checkPiece instanceof Queen) {
                            System.out.println("Attacking piece is a queen or rook");
                            System.out.println("Attacking piece is at coordinates: " + checkPiece.getPosition().getXPos() + " " + checkPiece.getPosition().getYPos());
                            return true;
                        }
                        else {
                            System.out.println("We somehow come here");
                            break; // found a piece that cannot attack
                        }
                    }
                    else {
                        break;
                    }
                }
                else {
                    kingCheck = false; // because king can only attack one square away
                }
                newX += dx;
                newY += dy;
            }
        }

        // check for diagonal directions
        for (int[] dir: DIAGONAL_DIRECTIONS) {
            int dx = dir[0], dy = dir[1]; // get x and y changes
            int newX = defX + dx, newY = defY + dy;
            boolean kingCheck = true;
            System.out.println("Diagonal checks being done for " + defendingColor);

            while (0<=newX && newX<8 && 0<=newY && newY<8) {

                Position checkPos = new Position(newX,newY);
                Piece checkPiece = board.getPieceAt(checkPos); // for verification

                if (!board.isEmptyPosition(checkPos)) {
                    // System.out.println("Checking diagonal directions!");

                    if (checkPiece.getColor() != defendingColor) {
                        // check if piece is rook or queen
                        // System.out.println("Potential Attacker found!!");
                        if (kingCheck) {
                            if (checkPiece instanceof King) {
                                // make sure this king himself isn't in check
                                boolean checking = makeMove(checkPos, pos, attackingColor, true);
                                if (!checking) break;
                                squareAttackedBy = checkPiece;
                                System.out.println("Square attacked by king");
                                return true;
                            }
                            else {
                                // System.out.println("Set kingCheck to false");
                                kingCheck = false;
                                if (checkPiece instanceof Bishop || checkPiece instanceof Queen) {
                                    System.out.println("Attacking piece is a bishop or queen!");
                                    return true;
                                }
                            }
                        }
                        else if (checkPiece instanceof Bishop || checkPiece instanceof Queen) {
                            System.out.println("Attacking piece is a bishop or queen!");
                            return true;
                        }
                        else {
                            break; // piece of same color that blocks the way
                        }
                    }
                    else {
                        break;
                    }
                }
                else {
                    // System.out.println("This is an empty square!");
                    kingCheck = false;
                }
                newX += dx;
                newY += dy;
            }
        }

        // check for knight moves
        for (int[] dir: KNIGHT_MOVES) {
            int newX = defX + dir[0], newY = defY + dir[1];
            Position newPos = new Position(newX, newY);
            if (!board.boundsCheck(newPos)) continue;
            Piece checkPiece = board.getPieceAt(newPos); // for verification
            if (checkPiece != null) {
                if (board.boundsCheck(newPos)) { // check if the position is safe
                    if (checkPiece.getColor() != defendingColor) {
                        // check if piece is knight
                        if (checkPiece instanceof Knight) {
                            System.out.println("Can be blocked by knight");
                            return true;
                        }
                    }
                    else {
                        break;
                    }
                }
            }
        }

        // check for pawn
        int pawnDir = (defendingColor == WHITE) ? -1 : 1;
        int[][] pawnAttackDirs = {
                {pawnDir, -1}, {pawnDir, 1}
        };

        for (int[] dir: pawnAttackDirs) {
            int newX = defX + dir[0], newY = defY + dir[1];
            Position newPos = new Position(newX, newY);
            if (!board.boundsCheck(newPos)) continue;
            Piece checkPiece = board.getPieceAt(newPos); // for verification
            if (checkPiece != null) {
                if (board.boundsCheck(newPos)) { // check if the position is safe
                    if (checkPiece.getColor() != defendingColor) {
                        // check if piece is pawn
                        if (checkPiece instanceof Pawn) {
                            System.out.println("We are coming here! King checked by pawn!");
                            return true;
                        }
                    }
                    else {
                        break;
                    }
                }
            }
        }


        return false;
    }

    // converts string move given from player to Position objects
    public List<Position> convertToMove(String[] move) {
        if (move.length != 2) return null;

        List<Position> convertedMove = new ArrayList<>();

        // ===== FROM =====
        String from = move[0];

        char file = from.charAt(0); // x coordinate
        char rank = from.charAt(1); // y coordinate

        int x = file - 'a'; // subtract 62
        int y = 8 - (rank - '0'); // convert to int and then subtract from 8

        Position fromPosition = new Position(y,x);
        convertedMove.add(fromPosition);

        // ===== TO =====
        from = move[1];

        file = from.charAt(0); // x coordinate
        rank = from.charAt(1); // y coordinate

        x = file - 'a'; // subtract 61
        y = 8 - (rank - '0'); // convert to int and then subtract from 8

        fromPosition = new Position(y,x);
        convertedMove.add(fromPosition);

        return convertedMove; // return the list of positions
    }

    // function to create move
    public boolean makeMove(Position from, Position to, Color movingColor, boolean simulation) {
        Board savedBoard = board.deepCopy(); // create a copy in case something goes wrong
        // increment counter
        moveCounter++;
        if (!simulation){
            enPassantMove = false; // reset at the beginning of every move
            castlingMove = false; // reset at beginning
        }

        Piece movingPiece = board.getPieceAt(from); // get the moving piece

        // validate the piece and turn
        if (movingPiece == null) {
            // System.out.println("Picked null piece.");
            return false;
        }
        if (movingPiece.getColor() != movingColor) {
            // System.out.println("Wrong turn.");
            return false;
        }

        // validate if move made is legal
        List<Position> legalMoves = movingPiece.getLegalMoves(board, this);
        boolean legal = false;
        for (Position iter: legalMoves) {
            if (iter.getXPos() == to.getXPos() && iter.getYPos() == to.getYPos()) {
                legal = true;
                break;
            }
        }

        if (!legal) {
            System.out.println("This is not in the legal moves list!");
            return false;
        }

        // if it is legal, update position of king if piece is instance of king
        if (movingPiece instanceof King) {
            // update the private variable for the kings
            if (movingPiece.getColor() == WHITE) {
                whiteKingPos = to;
            }
            else {
                blackKingPos = to;
            }
        }

        Piece captured = board.getPieceAt(to); // could be null
        boolean isEnPassant = false;
        boolean isCastling = false;
        boolean isPromotion = false;
        Piece promotedTo = null;

        // === EN PASSANT ===
        if (movingPiece instanceof Pawn && board.isEmptyPosition(to) && to.getYPos() != from.getYPos()) {
            if (moveCounter - enPassantTracker == 1 && enPassantTarget != null) {
                // System.out.println("Set enPassant to true");
                isEnPassant = true;
                int dir = (movingPiece.getColor() == Color.WHITE) ? 1 : -1;
                Position capturedPawnPos = new Position(to.getXPos() + dir, to.getYPos());
                captured = board.getPieceAt(capturedPawnPos);
                board.setPieceAt(capturedPawnPos, null); // remove captured pawn
                enPassantTarget = null;
                enPassantMove = true;
            } else {
                System.out.println("This is not a legal en passant");
                return false;
            }
        }

        // === CASTLING ===
        if (movingPiece instanceof King && Math.abs(to.getYPos() - from.getYPos()) == 2) {
            isCastling = true;
            int rookFromY = (to.getYPos() > from.getYPos()) ? 7 : 0;
            int rookToY = (to.getYPos() > from.getYPos()) ? to.getYPos() - 1 : to.getYPos() + 1;

            Position rookFrom = new Position(from.getXPos(), rookFromY);
            Position rookTo = new Position(from.getXPos(), rookToY);

            // assign variables
            initialCastlingRook = rookFrom;
            destinationCastlingRook = rookTo;
            castlingMove = true;
            board.movePiece(rookFrom, rookTo);
        }

        // ===== PAWN DOUBLE MOVE: SET EN PASSANT TARGET =====
        if (movingPiece instanceof Pawn && Math.abs(to.getXPos() - from.getXPos()) == 2) {
            enPassantTarget = movingPiece; // this pawn is susceptible to enPassant
            // System.out.println("We set smth to en passant");
            enPassantTracker = moveCounter; // set the tracker to the current move
        } else {
            enPassantTarget = null;
        }

        // ===== MOVE THE PIECE =====
        board.movePiece(from, to);

        // check if the current king is in check after this move
        boolean kingInCheck = (movingColor == Color.WHITE) ? isWhiteKingCheck() : isBlackKingCheck();

        // reset board if it is
        if (kingInCheck) {
            board = savedBoard;
            return false;
        }

        // if captured piece is not null, update the points according to piece
        if (!simulation) {
            if (captured != null) {
                if (movingPiece.getColor() == WHITE) {
                    // then increase points for white
                    if (captured instanceof Pawn) {
                        whitePlayer.increasePoints(1);
                    }
                    else if (captured instanceof Bishop || captured instanceof Knight) {
                        whitePlayer.increasePoints(3);
                    }
                    else if (captured instanceof Rook) {
                        whitePlayer.increasePoints(5);
                    }
                    else if (captured instanceof Queen) {
                        whitePlayer.increasePoints(9);
                    }
                }
                else {
                    // then increase points for black
                    if (captured instanceof Pawn) {
                        blackPlayer.increasePoints(1);
                    }
                    else if (captured instanceof Bishop || captured instanceof Knight) {
                        blackPlayer.increasePoints(3);
                    }
                    else if (captured instanceof Rook) {
                        blackPlayer.increasePoints(5);
                    }
                    else if (captured instanceof Queen) {
                        blackPlayer.increasePoints(9);
                    }
                }
                // update recent captured piece
                recentCaptured = captured;
                // update move history
                Move move = new Move(from, to, movingPiece, captured, isEnPassant, isCastling, isPromotion, promotedTo);
                moveHistory.add(move);
            }
            // update flags
            if (movingPiece instanceof King) {
                System.out.println("Some king has moved so updating flags!");
                if (currentTurn == Color.WHITE) whiteKingMoved = true;
                else blackKingMoved = true;
            }
            else if (movingPiece instanceof Rook) {
                System.out.println("Some rook has moved. Updating flags");
                if (from.getYPos() == 0) {
                    if (currentTurn == Color.WHITE) whiteQueensideRookMoved = true;
                    else blackQueensideRookMoved = true;
                } else if (from.getYPos() == 7) {
                    if (currentTurn == Color.WHITE) whiteKingsideRookMoved = true;
                    else blackKingsideRookMoved = true;
                }
            }
        } else {
            board = savedBoard; // in case of simulation
        }

        return true;
    }

    // function to check if a pawn has reached either side of the board
    public Position checkPromotionStatus(Color color) {
        int rank = (color == WHITE) ? 0 : 7; // white pawns travel to 0 and black pawns to 7

        // loop over the last row and check if any pawns are there
        for (int file = 0; file < 8; file++) {
            Position pos = new Position(rank,file);
            if (board.getPieceAt(pos) instanceof Pawn) {
                return pos;
            }
        }

        return null;
    }

    // function to handle promotion for the gui app
    public void handlePromotionGUI(Position pos, Color color, String choice) {
        // remove the pawn
        board.removePieceAt(pos);

        // create chosen piece
        Piece newPiece = null;
        String symbol;

        switch (choice) {
            case "q":
                symbol = (color == WHITE) ? "Q" : "q";
                newPiece = new Queen(pos, color, symbol);
                break;
            case "n":
                symbol = (color == WHITE) ? "N" : "n";
                newPiece = new Knight(pos, color, symbol);
                break;
            case "r":
                symbol = (color == WHITE) ? "R" : "r";
                newPiece = new Rook(pos, color, symbol);
                break;
            case "b":
                symbol = (color == WHITE) ? "B" : "b";
                newPiece = new Bishop(pos, color, symbol);
                break;
        }

        // set the piece
        board.setPieceAt(pos, newPiece);

        // ending statement
        System.out.println("Promotion complete! " + color + " pawn promoted to " + choice + ".");
    }

    // this function will handle promotion by prompting the user to pick a piece
    // and handle creation/deletion
    public void handlePromotion(Position pos, Color color, Scanner scanner) {
        System.out.println("Pawn promotion! Choose a piece to promote to:");
        System.out.println("Enter one of the following: Queen, Rook, Bishop, Knight");

        // get user input
        String choice = "";
        while (true) {
            choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("queen") || choice.equals("rook") ||
                    choice.equals("bishop") || choice.equals("knight")) {
                break;
            } else {
                System.out.println("Invalid choice. Please enter Queen, Rook, Bishop, or Knight:");
            }
        }

        // remove the pawn
        board.removePieceAt(pos);

        // create chosen piece
        Piece newPiece = null;
        String symbol;

        switch (choice) {
            case "queen":
                symbol = (color == WHITE) ? "Q" : "q";
                newPiece = new Queen(pos, color, symbol);
                break;
            case "knight":
                symbol = (color == WHITE) ? "N" : "n";
                newPiece = new Knight(pos, color, symbol);
                break;
            case "rook":
                symbol = (color == WHITE) ? "R" : "r";
                newPiece = new Rook(pos, color, symbol);
                break;
            case "bishop":
                symbol = (color == WHITE) ? "B" : "b";
                newPiece = new Bishop(pos, color, symbol);
                break;
        }

        // set the piece
        board.setPieceAt(pos, newPiece);

        // ending statement
        System.out.println("Promotion complete! " + color + " pawn promoted to " + choice + ".");
    }

    // this function will check if a particular king of a certain color has been checkmated
    // the function will only be called under the assumption that the king is already in check
    public boolean isKingInCheckMate(Color color) {
        Position pos = (color == WHITE) ? whiteKingPos : blackKingPos; // get the king under attack
        Move lastPlayed = moveHistory.getLast(); // get the move that resulted in check
        Piece attackingPiece = lastPlayed.getMovedPiece(); // get attacking piece

        // some helpful variables
        int attackingRank = attackingPiece.getPosition().getXPos();
        int attackingFile = attackingPiece.getPosition().getYPos();
        int defendingRank = pos.getXPos();
        int defendingFile = pos.getYPos();
        Color attackingColor = (color == WHITE) ? BLACK : WHITE;
        Piece king = board.getPieceAt(pos); // get the king

        /*k
        the algorithm for finding checkmate involves finding whether:
        1. The king is in check (this is already considered an assumption).
        2. The king has no legal moves left.
        3. The attacking piece cannot be captured by an allied piece.
        4. The path from the attacking piece to the king cannot be occupied by an allied piece.
         */

        // now in the special case that the attacking piece is a knight, all we need to check
        // is if it can be attacked by an allied piece and if not, then it is a checkmate since
        // nobody can block a knight's path.

        // first check whether the king has any legal moves
        List<Position> kingLegalMoves = king.getLegalMoves(board, this);

        // continue checking the legal moves list and see if any positions in the legal moves
        // list are under attack, if not, then return false
        for (Position check: kingLegalMoves) {
            System.out.println("We are checking for king's movement!");
            if (!isSquareUnderAttack(check, color)) {
                System.out.println("King can still escape!");
                return false;
            }
        }

        // secondly check whether the attacking piece can be captured
        boolean underAttack = isSquareUnderAttack(attackingPiece.getPosition(), attackingColor);

        if (underAttack && !(squareAttackedBy instanceof King)) return false; // immediately can return false since the piece can be captured

        // moving forward, attacking piece cannot be captured.
        System.out.println("Attacking piece cannot be captured");

        // ===== EXCEPTION OF KNIGHT CHECK =====
        if (attackingPiece instanceof Knight) return true; // this is a proper checkmate

        // dealing with horizontal checks
        if (attackingRank == defendingRank) { // horizontal check
            // we need to check for every square in between the king and the attacking piece
            int dy = (attackingFile > defendingFile) ? -1 : 1; // go back or forward
            Position checkPos = new Position(attackingRank, attackingFile+dy);
            // now for every square between the attacking piece and the king, check if it can be attacked
            while (checkPos.getYPos() != defendingFile) {
                underAttack = isSquareUnderAttack(checkPos, attackingColor); // check if square can be attacked
                if (underAttack) return false;
                checkPos.updateY(dy);
            }
        }
        // using same principle, we can do vertical check as well
        else if (attackingFile == defendingFile) { // vertical check
            int dx = (attackingRank > defendingRank) ? -1 : 1;
            Position checkPos = new Position(attackingRank + dx, attackingFile);
            while (checkPos.getXPos() != defendingRank) {
                underAttack = isSquareUnderAttack(checkPos, attackingColor);
                if (underAttack) return false;
                checkPos.updateX(dx);
            }
        }
        // lastly diagonal checks
        else if (Math.abs(attackingRank - defendingRank) == Math.abs(attackingFile - defendingFile)) {
            System.out.println("Check for diagonal attacks!");
            int dx = (attackingRank > defendingRank) ? -1 : 1;
            int dy = (attackingFile > defendingFile) ? -1 : 1;
            Position checkPos = new Position(attackingRank + dx, attackingFile + dy);
            while (checkPos.getXPos() != defendingRank && checkPos.getYPos() != defendingFile) {
                underAttack = isSquareUnderAttack(checkPos, attackingColor);
                if (underAttack) {
                    System.out.println("Diagonal checking! This piece can be blocked");
                    return false;
                }
                checkPos.updateX(dx);
                checkPos.updateY(dy);
            }
        }

        return true;
    }

    // check if king with given color is in stalemate
    public boolean isKingInStalemate(Color color) {
        /*
        essentially we need to loop over the current board and check if there are any
        possible legal moves for the defending color left. we need to check for:
        1. do the pieces have any legal moves left that do not result in the king being in check
        2. does the king have any moves left
         */
        // loop over the board and check for each piece
        Position initialPos = new Position(0,0);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                initialPos.changeX(i);
                initialPos.changeY(j);
                Piece piece = board.getPieceAt(initialPos);
                if (piece == null || piece.getColor() != color) continue;

                else if (piece.getColor() == color) { // it is the same color as defending color
                    List<Position> legalMoves = piece.getLegalMoves(board,this);
                    if (legalMoves.isEmpty()) continue;
                    // simulate every move in the legalMoves list and check if the king is in check after that
                    for (Position potentialMove: legalMoves) {
                        // now check if the king is in check after this move
                        boolean isKingSafe = makeMove(initialPos, potentialMove, color, true);
                        if (isKingSafe) { // there is a move possible
                            System.out.println("King is not in stalemate, he can play!");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    // start game and call all appropriate functions
    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        gameOver = false;
        currentTurn = WHITE; // starts with white
        Color opponent = BLACK;
        board.renderBoard();
        int turn = 1; // because the stupid enum doesn't work

        while (!gameOver) {
            Board savedBoard = board.deepCopy();
            Player currentPlayer = (currentTurn == Color.WHITE) ? whitePlayer : blackPlayer;
            opponent = (currentTurn == WHITE) ? BLACK : WHITE;
            String[] moveInput = currentPlayer.getInput(scanner);

            // convert input into move
            List<Position> moveMade = convertToMove(moveInput);
            if (moveMade.get(0) == null) continue;

            // attempt move
            if (!makeMove(moveMade.get(0), moveMade.get(1), currentTurn, false)) {
                System.out.println("Illegal move!! Please try again!");
                continue;
            }

            // check if player's own king is in check after the move (invalid)
            boolean kingInCheck = (currentTurn == Color.WHITE) ? isWhiteKingCheck() : isBlackKingCheck();
            if (kingInCheck) {
                System.out.println("This is not a legal move!! Your king is in check!! Try again!");
                board = savedBoard; // reload the saved board
                continue;
            }

            // opponent’s king check detection
            boolean opponentInCheck = (currentTurn == Color.WHITE) ? isBlackKingCheck() : isWhiteKingCheck();
            if (opponentInCheck) {
                System.out.println("Good job! Opponent's king is in check!");
                // add checkmate checking as well
                boolean checkmate = isKingInCheckMate(opponent);
                if (checkmate) {
                    System.out.println("GAME OVER!!! CONGRATULATIONS!!" + currentTurn + " WINS!");
                    System.out.println("Points for Player White: " + whitePlayer.getPoints());
                    System.out.println("Points for Player Black: " + blackPlayer.getPoints());
                    break;
                }
            }

            // check for stalemate conditions
            boolean stalemateCheck = isKingInStalemate(opponent);
            if (stalemateCheck) {
                System.out.println("Oops! Game over! It's a draw!");
                board.renderBoard();
                System.out.println("Points for Player White: " + whitePlayer.getPoints());
                System.out.println("Points for Player Black: " + blackPlayer.getPoints());
                break;
            }

            // check current promotion status
            Position promotionPos = checkPromotionStatus(currentTurn);

            // if not null, handle it
            if (promotionPos != null) handlePromotion(promotionPos, currentTurn, scanner);

            // successful move
            board.renderBoard();
            System.out.println("Good! Changing turns.");
            System.out.println("Points for Player White: " + whitePlayer.getPoints());
            System.out.println("Points for Player Black: " + blackPlayer.getPoints());

            // switch turn
            currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;

            // here update history
            gameStates.add(board); // for correct moves

        }
        scanner.close();
    }

    public Position getInitialCastlingRook() {
        return initialCastlingRook;
    }

    public Position getDestinationCastlingRook() {
        return destinationCastlingRook;
    }
}
