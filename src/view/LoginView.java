package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import manager.AuthenticationManager;

public class LoginView {

    // Auth manager
    private final AuthenticationManager authManager;

    // GUI components
    private TextField emailField;
    private PasswordField passwordField;
    Label message;

    public LoginView() {
        authManager = AuthenticationManager.getAuthenticationManager();
    }

    public VBox getView() {
        Label title = new Label("Login");

        emailField = new TextField();
        emailField.setPromptText("Email");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");

        message = new Label();

        loginBtn.setOnAction(e -> loginHandler());
        

        // Form Layout
        VBox form = new VBox(15,
                title,
                emailField,
                passwordField,
                loginBtn,
                message
        );
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

    private void loginHandler() {
        message.setText("");
        try {
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

            if (!authManager.logIn(email, password)) {
                message.setText("Invalid credentials");
            }

        } catch (Exception ex) {
            message.setText(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
