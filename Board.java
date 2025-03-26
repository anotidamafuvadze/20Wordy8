import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Random;

// The Board class manages the game grid, handling tile generation, movement, and merging. It updates the game
// state by responding to user inputs and periodically checking for target letters to change tile colors.

public class Board {

    // Instance variables
    private final Tile[][] board; // 2D array representing the game grid of tiles
    private final Pane gamePane; // The pane that holds and displays the game content
    private final HashMap<Tile, Integer> movingTiles; // Mapping of tiles to their movement targets during animations
    public Timeline updateTimeline; // Timeline for periodic game updates (e.g., animations, checks)
    private Timeline move; // Timeline for handling tile movement animations
    private String targetWord; // The target word that needs to be formed or reached in the game
    private boolean isAllowedToMove; // Flag indicating whether tile movement is currently allowed

    // Constructor: Initializes the game board, target word, and game state
    public Board(Pane gamePane, Tile[][] board, String targetWord) {
        this.gamePane = gamePane;
        this.board = board;
        this.targetWord = targetWord;
        this.movingTiles = new HashMap<>();
        this.isAllowedToMove = true;
        this.checkForTargetLetters();
    }

    // Sets the target word for the game
    public void setTargetWord(String targetWord) {this.targetWord = targetWord;}

    // Initializes the starting tiles on the board
    public void generateStartingTiles() {
        Random random = new Random();

        // Generate the first random tile with the letter 'A'
        int tileRow = random.nextInt(Constants.NUMBER_OF_ROWS);
        int tileCol = random.nextInt(Constants.NUMBER_OF_COLUMNS);

        // Calculate the position of the tile on the board (in pixels)
        int rowY = tileRow * Constants.TILE_HEIGHT;
        int colX = tileCol * Constants.TILE_WIDTH;

        // Create and place the first tile at the random position
        Tile tile = new Tile(this.gamePane, rowY, colX, tileRow, tileCol);
        this.board[tileRow][tileCol] = tile;
        tile.setLetter('A');

        // Generate one random blank tile, ensuring it does not overlap with the starting tile
        int row, col;
        do {
            row = random.nextInt(Constants.NUMBER_OF_ROWS);
            col = random.nextInt(Constants.NUMBER_OF_COLUMNS);
        } while (row == tileRow && col == tileCol);  // Ensure the blank tile isn't at the same position

        // Calculate the position of the blank tile on the board (in pixels)
        int blankTileRowY = row * Constants.TILE_HEIGHT - 10;
        int blankTileColX = col * Constants.TILE_WIDTH;

        // Create and place the blank tile at the calculated position
        BlankTile blankTile = new BlankTile(this.gamePane, blankTileRowY, blankTileColX, row, col);
        this.board[row][col] = blankTile;
        blankTile.setLetter('A');
    }

    // Generates a new tile on the board with a random letter
    public SolveGame.TileInfo generateNewTile(Tile[][] board, boolean isSimulation) {
        Random random = new Random();

        // Randomly choose a tile letter ('A' 90% of the time, 'B' 10% of the time)
        char tileLetter = random.nextInt(100) < 90 ? 'A' : 'B';

        // Find a random, unoccupied position on the board
        int tileRow, tileCol;
        do {
            tileRow = random.nextInt(Constants.NUMBER_OF_ROWS);
            tileCol = random.nextInt(Constants.NUMBER_OF_COLUMNS);
        } while (board[tileRow][tileCol] != null);  // Ensure the tile position is empty

        // Check if there are any blank tiles on the board
        boolean hasBlankTile = false;
        outerLoop:
        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
                if (board[row][col] != null &&
                        board[row][col].getTileColor().equals(Constants.BLANK_TILE_COLOR)) {
                    hasBlankTile = true;
                    break outerLoop;  // Exit both loops once a blank tile is found
                }
            }
        }

        // Calculate the position for the new tile
        int rowY = tileRow * Constants.TILE_HEIGHT;
        int colX = tileCol * Constants.TILE_WIDTH;

        if (!isSimulation) {
            // Create and place the new tile based on the presence of a blank tile
            if (hasBlankTile) {
                Tile newTile = new Tile(this.gamePane, rowY, colX, tileRow, tileCol);
                newTile.setLetter(tileLetter);
                board[tileRow][tileCol] = newTile;
            } else {
                BlankTile blankTile = new BlankTile(this.gamePane, rowY - 10, colX, tileRow, tileCol);
                blankTile.setLetter(tileLetter);
                board[tileRow][tileCol] = blankTile;
            }
        }
        else{
            // Create a new tile and place it on the board
            Tile newTile = new Tile(tileRow, tileCol);
            newTile.setLetter(tileLetter);
            board[tileRow][tileCol] = newTile;
        }
        // Return the tile information for tracking
        return new SolveGame.TileInfo(tileRow, tileCol, tileLetter);
    }

    // Generates a specific tile at the given row and column with the specified letter
    public void generateSpecificTile(int tileRow, int tileCol, char tileLetter) {
        // Ensure the row index is valid before proceeding
        if (tileRow != -1) {
            // Calculate the tile's position in pixels
            int posY = tileRow * Constants.TILE_HEIGHT;
            int posX = tileCol * Constants.TILE_WIDTH;

            // Create a new tile and assign its letter
            Tile newTile = new Tile(this.gamePane, posY, posX, tileRow, tileCol);
            newTile.setLetter(tileLetter);

            // Place the new tile on the game board
            this.board[tileRow][tileCol] = newTile;
        }
    }

    // Checks for a win by identifying a matching 3-letter word in rows or columns
    public Pair<Boolean, Tile[]> checkForWin(Tile[][] board, String targetWord) {
        // Check each row for a 3-letter word match
        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
            for (int startCol = 0; startCol <= Constants.NUMBER_OF_COLUMNS - 3; startCol++) { // Sliding window of size 3
                if (this.checkWordInLine(row, startCol, board, true, targetWord).getKey())
                    return this.checkWordInLine(row, startCol, board, true, targetWord); // Exit if a winning word is found
            }
        }

        // Check each column for a 3-letter word match
        for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
            for (int startRow = 0; startRow <= Constants.NUMBER_OF_ROWS - 3; startRow++) { // Sliding window of size 3
                if (this.checkWordInLine(startRow, col, board, false, targetWord).getKey())
                    return this.checkWordInLine(startRow, col, board, false, targetWord); // Exit if a winning word is found
            }
        }
        return new Pair<>(false, null);
    }

    // Checks a 3-letter sequence in a row or column for a match with the target word
    private Pair<Boolean, Tile[]> checkWordInLine(int startRow, int startCol, Tile[][] board, boolean isRowCheck, String targetWord) {
        StringBuilder currentWord = new StringBuilder();
        Tile[] winningTiles = new Tile[3];
        boolean allTilesPresent = true;

        // Build the 3-letter word
        for (int offset = 0; offset < 3; offset++) {
            int row = isRowCheck ? startRow : startRow + offset;
            int col = isRowCheck ? startCol + offset : startCol;
            Tile currentTile = board[row][col];

            if (currentTile == null) {
                allTilesPresent = false; // Stop if a tile is missing
                break;
            }
            currentWord.append(currentTile.getLetter());
            winningTiles[offset] = currentTile;
        }

        // Check for a match with the target word
        if (allTilesPresent && currentWord.toString().equals(targetWord)) {
            //this.gameIsWon(winningTiles);
            return new Pair<>(true, winningTiles); // Winning word found
        }

        return new Pair<>(false, null); // No match found
    }

    // Checks if the game is lost
    public boolean checkForLoss(Tile[][] board, int removeCounter) {

        // Check if all tiles are occupied
        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
                if (board[row][col] == null) {
                    return false;  // Found an empty tile, game is not over
                }
            }
        }

        // Check if no tiles can merge (no adjacent same tiles)
        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
                Tile currentTile = board[row][col];

                if (currentTile != null) {
                    // Check if the tile can merge with its right neighbor
                    if (col < Constants.NUMBER_OF_COLUMNS - 1 && board[row][col + 1] != null && currentTile.getLetter()
                            == board[row][col + 1].getLetter()) {
                        return false;  // Found a merge-able pair horizontally
                    }

                    // Check if the tile can merge with its bottom neighbor
                    if (row < Constants.NUMBER_OF_ROWS - 1 && board[row + 1][col] != null && currentTile.getLetter() ==
                            board[row + 1][col].getLetter()) {
                        return false;  // Found a merge-able pair vertically
                    }
                }
            }
        }

        // If no empty tiles, no possible merges, and no removes left game is over
        return removeCounter > 0;
    }


    // Checks each tile to see if it contains a target letter and updates its color accordingly
    private void checkForTargetLetters() {
        // Check for target letters in the tiles at regular intervals
        this.updateTimeline = new Timeline(new KeyFrame(Duration.millis(Constants.UPDATE_SPEED), event -> {
            char[] targetLetters = this.targetWord.toCharArray();
            for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
                for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
                    if (this.board[row][col] != null) {
                        boolean isTargetLetter = false;
                        for (char letter : targetLetters) {
                            if (this.board[row][col].getLetter() == letter) {
                                isTargetLetter = true;
                                break; // Exit the loop once a match is found
                            }
                        }

                        // Update the tile's color based on whether it contains a target letter
                        if (this.board[row][col].getTileColor().equals(Constants.TILE_COLOR)) {
                            if (isTargetLetter) this.board[row][col].setColor(Color.web(Constants.TARGET_LETTER_COLOR));
                            else this.board[row][col].setColor(Color.web(Constants.TILE_COLOR));
                        }
                    }
                }
            }
        }));
        this.updateTimeline.setCycleCount(Timeline.INDEFINITE);
        this.updateTimeline.play();
    }

    // Moves tiles to the left where possible
    public void moveLeft(Tile[][] board, boolean gameIsWon, boolean gameIsLost, boolean isSimulation) {
        // Prevent movement if not allowed or game is over
        if (!this.isAllowedToMove || gameIsWon || gameIsLost) return;

        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
            // Start from the second column (index 1) and move left
            for (int col = 1; col < Constants.NUMBER_OF_COLUMNS; col++) {
                Tile currentTile = board[row][col];
                if (currentTile != null && currentTile.getLockStatus() != Locks.DOUBLELOCKED) {
                    int targetCol = col;

                    // Find the farthest unoccupied column to the left
                    while (targetCol > 0 && board[row][targetCol - 1] == null) targetCol--;

                    // If a move is possible
                    if (targetCol != col) {
                        board[row][targetCol] = currentTile;  // Update the board's state
                        board[row][col] = null;              // Clear the previous position
                        currentTile.setCol(targetCol);            // Update the tile's column
                        if (!isSimulation) {
                            int targetX = currentTile.getX() - (Math.abs(col - targetCol) * Constants.TILE_WIDTH);
                            this.movingTiles.put(currentTile, targetX);
                        }
                    }
                }
            }
        }
    }

    // Moves tiles to the right where possible
    public void moveRight(Tile[][] board, boolean gameIsWon, boolean gameIsLost, boolean isSimulation) {
        // Prevent movement if not allowed or game is over
        if (!this.isAllowedToMove || gameIsWon || gameIsLost) return;

        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
            for (int col = Constants.NUMBER_OF_COLUMNS - 2; col >= 0; col--) {
                Tile currentTile = board[row][col];
                if (currentTile != null && currentTile.getLockStatus() != Locks.DOUBLELOCKED) {
                    int targetCol = col;

                    // Find the rightmost unoccupied position for the current tile
                    while (targetCol + 1 < Constants.NUMBER_OF_COLUMNS && board[row][targetCol + 1] == null)
                        targetCol++;

                    // If a move is possible
                    if (targetCol != col) {
                        board[row][targetCol] = currentTile;  // Update the board's state
                        board[row][col] = null;              // Clear the previous position
                        currentTile.setCol(targetCol);            // Update the tile's column
                        if (!isSimulation) {
                            int targetX = currentTile.getX() + (Math.abs(col - targetCol) * Constants.TILE_WIDTH);
                            this.movingTiles.put(currentTile, targetX);
                        }
                    }
                }
            }
        }
    }
    // Moves tiles upwards where possible
    public void moveUp(Tile[][] board, boolean gameIsWon, boolean gameIsLost, boolean isSimulation) {
        // Prevent movement if the game state doesn't allow it
        if (!this.isAllowedToMove || gameIsWon || gameIsLost) return;

        for (int row = 1; row < Constants.NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
                Tile currentTile = board[row][col];
                if (currentTile != null && currentTile.getLockStatus() != Locks.DOUBLELOCKED) {
                    int targetRow = row;

                    // Find the highest unoccupied position for the current tile
                    while (targetRow > 0 && board[targetRow - 1][col] == null) targetRow--;

                    // If the tile can move, update its position
                    if (targetRow != row) {
                        board[targetRow][col] = currentTile;
                        board[row][col] = null;
                        currentTile.setRow(targetRow);
                        if (!isSimulation){
                            int targetY = currentTile.getY() - (Math.abs(row - targetRow) * Constants.TILE_HEIGHT);
                            this.movingTiles.put(currentTile, targetY);
                        }
                    }
                }
            }
        }
    }

    // Moves tiles downwards where possible
    public void moveDown(Tile[][] board, boolean gameIsWon, boolean gameIsLost, boolean isSimulation) {
        // Prevent movement if the game state doesn't allow it
        if (!this.isAllowedToMove || gameIsWon || gameIsLost) return;

        for (int row = Constants.NUMBER_OF_ROWS - 2; row >= 0; row--) {
            for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
                Tile currentTile = board[row][col];
                if (currentTile != null && currentTile.getLockStatus() != Locks.DOUBLELOCKED) {
                    int targetRow = row;

                    // Find the furthest available row to move the tile downward
                    while (targetRow + 1 < Constants.NUMBER_OF_ROWS && board[targetRow + 1][col] == null)
                        targetRow++;

                    // Update the board and prepare animation if the tile moves
                    if (targetRow != row) {
                        board[targetRow][col] = currentTile;
                        board[row][col] = null;
                        currentTile.setRow(targetRow);
                        if (!isSimulation){
                            int targetY = currentTile.getY() + (Math.abs(row - targetRow) * Constants.TILE_HEIGHT);
                            this.movingTiles.put(currentTile, targetY);
                        }
                    }
                }
            }
        }
    }

    // Visually moves all tiles horizontally towards their target positions
    public void animateHorizontalTileMovement(Directions direction, boolean hasMerged, boolean gameIsWon, boolean gameIsLost) {
        int increment = (direction == Directions.LEFT) ? -1 : 1;
        this.isAllowedToMove = false;

        // Create a timeline to animate tile movements
        this.move = new Timeline(new KeyFrame(Duration.millis(Constants.MOVE_SPEED), event -> {
            boolean allTilesSnapped = true; // Flag to track if all tiles have finished moving

            // Iterate over the tiles and move them towards their target positions
            for (Tile tile : this.movingTiles.keySet()) {
                int targetX = this.movingTiles.get(tile);

                // Move tile closer to its target X position
                if (Math.abs(tile.getX() - targetX) > Math.abs(increment)) {
                    tile.getTile().setLayoutX(tile.getTile().getLayoutX() + increment);
                    allTilesSnapped = false; // At least one tile is still moving
                } else tile.getTile().setLayoutX(targetX);
            }

            // Stop the animation when all tiles have reached their target positions
            if (allTilesSnapped) {
                this.isAllowedToMove = true;
                this.movingTiles.clear();
                this.move.stop();

                // Merge tiles if needed, otherwise generate a new tile
                if (!hasMerged && !gameIsWon) this.mergeTiles(direction, false, -1, -1, '\0');

                else if (!gameIsWon && !gameIsLost) this.generateNewTile(this.board,false);
            }
        }));

        this.move.setCycleCount(Timeline.INDEFINITE);
        this.move.play();
    }

    // Visually moves all tiles horizontally towards their target positions in a simulation
    public void animateHorizontalTileMovement(Directions direction, boolean hasMerged, int row, int col, char letter) {
        int increment = (direction == Directions.LEFT) ? -1 : 1;
        this.isAllowedToMove = false;

        // Create a timeline to animate tile movements
        this.move = new Timeline(new KeyFrame(Duration.millis(Constants.MOVE_SPEED), event -> {
            boolean allTilesSnapped = true; // Flag to track if all tiles have finished moving

            // Iterate over the tiles and move them towards their target positions
            for (Tile tile : this.movingTiles.keySet()) {
                int targetX = this.movingTiles.get(tile);

                // Move tile closer to its target X position
                if (Math.abs(tile.getX() - targetX) > Math.abs(increment)) {
                    tile.getTile().setLayoutX(tile.getTile().getLayoutX() + increment);
                    allTilesSnapped = false; // At least one tile is still moving
                } else tile.getTile().setLayoutX(targetX);
            }

            // Stop the animation when all tiles have reached their target positions
            if (allTilesSnapped) {
                this.isAllowedToMove = true;
                this.movingTiles.clear();
                this.move.stop();

                // Merge tiles if needed, otherwise generate a new tile
                if (!hasMerged) this.mergeTiles(direction, true, row, col, letter);
                else if (row != -1) this.generateSpecificTile(row, col, letter);

            }
        }));

        this.move.setCycleCount(Timeline.INDEFINITE);
        this.move.play();
    }

    // Visually moves all tiles vertically towards their target positions
    public void animateVerticalTileMovement(Directions direction, boolean hasMerged, boolean gameIsWon, boolean gameIsLost) {
        // Determine the vertical movement increment based on the direction (UP or DOWN)
        int increment = (direction == Directions.UP) ? -1 : 1;
        this.isAllowedToMove = false;

        // Create a timeline for the tile movement animation
        this.move = new Timeline(
            new KeyFrame(Duration.millis(Constants.MOVE_SPEED), event -> {
                boolean allTilesSnapped = true; // Flag to track if all tiles have finished moving

                // Move tile closer to its target Y position
                for (Tile tile : this.movingTiles.keySet()) {
                    int targetY = this.movingTiles.get(tile);
                    if (Math.abs(tile.getY() - targetY) > Math.abs(increment)) {
                        tile.getTile().setLayoutY(tile.getTile().getLayoutY() + increment);
                        allTilesSnapped = false; // At least one tile is still moving
                    } else tile.getTile().setLayoutY(targetY);

                }

                // Stop the animation when all tiles have reached their target positions
                if (allTilesSnapped) {
                    this.isAllowedToMove = true;
                    this.movingTiles.clear();
                    this.move.stop();

                    // Merge tiles if needed, otherwise generate a new tile
                    if (!hasMerged && !gameIsWon) this.mergeTiles(direction, false, -1, -1, '\0');
                    else if (!gameIsWon && !gameIsLost) this.generateNewTile(this.board, false);
                }
            })
        );

        this.move.setCycleCount(Timeline.INDEFINITE);
        this.move.play();
    }

    // Visually moves all tiles vertically towards their target positions in a simulation
    public void animateVerticalTileMovement(Directions direction, boolean hasMerged, int row, int col, char letter) {
        // Determine the vertical movement increment based on the direction (UP or DOWN)
        int increment = (direction == Directions.UP) ? -1 : 1;
        this.isAllowedToMove = false;

        // Create a timeline for the tile movement animation
        this.move = new Timeline(
                new KeyFrame(Duration.millis(Constants.MOVE_SPEED), event -> {
                    boolean allTilesSnapped = true; // Flag to track if all tiles have finished moving

                    // Move tile closer to its target Y position
                    for (Tile tile : this.movingTiles.keySet()) {
                        int targetY = this.movingTiles.get(tile);
                        if (Math.abs(tile.getY() - targetY) > Math.abs(increment)) {
                            tile.getTile().setLayoutY(tile.getTile().getLayoutY() + increment);
                            allTilesSnapped = false; // At least one tile is still moving
                        } else tile.getTile().setLayoutY(targetY);

                    }

                    // Stop the animation when all tiles have reached their target positions
                    if (allTilesSnapped) {
                        this.isAllowedToMove = true;
                        this.movingTiles.clear(); // Clear the moving tiles map
                        this.move.stop(); // Stop the timeline animation

                        // Merge tiles if needed, otherwise generate a new tile
                        if (!hasMerged) this.mergeTiles(direction, true, row, col, letter);
                        else if (row != -1) this.generateSpecificTile(row, col, letter);

                    }
                })
        );

        this.move.setCycleCount(Timeline.INDEFINITE);
        this.move.play();
    }

    // Merges adjacent tiles of the same letter based on the specified direction
    private void mergeTiles(Directions direction, boolean isSimulation, int tileRow, int tileCol, char tileLetter) {
        if (this.checkForWin(this.board, this.targetWord).getKey()) return;
        switch (direction) {
            case LEFT:
                this.mergeLeft(isSimulation, tileRow, tileCol, tileLetter);
                break;
            case RIGHT:
                this.mergeRight(isSimulation, tileRow, tileCol, tileLetter);
                break;
            case UP:
                this.mergeUp(isSimulation, tileRow, tileCol, tileLetter);
                break;
            case DOWN:
                this.mergeDown(isSimulation, tileRow, tileCol, tileLetter);
                break;
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    // Merges tiles leftward if they have the same letter
    private void mergeLeft(boolean isSimulation, int tileRow, int tileCol, char tileLetter) {
        boolean hasMerged = false;
        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < Constants.NUMBER_OF_COLUMNS - 1; col++) {
                if (this.board[row][col] != null) {
                    Tile currentTile = this.board[row][col];
                    Tile nextTile = this.board[row][col + 1];

                    if (nextTile != null && currentTile.getLetter() == nextTile.getLetter() && currentTile.isLocked()
                        && nextTile.isLocked()) {
                        hasMerged = true;
                        currentTile.merge();
                        nextTile.remove();
                        this.board[row][col + 1] = null;
                    }
                }
            }
        }
        if (!hasMerged && !isSimulation) this.generateNewTile(this.board, false);
        else if (hasMerged && !isSimulation) this.moveAfterMerge(Directions.LEFT, false, -1, -1, '\0');
        else if (!hasMerged) this.generateSpecificTile(tileRow, tileCol, tileLetter);
        else this.moveAfterMerge(Directions.LEFT, true, tileRow, tileCol, tileLetter);
    }

    // Merges tiles rightward if they have the same letter
    private void mergeRight(boolean isSimulation, int tileRow, int tileCol, char tileLetter) {
        boolean hasMerged = false;
        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
            for (int col = Constants.NUMBER_OF_COLUMNS - 1; col > 0; col--) {
                if (this.board[row][col] != null) {
                    Tile currentTile = this.board[row][col];
                    Tile nextTile = this.board[row][col - 1];

                    if (nextTile != null && currentTile.getLetter() == nextTile.getLetter() && currentTile.isLocked()
                            && nextTile.isLocked()) {
                        hasMerged = true;
                        currentTile.merge();
                        nextTile.remove();
                        this.board[row][col - 1] = null;
                    }
                }
            }
        }
        if (!hasMerged && !isSimulation) this.generateNewTile(this.board, false);
        else if (hasMerged && !isSimulation) this.moveAfterMerge(Directions.RIGHT, false, -1, -1, '\0');
        else if (!hasMerged) this.generateSpecificTile(tileRow, tileCol, tileLetter);
        else this.moveAfterMerge(Directions.RIGHT, true, tileRow, tileCol, tileLetter);
    }

    // Merges tiles upward if they have the same letter
    private void mergeUp(boolean isSimulation, int tileRow, int tileCol, char tileLetter) {
        boolean hasMerged = false;
        for (int row = 0; row < Constants.NUMBER_OF_ROWS - 1; row++) {
            for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
                if (this.board[row][col] != null) {
                    Tile currentTile = this.board[row][col];
                    Tile nextTile = this.board[row + 1][col];

                    if (nextTile != null && currentTile.getLetter() == nextTile.getLetter() && currentTile.isLocked()
                            && nextTile.isLocked()) {
                        hasMerged = true;
                        currentTile.merge();
                        nextTile.remove();
                        this.board[row + 1][col] = null;
                    }
                }
            }
        }
        if (!hasMerged && !isSimulation) this.generateNewTile(this.board, false);
        else if (hasMerged && !isSimulation) this.moveAfterMerge(Directions.UP, false, -1, -1, '\0');
        else if (!hasMerged) this.generateSpecificTile(tileRow, tileCol, tileLetter);
        else this.moveAfterMerge(Directions.UP, true, tileRow, tileCol, tileLetter);
    }

    // Merges tiles downward if they have the same letter
    private void mergeDown(boolean isSimulation, int tileRow, int tileCol, char tileLetter) {
        boolean hasMerged = false;
        for (int row = Constants.NUMBER_OF_ROWS - 1; row > 0; row--) {
            for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
                if (this.board[row][col] != null) {
                    Tile currentTile = this.board[row][col];
                    Tile nextTile = this.board[row - 1][col];

                    if (nextTile != null && currentTile.getLetter() == nextTile.getLetter() && currentTile.isLocked()
                            && nextTile.isLocked()) {
                        hasMerged = true;
                        currentTile.merge();
                        nextTile.remove();
                        this.board[row - 1][col] = null;
                    }
                }
            }
        }
        if (!hasMerged && !isSimulation) this.generateNewTile(this.board, false);
        else if (hasMerged && !isSimulation) this.moveAfterMerge(Directions.DOWN, false, -1, -1, '\0');
        else if (!hasMerged) this.generateSpecificTile(tileRow, tileCol, tileLetter);
        else this.moveAfterMerge(Directions.DOWN, true, tileRow, tileCol, tileLetter);

    }

    // Moves all tiles after merging has occurred to fill in gaps
    private void moveAfterMerge(Directions direction, boolean isSimulation, int row, int col, char letter) {
        // Perform the initial tile movement in the specified direction
        switch (direction){
            case LEFT:
                this.moveLeft(this.board,false, false, false);
                break;
            case RIGHT:
                this.moveRight(this.board, false, false, false);
                break;
            case UP:
                this.moveUp(this.board, false, false, false);
                break;
            case DOWN:
                this.moveDown(this.board, false, false, false);
                break;
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }

        if (isSimulation){
            // Determine whether to animate horizontal or vertical movement
            if (direction == Directions.LEFT || direction == Directions.RIGHT) {
                this.animateHorizontalTileMovement(direction, true, row, col, letter);
            } else this.animateVerticalTileMovement(direction, true, row, col, letter);

        }
        else {
            // Determine whether to animate horizontal or vertical movement
            if (direction == Directions.LEFT || direction == Directions.RIGHT) {
                this.animateHorizontalTileMovement(direction, true, false, false);
            } else this.animateVerticalTileMovement(direction, true, false, false);

        }
    }
}

