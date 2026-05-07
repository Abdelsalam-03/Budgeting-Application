package view;

import manager.BudgetController;
import model.Budget;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.BudgetCategory;

/**
 * BudgetingView – Budgets list screen + inline Create/Edit form (US #4).
 *
 * Follows the same getView() pattern as the rest of the team's views.
 *
 * Usage in MainView.java: BudgetingView bv = new BudgetingView(() ->
 * showDashboard()); root.setCenter(bv.getView());
 */
public class BudgetingView {

    private BudgetController controller;
    private final YearMonth period = YearMonth.now();
    private final Runnable onGoBack;   // callback → back to Dashboard

    private static final String BG = "#1A1D2E";
    private static final String CARD = "#252840";
    private static final String ACCENT = "#6C63FF";
    private static final String GREEN = "#4CAF50";
    private static final String RED = "#F44336";
    private static final String ORANGE = "#FF9800";
    private static final String WHITE = "#FFFFFF";
    private static final String SUB = "#A0A3B1";
    private static final String FIELDBG = "#1E2138";

    private VBox listContainer; // refreshed after each save

    public BudgetingView(Runnable onGoBack) {
        this.onGoBack = onGoBack;
        controller = BudgetController.getInstance();
    }

    /**
     * Returns the fully-built budgets pane.
     */
    public Node getView() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color:" + BG + ";");

        // Header row
        Label title = lbl("Budgets", 22, FontWeight.BOLD, WHITE);
        Button addBtn = btn("+ New Budget", ACCENT);
        addBtn.setOnAction(e -> openForm(null));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(title, spacer, addBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(header, new Separator());

        // Budget list container
        listContainer = new VBox(12);
        refreshList();
        root.getChildren().add(listContainer);

        // Back to Dashboard
        Button backBtn = btn("← Back to Dashboard", "#444466");
        backBtn.setOnAction(e -> {
            if (onGoBack != null) {
                onGoBack.run();
            }
        });
        root.getChildren().add(backBtn);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + BG + "; -fx-background:" + BG + ";");
        return scroll;
    }

    // ── Budget list ───────────────────────────────────────────────────────────
    private void refreshList() {
        listContainer.getChildren().clear();
        List<Budget> budgets = controller.getBudgetsByPeriod(period);
        if (budgets.isEmpty()) {
            listContainer.getChildren().add(
                    lbl("No budgets yet. Click '+ New Budget' to get started.",
                            13, FontWeight.NORMAL, SUB));
        } else {
            budgets.forEach(b -> listContainer.getChildren().add(budgetCard(b)));
        }
    }

    private VBox budgetCard(Budget b) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color:" + CARD + "; -fx-background-radius:12;");

        Label cat = lbl(b.getCategory(), 15, FontWeight.BOLD, WHITE);
        Label amtLbl = lbl(String.format("$%.2f spent of $%.2f", b.getSpent(), b.amount),
                13, FontWeight.NORMAL, SUB);

        ProgressBar bar = new ProgressBar(Math.min(b.getSpent().doubleValue() / b.amount, 1.0));
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-accent:" + (b.isOverLimit() ? RED : b.isNearLimit() ? ORANGE : GREEN) + ";");

        String statusTxt = b.isOverLimit() ? "Over limit!"
                : b.isNearLimit() ? "Near limit"
                : String.format("$%.2f remaining", b.getRemaining());
        String statusColor = b.isOverLimit() ? RED : b.isNearLimit() ? ORANGE : GREEN;

        Button editBtn = btn("Edit", ACCENT);
        editBtn.setOnAction(e -> openForm(b));

        card.getChildren().addAll(cat, amtLbl, bar, lbl(statusTxt, 12, FontWeight.BOLD, statusColor), editBtn);
        return card;
    }

    // ── Create / Edit form (Dialog) ───────────────────────────────────────────
    private void openForm(Budget budget) {
        boolean isEdit = budget != null;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM yyyy");

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Budget" : "Create Budget");
        dialog.setHeaderText(null);

        // Form controls
        ComboBox<BudgetCategory> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(controller.getCategories());
        categoryBox.setPromptText("Select a category");
        styleCombo(categoryBox);
        if (isEdit) {
            categoryBox.setValue(
                    controller.getCategories()
                            .stream()
                            .filter(c -> c.id == budget.categoryId)
                            .findFirst()
                            .orElse(null)
            );
        }

        TextField amountField = new TextField();
        amountField.setPromptText("e.g. 300");
        styleField(amountField);
        if (isEdit) {
            amountField.setText(String.valueOf(budget.amount));
        }

        ComboBox<String> periodBox = new ComboBox<>();
        YearMonth now = YearMonth.now();
        for (int i = -1; i <= 3; i++) {
            periodBox.getItems().add(now.plusMonths(i).format(fmt));
        }
        periodBox.setValue(isEdit ? budget.period.format(fmt) : now.format(fmt));
        styleCombo(periodBox);

        Label errorLbl = new Label("");
        errorLbl.setTextFill(Color.web(RED));
        errorLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        errorLbl.setWrapText(true);

        VBox form = new VBox(10,
                lbl("Category", 13, FontWeight.NORMAL, SUB), categoryBox,
                lbl("Amount ($)", 13, FontWeight.NORMAL, SUB), amountField,
                lbl("Period", 13, FontWeight.NORMAL, SUB), periodBox,
                errorLbl);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color:" + BG + ";");
        form.setPrefWidth(360);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setStyle("-fx-background-color:" + BG + ";");

        ButtonType saveType = new ButtonType(isEdit ? "Update" : "Create", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, cancelType);

        Button saveBtn = (Button) dialog.getDialogPane().lookupButton(saveType);
        saveBtn.setStyle("-fx-background-color:" + ACCENT + "; -fx-text-fill:white;"
                + "-fx-font-size:13px; -fx-padding:8 20 8 20; -fx-background-radius:8;");

        // Validate before allowing close — matches sequence diagram alt path
        saveBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            errorLbl.setText("");
            BudgetCategory cat = categoryBox.getValue();
            String amtTx = amountField.getText().trim();

            if (cat == null) {
                errorLbl.setText("Please select a category.");
                event.consume();
                return;
            }
            if (amtTx.isEmpty()) {
                errorLbl.setText("Please enter an amount.");
                event.consume();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amtTx);
            } catch (NumberFormatException ex) {
                errorLbl.setText("Amount must be a valid number.");
                event.consume();
                return;
            }

            YearMonth selectedPeriod = YearMonth.parse(periodBox.getValue(), fmt);
            System.out.println("Period box value:");
            System.out.println(periodBox.getValue());

            BudgetController.ControllerResult<Budget> result = isEdit
                    ? controller.updateBudget(budget.id, cat.id, amount, selectedPeriod)
                    : controller.saveBudget(cat.id, amount, selectedPeriod);

            if (result.isSuccess()) {
                refreshList();          // update list behind the dialog
            } else {
                errorLbl.setText(result.getErrorMessage());
                event.consume();        // keep dialog open on error
            }
        });

        dialog.showAndWait();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Label lbl(String text, double size, FontWeight w, String hex) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", w, size));
        l.setTextFill(Color.web(hex));
        return l;
    }

    private Button btn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:" + color + "; -fx-text-fill:white;"
                + "-fx-font-size:13px; -fx-padding:8 18 8 18;"
                + "-fx-background-radius:8; -fx-cursor:hand;");
        return b;
    }

    private void styleField(TextField f) {
        f.setStyle("-fx-background-color:" + FIELDBG + "; -fx-text-fill:" + WHITE + ";"
                + "-fx-prompt-text-fill:" + SUB + "; -fx-font-size:13px;"
                + "-fx-padding:10 12 10 12; -fx-background-radius:8;"
                + "-fx-border-color:#444466; -fx-border-radius:8; -fx-border-width:1;");
        f.setMaxWidth(Double.MAX_VALUE);
    }

    private void styleCombo(ComboBox<?> box) {
        box.setStyle("-fx-background-color:" + FIELDBG + "; -fx-text-fill:" + WHITE + ";"
                + "-fx-font-size:13px;"
                + "-fx-border-color:#444466; -fx-border-radius:8; -fx-border-width:1;");
        box.setMaxWidth(Double.MAX_VALUE);
    }
}
