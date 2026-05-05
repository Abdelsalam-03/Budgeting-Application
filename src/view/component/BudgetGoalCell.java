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

/**
 * BudgetGoalCell — custom ListCell that renders a single budget goal row.
 *
 * SRP  : Only responsible for rendering one cell; delete is delegated via callback.
 * OCP  : Visual changes are isolated here; no other class needs to change.
 *
 * Mirrors {@link TransactionCell} in structure and conventions.
 */
public class BudgetGoalCell extends ListCell<BudgetGoalResource> {

    private final Consumer<Integer>                 deleteAction;
    private final ObservableList<BudgetGoalResource> listRef;

    // ─── Reusable node references ─────────────────────────────────────────────
    private final HBox      root        = new HBox(12);
    private final Label     statusIcon  = new Label();  // ✓ or ◎
    private final Label     nameLabel   = new Label();
    private final Label     amountLabel = new Label();
    private final ProgressBar progressBar = new ProgressBar(0);
    private final Label     percentLabel = new Label();
    private final Button    deleteBtn   = new Button("Delete");

    public BudgetGoalCell(ObservableList<BudgetGoalResource> listRef,
                          Consumer<Integer> deleteAction) {
        this.listRef      = listRef;
        this.deleteAction = deleteAction;

        buildLayout();
        wireHandlers();
    }

    // ─── Layout ───────────────────────────────────────────────────────────────

    private void buildLayout() {
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

        root.getChildren().addAll(statusIcon, infoBox, spacer, deleteBtn);
    }

    // ─── Handlers ─────────────────────────────────────────────────────────────

    private void wireHandlers() {
        // Click row → open detail dialog
        root.setOnMouseClicked(e -> {
            BudgetGoalResource item = getItem();
            if (item != null) {
                ViewBudgetGoalDialog.show(item);
            }
        });

        // Delete button
        deleteBtn.setOnAction(e -> {
            BudgetGoalResource item = getItem();
            if (item == null) return;
            try {
                deleteAction.accept(item.id);
                listRef.remove(item);
            } catch (Exception ex) {
                System.out.println("Delete failed: " + ex.getMessage());
            }
        });
    }

    // ─── Cell update (called by JavaFX) ───────────────────────────────────────

    @Override
    protected void updateItem(BudgetGoalResource item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        nameLabel.setText(item.name);
        amountLabel.setText(String.format("%.2f / %.2f EGP",
                item.currentAmount, item.targetAmount));

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