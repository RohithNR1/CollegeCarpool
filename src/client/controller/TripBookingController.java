package client.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Trip;
import database.DBConnection;
import util.SessionManager;

import java.sql.*;
import java.time.LocalDate;

public class TripBookingController {

    @FXML private TextField originField;
    @FXML private TextField destinationField;
    @FXML private DatePicker tripDatePicker;
    @FXML private TextField seatsField;

    @FXML private TableView<Trip> tripTable;
    @FXML private TableColumn<Trip, String> driverColumn;
    @FXML private TableColumn<Trip, String> originColumn;
    @FXML private TableColumn<Trip, String> destinationColumn;
    @FXML private TableColumn<Trip, Date> dateColumn;
    @FXML private TableColumn<Trip, Time> timeColumn;
    @FXML private TableColumn<Trip, Integer> seatsColumn;
    @FXML private TableColumn<Trip, Double> fareColumn;

    private ObservableList<Trip> tripList = FXCollections.observableArrayList();

    public void initialize() {
        driverColumn.setCellValueFactory(new PropertyValueFactory<>("driverName"));
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("tripDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("tripTime"));
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("seatsAvailable"));
        fareColumn.setCellValueFactory(new PropertyValueFactory<>("farePerSeat"));
    }

    @FXML
    private void searchTrips() {
        tripList.clear();
        String origin = originField.getText();
        String destination = destinationField.getText();
        LocalDate date = tripDatePicker.getValue();

        String query = "SELECT t.*, u.name as driver_name FROM trips t JOIN users u ON t.driver_id = u.user_id " +
                "WHERE t.origin = ? AND t.destination = ? AND t.trip_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, origin);
            stmt.setString(2, destination);
            stmt.setDate(3, Date.valueOf(date));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Trip trip = new Trip(
                        rs.getInt("trip_id"),
                        rs.getString("driver_name"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getDate("trip_date"),
                        rs.getTime("trip_time"),
                        rs.getInt("seats_available"),
                        rs.getDouble("fare_per_seat")
                );
                tripList.add(trip);
            }

            tripTable.setItems(tripList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void bookSelectedTrip() {
        Trip selectedTrip = tripTable.getSelectionModel().getSelectedItem();
        if (selectedTrip == null) {
            showAlert("No trip selected", "Please select a trip to book.");
            return;
        }

        int seatsToBook;
        try {
            seatsToBook = Integer.parseInt(seatsField.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid seats", "Please enter a valid number of seats.");
            return;
        }

        // Get the logged-in user ID from the session
        int passengerId = SessionManager.getLoggedInUserId();
        String passengerName = SessionManager.getLoggedInUserName();

        String insert = "INSERT INTO bookings (trip_id, passenger_id, seats_booked, total_fare) " +
                        "VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insert)) {

            stmt.setInt(1, selectedTrip.getTripId());
            stmt.setInt(2, passengerId);
            stmt.setInt(3, seatsToBook);
            stmt.setDouble(4, seatsToBook * selectedTrip.getFarePerSeat());
            int bookingResult = stmt.executeUpdate();
            
            if (bookingResult > 0) {
                // Also add to ride_history table
                addToRideHistory(selectedTrip.getTripId(), passengerId);
                
                // Get driver contact information
                int driverId = getDriverIdFromTrip(selectedTrip.getTripId());
                String[] driverContact = getUserContactInfo(driverId);
                
                if (driverContact != null) {
                    // Format trip details
                    String tripDetails = String.format(
                        "From: %s\nTo: %s\nDate: %s\nTime: %s\nSeats Booked: %d\nTotal Fare: $%.2f",
                        selectedTrip.getOrigin(),
                        selectedTrip.getDestination(),
                        selectedTrip.getTripDate(),
                        selectedTrip.getTripTime(),
                        seatsToBook,
                        seatsToBook * selectedTrip.getFarePerSeat()
                    );
                    
                    // Show driver contact info to passenger
                    ContactInfoDialog.show(
                        "Booking Successful", 
                        "Your ride has been booked successfully! Here's the driver's contact information:",
                        driverContact[0], // name
                        driverContact[1], // phone
                        driverContact[2], // email
                        tripDetails
                    );
                    
                    // Also notify the driver about the booking
                    notifyDriverAboutBooking(driverId, passengerId, selectedTrip.getTripId(), seatsToBook);
                } else {
                    showAlert("Success", "Trip booked successfully!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Booking failed. Please try again.");
        }
    }
    
    /**
     * Gets the driver ID for a specific trip
     */
    private int getDriverIdFromTrip(int tripId) {
        String query = "SELECT driver_id FROM trips WHERE trip_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, tripId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("driver_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Gets contact information for a user
     * @return Array containing [name, phone, email]
     */
    private String[] getUserContactInfo(int userId) {
        String query = "SELECT name, phone, email FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String[] contactInfo = new String[3];
                contactInfo[0] = rs.getString("name");
                contactInfo[1] = rs.getString("phone");
                contactInfo[2] = rs.getString("email");
                return contactInfo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Notifies the driver about a new booking by storing the notification
     */
    private void notifyDriverAboutBooking(int driverId, int passengerId, int tripId, int seatsBooked) {
        // Get passenger contact information
        String[] passengerContact = getUserContactInfo(passengerId);
        if (passengerContact == null) return;
        
        // Create a notification record in the database
        String insertNotification = "INSERT INTO notifications (user_id, message, is_read, created_at) VALUES (?, ?, 0, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertNotification)) {
            
            // Format the notification message
            String message = String.format(
                "New booking for your trip (ID: %d). %s has booked %d seat(s). Contact: %s, %s",
                tripId,
                passengerContact[0],
                seatsBooked,
                passengerContact[1],
                passengerContact[2]
            );
            
            stmt.setInt(1, driverId);
            stmt.setString(2, message);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
private void goToDashboard() {
    try {
        Stage stage = (Stage) destinationField.getScene().getWindow(); // adjust based on your field
        Parent root = FXMLLoader.load(getClass().getResource("/client/ui/Dashboard.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    private void addToRideHistory(int tripId, int passengerId) {
        String sql = "INSERT INTO ride_history (user_id, trip_id, role, status) VALUES (?, ?, 'passenger', 'completed')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, passengerId);
            stmt.setInt(2, tripId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
