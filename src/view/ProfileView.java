package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import manager.ProfileManager;
import resource.UserResource;

public class ProfileView {

    // Profile manager
    private final ProfileManager profileManager;

    public ProfileView() {
        profileManager = new ProfileManager();
    }

    public Parent getView() {

        UserResource user = profileManager.getCurrentUser();

        // Info card
        Label nameDisplay = new Label(user.getName());
        nameDisplay.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        Label idLabel = new Label("ID: " + user.getID());
        idLabel.setStyle("-fx-text-fill: #555; -fx-font-size: 12;");

        VBox infoCard = new VBox(6, nameDisplay, idLabel);
        infoCard.setStyle(
                "-fx-background-color: #F5F5F5;"
                + "-fx-background-radius: 10;"
                + "-fx-padding: 16;"
        );

        // Update name fields
        TextField nameField = new TextField(user.getName());
        nameField.setPromptText("New display name");

        Label nameMessage = new Label();

        Button updateNameBtn = new Button("Update Name");
        updateNameBtn.setStyle(
                "-fx-background-color: #3F51B5;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-font-weight: bold;"
        );

        updateNameBtn.setOnAction(e -> updateNameHandler(nameField, nameDisplay, nameMessage));

        VBox nameSection = new VBox(10,
                new Label("Update Name"),
                labeledField("New Name", nameField),
                updateNameBtn,
                nameMessage
        );
        nameSection.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #dddddd;"
                + "-fx-border-radius: 12;"
                + "-fx-padding: 16;"
        );

        // Change password fields
        PasswordField currentPassField = new PasswordField();
        currentPassField.setPromptText("Current password");

        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("New password");

        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm new password");

        Label passMessage = new Label();

        Button changePassBtn = new Button("Change Password");
        changePassBtn.setStyle(
                "-fx-background-color: #3F51B5;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-font-weight: bold;"
        );

        changePassBtn.setOnAction(e -> changePasswordHandler(
                currentPassField, newPassField, confirmPassField, passMessage));

        VBox passSection = new VBox(10,
                new Label("Change Password"),
                labeledField("Current Password", currentPassField),
                labeledField("New Password",     newPassField),
                labeledField("Confirm Password", confirmPassField),
                changePassBtn,
                passMessage
        );
        passSection.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #dddddd;"
                + "-fx-border-radius: 12;"
                + "-fx-padding: 16;"
        );

        // Form layout
        VBox form = new VBox(20, infoCard, nameSection, passSection);
        form.setPadding(new Insets(24));
        form.setMaxWidth(440);
        form.setAlignment(Pos.TOP_CENTER);

        return new StackPane(form);
    }

    private void updateNameHandler(TextField nameField, Label nameDisplay, Label message) {
        message.setText("");
        try {
            String newName = nameField.getText();
            if (newName.isEmpty()) {
                message.setText("Please fill in the name field");
                return;
            }

            profileManager.updateName(newName);
            nameDisplay.setText(newName);
            message.setText("Name updated successfully!");
            message.setStyle("-fx-text-fill: #4CAF50;");

        } catch (Exception ex) {
            message.setText(ex.getMessage());
            message.setStyle("-fx-text-fill: #F44336;");
        }
    }

    private void changePasswordHandler(PasswordField currentPassField,
                                        PasswordField newPassField,
                                        PasswordField confirmPassField,
                                        Label message) {
        message.setText("");
        try {
            String current = currentPassField.getText();
            if (current.isEmpty()) {
                message.setText("Please fill in the current password field");
                return;
            }

            String newPass = newPassField.getText();
            if (newPass.isEmpty()) {
                message.setText("Please fill in the new password field");
                return;
            }

            String confirm = confirmPassField.getText();
            if (!newPass.equals(confirm)) {
                message.setText("New passwords do not match");
                return;
            }

            profileManager.changePassword(current, newPass);

            currentPassField.clear();
            newPassField.clear();
            confirmPassField.clear();

            message.setText("Password changed successfully!");
            message.setStyle("-fx-text-fill: #4CAF50;");

        } catch (Exception ex) {
            message.setText(ex.getMessage());
            message.setStyle("-fx-text-fill: #F44336;");
        }
    }

    // Helper method for cleaner UI
    private VBox labeledField(String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        return new VBox(5, label, field);
    }
}