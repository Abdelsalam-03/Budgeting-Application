package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import manager.AuthenticationManager;
import manager.BudgetGoalManager;
import resource.BudgetGoalResource;
import view.component.BudgetGoalCell;

public class BudgetGoalView {

    // Budget goal manager
    private final BudgetGoalManager manager;

    public BudgetGoalView() {
        manager = new BudgetGoalManager();
    }

    public Parent getView() {

        int userId = AuthenticationManager.getAuthenticationManager().getUser().getID();

        // Shared observable list — both the ListView and AddBudgetView use this
        ObservableList<BudgetGoalResource> goalsList =
                FXCollections.observableArrayList(manager.getGoalsForUser(userId));

        // Goals list
        ListView<BudgetGoalResource> listView = new ListView<>(goalsList);
        listView.setCellFactory(lv -> new BudgetGoalCell(goalsList, id -> manager.deleteGoal(id)));
        listView.setPlaceholder(new Label("No budget goals yet."));

        Label listTitle = new Label("Your Budget Goals");
        listTitle.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");

        VBox listSection = new VBox(10, listTitle, listView);
        listSection.setPadding(new Insets(20));
        VBox.setVgrow(listView, Priority.ALWAYS);

        // Pass the shared list so new goals appear immediately
        AddBudgetGoalView addView = new AddBudgetGoalView(goalsList);
        Parent addForm = addView.getView();

        // Side by side layout
        HBox layout = new HBox(20, listSection, addForm);
        HBox.setHgrow(listSection, Priority.ALWAYS);
        layout.setPadding(new Insets(10));

        return layout;
    }
}