import javafx.animation.PauseTransition;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

// The BlankTile class represents a blank tile in the game that can be interacted with by the player
// to change its letter. It listens for key events to allow the player to input a letter,
// and disables key press handling after a short pause.
public class BlankTile extends Tile {

    // Constructor: Initializes the BlankTile's position and appearance.
    public BlankTile(Pane gamePane, int rowY, int colX, int row, int col) {
        super(gamePane, rowY + 10, colX, row, col); // Inherit positioning logic from the Tile class

        // Set the appearance for the blank tile
        this.tileColor = Constants.BLANK_TILE_COLOR;
        this.tileSquare.setFill(Color.web(this.tileColor));
        this.tileLetter.setFill(Color.web(Constants.BLANK_TILE_TEXT_COLOR));
        this.getTile().setFocusTraversable(true);
    }

    // Changes the letter of the tile by allowing the user to press a key.
    //After a 3-second pause, it disables further key presses.
    public void changeLetter() {
        // Set the key press event handler to allow user input
        this.getTile().setOnKeyPressed(this::handleKeyPress);

        // Request focus to ensure the tile can capture key presses
        this.getTile().requestFocus();

        // Create a pause for 3 seconds before disabling key events
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> this.getTile().setOnKeyPressed(null));
        pause.play();
    }

    // Handles key press events to update the letter on the tile
    private void handleKeyPress(KeyEvent event) {
        String keyText = event.getText();  // Get the text of the key pressed

        // Check if the key is a valid letter
        if (!keyText.isEmpty() && Character.isLetter(keyText.charAt(0))) {
            char keyChar = keyText.toUpperCase().charAt(0);  // Convert the letter to uppercase
            this.setLetter(keyChar);  // Update the tile's letter
        }
    }

    // Returns the color of the blank tile
    @Override
    public String getTileColor() {
        return Constants.BLANK_TILE_COLOR;
    }
}
