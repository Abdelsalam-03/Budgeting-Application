package view;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import manager.AuthenticationManager;

public class DashboardView {

    private static final String CARD = "#252840";
    private static final String SUB = "#A0A3B1";
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
        String btnStyle = "-fx-background-color: "+ SUB +";"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-font-weight: bold;";
        ToggleButton homeBtn = new ToggleButton("home");
        ToggleButton transactionsBtn = new ToggleButton("Transactions");
        ToggleButton budgetGoalsBtn  = new ToggleButton("Budget Goals");
        ToggleButton logOutBtn       = new ToggleButton("Logout");

        ToggleGroup group = new ToggleGroup();
        homeBtn.setToggleGroup(group);
        transactionsBtn.setToggleGroup(group);
        budgetGoalsBtn.setToggleGroup(group);
        logOutBtn.setToggleGroup(group);

        // Default selected
        homeBtn.setSelected(true);

        // Actions
        homeBtn.setOnAction(e -> showHomeView());
        transactionsBtn.setOnAction(e -> showTransactionsView());
        budgetGoalsBtn.setOnAction(e -> showBudgetGoalsView());
        logOutBtn.setOnAction(e -> authManager.logout());
        logOutBtn.setStyle(
                "-fx-background-color: #E53935;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-font-weight: bold;"
        );
        
        homeBtn.setStyle(btnStyle);
        budgetGoalsBtn.setStyle(btnStyle);
        transactionsBtn.setStyle(btnStyle);

        HBox header = new HBox(10, homeBtn, transactionsBtn, budgetGoalsBtn, logOutBtn);
        header.setStyle("-fx-padding: 10; -fx-background-color: " + CARD + ";");
        return header;
    }

    public void showHomeView() {
        final HomeView[] dashboardRef = new HomeView[1];

        HomeView dashboard = new HomeView(()
                -> root.setCenter(new BudgetingView(()
                        -> root.setCenter(dashboardRef[0].getView())
                ).getView())
        );

        dashboardRef[0] = dashboard;

        root.setCenter(dashboard.getView());

//        root.setCenter(layout);
    }

    public void showTransactionsView() {
        TransactionsView view = new TransactionsView();
        root.setCenter(view.getView());
    }

    public void showBudgetGoalsView() {
        BudgetGoalView view = new BudgetGoalView();
        root.setCenter(view.getView());
    }
}