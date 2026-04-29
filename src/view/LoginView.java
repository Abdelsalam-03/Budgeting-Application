package view;

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
    
    public LoginView(){
       authManager = AuthenticationManager.getAuthenticationManager();
    }

    public VBox getView() {

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        
        Label title = new Label("Login");

        emailField = new TextField();
        emailField.setPromptText("Email");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");

        message = new Label();

        loginBtn.setOnAction(e -> loginHandler());

        layout.getChildren().addAll(title, emailField, passwordField, loginBtn, message);

        return layout;
    }

    private void loginHandler(){
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

            if(! authManager.logIn(email, password)){
                message.setText("Invalid credentials");
            }
            
        } catch (Exception ex) {
            message.setText(ex.getMessage());
            ex.printStackTrace();
        }
    }
}