# Java Chess Game ♟️

![King Piece](/resources/images/wk.png)

This is a full-featured chess game built using **Java** and **JavaFX**.

It's the biggest and most exciting personal project I’ve completed so far. I started by building a console-based chess engine, then extended it with a complete graphical user interface using JavaFX - including custom sounds, move validation, castling, en passant, pawn promotion, captured piece tracking, check/checkmate detection, and a game-over popup with a restart option.

## Structure

The project is divided into the following components:

- `game/` — Manages the overall game flow, turn switching, state checks (check, checkmate, stalemate), promotion logic, etc. It is quite literally the brain of the engine. 
- `board/` — Contains the board representation and position logic. It manages the piece positioning for the engine. 
- `pieces/` — Abstract `Piece` class and all specific piece subclasses (`Pawn`, `Knight`, `Bishop`, etc.) with their movement rules.
- `gui/` — JavaFX-based graphical interface, handles rendering, event listening, sound effects, and UI feedback.
- `resources/` — Images and sounds used in the GUI.

## 🚀 Running the Game (JavaFX Configuration)

To run this project using JavaFX, you'll need to:

1. Have Java 17+ and JavaFX SDK installed.
2. Pass JavaFX modules as VM arguments.

### Run Configuration: Please add the following line to vm-options under run configurations. 

--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml --add-exports javafx.base/com.sun.javafx=ALL-UNNAMED --add-exports javafx.graphics/com.sun.glass.utils=ALL-UNNAMED

Make sure to replace `/path/to/javafx-sdk/lib` with the actual path on your machine.
You can also create a `.bat` or `.sh` script to run it more easily from the command line.

## Summary of what I learnt doing this project
Since this was my first real java project with front-end and back-end management, I have learnt quite a bit about optimal software design and project maintenance. 
I'd like to summarize what I learnt in the short points below:

- Clean object-oriented design for game rules and board state management.
- JavaFX UI creation: layout management, event handling, and styling.
- Playing sound effects with `AudioClip`.
- Handling user interaction like promotion choices and game restarts.
- Debugging and testing game logic thoroughly, especially edge cases like en passant and stalemate.
- Managing communication between backend engine and frontend objects.

Thanks for reading — and feel free to clone, fork, or suggest improvements!

