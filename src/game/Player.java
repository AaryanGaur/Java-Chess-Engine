package game;

import utils.Color;

import java.util.Scanner;

/*
 The player class holds information about its color, and what the move will be
 It will also have information about the points.
 */
public class Player {
    private final Color color;
    private int points;

    public Player(Color color) {
        this.color = color;
        points = 0; // initialized to 0
    }

    public Color getColor() {
        return color;
    }

    public int getPoints() {
        return points;
    }

    public void increasePoints(int captured) {
        points+=captured;
    }

    public String[] getInput(Scanner scanner) {
        System.out.println(color + "'s turn to enter a move!");
        boolean valid = false;
        String[] tokens = {};
        while (!valid) {
            System.out.print("Please enter move in the form -> \"from to\" for eg. e2 e4: ");
            String inputLine = scanner.nextLine();

            // break tokens
            tokens = inputLine.split(" ");
            if (tokens.length != 2 || tokens[0].length() != 2 || tokens[1].length() != 2) {
                System.out.println("Invalid input format. Use format like 'e2 e4'.");
            }
            else {
                valid = true;
            }
        }

        return tokens;
    }

}
