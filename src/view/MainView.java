package view;

import core.observer.Observer;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import manager.AuthenticationManager;

public class MainView implements Observer {

    private BorderPane root;
    private final AuthenticationManager authManager;

    public MainView() {
        authManager = AuthenticationManager.getAuthenticationManager();
        authManager.addObserver(this);
        root = new BorderPane();
    }

    @Override
    public void update() {
        try {
            authManager.getUser();
            showDashboardView();
        } catch (Exception ex) {
            showAuthView();
        }
    }

    public VBox getView() {
        VBox layout = new VBox();

        showAuthView();
        VBox.setVgrow(root, Priority.ALWAYS);

        layout.getChildren().add(root);
        return layout;
    }

    public void showDashboardView() {

       
        final DashboardView[] dashboardRef = new DashboardView[1];

        DashboardView dashboard = new DashboardView(() ->
            root.setCenter(new BudgetingView(() ->
                root.setCenter(dashboardRef[0].getView())
            ).getView())
        );

        dashboardRef[0] = dashboard;

        root.setCenter(dashboard.getView());
    }

    public void showAuthView() {
        AuthView authView = new AuthView();
        root.setCenter(authView.getView());
    }
}