package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages notifications for users
 */
public class NotificationManager {
    
    /**
     * Creates the notifications table if it doesn't exist
     */
    public static void initializeNotificationsTable() {
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS notifications (" +
            "notification_id INT AUTO_INCREMENT PRIMARY KEY, " +
            "user_id INT NOT NULL, " +
            "message TEXT NOT NULL, " +
            "is_read BOOLEAN DEFAULT FALSE, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
            ")";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gets all notifications for a user
     */
    public static List<Notification> getNotificationsForUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Notification notification = new Notification(
                    rs.getInt("notification_id"),
                    rs.getInt("user_id"),
                    rs.getString("message"),
                    rs.getBoolean("is_read"),
                    rs.getTimestamp("created_at")
                );
                notifications.add(notification);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    /**
     * Marks a notification as read
     */
    public static boolean markNotificationAsRead(int notificationId) {
        String update = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(update)) {
            
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Adds a new notification for a user
     */
    public static boolean addNotification(int userId, String message) {
        String insert = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, message);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Represents a notification
     */
    public static class Notification {
        private int id;
        private int userId;
        private String message;
        private boolean isRead;
        private Timestamp createdAt;
        
        public Notification(int id, int userId, String message, boolean isRead, Timestamp createdAt) {
            this.id = id;
            this.userId = userId;
            this.message = message;
            this.isRead = isRead;
            this.createdAt = createdAt;
        }
        
        public int getId() { return id; }
        public int getUserId() { return userId; }
        public String getMessage() { return message; }
        public boolean isRead() { return isRead; }
        public Timestamp getCreatedAt() { return createdAt; }
    }
}