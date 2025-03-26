import javafx.animation.ScaleTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;

// The Tile class represents a single tile in the game grid. It manages the tile's graphical
// representation, including its position, color, and letter.

public class Tile {

    // Instance variables
    private Pane gamePane; // The game pane containing all tiles and elements
    private StackPane tile; // Combines the tile's graphical elements (rectangle + letter)
    public Rectangle tileSquare; // The rectangle graphical representation of the tile
    public Text tileLetter; // The text element showing the letter on the tile
    public String tileColor; // The color of the tile, e.g., a hex code or color name
    private Locks lockStatus;
    private char currLetter; // The character currently displayed on the tile
    private int row; // The row index of the tile in the game grid
    private int col; // The column index of the tile in the game grid

    // Constructs a new Tile object
    public Tile(Pane gamePane, int rowY, int colX, int row, int col) {
        // Initialize tile's position in the grid
        this.row = row;
        this.col = col;
        this.gamePane = gamePane;

        // Default tile states
        this.lockStatus = Locks.UNLOCKED;

        // Calculate tile's graphical position based on offsets
        double x = colX + Constants.X_OFFSET + 7.4;
        double y = rowY + Constants.Y_OFFSET - 2.98;

        // Set tile color and graphical representation
        this.tileColor = Constants.TILE_COLOR;
        this.tileSquare = new Rectangle(x, y, Constants.TILE_WIDTH - 15, Constants.TILE_HEIGHT - 15);
        this.tileSquare.setFill(Color.web(this.tileColor));

        // Initialize tile letter as blank
        this.tileLetter = new Text(" ");
        this.tileLetter.setFont(Constants.TILE_FONT);
        this.tileLetter.setFill(Color.web(Constants.TILE_TEXT_COLOR));

        // Center text within the rectangle
        this.tileLetter.setX(this.tileSquare.getX() + (this.tileSquare.getWidth() / 2) - (this.tileLetter.getLayoutBounds().getWidth() / 2));
        this.tileLetter.setY(this.tileSquare.getY() + (this.tileSquare.getHeight() / 2) + (this.tileLetter.getLayoutBounds().getHeight() / 4));

        // Combine the rectangle and letter into a StackPane
        this.tile = new StackPane(this.tileSquare, this.tileLetter);
        this.tile.setLayoutX(x);
        this.tile.setLayoutY(y);

        // Initialize scale (start small for animation)
        this.tile.setScaleX(0);
        this.tile.setScaleY(0);

        // Create a scaling animation for tile appearance
        this.animateTile(0);

        // Add the tile to the game pane
        this.gamePane.getChildren().add(this.tile);

    }

    // Constructs a new Tile object with specified row and column indices
    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // Creates a new Tile object by copying the state of another Tile
    public Tile(Tile other) {
        this.currLetter = other.currLetter;
        this.row = other.row;
        this.col = other.col;
    }

    // Merges the tile by incrementing its current letter and animating the merge */
    public void merge() {
        if (this.isLocked()) {
            // Increment the letter, wrapping around to 'A' after 'Z'
            this.currLetter = (this.currLetter == 'Z') ? 'A' : (char) (this.currLetter + 1);

            // Update the tile's letter using the setLetter method
            this.setLetter(this.currLetter);
            this.animateTile(0.5);

        }
    }

    // Handles the behavior for a winning tile */
    public void wonTile() {
        // Set the tile's color to the winning color
        this.setColor(Color.web(Constants.WINNING_TILE_COLOR));

        // Change the letter color to white to highlight the win
        this.tileLetter.setFill(Color.WHITE);

        // Create and play the scale transition animation for the winning tile
        this.animateTile(0.3);
    }

    // Removes the tile from the game by removing it from the game pane */
    public void remove() {this.gamePane.getChildren().remove(this.tile);}

    // Animates tiles with a scaling effect
    private void animateTile(double startingSize) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(Constants.TILE_SPEED), this.tile);
        scale.setFromX(startingSize);
        scale.setFromY(startingSize);
        scale.setToX(1);
        scale.setToY(1);
        scale.setCycleCount(1);
        scale.setAutoReverse(false);
        scale.play();
    }

    // Important setters and getters
    public void setLock(Locks lock) {
        this.lockStatus = lock;

        // Ensure the stroke does not increase the size of the square
        this.tileSquare.setStrokeType(StrokeType.INSIDE);

        switch (lock) {
            case LOCKED:
                this.tileSquare.setStrokeWidth(Constants.LOCK_BORDER_WIDTH);
                this.tileSquare.setStroke(Color.web(Constants.LOCK_BORDER_COLOR));
                break;
            case DOUBLELOCKED:
                this.tileSquare.setStrokeWidth(Constants.LOCK_BORDER_WIDTH);
                this.tileSquare.setStroke(Color.web(Constants.DOUBLELOCKED_BORDER_COLOR));
                break;
            case UNLOCKED:
                this.tileSquare.setStrokeWidth(0);
                break;
            default:
                throw new IllegalArgumentException("Invalid lock: " + lock);
        }
    }

    public void setLetter(char letter) {
        this.currLetter = letter;
        if (this.tileLetter != null) {
            String tileLetterText = Character.toString(letter);
            this.tileLetter.setText(tileLetterText);
        }
    }
    public void setCol(int col){this.col = col;}
    public void setRow(int row){this.row = row;}
    public void setColor(Color color){this.tileSquare.setFill(color);}
    public StackPane getTile(){return this.tile;}
    public char getLetter() {return this.currLetter;}
    public String getTileColor() {return Constants.TILE_COLOR;}
    public Locks getLockStatus() {return this.lockStatus;}
    public boolean isLocked() {
        return this.lockStatus != Locks.LOCKED && this.lockStatus != Locks.DOUBLELOCKED;}
    public int getX() {return (int) this.tile.getLayoutX();}
    public int getY() {return (int) this.tile.getLayoutY();}

}
