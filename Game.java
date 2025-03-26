import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


// The Game class manages the overall game state, UI components, and game logic.
// It is responsible for initializing the game board, tracking the player's score,
// handling user inputs, updating the game UI, and determining the win or loss conditions.

 public class Game {

    // Instance variables
    private final Pane gamePane; // Pane for game UI components, holds the layout and visual elements of the game
    private final Board gameBoard; // Game board, an object that manages the board logic and tiles
    private final Tile[][] board; // Game board represented as a 2D array of Tiles
    private Timeline updateTimeline;  // A timeline that updates the game state at regular intervals
    private final Text removeText; // UI element to display the number of remaining tile removals available
    private final Text scoreText; // UI element to display the current score
    private Text targetWordText;  // UI element to display the target word the player is trying to form
    private String targetWord; // The target word to guess or match
    private final AtomicInteger clickCount; // Counter for mouse clicks
    private int score; // Current score of the player
    private int removeCounter; // Counter for tile removal actions
    private boolean gameIsWon, gameIsLost, hasBeenSolved; // Flags for tracking game win/loss status


    // Constructor: Initializes the game, UI components, and board state
    public Game(Pane pane, Text scoreText, Text removeText) {
        // Initialize game pane and state
        this.gamePane = pane;
        this.removeCounter = Constants.NUMBER_OF_REMOVES; // Allowable tile removals
        this.score = 0; // Start with a score of 0
        this.gameIsWon = false; // Game starts in a non-won state
        this.gameIsLost = false; // Game starts in a non-lost state
        this.hasBeenSolved = false;
        this.clickCount = new AtomicInteger(0);
        this.scoreText = scoreText;
        this.removeText = removeText;

        // Initialize game board (2D array of tiles)
        this.board = new Tile[Constants.NUMBER_OF_ROWS][Constants.NUMBER_OF_COLUMNS];

        // Set up the game with a random word and starting tiles
        this.generateRandomWord();
        this.gameBoard = new Board(this.gamePane, this.board, this.targetWord);
        this.gameBoard.generateStartingTiles();

        // Start the game loop to periodically update the game state
        this.updateGame();

        // Configure game pane for keyboard and mouse inputs
        this.gamePane.setFocusTraversable(true);
        this.gamePane.setOnKeyPressed(this::handleKeyPress);
        this.gamePane.setOnMouseClicked(this::handleMouseClick);
    }

    // Updates the game state at regular intervals
    private void updateGame() {
        this.updateTimeline = new Timeline(new KeyFrame(Duration.millis(Constants.UPDATE_SPEED), event -> {
            for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
                for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
                    Tile tile = this.board[row][col];
                    if (tile != null) {
                        tile.setRow(row);
                        tile.setCol(col);}
                }
            }

            // Check if the player has won or lost
            if (this.gameBoard.checkForWin(this.board, this.targetWord).getKey()) {
                this.gameIsWon(this.gameBoard.checkForWin(this.board, this.targetWord).getValue());
            }
            if (this.gameBoard.checkForLoss(this.board, this.removeCounter)) this.gameIsLost();

        }));

        this.updateTimeline.setCycleCount(Timeline.INDEFINITE);
        this.updateTimeline.play();
    }

    public void solveGame(){
        if (!this.hasBeenSolved &&!this.gameIsWon && !this.gameIsLost) {
            //Unlock all tiles
            for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++){
                for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++){
                    if (this.board[row][col] != null) this.board[row][col].setLock(Locks.UNLOCKED);
                }
            }

            // Remove all input listeners
            this.gamePane.setOnKeyPressed(null);
            this.gamePane.setOnMouseClicked(null);

            this.hasBeenSolved = true;
            new SolveGame(this.gamePane, this.board, this.targetWord);
        }
    }

    // Randomly selects a word from the word bank and sets it as the target word
    private void generateRandomWord() {
        // Remove the target word from screen if it already exists
        if (this.targetWordText != null) this.gamePane.getChildren().remove(this.targetWordText);

        // Initialize the target word text
        this.targetWordText = new Text();
        this.targetWordText.setFill(Color.web(Constants.BACKGROUND_COLOR));
        this.targetWordText.setFont(Constants.WORD_TEXT_FONT);

        // Select a random word from the word bank
        Random random = new Random();
        this.targetWord = Constants.WORD_BANK.get(random.nextInt(Constants.WORD_BANK.size()));
        this.targetWordText.setText(this.targetWord); // Set text before measuring bounds

        // Measure the text dimensions
        double textWidth = this.targetWordText.getBoundsInLocal().getWidth();
        double textHeight = this.targetWordText.getBoundsInLocal().getHeight();

        // Get the calculated position
        Pair<Double, Double> textPosition = this.calculateTextPosition(textWidth, textHeight);

        // Set the text position
        this.targetWordText.setLayoutX(textPosition.getKey()); // X-coordinate
        this.targetWordText.setLayoutY(textPosition.getValue()); // Y-coordinate

        // Add the text to the game pane
        this.gamePane.getChildren().add(this.targetWordText);
    }

    // Calculates the layout position for centering text within the word box
    private Pair<Double, Double> calculateTextPosition(double textWidth, double textHeight) {
        // Word box dimensions
        double wordBoxY = Constants.WORD_BOX_Y - 10; // Adjust for vertical alignment

        // Calculate center position
        double centerX = (double) Constants.WORD_BOX_X + ((double) Constants.WORD_BOX_WIDTH - textWidth) / 2; // Center horizontally
        double centerY = wordBoxY + ((double) Constants.WORD_BOX_HEIGHT + textHeight) / 2; // Center vertically

        return new Pair<>(centerX, centerY);
    }

    // Restarts the game by resetting the board, score, and other game components
    public void restartGame() {
        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++) {
            for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col++) {
                if (this.board[row][col] != null) this.board[row][col].remove(); // Remove tile from the UI
                this.board[row][col] = null; // Clear the tile from the board
            }
        }

        // Reset game state variables
        this.removeCounter = Constants.NUMBER_OF_REMOVES;
        this.score = 0;
        if (this.gameIsWon)this.gameIsWon = false;
        if (this.gameIsLost)this.gameIsLost = false;
        if (this.hasBeenSolved) this.hasBeenSolved = false;

        this.gamePane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("endBox"));
        this.gamePane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("endText"));


        // Update UI labels
        if (this.removeText != null) this.removeText.setText("REMOVES \n" + this.removeCounter);
        if (this.scoreText != null) this.scoreText.setText("SCORE \n" + this.score);

        // Generate a new target word and set up starting tiles
        this.generateRandomWord();
        this.gameBoard.generateStartingTiles();
        this.gameBoard.setTargetWord(this.targetWord);

        // Start all game-related timelines
        this.gameBoard.updateTimeline.play();
        this.updateTimeline.play();

        // Re-enable input listeners
        this.gamePane.setOnKeyPressed(this::handleKeyPress);
        this.gamePane.setOnMouseClicked(this::handleMouseClick);
    }

    // Handles the game logic when the game is won
    private void gameIsWon(Tile[] winningTiles) {
        this.gameIsWon = true; // Mark the game as won

        // Stop all timelines related to the game board
        if (this.gameBoard.updateTimeline != null) this.gameBoard.updateTimeline.stop();
        if (this.updateTimeline != null) this.updateTimeline.stop();

        // Enable key listening if no key press handler is set
        if (this.gamePane.getOnKeyPressed() == null) this.gamePane.setOnKeyPressed(this::handleKeyPress);

        // Change the color of the winning tiles to indicate victory
        for (Tile tile : winningTiles) tile.wonTile();

        // Display the winning message on the screen
        this.createWinningMessage();
    }

    // Handles the game logic when the game is lost
    private void gameIsLost() {
        this.gameIsLost = true;  // Mark the game as lost

        // Stop all timelines related to the game board
        if (this.gameBoard.updateTimeline != null) this.gameBoard.updateTimeline.stop();
        if (this.updateTimeline != null) this.updateTimeline.stop();

        // Display the losing message on the screen
        this.createLosingMessage();
    }

    // Creates and displays the winning message
    private void createWinningMessage() {
        // Create a rectangle to display the losing message background
        this.createEndBox("You Win!");
    }

    // Creates and displays the losing message
    private void createLosingMessage() {
        // Create a rectangle to display the losing message background
        this.createEndBox("You Lose");
    }

    // Creates the end box rectangle at the specified location
    private void createEndBox(String message) {
        Rectangle endBox = new Rectangle(Constants.END_BOX_X, Constants.END_BOX_Y,
                Constants.END_BOX_WIDTH, Constants.END_BOX_HEIGHT);
        endBox.setFill(Color.web(Constants.END_BOX_COLOR));
        endBox.setArcWidth(Constants.END_BOX_ARC);
        endBox.setArcHeight(Constants.END_BOX_ARC);
        endBox.setId("endBox");


        // Create the text to display "You Lose"
        Text endText = new Text(message);
        endText.setFill(Color.web(Constants.END_TEXT_COLOR));
        endText.setFont(Constants.END_TEXT_FONT);
        endText.setLayoutX(Constants.END_TEXT_X - 1.5);
        endText.setLayoutY(Constants.END_TEXT_Y);
        endText.setId("endText");

        this.gamePane.getChildren().addAll(endBox, endText);
    }

    // Handles key press events for moving the game board
    private void handleKeyPress(javafx.scene.input.KeyEvent event) {
        switch (event.getCode()) {
            case LEFT:
                this.gameBoard.moveLeft(this.board, this.gameIsWon, this.gameIsLost, false);  // Move tiles left
                this.gameBoard.animateHorizontalTileMovement(Directions.LEFT, false,
                        this.gameBoard.checkForWin(this.board, this.targetWord).getKey(),
                        this.gameBoard.checkForLoss(this.board, this.removeCounter));
                break;
            case RIGHT:
                this.gameBoard.moveRight(this.board, this.gameIsWon, this.gameIsLost, false); // Move tiles right
                this.gameBoard.animateHorizontalTileMovement(Directions.RIGHT, false,
                        this.gameBoard.checkForWin(this.board, this.targetWord).getKey(),
                        this.gameBoard.checkForLoss(this.board, this.removeCounter));
                break;
            case UP:
                this.gameBoard.moveUp(this.board, this.gameIsWon, this.gameIsLost, false); // Move tiles up
                this.gameBoard.animateVerticalTileMovement(Directions.UP, false,
                        this.gameBoard.checkForWin(this.board, this.targetWord).getKey(),
                        this.gameBoard.checkForLoss(this.board, this.removeCounter));
                break;
            case DOWN:
                this.gameBoard.moveDown(this.board, this.gameIsWon, this.gameIsLost, false); // Move tiles down
                this.gameBoard.animateVerticalTileMovement(Directions.DOWN, false,
                        this.gameBoard.checkForWin(this.board, this.targetWord).getKey(),
                        this.gameBoard.checkForLoss(this.board, this.removeCounter));
                break;
            default:
                return;
        }
        if (!this.gameIsWon && !this.gameIsLost) this.score = Constants.updateScore(this.score, this.scoreText); // If game isn't over increase score
    }

    // Handles mouse click events
    private void handleMouseClick(MouseEvent event) {
        // Get the row and column based on the mouse click coordinates
        int row = Constants.getRowForYCoordinate(event.getY());
        int col = Constants.getColForXCoordinate(event.getX());


        if (row != -1 && col != -1 && this.board[row][col] != null) {
            if (event.getButton() == MouseButton.PRIMARY) {

                if (Objects.equals(this.board[row][col].getTileColor(), Constants.BLANK_TILE_COLOR)) {
                    BlankTile blankTile = (BlankTile) this.board[row][col];
                    blankTile.changeLetter();
                } else {
                    Tile tile = this.board[row][col];

                    PauseTransition clickTimeout = new PauseTransition(Duration.seconds(Constants.CLICK_SPEED));
                    clickTimeout.setOnFinished(e -> {
                        if (this.clickCount.get() == 1) {
                            if (tile.getLockStatus() != Locks.LOCKED) tile.setLock(Locks.LOCKED);
                            else tile.setLock(Locks.UNLOCKED);
                            this.clickCount.set(0);}});

                    int currentClickCount = this.clickCount.incrementAndGet(); // Increment and get the current value
                    if (currentClickCount == 1) {
                        clickTimeout.playFromStart();
                    }

                    if (currentClickCount == 2) {
                        if (tile.getLockStatus() != Locks.DOUBLELOCKED) tile.setLock(Locks.DOUBLELOCKED);
                        else tile.setLock(Locks.UNLOCKED);
                        clickTimeout.stop();
                        this.clickCount.set(0);
                    }
                }
            }

            //Remove a tile
            if (event.getButton() == MouseButton.SECONDARY) {
                if (this.removeCounter > 0) {
                    this.removeCounter = Constants.removeTile(this.board[row][col], this.removeCounter, this.removeText);
                    this.board[row][col] = null;
                }
            }
        }
    }
}