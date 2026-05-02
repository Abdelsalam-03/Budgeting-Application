package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import manager.AuthenticationManager;

public class SignupView {

    // Auth manager
    private final AuthenticationManager authManager;

    // GUI components
    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;

    Label message;

    public SignupView() {
        authManager = AuthenticationManager.getAuthenticationManager();
    }

    public VBox getView() {
        Label title = new Label("Sign Up");

        nameField = new TextField();
        nameField.setPromptText("User Name");

        emailField = new TextField();
        emailField.setPromptText("Email");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button signupBtn = new Button("Signup");

        message = new Label();

        signupBtn.setOnAction(e -> signupHandler());

        // Form Layout
        VBox form = new VBox(15, title, nameField, emailField, passwordField, signupBtn, message);
        form.setAlignment(Pos.CENTER);

        form.setPadding(new Insets(20));
        form.setMaxWidth(400);
        form.setMaxHeight(300);

        form.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #dddddd;"
                + "-fx-border-radius: 12;"
        );
        return form;
    }

    private void signupHandler() {
        message.setText("");
        try {
            String name = nameField.getText();
            if (name.isEmpty()) {
                message.setText("Please fill in the name field");
                return;
            }

            String email = emailField.getText();
            if (email.isEmpty()) {
                message.setText("Please fill in the email field");
                return;
            }
            String password = passwordField.getText();
            if (password.isEmpty()) {
                message.setText("Please fill in the password field");
                return;
            }

            if (!authManager.signUp(name, email, password)) {
                message.setText("An error occurred. Please try again later.");
            }

        } catch (Exception ex) {
            message.setText(ex.getMessage());
        }
    }
}
