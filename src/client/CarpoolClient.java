package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import database.NotificationManager;

public class CarpoolClient extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database tables
            initializeDatabaseTables();
            
            // Load FXML from resources folder
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ui/Login.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("College Carpool Coordinator");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Better to also show an Alert here
        }
    }
    
    /**
     * Initialize any database tables needed for the application
     */
    private void initializeDatabaseTables() {
        // Initialize notifications table
        NotificationManager.initializeNotificationsTable();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
