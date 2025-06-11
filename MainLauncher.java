import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainLauncher extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Stock Management System");

        // Create buttons
        Button consoleBtn = new Button("Console Version");
        Button guiBtn = new Button("GUI Version");

        // Style buttons
        String buttonStyle = "-fx-font-size: 14px; -fx-padding: 10 20; -fx-min-width: 150px;";
        consoleBtn.setStyle(buttonStyle);
        guiBtn.setStyle(buttonStyle);

        // Add actions
        consoleBtn.setOnAction(e -> {
            primaryStage.close();
            new StockManagement().runConsole();
            Platform.exit();
        });

        guiBtn.setOnAction(e -> {
            primaryStage.close();
            // Launch GUI
            Stage guiStage = new Stage();
            new StockManagementGUI().start(guiStage);
        });

        // Create layout
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.getChildren().addAll(consoleBtn, guiBtn);

        // Set scene
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
