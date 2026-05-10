package model;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import manager.AuthenticationManager;

/**
 * BudgetService – Central service class that handles all business logic
 * related to budgets, transactions, and dashboard data.
 *
 * <p>This class follows the <b>Singleton</b> design pattern to ensure that
 * only one instance exists throughout the application, allowing all views
 * and managers to share the same data.</p>
 *
 * <p>Responsibilities include:
 * </p>
 * <ul>
 *   <li>Creating and editing budgets</li>
 *   <li>Retrieving budget and transaction data</li>
 *   <li>Providing dashboard summary statistics</li>
 *   <li>Detecting budget warnings (near or over limit)</li>
 * </ul>
 */
public class BudgetService {

    /** List of all budgets loaded for the current user. */
    private List<Budget> budgets = new ArrayList<>();

    /** List of all transactions loaded for the current user. */
    private List<Transaction> transactions = new ArrayList<>();
    
    /** List of all transactions loaded for the current user. */
    private List<BudgetGoal> goals = new ArrayList<>();

    /** The ID of the currently authenticated user. */
    private int userId;

    /** The single shared instance of this service (Singleton pattern). */
    private static BudgetService instance;

    // ── Singleton Access ──────────────────────────────────────────────────────

    /**
     * Returns the single instance of BudgetService.
     *
     * <p>On each call, it refreshes the current user ID, transactions,
     * and budgets from the data layer to ensure up-to-date data.</p>
     *
     * @return the shared {@code BudgetService} instance
     */
    public static BudgetService getInstance() {
        if (instance == null) {
            instance = new BudgetService();
        }
        // Refresh user context and reload data on every access
        instance.userId = AuthenticationManager.getAuthenticationManager().getUser().getID();
        instance.transactions = Transaction.all(instance.userId);
        instance.budgets = Budget.all(instance.userId);
        instance.goals = BudgetGoal.all(instance.userId);
        return instance;
    }

    /**
     * Private constructor to prevent direct instantiation.
     * Use {@link #getInstance()} instead.
     */
    private BudgetService() {
    }

    // ── Create / Edit Budget ──────────────────────────────────────────────────

    /**
     * Validates that a given amount is a positive number.
     *
     * @param amount the amount to validate
     * @throws IllegalArgumentException if the amount is zero or negative
     */
    public void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be a positive number.");
        }
    }

    /**
     * Checks whether a budget already exists for the given category and period,
     * excluding a specific budget ID (used when editing an existing budget).
     *
     * @param category the category ID to check
     * @param period   the year-month period to check
     * @param editId   the ID of the budget being edited (-1 if creating new)
     * @return {@code true} if a conflicting budget exists, {@code false} otherwise
     */
    public boolean hasConflict(int category, YearMonth period, int editId) {
        return budgets.stream().anyMatch(b
                -> b.categoryId == category
                && b.period.equals(period)
                && b.id != editId);
    }

    /**
     * Creates a new budget for the current user.
     *
     * <p>Validates the amount and checks for duplicate budgets in the same
     * category and period before saving.</p>
     *
     * @param category the category ID for this budget
     * @param amount   the budget limit amount (must be positive)
     * @param period   the year-month this budget applies to
     * @return the newly created {@link Budget} object
     * @throws IllegalArgumentException if the amount is not positive
     * @throws IllegalStateException    if a budget for this category/period already exists
     */
    public Budget createBudget(int category, double amount, YearMonth period) {
        validateAmount(amount);
        if (hasConflict(category, period, -1)) {
            throw new IllegalStateException(
                    "A budget for this category already exists for " + period + ".");
        }
        Budget b = Budget.create(category, userId, amount, period);
        budgets.add(b); // Add to local cache
        return b;
    }

    /**
     * Edits an existing budget by its ID.
     *
     * <p>Updates the category, amount, and period, then persists the changes.
     * Validates the amount and checks for conflicts with other budgets.</p>
     *
     * @param id       the ID of the budget to edit
     * @param category the new category ID
     * @param amount   the new budget limit amount (must be positive)
     * @param period   the new year-month period
     * @return the updated {@link Budget} object
     * @throws IllegalArgumentException if the amount is not positive
     * @throws IllegalStateException    if a conflicting budget exists
     */
    public Budget editBudget(int id, int category, double amount, YearMonth period) {
        validateAmount(amount);
        Budget b = new Budget(id);
        if (hasConflict(category, period, id)) {
            throw new IllegalStateException(
                    "A budget for this category already exists for " + period + ".");
        }
        b.categoryId = category;
        b.amount = amount;
        b.period = period;
        b.save(); // Persist changes to the data layer
        return b;
    }

    /**
     * Returns a copy of all budgets for the current user.
     *
     * @return a new {@link List} containing all {@link Budget} objects
     */
    public List<Budget> getAllBudgets() {
        return new ArrayList<>(budgets);
    }

    /**
     * Finds a budget by its unique ID.
     *
     * @param id the budget ID to search for
     * @return an {@link Optional} containing the budget if found, or empty if not
     */
    public Optional<Budget> findBudgetById(int id) {
        return budgets.stream().filter(b -> b.id == id).findFirst();
    }

    /**
     * Returns all budgets that belong to a specific year-month period.
     *
     * @param p the {@link YearMonth} period to filter by
     * @return a list of budgets for the given period
     */
    public List<Budget> getBudgetsByPeriod(YearMonth p) {
        return budgets.stream().filter(b -> b.period.equals(p)).collect(Collectors.toList());
    }

    // ── Dashboard Data ────────────────────────────────────────────────────────

    /**
     * Calculates the total income for a given month.
     *
     * @param p the {@link YearMonth} to calculate income for
     * @return the sum of all income transactions in that period
     */
    public double getMonthlyIncome(YearMonth p) {
        return transactions.stream()
                .filter(t -> t.isIncome
                && YearMonth.from(t.date).equals(p))
                .mapToDouble(t -> t.amount).sum();
    }

    /**
     * Calculates the total expenses for a given month.
     *
     * @param p the {@link YearMonth} to calculate expenses for
     * @return the sum of all expense transactions in that period
     */
    public double getMonthlyExpenses(YearMonth p) {
        return transactions.stream()
                .filter(t -> YearMonth.from(t.date).equals(p)
                && !t.isIncome)
                .mapToDouble(t -> t.amount).sum();
    }

    /**
     * Calculates the net balance for a given month (income minus expenses).
     *
     * @param p the {@link YearMonth} to calculate the balance for
     * @return the net balance (can be negative if expenses exceed income)
     */
    public double getTotalBalance(YearMonth p) {
        return getMonthlyIncome(p) - getMonthlyExpenses(p);
    }

    /**
     * Returns a summary count array for dashboard status display.
     *
     * <p>The returned array contains:
     * </p>
     * <ul>
     *   <li>Index 0: total number of transactions</li>
     *   <li>Index 1: total number of budgets</li>
     *   <li>Index 2: reserved for future use (currently 0)</li>
     * </ul>
     *
     * @return an int array of size 3 with status counts
     */
    public int[] getStatusCounts() {
        return new int[]{transactions.size(), budgets.size(), goals.size()};
    }

    /**
     * Returns the most recent transactions up to a specified limit,
     * sorted by date in descending order (newest first).
     *
     * @param limit the maximum number of transactions to return
     * @return a list of the most recent {@link Transaction} objects
     */
    public List<Transaction> getRecentTransactions(int limit) {
        return transactions.stream()
                .sorted((a, b) -> b.date.compareTo(a.date))
                .limit(limit).collect(Collectors.toList());
    }

    /**
     * Returns all budgets for a given period that are near or over their limit.
     *
     * <p>Used to display budget warnings on the dashboard.</p>
     *
     * @param p the {@link YearMonth} period to check
     * @return a list of {@link Budget} objects that need attention
     */
    public List<Budget> getBudgetWarnings(YearMonth p) {
        return getBudgetsByPeriod(p).stream()
                .filter(b -> b.isNearLimit() || b.isOverLimit())
                .collect(Collectors.toList());
    }

    /**
     * Returns all available budget categories for the current user.
     *
     * @return a list of {@link BudgetCategory} objects
     */
    public List<BudgetCategory> getCategories() {
        return BudgetCategory.all(userId);
    }

    // ── Internal Helpers ──────────────────────────────────────────────────────

    /**
     * Internal helper that creates a raw budget and adds it to the local cache.
     * Used for seeding demo/test data.
     *
     * @param cat the category ID
     * @param amt the budget amount
     * @param p   the year-month period
     * @return the created {@link Budget} object
     */
    private Budget makeRaw(int cat, double amt, YearMonth p) {
        Budget b = Budget.create(cat, userId, amt, p);
        budgets.add(b);
        return b;
    }
}