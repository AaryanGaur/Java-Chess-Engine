package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

public class ChessApp extends Application {

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        ChessBoard chessBoard = new ChessBoard();

        Scene scene = new Scene(chessBoard.getUI(), 800, 700);
        primaryStage.setTitle("Chess Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
