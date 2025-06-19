package gui;

import java.io.FileNotFoundException;
import java.util.List;

import game.Game;
import board.Position;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pieces.Piece;
import static utils.Color.WHITE;
import static utils.Color.BLACK;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.Cursor;


public class ChessBoard {
    private final BorderPane rootContainer; // main container that holds everything
    private final GridPane board;
    private final StackPane[][] boardTiles;
    private static final int TILE_SIZE = 80;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Game game;
    private final VBox whiteCapturedBox = new VBox(5);
    private final VBox blackCapturedBox = new VBox(5);
    private final Label whitePointsLabel = new Label("White: 0 pts");
    private final Label blackPointsLabel = new Label("Black: 0 pts");

    // sounds
    private final AudioClip moveSound = new AudioClip(getClass().getResource("/resources/sounds/move-self_wav.wav").toExternalForm());
    private final AudioClip castleSound = new AudioClip(getClass().getResource("/resources/sounds/castle.wav").toExternalForm());
    private final AudioClip illegalSound = new AudioClip(getClass().getResource("/resources/sounds/illegal.wav").toExternalForm());
    private final AudioClip checkSound = new AudioClip(getClass().getResource("/resources/sounds/move-check.wav").toExternalForm());
    private final AudioClip captureSound = new AudioClip(getClass().getResource("/resources/sounds/capture.wav").toExternalForm());
    private final AudioClip checkmateSound = new AudioClip(getClass().getResource("/resources/sounds/game-end.wav").toExternalForm());
    private final AudioClip promoteSound = new AudioClip(getClass().getResource("/resources/sounds/promote.wav").toExternalForm());
    private final AudioClip stalemateSound = new AudioClip(getClass().getResource("/resources/sounds/game-draw.wav").toExternalForm());

    // chess engine stuff
    StackPane enPassantTile;


    public ChessBoard() throws FileNotFoundException {
        board = new GridPane();
        boardTiles = new StackPane[8][8];
        initializeBoard();
        setClickListeners();

        game = new Game();

        whitePointsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        blackPointsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        whiteCapturedBox.setAlignment(Pos.CENTER);
        blackCapturedBox.setAlignment(Pos.CENTER);

        // pieces captured by black
        VBox leftCaptured = new VBox(5, blackCapturedBox);
        leftCaptured.setAlignment(Pos.CENTER);

        // pieces captured by white
        VBox rightCaptured = new VBox(5, whiteCapturedBox);
        rightCaptured.setAlignment(Pos.CENTER);

        // hBox in the center to contain leftCaptured, rightCaptured and the board
        HBox centerBox = new HBox(5, leftCaptured, board, rightCaptured);
        centerBox.setAlignment(Pos.CENTER);

        // root container
        rootContainer = new BorderPane();
        rootContainer.setTop(blackPointsLabel);
        rootContainer.setBottom(whitePointsLabel);
        rootContainer.setCenter(centerBox);
        BorderPane.setAlignment(blackPointsLabel, Pos.CENTER);
        BorderPane.setAlignment(whitePointsLabel, Pos.CENTER);
        rootContainer.setStyle("-fx-padding: 10px; -fx-background-color: beige;");
    }

    // return board object
    public GridPane getBoard() {
        return board;
    }

    // return ui container
    public BorderPane getUI() {
        return rootContainer;
    }

    // this method will create an 8x8 grid with alternating black and white squares
    private void initializeBoard() throws FileNotFoundException {
        boolean isWhiteSquare = true;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane tile = new StackPane(); // get the tile

                // square background
                Rectangle square = new Rectangle(TILE_SIZE, TILE_SIZE);
                square.setFill(isWhiteSquare ? Color.SANDYBROWN : Color.SADDLEBROWN);
                tile.getChildren().add(square);

                // add initial pieces using row/col coordinates
                String pieceSymbol = getPieceName(row,col);
                if (pieceSymbol != null) {
                    Image image = new Image(getClass().getResourceAsStream("/resources/images/" + pieceSymbol + ".png"));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(TILE_SIZE-20);
                    imageView.setFitHeight(TILE_SIZE-20);
                    // depending on the color of the piece, we will set an id that will be useful later
                    imageView.setUserData((pieceSymbol.charAt(0) == 'w') ? WHITE : BLACK);
                    tile.getChildren().add(imageView);
                }

                board.add(tile, col, row);
                boardTiles[row][col] = tile;
                isWhiteSquare = !isWhiteSquare;
            }
            isWhiteSquare = !isWhiteSquare;
        }
    }

    // set up event listeners for clicking pieces
    public void setClickListeners() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int currentRow = row;
                int currentCol = col;

                boardTiles[row][col].setOnMouseClicked(event -> {
                    removeHighlight();
                    handleTileClick(currentRow, currentCol);
                });
            }
        }
    }

    // method to highlight clicked cells
    public void highlightTile(int row, int col) {
        StackPane tile = boardTiles[row][col];
        Rectangle highlight = new Rectangle(TILE_SIZE, TILE_SIZE); // create highlighting layer
        highlight.setFill(Color.rgb(100,255,100,0.3)); // transparent yellow
        highlight.setMouseTransparent(true); // so user does not click on rectangle
        highlight.setId("highlight");
        tile.getChildren().add(highlight);
    }

    // method to remove highlight once destination cell is selected
    public void removeHighlight() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane tile = boardTiles[row][col];
                // Remove any extra layers beyond the first 1-2 (square and piece)
                tile.getChildren().removeIf(node -> node instanceof Rectangle && "highlight".equals(node.getId()));
            }
        }
    }

    // method to handle tile click and various game mechanics
    public void handleTileClick(int row, int col) {
        boolean gameOver = false;
        // if this is the first click
        if (selectedCol == -1) {
            if (boardTiles[row][col].getChildren().size() > 1) {
                // check if the current turn reflects the piece's color
                ImageView piece = (ImageView) boardTiles[row][col].getChildren().get(1); // get the piece
                if (piece.getUserData() == game.getCurrentTurn()) { // selected piece matches the game turn
                    selectedRow = row;
                    selectedCol = col;
                    System.out.println("Selected piece at: " + row + "," + col);
                    highlightTile(row, col); // highlight selected piece
                    addHighlightToPath(row, col); // coordinate of the piece
                }
                else {
                    System.out.println("Current turn is " + game.getCurrentTurn() + "!");
                }
            }
            else {
                System.out.println("This is an empty square! Select a square with a piece!");
            }
        }
        else {
            // second click
            removeHighlight(); // remove all highlights
            StackPane fromTile = boardTiles[selectedRow][selectedCol]; // first tile
            StackPane toTile = boardTiles[row][col]; // second tile

            // if its the same square selected again then do nothing
            if (fromTile == toTile) {
                selectedRow = -1;
                selectedCol = -1;
                return;
            }

            // first check the validity of the move
            boolean valid = game.makeMove(new Position(selectedRow, selectedCol), new Position(row,col), game.getCurrentTurn(), false);

            if (!valid) {
                // this move is wrong and nothing will change
                System.out.println("This move is incorrect! Please try again!");
                selectedRow = -1;
                selectedCol = -1;
                illegalSound.play();
            }
            else {
                boolean isCapture = false;

                // see if destination square has an existing piece
                if (toTile.getChildren().size() > 1) isCapture = true;

                ImageView piece = (ImageView) fromTile.getChildren().get(1);
                fromTile.getChildren().remove(piece);

                // check if piece is a pawn that moved to the last rank and needs to be promoted
                Position promotionPos = game.checkPromotionStatus(game.getCurrentTurn());
                if (promotionPos != null) promotionHandler(promotionPos);

                // ===== CHECK AND CHECKMATE =====
                // check if opponent king is in check
                System.out.println("Checking if opponent king is in check!");
                boolean opponentCheck = game.isKingInCheck(game.getOpponent());
                if (opponentCheck) {
                    // check if opponent's king is in checkmate
                    gameOver = game.isKingInCheckMate(game.getOpponent());
                    if (gameOver) System.out.println("Game is over!");
                    else System.out.println("Game can still go on");
                }

                // ===== STALEMATE =====
                boolean gameDraw = game.isKingInStalemate(game.getOpponent());
                if (gameDraw) {
                    System.out.println("Game over! It ends in a draw!");
                }

                // ===== EN PASSANT=====
                Piece enPassant = game.getEnPassantTarget();
                if (enPassant != null) { // meaning this pawn has moved two steps
                    enPassantTile = boardTiles[row][col]; // save it
                    System.out.println("Set an en passant target at: " + row + " " + col);
                }
                if (enPassantTile == null) System.out.println("The fuck");
                if (game.getEnPassantMove() && enPassantTile != null) { // this is a legal en passant
                    ImageView enPassantCapture = (ImageView) enPassantTile.getChildren().get(1);
                    enPassantTile.getChildren().remove(enPassantCapture);
                    enPassantTile = null;
                }

                // ===== CASTLING MECHANICS =====
                if (game.getCastlingMove()) {
                    Position initial = game.getInitialCastlingRook();
                    Position dest = game.getDestinationCastlingRook();

                    fromTile = boardTiles[initial.getXPos()][initial.getYPos()];
                    StackPane destinationTile = boardTiles[dest.getXPos()][dest.getYPos()];

                    // move the rook
                    ImageView rook = (ImageView) fromTile.getChildren().get(1); // get the piece
                    fromTile.getChildren().remove(rook);
                    destinationTile.getChildren().add(rook);
                    System.out.println("Castling move!");
                    castleSound.play();
                }

                // check if there is a piece on the destination tile
                toTile.getChildren().removeIf(node -> node instanceof ImageView && game.getOpponent().equals(node.getUserData())); // remove opposing piece
                if (promotionPos == null) {
                    toTile.getChildren().add(piece);
                }

                // play sounds
                if (isCapture) {
                    captureSound.play();
                    Piece capturedPiece = game.getRecentCaptured();
                    String prefix = (game.getCurrentTurn() == WHITE) ? "b" : "w";
                    String capturedSymbol = prefix + capturedPiece.getSymbol().toLowerCase();
                    Image img = new Image(getClass().getResourceAsStream("/resources/images/" + capturedSymbol + ".png"));
                    ImageView capturedView = new ImageView(img);
                    capturedView.setFitWidth(30); // small size
                    capturedView.setFitHeight(30);

                    if (game.getCurrentTurn() == WHITE) {
                        whitePointsLabel.setText("White: " + game.getWhitePlayer().getPoints() + " pts");
                        whiteCapturedBox.getChildren().add(capturedView);
                    } else {
                        blackPointsLabel.setText("Black: " + game.getBlackPlayer().getPoints() + " pts");
                        blackCapturedBox.getChildren().add(capturedView);
                    }

                    if (opponentCheck) {
                        if (gameOver)  {
                            checkmateSound.play();
                            showGameOverPopup(game.getWhitePlayer().getPoints(), game.getBlackPlayer().getPoints(), false);
                            return;
                        }
                        else checkSound.play();
                    }
                }
                else if (opponentCheck){
                    // if opponent is checkmated as well, then play game over sound
                    if (gameOver) {
                        checkmateSound.play();
                    }
                    else checkSound.play();
                }
                else if (gameDraw) {
                    // play draw sound and show game over popup same as checkmate
                    stalemateSound.play();
                    showGameOverPopup(game.getWhitePlayer().getPoints(), game.getBlackPlayer().getPoints(), true);
                }
                else {
                    moveSound.play();
                }
                game.switchTurns();
                game.getBoard().renderBoard();
            }

            selectedRow = -1;
            selectedCol = -1;
        }
    }

    // function to update pawn image
    public void updatePromotionImage(Position pos, String pieceSymbol) {
        StackPane tile = boardTiles[pos.getXPos()][pos.getYPos()];

        // Remove existing pawn image
        tile.getChildren().removeIf(node -> node instanceof ImageView && game.getCurrentTurn().equals(node.getUserData()));

        // Load and add new piece
        Image image = new Image(getClass().getResourceAsStream("/resources/images/" + pieceSymbol + ".png"));
        ImageView newPiece = new ImageView(image);
        newPiece.setFitWidth(TILE_SIZE - 20);
        newPiece.setFitHeight(TILE_SIZE - 20);
        newPiece.setUserData((pieceSymbol.charAt(0) == 'w') ? WHITE : BLACK);
        tile.getChildren().add(newPiece);
    }

    // function to handle promotion of pawn based on user choice
    public void promotionHandler(Position pawnPosition) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // blocks clicks to the main window
        popupStage.setTitle("Choose Promotion Piece");

        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);

        String[] pieces = {"q", "r", "b", "n"}; // queen, rook, bishop, knight
        String colorPrefix = (game.getCurrentTurn() == WHITE) ? "w" : "b";

        for (String pieceType : pieces) {
            String imagePath = "/resources/images/" + colorPrefix + pieceType + ".png";
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(60);
            imageView.setFitWidth(60);

            // hover animation
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), imageView);
            scaleUp.setToX(1.2);
            scaleUp.setToY(1.2);

            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), imageView);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);

            imageView.setOnMouseEntered(e -> {
                scaleDown.stop(); // stop shrinking if still running
                scaleUp.playFromStart(); // play enlarging animation
            });

            imageView.setOnMouseExited(e -> {
                scaleUp.stop(); // stop enlarging if still running
                scaleDown.playFromStart(); // play shrinking animation
            });

            imageView.setCursor(Cursor.HAND); // make it look clickable

            imageView.setOnMouseClicked(e -> {
                // promote the pawn to the piece that's clicked
                game.handlePromotionGUI(pawnPosition, game.getCurrentTurn(), pieceType); // handle promotion in backend
                updatePromotionImage(pawnPosition, colorPrefix + pieceType); // replace pawn
                popupStage.close();
                promoteSound.play();
            });

            box.getChildren().add(imageView);
        }

        Scene scene = new Scene(box, 300, 100);
        popupStage.setScene(scene);
        popupStage.showAndWait(); // wait for user choice
    }

    // play again functionality
    private void showGameOverPopup(int whiteScore, int blackScore, boolean draw) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Game Over");


        Label resultLabel;
        if (draw) {
            resultLabel = new Label("The game ends in a draw!");
        }
        else {
            resultLabel = new Label(game.getCurrentTurn() + " wins!");
        }
        resultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label scoreLabel = new Label("White: " + whiteScore + " pts  |  Black: " + blackScore + " pts");
        scoreLabel.setStyle("-fx-font-size: 14px;");

        javafx.scene.control.Button playAgain = new javafx.scene.control.Button("Play Again");
        playAgain.setOnAction(e -> {
            try {
                resetBoard(); // restart the game
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            popupStage.close();
        });

        VBox layout = new VBox(10, resultLabel, scoreLabel, playAgain);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        Scene scene = new Scene(layout, 300, 150);
        popupStage.setScene(scene);
        popupStage.show();
    }

    // reset the game for restart functionality
    private void resetBoard() throws FileNotFoundException {
        System.out.println("We are restarting!");
        board.getChildren().clear(); // clear the board grid
        initializeBoard(); // reinitialize board with pieces
        setClickListeners(); // reattach click listeners to new tiles
        game = new Game(); // reset the game engine
        whiteCapturedBox.getChildren().clear();
        blackCapturedBox.getChildren().clear();
        whitePointsLabel.setText("White: 0 pts");
        blackPointsLabel.setText("Black: 0 pts");
        selectedRow = -1;
        selectedCol = -1;
    }


    // this function will add highlights to all the paths of the piece
    public void addHighlightToPath(int row, int col) {
        // get piece at the coordinate
        Piece selectedPiece = game.getBoard().getPieceAt(new Position(row, col));
        // now for all the legal moves, we will highlight the square
        List<Position> legalMoves = selectedPiece.getLegalMoves(game.getBoard(), game);
        // iterate over the list and call highlight method
        for (Position position: legalMoves) {
            highlightTile(position.getXPos(), position.getYPos());
        }
    }

    // function to return piece image path based on coordinates
    public String getPieceName(int x, int y) {
        // for pawns
        if (x==1) return "bp";
        if (x==6) return "wp";

        // other pieces
        if (x==0 || x==7) {
            boolean white = (x==7);
            switch (y) {
                case 0: case 7: return white ? "wr" : "br";
                case 1: case 6: return white ? "wn" : "bn";
                case 2: case 5: return white ? "wb" : "bb";
                case 3: return white ? "wq" : "bq";
                case 4: return white ? "wk" : "bk";
            }
        }

        return null;
    }

}
