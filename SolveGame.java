import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.*;

// The SolveGame class provides the logic for solving and animating a game board towards achieving
// a specified target word. It employs a Breadth-First Search (BFS) algorithm to determine the shortest
// sequence of moves to form the target word while handling game mechanics like tile generation, movement,
// merging, and game-over conditions.

public class SolveGame {
    // Instance variables
    private final Board gameBoard; // Current state of the game board
    private final Pane gamePane; // Pane for game UI components
    private List<Directions> directionsSequence; // Sequence of directions for solving the game
    private List<TileInfo> newTiles; // Information about new tiles appearing on the board
    private final String targetWord; // Target word for the game level
    private final Tile[][] board;

    // Constructor: Initializes the board state, solves the game, and animates the solution sequence
    public SolveGame(Pane gamePane, Tile[][] initialBoard, String targetWord) {
        // Initial configuration of the game board
        this.gamePane = gamePane;
        this.targetWord = targetWord;
        this.gameBoard = new Board(gamePane, initialBoard, targetWord);
        this.gameBoard.updateTimeline.stop();
        this.board = initialBoard;

        // Solve the game and retrieve the solution sequence
        this.findGameSolution(initialBoard);

        // Animate the solution
        this.animateGameSolver(this.directionsSequence, this.newTiles);
    }

    // Solves the game by using a Breadth-First Search (BFS) approach to find the shortest sequence
    // of moves (directions) that transforms the initial board into a state where the target word is formed
    private void findGameSolution(Tile[][] initialBoard) {
        // Initialize the queue for BFS (each element is a Triple: board state, direction sequence, and new tiles)
        Queue<Triple> queue = new LinkedList<>();
        // Set to track visited states
        Set<String> visited = new HashSet<>();
        // Start with an empty sequence of directions and new tiles
        List<Directions> initialDirections = new ArrayList<>();
        List<TileInfo> initialTiles = new ArrayList<>();

        // Enqueue the initial state
        queue.offer(new Triple(initialBoard, initialDirections, initialTiles));
        visited.add(this.serializeBoard(initialBoard));

        // Perform BFS
        while (!queue.isEmpty()) {
            // Dequeue the current board state and its corresponding sequences
            Triple currentTriple = queue.poll();
            Tile[][] currentBoard = currentTriple.board;
            List<Directions> currentSequence = currentTriple.sequence;
            List<TileInfo> currentTiles = currentTriple.newTiles;

            // Check for game over condition
            if (this.gameBoard.checkForLoss(currentBoard, 0)){
                this.directionsSequence = Collections.singletonList(Directions.INVALID); // Indicate invalid move sequence
                this.newTiles = new ArrayList<>();
                return;
            }

            // Check if the target word exists on the current board
            if (this.gameBoard.checkForWin(currentBoard, this.targetWord).getKey()) {
                this.directionsSequence = currentSequence; // Store the valid direction sequence
                this.newTiles = currentTiles;  // Store the associated tile information
                return;
            }

            // Explore each possible direction
            for (Directions direction : Directions.values()) {
                // Skip the INVALID direction
                if (direction == Directions.INVALID) continue;

                // Create a deep copy of the current board
                Tile[][] newBoard = this.copyBoard(currentBoard);

                // Apply the move and get information about any new tiles
                TileInfo newTileInfo = this.applyMove(newBoard, direction);

                // Serialize the new board state to check for uniqueness
                String serializedBoard = this.serializeBoard(newBoard);

                // If the new state is unvisited, process it
                if (!visited.contains(serializedBoard)) {
                    visited.add(serializedBoard); // Mark the state as visited

                    // Create updated sequences for directions and new tiles
                    List<Directions> newSequence = new ArrayList<>(currentSequence);
                    newSequence.add(direction);

                    List<TileInfo> newTileSequence = new ArrayList<>(currentTiles);
                    if (newTileInfo != null) newTileSequence.add(newTileInfo);

                    // Enqueue the new state with updated sequences
                    queue.offer(new Triple(newBoard, newSequence, newTileSequence));
                }
            }
        }

        // If no solution is found, indicate failure
        this.directionsSequence = Collections.singletonList(Directions.INVALID);
        this.newTiles = new ArrayList<>();
    }

    // Applies a specified move to the board and returns information about any resulting tile changes
    private TileInfo applyMove(Tile[][] board, Directions direction) {
        switch (direction) {
            case UP:
                return this.moveUp(board);
            case DOWN:
                return this.moveDown(board);
            case LEFT:
                return this.moveLeft(board);
            case RIGHT:
                return this.moveRight(board);
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    // Moves all tiles on the board to the left, merging tiles with the same letter if the game is not won
    private TileInfo moveLeft(Tile[][] board) {
        // Shift all tiles to the left
        this.gameBoard.moveLeft(board,false, false, true);

        // Check for a win condition
        if (this.gameBoard.checkForWin(board, this.targetWord).getKey()) return null;

        // Merge tiles with the same letter
        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < Constants.NUMBER_OF_COLUMNS - 1; col++) {
                Tile currentTile = board[row][col];
                Tile nextTile = board[row][col + 1];

                if (currentTile != null && nextTile != null && currentTile.getLetter() == nextTile.getLetter()) {
                    currentTile.merge();
                    board[row][col + 1] = null;
                }
            }
        }

        // Shift again to fill gaps created by merging
        this.gameBoard.moveLeft(board, false, false, true);

        // Generate a new tile
        return this.gameBoard.generateNewTile(board, true);
    }


    // Moves all tiles on the board to the right, merging tiles with the same letter if the game is not won //
    private TileInfo moveRight(Tile[][] board) {
        // Shift all tiles to the right
        this.gameBoard.moveRight(board,false, false, true);

        // Check for a win condition
        if (this.gameBoard.checkForWin(board, this.targetWord).getKey()) return null;

        // Merge tiles with the same letter
        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
            for (int col = Constants.NUMBER_OF_COLUMNS - 1; col > 0; col--) {
                Tile currentTile = board[row][col];
                Tile nextTile = board[row][col - 1];

                if (currentTile != null && nextTile != null && currentTile.getLetter() == nextTile.getLetter()) {
                    currentTile.merge();
                    board[row][col - 1] = null;
                }
            }
        }

        // Shift again to fill gaps created by merging
        this.gameBoard.moveRight(board,false, false, true);

        // Generate a new tile
        return this.gameBoard.generateNewTile(board, true);
    }

    // Moves all tiles on the board upwards, merging tiles with the same letter if the game is not won //
    private TileInfo moveUp(Tile[][] board) {
        // Shift all tiles upward
        this.gameBoard.moveUp(board,false, false, true);

        // Check for a win condition
        if (this.gameBoard.checkForWin(board, this.targetWord).getKey()) return null;

        // Merge tiles with the same letter
        for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
            for (int row = 0; row < Constants.NUMBER_OF_ROWS - 1; row++) {
                Tile currentTile = board[row][col];
                Tile nextTile = board[row + 1][col];

                if (currentTile != null && nextTile != null && currentTile.getLetter() == nextTile.getLetter()) {
                    currentTile.merge();
                    board[row + 1][col] = null;
                }
            }
        }

        // Shift again to fill gaps created by merging
        this.gameBoard.moveUp(board,false, false, true);

        // Generate a new tile
        return this.gameBoard.generateNewTile(board, true);
    }

    // Moves all tiles on the board downwards, merging tiles with the same letter if the game is not won
    private TileInfo moveDown(Tile[][] board) {
        // Shift all tiles downward to fill gaps
        this.gameBoard.moveDown(board,false, false, true);

        // Check for a win condition
        if (this.gameBoard.checkForWin(board, this.targetWord).getKey()) return null;

        // Merge tiles with the same letter
        for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
            for (int row = Constants.NUMBER_OF_ROWS - 1; row > 0; row--) { // Start from the bottom
                Tile currentTile = board[row][col];
                Tile nextTile = board[row - 1][col];

                if (currentTile != null && nextTile != null && currentTile.getLetter() == nextTile.getLetter()) {
                    currentTile.merge();
                    board[row - 1][col] = null;
                }
            }
        }

        // Shift again to fill gaps created by merging
        this.gameBoard.moveDown(board,false, false, true);

        // Generate a new tile
        return this.gameBoard.generateNewTile(board, true);
    }

    // Creates a copy of the given game board
    private Tile[][] copyBoard(Tile[][] board) {
        Tile[][] copy = new Tile[Constants.NUMBER_OF_ROWS][Constants.NUMBER_OF_COLUMNS];

        // Iterate through each cell of the board
        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
                if (board[row][col] != null) copy[row][col] = new Tile(board[row][col]); // Copy each tile
            }
        }
        return copy;
    }

    // Serializes the board into a unique string representation
    private String serializeBoard(Tile[][] board) {
        StringBuilder serialized = new StringBuilder();

        // Iterate through each row and tile on the board
        for (Tile[] row : board) {
            for (Tile tile : row) {
                serialized.append(tile != null ? tile.getLetter() : '0');} // Append the tile's letter or '0' if empty
        }
        return serialized.toString();
    }

    // Animates a sequence of game solver moves and corresponding new tile placements
    private void animateGameSolver(List<Directions> directionsSequence, List<TileInfo> newTiles) {
        // Handle the invalid case
        if (this.directionsSequence.equals(Collections.singletonList(Directions.INVALID))) {
            this.noSolutionFound();
            return;
        }

        SequentialTransition sequentialTransition = new SequentialTransition();

        for (int i = 0; i < directionsSequence.size(); i++) {
            Directions direction = directionsSequence.get(i);

            // Declare variables for tile position and letter with default values
            int newTileRow;
            int newTileCol;
            char newTileLetter;

            // Ensure the index is within bounds for newTiles
            if (i < newTiles.size()) {
                newTileRow = newTiles.get(i).row;
                newTileCol = newTiles.get(i).col;
                newTileLetter = newTiles.get(i).letter;
            } else {
                newTileLetter = '\0';
                newTileCol = -1;
                newTileRow = -1;
            }

            // Create a PauseTransition for the delay
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));

            // Create an action for the direction and add it to the SequentialTransition
            PauseTransition actionTransition = new PauseTransition(Duration.ZERO);
            actionTransition.setOnFinished(e -> {
                switch (direction) {
                    case LEFT:
                        this.gameBoard.moveLeft(this.board, false, false, false);
                        this.gameBoard.animateHorizontalTileMovement(Directions.LEFT, false, newTileRow, newTileCol, newTileLetter);
                        break;
                    case RIGHT:
                        this.gameBoard.moveRight(this.board, false, false, false);
                        this.gameBoard.animateHorizontalTileMovement(Directions.RIGHT, false, newTileRow, newTileCol, newTileLetter);
                        break;
                    case UP:
                        this.gameBoard.moveUp(this.board, false, false, false);
                        this.gameBoard.animateVerticalTileMovement(Directions.UP, false, newTileRow, newTileCol, newTileLetter);
                        break;
                    case DOWN:
                        this.gameBoard.moveDown(this.board, false, false, false);
                        this.gameBoard.animateVerticalTileMovement(Directions.DOWN, false, newTileRow, newTileCol, newTileLetter);
                        break;
                }
            });

            // Add the action and pause to the SequentialTransition
            sequentialTransition.getChildren().addAll(actionTransition, pause);
        }

        // Play the animation sequence
        sequentialTransition.play();
    }

    // Displays a "No Solution" message on the game pane when no solution is found
    private void noSolutionFound(){
        // Create a rectangle to display the losing message background
        Rectangle endBox = new Rectangle(Constants.END_BOX_X, Constants.END_BOX_Y,
                Constants.END_BOX_WIDTH, Constants.END_BOX_HEIGHT);
        endBox.setFill(Color.web(Constants.END_BOX_COLOR));
        endBox.setArcWidth(Constants.END_BOX_ARC);
        endBox.setArcHeight(Constants.END_BOX_ARC);
        endBox.setId("endBox");

        // Create the text to display "You Lose"
        Text noSolutionText = new Text("No Solution");
        noSolutionText.setFill(Color.web(Constants.END_TEXT_COLOR));
        noSolutionText.setFont(Constants.NO_SOLUTION_FONT);
        noSolutionText.setLayoutX(Constants.END_TEXT_X - 6);
        noSolutionText.setLayoutY(Constants.END_TEXT_Y);
        noSolutionText.setId("endText");

        // Add the losing message elements (box and text) to the game pane
        this.gamePane.getChildren().addAll(endBox, noSolutionText);
    }


    // Triple class to hold board state, direction sequence, and new tile sequence */
    private static class Triple {
        Tile[][] board;
        List<Directions> sequence;
        List<TileInfo> newTiles;

        public Triple(Tile[][] board, List<Directions> sequence, List<TileInfo> newTiles) {
            this.board = board;
            this.sequence = sequence;
            this.newTiles = newTiles;
        }
    }

    // Tile information class to hold information about a tile on the game board (row, column, letter)
    public static class TileInfo {
        int row, col;
        char letter;

        public TileInfo(int row, int col, char letter) {
            this.row = row;
            this.col = col;
            this.letter = letter;
        }

        @Override
        public String toString() {
            return String.format("TileInfo[row=%d, col=%d, letter=%c]", this.row, this.col, this.letter);
        }
    }
}