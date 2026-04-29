package view;

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
    
    public SignupView(){
       authManager = AuthenticationManager.getAuthenticationManager();
    }

    public VBox getView() {

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        
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

        layout.getChildren().addAll(title, nameField, emailField, passwordField, signupBtn, message);

        return layout;
    }

    private void signupHandler(){
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

            if(! authManager.signUp(name, email, password)){
                message.setText("An error occurred. Please try again later.");
            }
            
        } catch (Exception ex) {
            message.setText(ex.getMessage());
        }
    }
}