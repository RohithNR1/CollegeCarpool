package client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import database.DBConnection;
import util.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class TripFormController {

    @FXML private TextField originField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TextField seatsField;
    @FXML private TextField fareField;
    @FXML private Label statusLabel;

    @FXML
    private void handleCreateTrip() {
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        LocalDate date = datePicker.getValue();
        String time = timeField.getText().trim();
        String seats = seatsField.getText().trim();
        String fare = fareField.getText().trim();

        if (origin.isEmpty() || destination.isEmpty() || date == null || time.isEmpty() || seats.isEmpty() || fare.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            int seatsAvailable = Integer.parseInt(seats);
            double farePerSeat = Double.parseDouble(fare);

            int currentUserId = SessionManager.getLoggedInUserId(); // Retrieve user from session

            String sql = "INSERT INTO trips (driver_id, origin, destination, trip_date, trip_time, seats_available, fare_per_seat) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";

            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, currentUserId);
            stmt.setString(2, origin);
            stmt.setString(3, destination);
            stmt.setDate(4, java.sql.Date.valueOf(date));
            stmt.setString(5, time);
            stmt.setInt(6, seatsAvailable);
            stmt.setDouble(7, farePerSeat);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                // Get the last inserted trip_id
                java.sql.ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int tripId = rs.getInt(1);
                    // Add to ride_history table
                    addToRideHistory(tripId, currentUserId);
                }
                
                statusLabel.setText("Trip created successfully!");
                clearForm();
            } else {
                statusLabel.setText("Failed to create trip.");
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Seats and Fare must be numbers.");
        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }

    private void clearForm() {
        originField.clear();
        destinationField.clear();
        datePicker.setValue(null);
        timeField.clear();
        seatsField.clear();
        fareField.clear();
    }
    
    private void addToRideHistory(int tripId, int driverId) {
        String sql = "INSERT INTO ride_history (user_id, trip_id, role, status) VALUES (?, ?, 'driver', 'completed')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setInt(2, tripId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
private void goToDashboard() {
    try {
        Stage stage = (Stage) originField.getScene().getWindow(); // assuming originField is one of your UI fields
        Parent root = FXMLLoader.load(getClass().getResource("/client/ui/Dashboard.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
