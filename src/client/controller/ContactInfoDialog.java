package client.controller;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * A dialog for displaying contact information after a successful trip booking
 */
public class ContactInfoDialog {

    /**
     * Shows a dialog with contact information
     * 
     * @param title The dialog title
     * @param headerText The header text
     * @param name The name of the person
     * @param phone The phone number of the person
     * @param email The email of the person
     * @param tripDetails Additional trip details to display
     */
    public static void show(String title, String headerText, String name, String phone, String email, String tripDetails) {
        // Create the dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        
        // Set the button types
        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButtonType);
        
        // Create the content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20, 20, 10, 20));
        
        // Trip details section
        if (tripDetails != null && !tripDetails.isEmpty()) {
            Label tripDetailsLabel = new Label("Trip Details:");
            tripDetailsLabel.setStyle("-fx-font-weight: bold;");
            TextArea tripDetailsArea = new TextArea(tripDetails);
            tripDetailsArea.setEditable(false);
            tripDetailsArea.setPrefRowCount(3);
            tripDetailsArea.setWrapText(true);
            content.getChildren().addAll(tripDetailsLabel, tripDetailsArea);
        }
        
        // Contact information section
        Label contactLabel = new Label("Contact Information:");
        contactLabel.setStyle("-fx-font-weight: bold;");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 0, 0, 0));
        
        // Add contact details to grid
        grid.add(new Label("Name:"), 0, 0);
        grid.add(new Label(name), 1, 0);
        
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(new Label(phone), 1, 1);
        
        grid.add(new Label("Email:"), 0, 2);
        grid.add(new Label(email), 1, 2);
        
        content.getChildren().addAll(contactLabel, grid);
        
        // Set the content
        dialog.getDialogPane().setContent(content);
        
        // Show the dialog
        dialog.showAndWait();
    }
}