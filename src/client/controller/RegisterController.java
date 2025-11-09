package client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField nameField, emailField, phoneField;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private Label statusLabel;

    @FXML
    public void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Input validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        // Email format validation
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            statusLabel.setText("Please enter a valid email address.");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            
            // First check if email already exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                statusLabel.setText("Email address is already registered.");
                return;
            }
            
            // If email doesn't exist, proceed with registration
            String insertQuery = "INSERT INTO users (name, email, phone, password_hash) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, name);
            insertStmt.setString(2, email);
            insertStmt.setString(3, phone);
            insertStmt.setString(4, password); // NOTE: Hash in production

            int rowsInserted = insertStmt.executeUpdate();
            if (rowsInserted > 0) {
                statusLabel.setText("Registration successful!");
                goToLogin();
            } else {
                statusLabel.setText("Failed to register. Please try again.");
            }

        } catch (SQLException e) {
            // More specific error handling
            String errorMessage = "Database error: ";
            if (e.getErrorCode() == 1062) { // MySQL duplicate key error
                errorMessage = "Email already in use.";
            } else if (e.getErrorCode() == 1064) { // SQL syntax error
                errorMessage = "Invalid input format.";
            } else if (e.getMessage().contains("Communications link failure")) {
                errorMessage = "Cannot connect to database. Please check your connection.";
            } else {
                errorMessage += e.getMessage();
            }
            statusLabel.setText(errorMessage);
            e.printStackTrace();
        } finally {
            // Close connection in finally block
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/ui/Login.fxml"));
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Login - College Carpool");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error navigating to login page: " + e.getMessage());
        }
    }
}
