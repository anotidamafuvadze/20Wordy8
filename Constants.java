import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.Arrays;

// This Constants class contains all constant values used across the application.
// Constants include configuration values for layout, fonts, colors, and game settings.
public class Constants {

    // Word Bank Constants
    public static final ArrayList<String> WORD_BANK = new ArrayList<>(Arrays.asList(
            "BAD", "CAB", "CAD", "ABC", "ACD", "ABD", "BDC", "DAB", "BCA", "CBA", "DAC", "ABB", "BAA", "CBD"));

    // Screen Dimensions
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 800;

    // Colors
    public static final String LOGO_COLOR = "#776e66";
    public static final String BACKGROUND_COLOR = "#faf8ef";
    public static final String SCORE_BOX_COLOR = "#baa99e";
    public static final String REMOVE_BOX_COLOR = "#baa99e";
    public static final String INSTRUCTIONS_COLOR = "#776e66";
    public static final String NEW_GAME_BUTTON_COLOR = "#8e7a65"; // Color for new game button
    public static final String QUIT_BUTTON_COLOR = "#8e7a65"; // Color for quit button
    public static final String YOUR_WORD_COLOR = "#776e66"; // Color for "Your Word" label
    public static final String WORD_BOX_COLOR = "#baa99e"; // Color for word box

    // Fonts
    public static final Font LOGO_FONT = Font.font("Arial", FontWeight.EXTRA_BOLD, 65);
    public static final Font SCORE_TEXT_FONT = Font.font("Arial", FontWeight.BOLD, 20);
    public static final Font REMOVE_TEXT_FONT = Font.font("Arial", FontWeight.BOLD, 20);
    public static final Font INSTRUCTIONS_FONT = Font.font("Arial", FontWeight.MEDIUM, 24);
    public static final Font INSTRUCTIONS_WORD_FONT = Font.font("Arial", FontWeight.BOLD, 24);
    public static final Font NEW_GAME_FONT = Font.font("Arial", FontWeight.BOLD, 20);
    public static final Font QUIT_BUTTON_FONT = Font.font("Arial", FontWeight.BOLD, 15);
    public static final Font YOUR_WORD_FONT = Font.font("Arial", FontWeight.EXTRA_BOLD, 40);
    public static final Font WORD_TEXT_FONT = Font.font("Arial", FontWeight.EXTRA_BOLD, 50);

    // Layout Coordinates
    public static final int LOGO_X = 75;
    public static final int LOGO_Y = 75;
    public static final int INSTRUCTIONS_X = 75;
    public static final int INSTRUCTIONS_Y = 135;
    public static final int SCORE_BOX_X = 475;
    public static final int SCORE_BOX_Y = 20;
    public static final int REMOVE_BOX_X = 615;
    public static final int REMOVE_BOX_Y = 20;
    public static final int SCORE_TEXT_X = 489;
    public static final int SCORE_TEXT_Y = 45;
    public static final int REMOVE_TEXT_X = 624;
    public static final int REMOVE_TEXT_Y = 45;
    public static final int SCORE_BOX_WIDTH = 100;
    public static final int SCORE_BOX_HEIGHT = 60;
    public static final int REMOVE_BOX_WIDTH = 120;
    public static final int REMOVE_BOX_HEIGHT = 60;
    public static final int WORD_SPACING = 355;
    public static final int NEW_GAME_X = 540;
    public static final int NEW_GAME_Y = 114;
    public static final int QUIT_BUTTON_X = 10;
    public static final int QUIT_BUTTON_Y = 10;
    public static final int YOUR_WORD_X = 80;
    public static final int YOUR_WORD_Y = 720;
    public static final int WORD_BOX_X = 360;
    public static final int WORD_BOX_Y = 670;
    public static final int WORD_BOX_WIDTH = 200;
    public static final int WORD_BOX_HEIGHT = 70;
    public static final int WORD_BOX_ARC = 20;

    // Row Boundaries
    public static final int[] ROW_1 = {220, 315};    // Row 1 is between 220 and 315 in Y coordinate
    public static final int[] ROW_2 = {330, 425};  // Row 2 is between 330 and 425 in Y coordinate
    public static final int[] ROW_3 = {440, 535};  // Row 3 is between 440 and 535 in Y coordinate
    public static final int[] ROW_4 = {550, 645};  // Row 4 is between 550 and 645 in Y coordinate

    // Column Boundaries
    public static final int[] COL_1 = {160, 260};    // Column 1 is between 160 and 260 in X coordinate
    public static final int[] COL_2 = {280, 380};  // Column 2 is between 280 and 380 in X coordinate
    public static final int[] COL_3 = {400, 500};  // Column 3 is between 400 and 500 in X coordinate
    public static final int[] COL_4 = {520, 620};  // Column 4 is between 520 and 620 in X coordinate

    // Rounded Corner Radii
    public static final int SCORE_BOX_ARC = 20;
    public static final int REMOVE_BOX_ARC = 20;

    // Game Settings
    public static final int NUMBER_OF_REMOVES = 3; // Number of removes allowed
    public static final int NEW_GAME_WIDTH = 150;  // Width of new game button
    public static final int NEW_GAME_HEIGHT = 50;  // Height of new game button
    public static final double QUIT_BUTTON_WIDTH = 80;  // Width of quit button
    public static final double QUIT_BUTTON_HEIGHT = 30; // Height of quit button

    public static final int NUMBER_OF_ROWS = 4;
    public static final int NUMBER_OF_COLUMNS = 4;

    // Tile Measurements
    public static final int TILE_WIDTH = 120;
    public static final int TILE_HEIGHT = 110;

    // Offsets for Tile Positioning
    public static final int X_OFFSET = 150;
    public static final int Y_OFFSET = 220;

    // Tile Appearance Colors
    public static final String TILE_COLOR = "#787c7f";
    public static final String BLANK_TILE_COLOR = "#FFFFFF";
    public static final String TILE_BORDER_COLOR = "#545454";
    public static final String BOARD_COLOR = "#9DA0A2";
    public static final String LOCK_BORDER_COLOR = "#f1e2a6";
    public static final String DOUBLELOCKED_BORDER_COLOR = "#f9f2d7";

    // Tile Border Width
    public static final int TILE_BORDER_WIDTH = 15;
    public static final int LOCK_BORDER_WIDTH = 8;

    // Tile Text Properties
    public static final Font TILE_FONT = Font.font("Arial", FontWeight.BOLD, 50);
    public static final String TILE_TEXT_COLOR = "white";
    public static final String BLANK_TILE_TEXT_COLOR = "#787c80";

    // Game Animation Settings
    public static final double MOVE_SPEED = 0.4;
    public static final int TILE_SPEED = 180;
    public static final int UPDATE_SPEED = 10;
    public static final double CLICK_SPEED = 0.5;

    // Special Tile Colors
    public static final String TARGET_LETTER_COLOR = "#c8b450";
    public static final String WINNING_TILE_COLOR = "#67ac61";

    // Winning Box Constants
    public static final int END_BOX_X = 340; // X-coordinate for the box
    public static final int END_BOX_Y = 156; // Y-coordinate for the box
    public static final int END_BOX_WIDTH = 115; // Width of the box
    public static final int END_BOX_HEIGHT = 35; // Height of the box
    public static final String END_BOX_COLOR = "black";
    public static final int END_BOX_ARC = 20; // Rounded corner arc size

    // Winning Text Constants
    public static final String END_TEXT_COLOR = "white"; // Color for the text
    public static final Font END_TEXT_FONT = Font.font("Arial", FontWeight.BOLD, 20); // Font for the text
    public static final double END_TEXT_X = 354.7; // X-coordinate for the text
    public static final int END_TEXT_Y = 180; // Y-coordinate for the text
    public static final int POINTS_PER_MOVE = 50;

    // No Solution Text Font
    public static final Font NO_SOLUTION_FONT = Font.font("Arial", FontWeight.BOLD, 18); // Font for "No Solution" text

    // Solve Button Dimensions
    public static final int SOLVE_BUTTON_WIDTH = 120;
    public static final int SOLVE_BUTTON_HEIGHT = 50;

    // Solve Button Colors
    public static final String SOLVE_BUTTON_COLOR = "#8e7a65"; // Solve button background color

    // Solve Button Font
    public static final Font SOLVE_BUTTON_FONT = Font.font("Arial", FontWeight.BOLD, 25);

    // Solve Button Layout
    public static final int SOLVE_BUTTON_X = 670;
    public static final int SOLVE_BUTTON_Y = 700;

    // Updates the Score
    public static int updateScore(int score, Text scoreText) {
        score += POINTS_PER_MOVE; // Increase the score by a fixed amount
        scoreText.setText("SCORE \n" + score); // Update the score display
        return score;
    }

    // Remove Tile and Decrease Counter
    public static int removeTile(Tile tile, int removeCounter, Text removeText){
        tile.remove();
        removeCounter--;
        removeText.setText("REMOVES \n" + removeCounter);
        return removeCounter;
    }

    // Method to determine the row based on the Y-coordinate
    public static int getRowForYCoordinate(double y) {
        if (y >= ROW_1[0] && y < ROW_1[1]) return 0; // Row 1
        else if (y >= ROW_2[0] && y < ROW_2[1]) return 1; // Row 2
        else if (y >= ROW_3[0] && y < ROW_3[1]) return 2; // Row 3
        else if (y >= ROW_4[0] && y < ROW_4[1]) return 3; // Row 3
        else return -1; // Invalid row
    }

    // Method to determine the column based on the X-coordinate
    public static int getColForXCoordinate(double x) {
        if (x >= COL_1[0] && x < COL_1[1]) return 0; // Column 1
        else if (x >= COL_2[0] && x < COL_2[1]) return 1; // Column 2
        else if (x >= COL_3[0] && x < COL_3[1]) return 2; // Column 3
        else if (x >= COL_4[0] && x < COL_4[1]) return 3; // Column 4
        else return -1; // Invalid column
    }

}
