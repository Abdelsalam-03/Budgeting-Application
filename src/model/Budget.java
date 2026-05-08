package model;

import core.database.Model;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Budget – Model class representing a monthly spending budget for a specific category.
 *
 * <p>Each budget is tied to a user and a category, and defines a spending limit
 * for a given month (period). It tracks how much has been spent and provides
 * status checks such as whether the budget is near or over its limit.</p>
 *
 * <p>This class extends {@link Model} to leverage common database operations
 * such as insert, update, and query.</p>
 */
public class Budget extends Model {

    /** Unique identifier for this budget record. */
    public int id;

    /** The ID of the spending category this budget belongs to. */
    public int categoryId;

    /** The ID of the user who owns this budget. */
    public int userId;

    /** The maximum spending limit for this budget. */
    public double amount;

    /**
     * The total amount spent so far in this budget's category and period.
     * Lazily loaded on first access — {@code null} means not yet calculated.
     */
    public Double spent = null;

    /** The year and month this budget applies to. */
    public YearMonth period;

    /**
     * The display name of the category, lazily loaded on first access.
     * {@code null} until {@link #getCategory()} is called.
     */
    private String categoryName = null;

    /**
     * Formatter used to parse and format date strings from/to the database
     * in the format {@code yyyy-MM-dd}.
     */
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ── Constructors ──────────────────────────────────────────────────────────

    /**
     * Full constructor to create a Budget instance with all fields provided.
     *
     * @param id         the unique budget ID
     * @param categoryId the category this budget belongs to
     * @param userId     the owner user's ID
     * @param amount     the spending limit
     * @param period     the year-month this budget covers
     */
    public Budget(int id, int categoryId, int userId, double amount, YearMonth period) {
        this.id = id;
        this.categoryId = categoryId;
        this.userId = userId;
        this.amount = amount;
        this.period = period;
    }

    /**
     * Lookup constructor that fetches a budget from the database by its ID.
     *
     * <p>Initializes all fields from the retrieved record and sets
     * {@code spent} to 0.0 as a default starting value.</p>
     *
     * @param id the ID of the budget to load
     * @throws RuntimeException if no budget is found with the given ID
     */
    public Budget(int id) {
        try {
            Budget result = this.get(
                    new String[]{"id"},
                    new Object[]{id},
                    rs -> new Budget(
                            rs.getInt("id"),
                            rs.getInt("category_id"),
                            rs.getInt("user_id"),
                            rs.getDouble("limit_amount"),
                            YearMonth.parse(rs.getString("period"), formatter))
            );
            this.id = result.id;
            this.categoryId = result.categoryId;
            this.userId = result.userId;
            this.amount = result.amount;
            this.period = result.period;
            this.spent = 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Category not found with id: " + id);
        }
    }

    /**
     * Empty constructor required by the ORM base class for internal operations.
     */
    public Budget() {
    }

    // ── Database Operations ───────────────────────────────────────────────────

    /**
     * Creates and persists a new budget record in the database.
     *
     * <p>Uses the first day of the given period as the stored date value.</p>
     *
     * @param categoryId the category ID for the budget
     * @param userId     the user who owns the budget
     * @param limit      the spending limit amount
     * @param date       the year-month period for the budget
     * @return the newly created {@link Budget} with its generated ID
     * @throws RuntimeException if a database error occurs during insertion
     */
    public static Budget create(int categoryId, int userId, double limit, YearMonth date) {
        try {
            String[] cols = {"category_id", "user_id", "limit_amount", "period"};
            Object[] values = {categoryId, userId, limit, date.atDay(1)};
            int id = new Budget().insertAndReturnId(cols, values);
            return new Budget(id, categoryId, userId, limit, date);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred");
        }
    }

    /**
     * Saves (updates) the current budget's fields to the database.
     *
     * <p>Updates the {@code limit_amount}, {@code period}, and {@code category_id}
     * fields for the record matching this budget's ID.</p>
     *
     * @throws RuntimeException if a database error occurs during the update
     */
    public void save() {
        try {
            String[] cols = {"limit_amount", "period", "category_id"};
            Object[] values = {amount, period.atDay(1), categoryId};

            String[] whereCols = {"id"};
            Object[] whereVals = {id};

            new Budget().update(cols, values, whereCols, whereVals);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred");
        }
    }

    /**
     * Retrieves all budgets for a given user from the database.
     *
     * @param userId the ID of the user whose budgets to load
     * @return a list of all {@link Budget} objects belonging to the user
     * @throws RuntimeException if a database error occurs
     */
    public static List<Budget> all(int userId) {
        try {
            return new Budget().getAll(
                    new String[]{"user_id"},
                    new Object[]{userId},
                    rs -> new Budget(
                            rs.getInt("id"),
                            rs.getInt("category_id"),
                            rs.getInt("user_id"),
                            rs.getDouble("limit_amount"),
                            YearMonth.parse(rs.getString("period"), formatter)));
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred");
        }
    }

    /**
     * Returns the name of the database table associated with this model.
     *
     * @return the string {@code "budgets"}
     */
    @Override
    protected String getTable() {
        return "budgets";
    }

    // ── Status Checks ─────────────────────────────────────────────────────────

    /**
     * Checks whether spending has reached or exceeded the budget limit.
     *
     * @return {@code true} if spent amount is greater than or equal to the limit
     */
    public boolean isOverLimit() {
        return getSpent() >= amount;
    }

    /**
     * Checks whether spending is approaching the budget limit (80% or more used),
     * but has not yet exceeded it.
     *
     * @return {@code true} if spending is between 80% and 100% of the limit
     */
    public boolean isNearLimit() {
        return !isOverLimit() && (getSpent() / amount) >= 0.80;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /**
     * Returns the display name of this budget's category.
     *
     * <p>The category name is lazily loaded from the database on first call
     * and cached for subsequent calls.</p>
     *
     * @return the category name as a string
     */
    public String getCategory() {
        if (categoryName == null) {
            BudgetCategory category = new BudgetCategory(categoryId);
            categoryName = category.name;
        }
        return categoryName;
    }

    /**
     * Returns the total amount spent in this budget's category and period.
     *
     * <p>The value is lazily calculated on first call by summing all matching
     * expense transactions for the current user, category, and period.
     * The result is cached in {@code spent} for subsequent calls.</p>
     *
     * @return the total amount spent as a {@link Double}
     */
    public Double getSpent() {
        if (spent == null) {
            List<Transaction> transactions = Transaction.all(userId);
            spent = 0.0;
            // Sum all expenses matching this budget's category and period
            spent = transactions.stream()
                .filter(t -> YearMonth.from(t.date).equals(period)
                && !t.isIncome
                && t.categoryId == categoryId)
                .mapToDouble(t -> t.amount).sum();
        }
        return spent;
    }

    /**
     * Returns the remaining budget amount (limit minus amount spent).
     *
     * <p>A negative value indicates the budget has been exceeded.</p>
     *
     * @return the remaining budget as a {@link Double}
     */
    public Double getRemaining() {
        return amount - getSpent();
    }
}