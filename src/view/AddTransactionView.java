package view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import manager.AuthenticationManager;
import manager.BudgetController;

import manager.TransactionManager;
import model.BudgetCategory;
import model.GoalCategory;

public class AddTransactionView {

    public Parent getView() {
        int userId = AuthenticationManager.getAuthenticationManager().getUser().getID();
        // Toggle (Income / Expense)
        ToggleButton incomeToggle = new ToggleButton("Income");
        ToggleButton expenseToggle = new ToggleButton("Expense");
        incomeToggle.setStyle(
                "-fx-background-color: #4CAF50;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-font-weight: bold;"
        );
        expenseToggle.setStyle(
                "-fx-background-color: #F44336;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-font-weight: bold;"
        );

        ToggleGroup typeGroup = new ToggleGroup();
        incomeToggle.setToggleGroup(typeGroup);
        expenseToggle.setToggleGroup(typeGroup);

        // default
        incomeToggle.setSelected(true);

        HBox toggleBox = new HBox(10, incomeToggle, expenseToggle);

        // Fields
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");

        ComboBox<BudgetCategory> categoryDropdown = new ComboBox<>();
        BudgetController controller = BudgetController.getInstance();
        categoryDropdown.getItems().addAll(controller.getCategories());
        categoryDropdown.setPromptText("Select a category");

        ComboBox<GoalCategory> goalDropdown = new ComboBox<>();
        goalDropdown.getItems().addAll(GoalCategory.all(userId));
        goalDropdown.setPromptText("Select a category");

        DatePicker datePicker = new DatePicker(LocalDateTime.now().toLocalDate());

        CheckBox nowCheck = new CheckBox("Now");
        nowCheck.setSelected(true);

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Optional notes...");
        notesArea.setPrefRowCount(3);

        // Layout containers
        VBox categoryBox = new VBox(5,
                new Label("Category"),
                categoryDropdown
        );
        // Layout containers
        VBox goalBox = new VBox(5,
                new Label("Category"),
                goalDropdown
        );

        // Initially hidden (because default is Income)
        categoryBox.setVisible(false);
        categoryBox.setManaged(false);

        // Toggle behavior
        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isExpense = newVal == expenseToggle;

            categoryBox.setVisible(isExpense);
            categoryBox.setManaged(isExpense);
            goalBox.setVisible(!isExpense);
            goalBox.setManaged(!isExpense);
        });

        // Now checkbox behavior
        nowCheck.selectedProperty().addListener((obs, oldVal, isNow) -> {
            if (isNow) {
                datePicker.setValue(LocalDateTime.now().toLocalDate());
                datePicker.setDisable(true);
            } else {
                datePicker.setDisable(false);
            }
        });

        datePicker.setDisable(true); // default because "Now" is checked

        // Submit button
        Button createBtn = new Button("Create Transaction");

        createBtn.setOnAction(e -> {
            boolean isIncome = incomeToggle.isSelected();

            double amount = Double.parseDouble(amountField.getText());
            Integer category = isIncome ? goalDropdown.getValue().id : categoryDropdown.getValue().id;
            LocalDateTime date = LocalDateTime.of(datePicker.getValue(), LocalTime.of(0, 0, 0, 0));
            String notes = notesArea.getText();

            TransactionManager tmg = new TransactionManager();
            try {
                tmg.addTransaction(amount, category, date, notes, isIncome);
                amountField.setText("");
                notesArea.setText("");
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        });

        // Form Layout
        VBox form = new VBox(15,
                toggleBox,
                labeledField("Amount", amountField),
                categoryBox,
                goalBox,
                labeledField("Date", new HBox(10, datePicker, nowCheck)),
                labeledField("Notes", notesArea),
                createBtn
        );

        form.setPadding(new Insets(20));
        form.setMaxWidth(400);
        form.setMaxHeight(400);

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
