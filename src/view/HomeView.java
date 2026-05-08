package view;

import manager.BudgetController;
import model.Budget;
import model.Transaction;

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

/**
 * DashboardView – home screen showing balance, stats, recent transactions,
 * and budget warnings.
 *
 * Follows the same getView() pattern as the rest of the team's views.
 *
 * Usage in MainView.java:
 *   DashboardView dash = new DashboardView(() -> showBudgetingView());
 *   root.setCenter(dash.getView());
 */
public class HomeView {

    private final BudgetController controller = BudgetController.getInstance();
    private final YearMonth        period     = YearMonth.now();
    private final Runnable         onGoToBudgets;

    private static final String BG     = "#1A1D2E";
    private static final String CARD   = "#252840";
    private static final String ACCENT = "#6C63FF";
    private static final String GREEN  = "#4CAF50";
    private static final String RED    = "#F44336";
    private static final String ORANGE = "#FF9800";
    private static final String WHITE  = "#FFFFFF";
    private static final String SUB    = "#A0A3B1";

    /**
     * @param onGoToBudgets lambda called when user clicks "Manage Budgets",
     *                      so MainView can swap the centre pane.
     */
    public HomeView(Runnable onGoToBudgets) {
        this.onGoToBudgets = onGoToBudgets;
    }

    /** Returns the fully-built dashboard pane. */
    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color:" + BG + ";");

        // Header
        root.getChildren().addAll(
                lbl("Dashboard", 22, FontWeight.BOLD, WHITE),
                lbl(period.format(DateTimeFormatter.ofPattern("MMMM yyyy")), 14, FontWeight.NORMAL, SUB),
                new Separator());

        // 1. Total Balance card
        double income   = controller.getMonthlyIncome(period);
        double expenses = controller.getMonthlyExpenses(period);
        double balance  = controller.getTotalBalance(period);
        root.getChildren().add(balanceCard(balance, income, expenses));

        // 2. Stat mini-cards
        int[] counts = controller.getStatusCounts();
        HBox stats = new HBox(12,
                statCard("Transactions", counts[0], ACCENT),
                statCard("Budgets",      counts[1], GREEN),
                statCard("Goals",        counts[2], ORANGE));
        for (Node n : stats.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);
        root.getChildren().add(stats);

        // 3. Recent transactions
        root.getChildren().add(lbl("Recent Transactions", 16, FontWeight.BOLD, WHITE));
        List<Transaction> recent = controller.getRecentTransactions(5);
        if (recent.isEmpty()) {
            root.getChildren().add(lbl("No transactions yet.", 13, FontWeight.NORMAL, SUB));
        } else {
            VBox list = new VBox(8);
            recent.forEach(t -> list.getChildren().add(txRow(t)));
            root.getChildren().add(list);
        }

        // 4. Budget warnings
        List<Budget> warnings = controller.getBudgetWarnings(period);
        if (!warnings.isEmpty()) {
            root.getChildren().add(lbl("⚠ Budget Warnings", 16, FontWeight.BOLD, ORANGE));
            VBox wList = new VBox(8);
            warnings.forEach(b -> wList.getChildren().add(warningRow(b)));
            root.getChildren().add(wList);
        }

        // Navigate to Budgets
        Button budgetsBtn = btn("Manage Budgets →", ACCENT);
        budgetsBtn.setOnAction(e -> { if (onGoToBudgets != null) onGoToBudgets.run(); });
        root.getChildren().add(budgetsBtn);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + BG + "; -fx-background:" + BG + ";");
        return scroll;
    }

    // ── Card builders ─────────────────────────────────────────────────────────

    private VBox balanceCard(double balance, double income, double expenses) {
        VBox card = card();
        card.getChildren().addAll(
                lbl("Total Balance", 13, FontWeight.NORMAL, SUB),
                lbl(String.format("$%.2f", balance), 28, FontWeight.BOLD, balance >= 0 ? GREEN : RED),
                new HBox(32, infoCol("Income", income, GREEN), infoCol("Expenses", expenses, RED)));
        return card;
    }

    private VBox infoCol(String title, double amount, String color) {
        VBox col = new VBox(2);
        col.getChildren().addAll(
                lbl(title, 12, FontWeight.NORMAL, SUB),
                lbl(String.format("$%.2f", amount), 15, FontWeight.BOLD, color));
        return col;
    }

    private VBox statCard(String title, int count, String accent) {
        VBox card = card();
        card.setAlignment(Pos.CENTER);
        card.setBorder(new Border(new BorderStroke(Color.web(accent),
                BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
        card.getChildren().addAll(
                lbl(String.valueOf(count), 26, FontWeight.BOLD, accent),
                lbl(title, 11, FontWeight.NORMAL, SUB));
        return card;
    }

    private HBox txRow(Transaction t) {
        HBox row = new HBox(12);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color:" + CARD + "; -fx-background-radius:8;");

        boolean isIncome = t.isIncome;
        String color = isIncome ? GREEN : RED;

        String infoLabel = t.date.toString();
        if (! isIncome) {
            infoLabel+= "  •  " + t.getCategory();
        }
        
        VBox info = new VBox(2,
                lbl(t.notes, 13, FontWeight.BOLD, WHITE),
                lbl(infoLabel, 11, FontWeight.NORMAL, SUB)); 
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        row.getChildren().addAll(
                lbl(isIncome ? "↑" : "↓", 16, FontWeight.BOLD, color),
                info, sp,
                lbl((isIncome ? "+" : "-") + "$" + String.format("%.2f", t.amount),
                    14, FontWeight.BOLD, color));
        return row;
    }

    private HBox warningRow(Budget b) {
        HBox row = new HBox(12);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color:" + (b.isOverLimit() ? "#3B1F1F" : "#3B2F1A")
                   + "; -fx-background-radius:8;");
        String color = b.isOverLimit() ? RED : ORANGE;

        VBox info = new VBox(2,
                lbl(b.getCategory(), 13, FontWeight.BOLD, WHITE),
                lbl(String.format("Spent $%.2f of $%.2f", b.getSpent(), b.amount),
                    11, FontWeight.NORMAL, SUB));
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        row.getChildren().addAll(
                lbl(b.isOverLimit() ? "🔴" : "🟡", 16, FontWeight.NORMAL, color),
                info, sp,
                lbl(b.isOverLimit() ? "OVER LIMIT" : "NEAR LIMIT", 11, FontWeight.BOLD, color));
        return row;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private VBox card() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(16));
        v.setStyle("-fx-background-color:" + CARD + "; -fx-background-radius:12;");
        return v;
    }

    private Label lbl(String text, double size, FontWeight w, String hex) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", w, size));
        l.setTextFill(Color.web(hex));
        return l;
    }

    private Button btn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:" + color + "; -fx-text-fill:white;"
                 + "-fx-font-size:14px; -fx-padding:10 24 10 24;"
                 + "-fx-background-radius:8; -fx-cursor:hand;");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }
}