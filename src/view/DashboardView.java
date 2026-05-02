package view;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import manager.AuthenticationManager;

public class DashboardView {

    private final BorderPane root;
    private final AuthenticationManager authManager;

    public DashboardView() {
        root = new BorderPane();
        authManager = AuthenticationManager.getAuthenticationManager();
    }

    public VBox getView() {

        VBox layout = new VBox();

        HBox header = createHeader();

        root.setTop(header);
        showHomeView();
        layout.getChildren().add(root);
        VBox.setVgrow(root, Priority.ALWAYS);
        return layout;

    }

    private HBox createHeader() {
        ToggleButton homeBtn = new ToggleButton("home");
        ToggleButton transactionsBtn = new ToggleButton("Transactions");
        ToggleButton logOutBtn = new ToggleButton("Logout");

        ToggleGroup group = new ToggleGroup();
        homeBtn.setToggleGroup(group);
        transactionsBtn.setToggleGroup(group);
        logOutBtn.setToggleGroup(group);

        // Default selected
        homeBtn.setSelected(true);

        // Actions
        homeBtn.setOnAction(e -> showHomeView());
        transactionsBtn.setOnAction(e -> showTransactionsView());
        logOutBtn.setOnAction(e -> authManager.logout());
        logOutBtn.setStyle(
                "-fx-background-color: #E53935;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-font-weight: bold;"
        );

        HBox header = new HBox(10, homeBtn, transactionsBtn, logOutBtn);
        header.setStyle("-fx-padding: 10; -fx-background-color: white;");

        return header;
    }

    public void showHomeView() {
//        View view = new View();
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("Welcome To Home Page");

        layout.getChildren().addAll(title);

        root.setCenter(layout);
    }
    
    public void showTransactionsView() {
        TransactionsView view = new TransactionsView();
        root.setCenter(view.getView());
    }

}
