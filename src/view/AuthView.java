package view;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class AuthView {
    
    // Colors
    private static final String BG     = "#1A1D2E";
    private static final String SUB    = "#A0A3B1";


    private final BorderPane root;
    

    public AuthView(){
        root = new BorderPane();
    }

    public VBox getView() {
        
        VBox layout = new VBox();
        
        HBox header = createHeader();
        root.setStyle("-fx-background-color: "+ BG +";");
        root.setTop(header);
        showLoginView();
        layout.getChildren().add(root);
        VBox.setVgrow(root, Priority.ALWAYS);
        return layout;   
    }
    
    private HBox createHeader() {
        String btnStyle = "-fx-background-color: "+ SUB +";"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-font-weight: bold;";
        ToggleButton logInBtn = new ToggleButton("Log In");
        ToggleButton signUpBtn = new ToggleButton("Sign Up");

        ToggleGroup group = new ToggleGroup();
        logInBtn.setToggleGroup(group);
        signUpBtn.setToggleGroup(group);
        
        logInBtn.setStyle(btnStyle);
        signUpBtn.setStyle(btnStyle);

        // Default selected
        logInBtn.setSelected(true);

        // Actions
        logInBtn.setOnAction(e -> showLoginView());
        signUpBtn.setOnAction(e -> showSignupView());
        
        HBox header = new HBox(10, logInBtn, signUpBtn);
        header.setStyle("-fx-padding: 10; -fx-background-color: "+ BG +";");


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