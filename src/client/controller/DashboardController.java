package client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.SessionManager;
import database.NotificationManager;
import database.NotificationManager.Notification;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private BorderPane dashboardPane;
    @FXML private HBox topHBox; // Reference to the top HBox in the FXML
    
    private int userId;
    private String userName;
    
    private Button notificationsBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // This method is called after the FXML file has been loaded
        // Initialize any UI components here
        
        // Check if user is already set in SessionManager
        if (SessionManager.isUserLoggedIn()) {
            userId = SessionManager.getLoggedInUserId();
            userName = SessionManager.getLoggedInUserName();
            updateUserDisplay();
            
            // Add notifications button
            addNotificationsButton();
            
            // Check for notifications
            checkForNotifications();
        }
    }
    
    /**
     * Adds a notifications button to the top HBox
     */
    private void addNotificationsButton() {
        notificationsBtn = new Button("Notifications");
        notificationsBtn.setOnAction(e -> showNotifications());
        
        // Add it to the HBox after the welcome label
        if (topHBox != null) {
            topHBox.getChildren().add(notificationsBtn);
        }
    }
    
    /**
     * Checks for unread notifications and updates the button text
     */
    private void checkForNotifications() {
        List<Notification> notifications = NotificationManager.getNotificationsForUser(userId);
        
        // Count unread notifications
        long unreadCount = notifications.stream()
                .filter(n -> !n.isRead())
                .count();
        
        // Update button text if there are unread notifications
        if (unreadCount > 0) {
            notificationsBtn.setText("Notifications (" + unreadCount + ")");
            notificationsBtn.setStyle("-fx-font-weight: bold;");
        }
    }
    
    /**
     * Shows a dialog with all notifications for the current user
     */
    private void showNotifications() {
        List<Notification> notifications = NotificationManager.getNotificationsForUser(userId);
        
        if (notifications.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notifications");
            alert.setHeaderText("No Notifications");
            alert.setContentText("You don't have any notifications at this time.");
            alert.showAndWait();
            return;
        }
        
        // Create a dialog to show notifications
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Your Notifications");
        alert.setHeaderText("Booking Notifications");
        
        // Create a VBox to hold all notifications
        VBox notificationsBox = new VBox(10);
        
        for (Notification notification : notifications) {
            Label messageLabel = new Label(notification.getMessage());
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(400);
            
            // Add a timestamp
            Label timestampLabel = new Label("Received: " + notification.getCreatedAt().toString());
            timestampLabel.setStyle("-fx-font-size: 10px; -fx-font-style: italic;");
            
            VBox notificationBox = new VBox(5, messageLabel, timestampLabel);
            notificationBox.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
            
            notificationsBox.getChildren().add(notificationBox);
            
            // Mark as read
            if (!notification.isRead()) {
                NotificationManager.markNotificationAsRead(notification.getId());
            }
        }
        
        // Update the notifications button
        notificationsBtn.setText("Notifications");
        notificationsBtn.setStyle("");
        
        // Set the content
        alert.getDialogPane().setContent(notificationsBox);
        alert.showAndWait();
    }
    
    /**
     * Updates the UI to display user information
     */
    private void updateUserDisplay() {
        if (topHBox != null && userName != null && !userName.isEmpty()) {
            // Create a welcome label
            Label welcomeLabel = new Label("Welcome, " + userName + "!");
            welcomeLabel.setStyle("-fx-font-size: 14px; -fx-padding: 0 0 0 20px;");
            
            // Add it to the HBox
            topHBox.getChildren().add(welcomeLabel);
        }
    }

    @FXML
    private void goToTripForm() {
        loadScene("TripForm.fxml");
    }

    @FXML
    private void goToTripBooking() {
        loadScene("TripBooking.fxml");
    }

    @FXML
    private void goToRideHistory() {
        loadScene("RideHistory.fxml");
    }
    
    @FXML
    private void handleLogout() {
        // Clear the session
        SessionManager.clearSession();
        
        try {
            // Navigate back to login screen
            Stage stage = (Stage) dashboardPane.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/client/ui/Login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - College Carpool");
            stage.show();
        } catch (Exception e) {
            System.err.println("Error navigating to login page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sets the user information and updates the UI
     */
    public void setUserInfo(int userId, String userName) {
        System.out.println("Setting user info: ID=" + userId + ", Name=" + userName);
        this.userId = userId;
        this.userName = userName;
        updateUserDisplay();
    }

    /**
     * Loads a new scene
     */
    private void loadScene(String fxmlFile) {
        try {
            // Get the current stage
            Stage stage = (Stage) dashboardPane.getScene().getWindow();
            
            // Create a new FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ui/" + fxmlFile));
            
            // Load the FXML
            Parent root = loader.load();
            
            // Create a new scene
            Scene scene = new Scene(root);
            
            // Set the scene on the stage
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading scene: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
