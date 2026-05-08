package view;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import manager.BudgetGoalManager;
import manager.AuthenticationManager;
import resource.BudgetGoalResource;

public class AddBudgetGoalView {

    // Budget goal manager
    private final BudgetGoalManager manager;

    // Shared list reference to update after creation
    private final ObservableList<BudgetGoalResource> goalsList;

    public AddBudgetGoalView(ObservableList<BudgetGoalResource> goalsList) {
        manager = new BudgetGoalManager();
        this.goalsList = goalsList;
    }

    public Parent getView() {

        int userId = AuthenticationManager.getAuthenticationManager().getUser().getID();

        // Fields
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Save for laptop");

        TextField targetField = new TextField();
        targetField.setPromptText("Enter target amount");

        ComboBox<String> categoryDropdown = new ComboBox<>();
        categoryDropdown.getItems().addAll(
                "Savings / General", "Food", "Transport", "Entertainment", "Utilities", "Shopping"
        );
        categoryDropdown.getSelectionModel().selectFirst();

        DatePicker deadlinePicker = new DatePicker(LocalDate.now().plusMonths(1));

        CheckBox noDeadlineCheck = new CheckBox("No deadline");
        noDeadlineCheck.selectedProperty().addListener((obs, oldVal, isChecked) ->
                deadlinePicker.setDisable(isChecked)
        );

        Label message = new Label();

        // Submit button
        Button createBtn = new Button("Create Goal");
        createBtn.setStyle(
                "-fx-background-color: #3F51B5;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-font-weight: bold;"
        );

        createBtn.setOnAction(e -> {
            message.setText("");
            try {
                String name = nameField.getText();
                if (name.isEmpty()) {
                    message.setText("Please fill in the name field");
                    return;
                }

                double targetAmount = Double.parseDouble(targetField.getText().trim());
                int categoryId = categoryDropdown.getSelectionModel().getSelectedIndex();

                Date deadline = noDeadlineCheck.isSelected()
                        ? null
                        : Date.from(deadlinePicker.getValue()
                                                  .atStartOfDay(ZoneId.systemDefault())
                                                  .toInstant());

                BudgetGoalResource newGoal = manager.addGoal(userId, name, targetAmount, categoryId, deadline);

                // Add to shared list so ListView updates immediately
                goalsList.add(newGoal);

                // Reset form
                nameField.clear();
                targetField.clear();
                categoryDropdown.getSelectionModel().selectFirst();
                deadlinePicker.setValue(LocalDate.now().plusMonths(1));

                message.setText("Goal created successfully!");
                message.setStyle("-fx-text-fill: #4CAF50;");

            } catch (NumberFormatException ex) {
                message.setText("Please enter a valid number for the target amount");
                message.setStyle("-fx-text-fill: #F44336;");
            } catch (Exception ex) {
                message.setText(ex.getMessage());
                message.setStyle("-fx-text-fill: #F44336;");
            }
        });

        // Deadline row
        HBox deadlineRow = new HBox(10, deadlinePicker, noDeadlineCheck);
        deadlineRow.setAlignment(Pos.CENTER_LEFT);

        // Form layout
        VBox form = new VBox(15,
                labeledField("Goal Name",     nameField),
                labeledField("Target Amount", targetField),
                labeledField("Category",      categoryDropdown),
                labeledField("Deadline",      deadlineRow),
                createBtn,
                message
        );

        form.setPadding(new Insets(20));
        form.setMaxWidth(400);
        form.setMaxHeight(440);

        form.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #dddddd;"
                + "-fx-border-radius: 12;"
        );

        return new StackPane(form);
    }

    // Helper method for cleaner UI
    private VBox labeledField(String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        return new VBox(5, label, field);
    }
}