package view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.Date;
import java.time.ZoneId;

import manager.TransactionManager;

public class AddTransactionView {

    public Parent getView() {

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

        // Same categories used in budget goals — always visible
        ComboBox<String> categoryDropdown = new ComboBox<>();
        categoryDropdown.getItems().addAll(
                "Savings / General", "Food", "Transport", "Entertainment", "Utilities", "Shopping"
        );
        categoryDropdown.getSelectionModel().selectFirst();

        DatePicker datePicker = new DatePicker(LocalDate.now());

        CheckBox nowCheck = new CheckBox("Now");
        nowCheck.setSelected(true);

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Optional notes...");
        notesArea.setPrefRowCount(3);

        // Now checkbox behavior
        nowCheck.selectedProperty().addListener((obs, oldVal, isNow) -> {
            if (isNow) {
                datePicker.setValue(LocalDate.now());
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

            try {
                double amount = Double.parseDouble(amountField.getText());
                int categoryId = categoryDropdown.getSelectionModel().getSelectedIndex();
                Date date = Date.from(
                        datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()
                );
                String notes = notesArea.getText();

                TransactionManager tmg = new TransactionManager();
                tmg.addTransaction(amount, categoryId, date, notes, isIncome);

                amountField.setText("");
                notesArea.setText("");
                categoryDropdown.getSelectionModel().selectFirst();

            } catch (Exception ee) {
            }
        });

        // Form Layout
        VBox form = new VBox(15,
                toggleBox,
                labeledField("Amount", amountField),
                labeledField("Category", categoryDropdown),
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