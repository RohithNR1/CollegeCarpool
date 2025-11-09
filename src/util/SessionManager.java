package util;

/**
 * Manages user session information throughout the application.
 * Stores the currently logged-in user's ID and other session data.
 */
public class SessionManager {
    
    private static int loggedInUserId = -1;
    private static String loggedInUserName = "";
    
    /**
     * Private constructor to prevent instantiation
     */
    private SessionManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Sets the currently logged-in user's ID
     * @param userId The user ID to set
     */
    public static void setLoggedInUserId(int userId) {
        loggedInUserId = userId;
    }
    
    /**
     * Gets the currently logged-in user's ID
     * @return The logged-in user's ID, or -1 if no user is logged in
     */
    public static int getLoggedInUserId() {
        return loggedInUserId;
    }
    
    /**
     * Sets the currently logged-in user's name
     * @param userName The user name to set
     */
    public static void setLoggedInUserName(String userName) {
        loggedInUserName = userName;
    }
    
    /**
     * Gets the currently logged-in user's name
     * @return The logged-in user's name, or an empty string if no user is logged in
     */
    public static String getLoggedInUserName() {
        return loggedInUserName;
    }
    
    /**
     * Clears all session data when a user logs out
     */
    public static void clearSession() {
        loggedInUserId = -1;
        loggedInUserName = "";
    }
    
    /**
     * Checks if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    public static boolean isUserLoggedIn() {
        return loggedInUserId != -1;
    }
}