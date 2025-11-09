package client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import database.DBConnection;
import java.io.IOException;
import java.sql.*;
import util.SessionManager;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    public void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Clear previous status
        statusLabel.setText("");

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        Connection conn = null;
        try {
            // Show that login is in progress
            statusLabel.setText("Logging in...");
            
            conn = DBConnection.getConnection();
            String query = "SELECT * FROM users WHERE email = ? AND password_hash = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password); // Note: Use hashed password in production

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                
                // Set session information
                SessionManager.setLoggedInUserId(userId);
                SessionManager.setLoggedInUserName(name);
                
                // Show success message before navigation
                statusLabel.setText("Login successful! Loading dashboard...");
                
                // Navigate to dashboard
                goToDashboard(userId, name);
            } else {
                statusLabel.setText("Invalid email or password.");
            }
        } catch (SQLException e) {
            String errorMessage = "Database error: ";
            if (e.getMessage().contains("Communications link failure")) {
                errorMessage = "Cannot connect to database. Please check your connection.";
            } else {
                errorMessage += e.getMessage();
            }
            statusLabel.setText(errorMessage);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void goToDashboard(int userId, String name) {
        try {
            // Create the loader
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/client/ui/Dashboard.fxml"));
            
            // Load the FXML
            Scene scene = new Scene(loader.load());
            
            // Get the controller and set user info
            DashboardController controller = loader.getController();
            if (controller != null) {
                controller.setUserInfo(userId, name);
            } else {
                System.err.println("Dashboard controller is null!");
                statusLabel.setText("Error: Could not initialize dashboard.");
                return;
            }
            
            // Set the scene on the stage
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard - College Carpool");
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            statusLabel.setText("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ui/Register.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Register - College Carpool");
        } catch (Exception e) {
            statusLabel.setText("Error navigating to register page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
