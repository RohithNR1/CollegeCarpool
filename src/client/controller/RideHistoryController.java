package client.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.RideHistory;
import database.DBConnection;
import util.SessionManager;

import java.sql.*;

public class RideHistoryController {

    @FXML private TableView<RideHistory> rideHistoryTable;
    @FXML private TableColumn<RideHistory, Integer> tripIdColumn;
    @FXML private TableColumn<RideHistory, String> originColumn;
    @FXML private TableColumn<RideHistory, String> destinationColumn;
    @FXML private TableColumn<RideHistory, Date> dateColumn;
    @FXML private TableColumn<RideHistory, Time> timeColumn;
    @FXML private TableColumn<RideHistory, Integer> seatsBookedColumn;
    @FXML private TableColumn<RideHistory, Double> fareColumn;
    @FXML private TableColumn<RideHistory, String> roleColumn;

    private ObservableList<RideHistory> rideList = FXCollections.observableArrayList();

    public void initialize() {
        tripIdColumn.setCellValueFactory(cell -> cell.getValue().tripIdProperty().asObject());
        originColumn.setCellValueFactory(cell -> cell.getValue().originProperty());
        destinationColumn.setCellValueFactory(cell -> cell.getValue().destinationProperty());
        dateColumn.setCellValueFactory(cell -> cell.getValue().tripDateProperty());
        timeColumn.setCellValueFactory(cell -> cell.getValue().tripTimeProperty());
        seatsBookedColumn.setCellValueFactory(cell -> cell.getValue().seatsBookedProperty().asObject());
        fareColumn.setCellValueFactory(cell -> cell.getValue().totalFareProperty().asObject());
        roleColumn.setCellValueFactory(cell -> cell.getValue().roleProperty());

        loadRideHistory();
    }

    private void loadRideHistory() {
        rideList.clear();

        // Get the logged-in user ID from the session
        int passengerId = SessionManager.getLoggedInUserId();

        String query = """
            SELECT b.trip_id, t.origin, t.destination, t.trip_date, t.trip_time,
                   b.seats_booked, b.total_fare, rh.role
            FROM bookings b
            JOIN trips t ON b.trip_id = t.trip_id
            JOIN ride_history rh ON b.trip_id = rh.trip_id AND b.passenger_id = rh.user_id
            WHERE b.passenger_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, passengerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RideHistory rh = new RideHistory(
                    rs.getInt("trip_id"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    rs.getDate("trip_date"),
                    rs.getTime("trip_time"),
                    rs.getInt("seats_booked"),
                    rs.getDouble("total_fare"),
                    rs.getString("role")
                );
                rideList.add(rh);
            }

            rideHistoryTable.setItems(rideList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    } @FXML
private void goToDashboard() {
    try {
        Stage stage = (Stage) rideHistoryTable.getScene().getWindow(); // adjust if needed
        Parent root = FXMLLoader.load(getClass().getResource("/client/ui/Dashboard.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
