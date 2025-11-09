package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingManager {

    public boolean bookTrip(int userId, int tripId) {
        String insertBookingSql = "INSERT INTO bookings (user_id, trip_id, status) VALUES (?, ?, 'booked')";
        String updateSeatsSql = "UPDATE trips SET available_seats = available_seats - 1 WHERE trip_id = ? AND available_seats > 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement insertBookingStmt = conn.prepareStatement(insertBookingSql);
             PreparedStatement updateSeatsStmt = conn.prepareStatement(updateSeatsSql)) {

            conn.setAutoCommit(false); // Start transaction

            // Insert booking
            insertBookingStmt.setInt(1, userId);
            insertBookingStmt.setInt(2, tripId);
            int bookingResult = insertBookingStmt.executeUpdate();

            // Update available seats
            updateSeatsStmt.setInt(1, tripId);
            int seatsResult = updateSeatsStmt.executeUpdate();

            if (bookingResult > 0 && seatsResult > 0) {
                conn.commit(); // Commit transaction
                return true;
            } else {
                conn.rollback(); // Rollback transaction
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Booking> viewUserBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.trip_id, t.origin, t.destination, t.date, b.status " +
                     "FROM bookings b JOIN trips t ON b.trip_id = t.trip_id WHERE b.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getInt("booking_id"),
                    rs.getInt("trip_id"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    rs.getString("date"),
                    rs.getString("status")
                );
                bookings.add(booking);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean cancelBooking(int bookingId) {
        String updateBookingSql = "UPDATE bookings SET status = 'cancelled' WHERE booking_id = ?";
        String updateSeatsSql = "UPDATE trips SET available_seats = available_seats + 1 WHERE trip_id = " +
                                "(SELECT trip_id FROM bookings WHERE booking_id = ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement updateBookingStmt = conn.prepareStatement(updateBookingSql);
             PreparedStatement updateSeatsStmt = conn.prepareStatement(updateSeatsSql)) {

            conn.setAutoCommit(false); // Start transaction

            // Update booking status
            updateBookingStmt.setInt(1, bookingId);
            int bookingResult = updateBookingStmt.executeUpdate();

            // Update available seats
            updateSeatsStmt.setInt(1, bookingId);
            int seatsResult = updateSeatsStmt.executeUpdate();

            if (bookingResult > 0 && seatsResult > 0) {
                conn.commit(); // Commit transaction
                return true;
            } else {
                conn.rollback(); // Rollback transaction
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class Booking {
        private int bookingId;
        private int tripId;
        private String origin;
        private String destination;
        private String date;
        private String status;

        public Booking(int bookingId, int tripId, String origin, String destination, String date, String status) {
            this.bookingId = bookingId;
            this.tripId = tripId;
            this.origin = origin;
            this.destination = destination;
            this.date = date;
            this.status = status;
        }

        // Getters and setters can be added here if needed
    }
}
