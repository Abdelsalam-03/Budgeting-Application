package view;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import manager.BudgetGoalManager;

/**
 * AddBudgetGoalView — form for creating a new budget goal.
 *
 * SRP : Only responsible for capturing user input and delegating to the manager.
 *       No business logic lives here.
 *
 * Matches the style and structure of {@link AddTransactionView}.
 */
public class AddBudgetGoalView {

    // Injected via constructor — DIP: view depends on manager, not concrete repo
    private final BudgetGoalManager manager;

    // Hard-coded user for now, same assumption as AddTransactionView
    private static final int CURRENT_USER_ID = 1;

    public AddBudgetGoalView() {
        this(new BudgetGoalManager());
    }

    public AddBudgetGoalView(BudgetGoalManager manager) {
        this.manager = manager;
    }

    // ─── Build ────────────────────────────────────────────────────────────────

    public Parent getView() {

        // Goal name
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Save for laptop");

        // Target amount
        TextField targetField = new TextField();
        targetField.setPromptText("Enter target amount");

        // Category (0 = savings / general)
        ComboBox<String> categoryDropdown = new ComboBox<>();
        categoryDropdown.getItems().addAll(
                "Savings / General", "Food", "Transport", "Entertainment", "Utilities", "Shopping"
        );
        categoryDropdown.getSelectionModel().selectFirst();

        // Deadline
        DatePicker deadlinePicker = new DatePicker(LocalDate.now().plusMonths(1));

        CheckBox noDeadlineCheck = new CheckBox("No deadline");
        noDeadlineCheck.selectedProperty().addListener((obs, oldVal, isChecked) ->
                deadlinePicker.setDisable(isChecked)
        );

        // Status label for feedback
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 12;");

        // Submit button
        Button createBtn = new Button("Create Goal");
        createBtn.setStyle(
                "-fx-background-color: #3F51B5;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-font-weight: bold;"
        );

        createBtn.setOnAction(e -> handleCreate(
                nameField, targetField, categoryDropdown,
                deadlinePicker, noDeadlineCheck, statusLabel
        ));

        // ─── Layout ───────────────────────────────────────────────────────────

        HBox deadlineRow = new HBox(10, deadlinePicker, noDeadlineCheck);
        deadlineRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox form = new VBox(15,
                labeledField("Goal Name",      nameField),
                labeledField("Target Amount",  targetField),
                labeledField("Category",       categoryDropdown),
                labeledField("Deadline",       deadlineRow),
                createBtn,
                statusLabel
        );

        form.setPadding(new Insets(20));
        form.setMaxWidth(420);
        form.setMaxHeight(440);
        form.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #dddddd;"
                + "-fx-border-radius: 12;"
        );

        return new StackPane(form);
    }

    // ─── Event handler (SRP: kept private, returns void) ─────────────────────

    private void handleCreate(TextField nameField,
                               TextField targetField,
                               ComboBox<String> categoryDropdown,
                               DatePicker deadlinePicker,
                               CheckBox noDeadlineCheck,
                               Label statusLabel) {
        try {
            String name         = nameField.getText().trim();
            double targetAmount = Double.parseDouble(targetField.getText().trim());
            int    categoryId   = categoryDropdown.getSelectionModel().getSelectedIndex();

            Date deadline = noDeadlineCheck.isSelected()
                    ? null
                    : Date.from(deadlinePicker.getValue()
                                              .atStartOfDay(ZoneId.systemDefault())
                                              .toInstant());

            manager.addGoal(CURRENT_USER_ID, name, targetAmount, categoryId, deadline);

            // Reset form
            nameField.clear();
            targetField.clear();
            categoryDropdown.getSelectionModel().selectFirst();
            deadlinePicker.setValue(LocalDate.now().plusMonths(1));

            showSuccess(statusLabel, "Goal created successfully!");

        } catch (NumberFormatException ex) {
            showError(statusLabel, "Please enter a valid number for the target amount.");
        } catch (Exception ex) {
            showError(statusLabel, ex.getMessage());
        }
    }

    // ─── UI helpers ──────────────────────────────────────────────────────────

    private VBox labeledField(String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        return new VBox(5, label, field);
    }

    private void showSuccess(Label label, String msg) {
        label.setText(msg);
        label.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12;");
    }

    private void showError(Label label, String msg) {
        label.setText(msg);
        label.setStyle("-fx-text-fill: #F44336; -fx-font-size: 12;");
    }
}