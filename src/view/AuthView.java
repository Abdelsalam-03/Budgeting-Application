package view;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class AuthView {

    private final BorderPane root;
    

    public AuthView(){
        root = new BorderPane();
    }

    public VBox getView() {
        
        VBox layout = new VBox();
        
        HBox header = createHeader();

        root.setTop(header);
        showLoginView();
        layout.getChildren().add(root);
        VBox.setVgrow(root, Priority.ALWAYS);
        return layout;   
    }
    
    private HBox createHeader() {
        ToggleButton logInBtn = new ToggleButton("Log In");
        ToggleButton signUpBtn = new ToggleButton("Sign Up");

        ToggleGroup group = new ToggleGroup();
        logInBtn.setToggleGroup(group);
        signUpBtn.setToggleGroup(group);

        // Default selected
        logInBtn.setSelected(true);

        // Actions
        logInBtn.setOnAction(e -> showLoginView());
        signUpBtn.setOnAction(e -> showSignupView());
        
        HBox header = new HBox(10, logInBtn, signUpBtn);
        header.setStyle("-fx-padding: 10; -fx-background-color: white;");

        return header;
    }

     public void showLoginView() {
        LoginView loginView = new LoginView();
        root.setCenter(loginView.getView());
    }
    
    public void showSignupView() {
        SignupView signupView = new SignupView();
        root.setCenter(signupView.getView());
    }
}