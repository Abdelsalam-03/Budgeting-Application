package view.component;

import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;

import resource.BudgetGoalResource;

public class BudgetGoalCell extends ListCell<BudgetGoalResource> {

    private Consumer<Integer> deleteAction;

    private ObservableList<BudgetGoalResource> listRef;

    // Cell nodes
    private HBox root = new HBox(12);
    private Label statusIcon  = new Label();
    private Label nameLabel   = new Label();
    private Label amountLabel = new Label();
    private ProgressBar progressBar = new ProgressBar(0);
    private Label percentLabel = new Label();
    private Button viewBtn   = new Button("View");
    private Button editBtn   = new Button("Edit");
    private Button deleteBtn = new Button("Delete");

    public BudgetGoalCell(ObservableList<BudgetGoalResource> listRef, Consumer<Integer> deleteAction) {
        this.listRef      = listRef;
        this.deleteAction = deleteAction;

        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(10));

        progressBar.setPrefWidth(120);
        progressBar.setMaxHeight(8);

        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        amountLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        percentLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #555;");

        VBox infoBox = new VBox(4, nameLabel, amountLabel,
                new HBox(8, progressBar, percentLabel));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttons = new HBox(6, viewBtn, editBtn, deleteBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(statusIcon, infoBox, spacer, buttons);

        // View button → open detail dialog
        viewBtn.setOnAction(e -> {
            if (getItem() != null) {
                ViewBudgetGoalDialog.show(getItem());
            }
        });

        // Edit button → open edit dialog
        editBtn.setOnAction(e -> {
            if (getItem() != null) {
                EditBudgetGoalDialog.show(getItem(), listRef);
            }
        });

        // Delete button
        deleteBtn.setOnAction(e -> {
            try {
                deleteAction.accept(getItem().id);
                BudgetGoalResource item = getItem();
                if (item != null) {
                    listRef.remove(item);
                }
            } catch (Exception exception) {
                System.out.println("Exception occurred");
            }
        });
    }

    @Override
    protected void updateItem(BudgetGoalResource item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            nameLabel.setText(item.name);
            amountLabel.setText(String.format("%.2f / %.2f EGP", item.currentAmount, item.targetAmount));

            double ratio = item.progressRatio();
            progressBar.setProgress(ratio);
            percentLabel.setText(String.format("%.0f%%", ratio * 100));

            if (item.isCompleted) {
                statusIcon.setText("✓");
                statusIcon.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 18; -fx-font-weight: bold;");
                progressBar.setStyle("-fx-accent: #4CAF50;");
            } else {
                statusIcon.setText("◎");
                statusIcon.setStyle("-fx-text-fill: #3F51B5; -fx-font-size: 18;");
                progressBar.setStyle("-fx-accent: #3F51B5;");
            }

            setGraphic(root);
        }
    }
}