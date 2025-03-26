import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

// The PaneOrganizer class organizes and arranges the game's user interface elements.
// This class is responsible for setting up the layout and game components.
public class PaneOrganizer {

    // The root pane for the game UI
    private final BorderPane rootPane;

    // Constructor: Initializes the main layout for the game using a BorderPane and sets up the game UI elements
    public PaneOrganizer() {
        this.rootPane = new BorderPane();
        this.rootPane.setStyle("-fx-background-color: " + Constants.BACKGROUND_COLOR);

        Pane gamePane = new Pane();
        this.rootPane.setCenter(gamePane);

        // Initialize game UI elements
        this.createLogo(gamePane);
        this.createInstructions(gamePane);
        this.createWordBox(gamePane);
        this.createGrid(gamePane);

        this.createQuitButton();
        Game game = new Game(gamePane, this.createScoreLabel(gamePane), this.createRemoveLabel(gamePane));
        this.createNewGameButton(game, gamePane);
        this.createSolveButton(game, gamePane);
    }

    // Creates the Quit button and places it in the top section of the layout */
    private void createQuitButton() {
        Pane quitPane = new Pane();
        Button quitButton = new Button("Quit");
        quitButton.setPrefSize(Constants.QUIT_BUTTON_WIDTH, Constants.QUIT_BUTTON_HEIGHT);
        quitButton.setStyle("-fx-background-color: " + Constants.QUIT_BUTTON_COLOR + "; -fx-text-fill: " +
                Constants.BACKGROUND_COLOR + "; -fx-border-color: transparent");
        quitButton.setFont(Constants.QUIT_BUTTON_FONT);
        quitButton.setLayoutX(Constants.QUIT_BUTTON_X);
        quitButton.setLayoutY(Constants.QUIT_BUTTON_Y);

        quitButton.setFocusTraversable(false);
        quitButton.setOnAction(event -> System.exit(0));

        quitPane.getChildren().add(quitButton);
        this.rootPane.setTop(quitPane);
    }

    // Creates and displays the "New Game" button
    private void createNewGameButton(Game game, Pane gamePane) {
        // Create the "New Game" button and set its size, style, and layout
        Button newGameButton = new Button("NEW GAME");
        newGameButton.setPrefSize(Constants.NEW_GAME_WIDTH, Constants.NEW_GAME_HEIGHT);
        newGameButton.setStyle("-fx-background-color: " + Constants.NEW_GAME_BUTTON_COLOR +
                "; -fx-text-fill: " + Constants.BACKGROUND_COLOR +
                "; -fx-border-color: transparent");
        newGameButton.setFont(Constants.NEW_GAME_FONT);
        newGameButton.setLayoutX(Constants.NEW_GAME_X);
        newGameButton.setLayoutY(Constants.NEW_GAME_Y);

        // Prevent the button from receiving focus and set up its action to restart the game
        newGameButton.setFocusTraversable(false);
        newGameButton.setOnAction(e -> game.restartGame());

        // Add the button to the game pane
        gamePane.getChildren().add(newGameButton);
    }

    // Creates the Solve button and places it in the top section of the layout */
    private void createSolveButton(Game game, Pane gamePane) {
        // Create the solve button and its container
        Button solveButton = new Button("Solve");

        // Configure the solve button's appearance
        solveButton.setPrefSize(Constants.SOLVE_BUTTON_WIDTH, Constants.SOLVE_BUTTON_HEIGHT);
        solveButton.setStyle("-fx-background-color: " + Constants.SOLVE_BUTTON_COLOR +
                "; -fx-text-fill: " + Constants.BACKGROUND_COLOR +
                "; -fx-border-color: transparent");
        solveButton.setFont(Constants.SOLVE_BUTTON_FONT);
        solveButton.setLayoutX(Constants.SOLVE_BUTTON_X);
        solveButton.setLayoutY(Constants.SOLVE_BUTTON_Y);

        // Set up the button action
        solveButton.setFocusTraversable(false);
        solveButton.setOnAction(event -> game.solveGame());

        // Add the button to the pane and game
        gamePane.getChildren().add(solveButton);
    }

    // Creates the logo text and places it in the center of the gamePane */
    private void createLogo(Pane gamePane) {
        Text logoText = new Text("20WORDY8");
        logoText.setFill(Color.web(Constants.LOGO_COLOR));
        logoText.setFont(Constants.LOGO_FONT);
        logoText.setLayoutX(Constants.LOGO_X);
        logoText.setLayoutY(Constants.LOGO_Y);

        gamePane.getChildren().add(logoText);
    }

    // Creates the instructions for the game and places them in the gamePane */
    private void createInstructions(Pane gamePane) {
        Text instructionText1 = new Text("Join the letters and complete the ");
        Text instructionText2 = new Text("word!");

        instructionText1.setFill(Color.web(Constants.INSTRUCTIONS_COLOR));
        instructionText1.setFont(Constants.INSTRUCTIONS_FONT);
        instructionText1.setLayoutX(Constants.INSTRUCTIONS_X);
        instructionText1.setLayoutY(Constants.INSTRUCTIONS_Y);

        instructionText2.setFill(Color.web(Constants.INSTRUCTIONS_COLOR));
        instructionText2.setFont(Constants.INSTRUCTIONS_WORD_FONT);
        instructionText2.setLayoutX(Constants.INSTRUCTIONS_X + Constants.WORD_SPACING);
        instructionText2.setLayoutY(Constants.INSTRUCTIONS_Y);

        gamePane.getChildren().addAll(instructionText1, instructionText2);
    }

    // Creates the "Your Word" text and word box and places them in the gamePane */
    private void createWordBox(Pane gamePane) {
        Text wordLabel = new Text("YOUR WORD:");
        wordLabel.setFill(Color.web(Constants.YOUR_WORD_COLOR));
        wordLabel.setFont(Constants.YOUR_WORD_FONT);
        wordLabel.setLayoutX(Constants.YOUR_WORD_X);
        wordLabel.setLayoutY(Constants.YOUR_WORD_Y);

        Rectangle wordBox = new Rectangle(Constants.WORD_BOX_X, Constants.WORD_BOX_Y,
                Constants.WORD_BOX_WIDTH, Constants.WORD_BOX_HEIGHT);
        wordBox.setFill(Color.web(Constants.WORD_BOX_COLOR));
        wordBox.setArcWidth(Constants.WORD_BOX_ARC);
        wordBox.setArcHeight(Constants.WORD_BOX_ARC);

        gamePane.getChildren().addAll(wordLabel, wordBox);
    }

    // Creates a grid of tiles on the specified game pane
    private void createGrid (Pane gamePane){
        double rowY = -Constants.TILE_WIDTH - 0.5;
        for (int row = 0; row < Constants.NUMBER_OF_ROWS; row++){
            rowY += Constants.TILE_HEIGHT;
            double colX = -Constants.TILE_WIDTH - 0.5;
            for (int col = 0; col < Constants.NUMBER_OF_COLUMNS; col ++){
                colX += Constants.TILE_WIDTH;
                double x = colX + Constants.X_OFFSET;
                double y = rowY + Constants.Y_OFFSET;

                Rectangle tileSquare = new Rectangle(x, y, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
                tileSquare.setFill(Color.web(Constants.BOARD_COLOR));
                tileSquare.setStroke(Color.web(Constants.TILE_BORDER_COLOR));
                tileSquare.setStrokeWidth(Constants.TILE_BORDER_WIDTH + 1);
                gamePane.getChildren().add(tileSquare);
            }
        }
    }

    // Creates and displays the score label, including the score box and the score text */
    private Text createScoreLabel(Pane gamePane) {

        // Create and style the score box
        Rectangle scoreBox = new Rectangle(
                Constants.SCORE_BOX_X, Constants.SCORE_BOX_Y,
                Constants.SCORE_BOX_WIDTH, Constants.SCORE_BOX_HEIGHT);
        scoreBox.setFill(Color.web(Constants.SCORE_BOX_COLOR));
        scoreBox.setArcWidth(Constants.SCORE_BOX_ARC);
        scoreBox.setArcHeight(Constants.SCORE_BOX_ARC);

        // Create score text and set its appearance
        Text scoreText = new Text("SCORE \n" + 0);
        scoreText.setFill(Color.web(Constants.BACKGROUND_COLOR));
        scoreText.setFont(Constants.SCORE_TEXT_FONT);
        scoreText.setTextAlignment(TextAlignment.CENTER);
        scoreText.setLayoutX(Constants.SCORE_TEXT_X);
        scoreText.setLayoutY(Constants.SCORE_TEXT_Y);

        // Add score box and text to the game pane
        gamePane.getChildren().addAll(scoreBox, scoreText);
        return scoreText;
    }

    // Creates and displays the removes label, including the removes box and the remove counter text
    private Text createRemoveLabel(Pane gamePane) {

        // Create and style the remove box
        Rectangle removeBox = new Rectangle(Constants.REMOVE_BOX_X, Constants.REMOVE_BOX_Y,
                Constants.REMOVE_BOX_WIDTH, Constants.REMOVE_BOX_HEIGHT);
        removeBox.setFill(Color.web(Constants.REMOVE_BOX_COLOR));
        removeBox.setArcWidth(Constants.REMOVE_BOX_ARC);
        removeBox.setArcHeight(Constants.REMOVE_BOX_ARC);

        // Create remove text and set its appearance
        Text removeText = new Text("REMOVES \n" + Constants.NUMBER_OF_REMOVES);
        removeText.setFill(Color.web(Constants.BACKGROUND_COLOR));
        removeText.setFont(Constants.REMOVE_TEXT_FONT);
        removeText.setTextAlignment(TextAlignment.CENTER);
        removeText.setLayoutX(Constants.REMOVE_TEXT_X);
        removeText.setLayoutY(Constants.REMOVE_TEXT_Y);

        // Add remove box and text to the game pane
        gamePane.getChildren().addAll(removeBox, removeText);
        return removeText;
    }

    // Gets the root pane containing all the UI elements
    public BorderPane getRoot() {
        return this.rootPane;
    }
}
