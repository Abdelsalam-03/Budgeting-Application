package model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BudgetService – all business logic for budgets and dashboard data.
 * Singleton so the same data is shared across all views and managers.
 */
public class BudgetService {

    private final List<Budget>      budgets      = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private int nextBudgetId = 1;
    private int nextTxId     = 1;

    private static BudgetService instance;
    public static BudgetService getInstance() {
        if (instance == null) instance = new BudgetService();
        return instance;
    }
    private BudgetService() { seedDemoData(); }

    // ── Create / Edit Budget ──────────────────────────────────────────────────

    public void validateAmount(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be a positive number.");
    }

    public boolean hasConflict(String category, YearMonth period, int editId) {
        return budgets.stream().anyMatch(b ->
                b.getCategory().equalsIgnoreCase(category)
                && b.getPeriod().equals(period)
                && b.getId() != editId);
    }

    public Budget createBudget(String category, double amount, YearMonth period) {
        validateAmount(amount);
        if (hasConflict(category, period, -1))
            throw new IllegalStateException(
                "A budget for this category already exists for " + period + ".");
        Budget b = new Budget(nextBudgetId++, category, amount, period);
        budgets.add(b);
        return b;
    }

    public Budget editBudget(int id, String category, double amount, YearMonth period) {
        validateAmount(amount);
        Budget b = findBudgetById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found: " + id));
        if (hasConflict(category, period, id))
            throw new IllegalStateException(
                "A budget for this category already exists for " + period + ".");
        b.setCategory(category);
        b.setAmount(amount);
        b.setPeriod(period);
        return b;
    }

    public List<Budget>     getAllBudgets()         { return new ArrayList<>(budgets); }
    public Optional<Budget> findBudgetById(int id) { return budgets.stream().filter(b -> b.getId() == id).findFirst(); }

    public List<Budget> getBudgetsByPeriod(YearMonth p) {
        return budgets.stream().filter(b -> b.getPeriod().equals(p)).collect(Collectors.toList());
    }

    // ── Dashboard data ────────────────────────────────────────────────────────

    public double getMonthlyIncome(YearMonth p) {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME
                          && YearMonth.from(t.getDate()).equals(p))
                .mapToDouble(Transaction::getAmount).sum();
    }

    public double getMonthlyExpenses(YearMonth p) {
        return transactions.stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(p)
                          && t.getType() == Transaction.Type.EXPENSE)
                .mapToDouble(Transaction::getAmount).sum();
    }

    public double getTotalBalance(YearMonth p)  { return getMonthlyIncome(p) - getMonthlyExpenses(p); }
    public int[]  getStatusCounts()             { return new int[]{ transactions.size(), budgets.size(), 0 }; }

    public List<Transaction> getRecentTransactions(int limit) {
        return transactions.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(limit).collect(Collectors.toList());
    }

    public List<Budget> getBudgetWarnings(YearMonth p) {
        return getBudgetsByPeriod(p).stream()
                .filter(b -> b.isNearLimit() || b.isOverLimit())
                .collect(Collectors.toList());
    }

    public static List<String> getCategories() {
        return new ArrayList<>(Arrays.asList("Food & Dining", "Transportation", "Housing",
                                    "Entertainment", "Health & Fitness", "Shopping",
                                    "Education", "Utilities", "Travel", "Other"));
    }

    // ── Demo seed data ────────────────────────────────────────────────────────
    private void seedDemoData() {
        YearMonth now = YearMonth.now();
        addTx("Salary",        3500, Transaction.Type.INCOME,   "Other",            LocalDate.now().minusDays(20));
        addTx("Grocery Store",  120, Transaction.Type.EXPENSE,  "Food & Dining",    LocalDate.now().minusDays(3));
        addTx("Netflix",         15, Transaction.Type.EXPENSE,  "Entertainment",    LocalDate.now().minusDays(5));
        addTx("Bus Pass",        40, Transaction.Type.EXPENSE,  "Transportation",   LocalDate.now().minusDays(7));
        addTx("Gym",             30, Transaction.Type.EXPENSE,  "Health & Fitness", LocalDate.now().minusDays(10));
        addTx("Restaurant",      65, Transaction.Type.EXPENSE,  "Food & Dining",    LocalDate.now().minusDays(1));
        addTx("Freelance",      400, Transaction.Type.INCOME,   "Other",            LocalDate.now().minusDays(14));

        Budget food  = makeRaw("Food & Dining",    200, now); food.setSpent(185);  // near limit
        Budget trans = makeRaw("Transportation",    80, now); trans.setSpent(40);
        Budget ent   = makeRaw("Entertainment",     50, now); ent.setSpent(55);    // over limit
        Budget hlth  = makeRaw("Health & Fitness",  60, now); hlth.setSpent(30);
    }

    private void addTx(String desc, double amt, Transaction.Type type, String cat, LocalDate date) {
        transactions.add(new Transaction(nextTxId++, desc, amt, type, cat, date));
    }
    private Budget makeRaw(String cat, double amt, YearMonth p) {
        Budget b = new Budget(nextBudgetId++, cat, amt, p);
        budgets.add(b);
        return b;
    }
}