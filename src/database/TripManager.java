package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TripManager {

    public boolean createTrip(int driverId, String origin, String destination, String date, int availableSeats) {
        String sql = "INSERT INTO trips (driver_id, origin, destination, date, available_seats) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, driverId);
            pstmt.setString(2, origin);
            pstmt.setString(3, destination);
            pstmt.setString(4, date);
            pstmt.setInt(5, availableSeats);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Trip> viewAvailableTrips() {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM trips WHERE available_seats > 0 AND date >= CURDATE()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Trip trip = new Trip(
                    rs.getInt("trip_id"),
                    rs.getInt("driver_id"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    rs.getString("date"),
                    rs.getInt("available_seats")
                );
                trips.add(trip);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trips;
    }

    public List<Trip> filterTrips(String origin, String destination, String date) {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM trips WHERE origin = ? AND destination = ? AND date = ? AND available_seats > 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, origin);
            pstmt.setString(2, destination);
            pstmt.setString(3, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Trip trip = new Trip(
                        rs.getInt("trip_id"),
                        rs.getInt("driver_id"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getString("date"),
                        rs.getInt("available_seats")
                    );
                    trips.add(trip);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trips;
    }

    public static class Trip {
        private int tripId;
        private int driverId;
        private String origin;
        private String destination;
        private String date;
        private int availableSeats;

        public Trip(int tripId, int driverId, String origin, String destination, String date, int availableSeats) {
            this.tripId = tripId;
            this.driverId = driverId;
            this.origin = origin;
            this.destination = destination;
            this.date = date;
            this.availableSeats = availableSeats;
        }

        // Getters and setters can be added here if needed
    }
}
