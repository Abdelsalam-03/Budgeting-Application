package view;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import manager.AuthenticationManager;

/**
 * DashboardView represents the main dashboard screen of the application.
 *
 * <p>
 * This class is responsible for:
 * </p>
 * <ul>
 *     <li>Creating the main dashboard layout.</li>
 *     <li>Displaying the navigation header.</li>
 *     <li>Switching between different views such as:
 *         <ul>
 *             <li>Home View</li>
 *             <li>Transactions View</li>
 *             <li>Budget Goals View</li>
 *         </ul>
 *     </li>
 *     <li>Handling user logout functionality.</li>
 * </ul>
 */
public class DashboardView {

    /**
     * Background color used for dashboard cards and header.
     */
    private static final String CARD = "#252840";

    /**
     * Secondary color used for button styling.
     */
    private static final String SUB = "#A0A3B1";

    /**
     * Root container for the dashboard layout.
     */
    private final BorderPane root;

    /**
     * Handles user authentication operations such as logout.
     */
    private final AuthenticationManager authManager;

    /**
     * Constructs a new DashboardView instance.
     *
     * <p>
     * Initializes the root layout and retrieves the singleton
     * instance of the AuthenticationManager.
     * </p>
     */
    public DashboardView() {
        root = new BorderPane();
        authManager = AuthenticationManager.getAuthenticationManager();
    }

    /**
     * Creates and returns the main dashboard view layout.
     *
     * <p>
     * This method:
     * </p>
     * <ul>
     *     <li>Creates the main VBox container.</li>
     *     <li>Adds the navigation header.</li>
     *     <li>Displays the default Home View.</li>
     * </ul>
     *
     * @return VBox containing the dashboard interface.
     */
    public VBox getView() {
        VBox layout = new VBox();

        HBox header = createHeader();
        root.setTop(header);

        showHomeView();

        layout.getChildren().add(root);

        VBox.setVgrow(root, Priority.ALWAYS);

        return layout;
    }

    /**
     * Creates the top navigation header for the dashboard.
     *
     * <p>
     * The header contains navigation buttons for:
     * </p>
     * <ul>
     *     <li>Home</li>
     *     <li>Transactions</li>
     *     <li>Budget Goals</li>
     *     <li>Logout</li>
     * </ul>
     *
     * <p>
     * Each button switches the center content of the dashboard
     * to the corresponding view.
     * </p>
     *
     * @return HBox containing the navigation controls.
     */
    private HBox createHeader() {

        String btnStyle = "-fx-background-color: " + SUB + ";"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-font-weight: bold;";

        ToggleButton homeBtn = new ToggleButton("home");
        ToggleButton transactionsBtn = new ToggleButton("Transactions");
        ToggleButton budgetGoalsBtn = new ToggleButton("Budget Goals");
        ToggleButton logOutBtn = new ToggleButton("Logout");

        // Ensures only one navigation button can be selected at a time
        ToggleGroup group = new ToggleGroup();

        homeBtn.setToggleGroup(group);
        transactionsBtn.setToggleGroup(group);
        budgetGoalsBtn.setToggleGroup(group);
        logOutBtn.setToggleGroup(group);

        // Set Home button as the default selected option
        homeBtn.setSelected(true);

        // Navigation button actions
        homeBtn.setOnAction(e -> showHomeView());

        transactionsBtn.setOnAction(e -> showTransactionsView());

        budgetGoalsBtn.setOnAction(e -> showBudgetGoalsView());

        // Logs the current user out of the application
        logOutBtn.setOnAction(e -> authManager.logout());

        // Logout button has a different style for emphasis
        logOutBtn.setStyle(
                "-fx-background-color: #E53935;"
                        + "-fx-text-fill: white;"
                        + "-fx-background-radius: 6;"
                        + "-fx-font-weight: bold;"
        );

        // Apply shared button style
        homeBtn.setStyle(btnStyle);
        transactionsBtn.setStyle(btnStyle);

        // Create header layout
        HBox header = new HBox(10, homeBtn, transactionsBtn, logOutBtn);

        // Header styling
        header.setStyle("-fx-padding: 10; -fx-background-color: " + CARD + ";");

        return header;
    }

    /**
     * Displays the Home View in the center of the dashboard.
     *
     * <p>
     * This method initializes the HomeView and provides
     * navigation support to return from the BudgetingView.
     * </p>
     */
    public void showHomeView() {

        final HomeView[] dashboardRef = new HomeView[1];

        HomeView dashboard = new HomeView(() ->
                root.setCenter(
                        new BudgetingView(() ->
                                root.setCenter(dashboardRef[0].getView())
                        ).getView()
                )
        );

        dashboardRef[0] = dashboard;

        // Display Home View
        root.setCenter(dashboard.getView());
    }

    /**
     * Displays the Transactions View in the dashboard center.
     */
    public void showTransactionsView() {

        TransactionsView view = new TransactionsView();

        root.setCenter(view.getView());
    }

    /**
     * Displays the Budget Goals View in the dashboard center.
     */
    public void showBudgetGoalsView() {

        AddBudgetGoalView view = new AddBudgetGoalView();

        root.setCenter(view.getView());
    }
}