import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main entry point for the 20Wordy* game application.
 * This class sets up the initial scene and window.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Create the PaneOrganizer to manage the layout
        PaneOrganizer paneOrganizer = new PaneOrganizer();

        // Create a scene with the root from PaneOrganizer and set the size from constants
        Scene scene = new Scene(paneOrganizer.getRoot(), Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        // Set the scene to the stage (window) and configure the title
        stage.setScene(scene);
        stage.setTitle("20Wordy8");

        // Show the window
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // Launch the application
    }
}
