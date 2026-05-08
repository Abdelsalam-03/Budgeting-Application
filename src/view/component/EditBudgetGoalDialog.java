package view.component;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import manager.BudgetGoalManager;
import resource.BudgetGoalResource;

public class EditBudgetGoalDialog {

    public static void show(BudgetGoalResource goal, ObservableList<BudgetGoalResource> listRef) {

        Stage stage = new Stage();
        stage.setTitle("Edit Budget Goal");
        stage.initModality(Modality.APPLICATION_MODAL);

        // Pre-filled fields
        TextField nameField = new TextField(goal.name);
        nameField.setPromptText("Goal name");

        TextField targetField = new TextField(String.valueOf(goal.targetAmount));
        targetField.setPromptText("Target amount");

        ComboBox<String> categoryDropdown = new ComboBox<>();
        categoryDropdown.getItems().addAll(
                "Savings / General", "Food", "Transport", "Entertainment", "Utilities", "Shopping"
        );
        categoryDropdown.getSelectionModel().select(goal.categoryId);

        // Pre-fill deadline
        LocalDate deadlineDate = goal.deadline != null
                ? goal.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : LocalDate.now().plusMonths(1);

        DatePicker deadlinePicker = new DatePicker(deadlineDate);

        CheckBox noDeadlineCheck = new CheckBox("No deadline");
        noDeadlineCheck.setSelected(goal.deadline == null);
        deadlinePicker.setDisable(goal.deadline == null);

        noDeadlineCheck.selectedProperty().addListener((obs, oldVal, isChecked) ->
                deadlinePicker.setDisable(isChecked)
        );

        Label message = new Label();

        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle(
                "-fx-background-color: #3F51B5;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-font-weight: bold;"
        );

        saveBtn.setOnAction(e -> saveHandler(
                goal, nameField, targetField, categoryDropdown,
                deadlinePicker, noDeadlineCheck, message, listRef, stage
        ));

        // Layout
        HBox deadlineRow = new HBox(10, deadlinePicker, noDeadlineCheck);
        deadlineRow.setAlignment(Pos.CENTER_LEFT);

        VBox form = new VBox(15,
                labeledField("Goal Name",     nameField),
                labeledField("Target Amount", targetField),
                labeledField("Category",      categoryDropdown),
                labeledField("Deadline",      deadlineRow),
                saveBtn,
                message
        );

        form.setPadding(new Insets(20));
        form.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #dddddd;"
                + "-fx-border-radius: 12;"
        );

        Scene scene = new Scene(form, 400, 360);

        // Close on ESC
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE:
                    stage.close();
            }
        });

        stage.setScene(scene);
        stage.showAndWait();
    }

    private static void saveHandler(BudgetGoalResource goal,
                                     TextField nameField,
                                     TextField targetField,
                                     ComboBox<String> categoryDropdown,
                                     DatePicker deadlinePicker,
                                     CheckBox noDeadlineCheck,
                                     Label message,
                                     ObservableList<BudgetGoalResource> listRef,
                                     Stage stage) {
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

            BudgetGoalManager manager = new BudgetGoalManager();
            manager.editGoal(goal.id, name, targetAmount, categoryId, deadline);

            // Refresh the list item
            BudgetGoalResource updated = manager.getGoalById(goal.id);
            int index = listRef.indexOf(goal);
            if (index >= 0) {
                listRef.set(index, updated);
            }

            stage.close();

        } catch (NumberFormatException ex) {
            message.setText("Please enter a valid number for the target amount");
            message.setStyle("-fx-text-fill: #F44336;");
        } catch (Exception ex) {
            message.setText(ex.getMessage());
            message.setStyle("-fx-text-fill: #F44336;");
        }
    }

    // Helper method for cleaner UI
    private static VBox labeledField(String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        return new VBox(5, label, field);
    }
}