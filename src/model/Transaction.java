package model;

import core.database.Model;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

/**
 * Transaction – Model class representing a single financial transaction
 * (either an income or an expense) made by a user.
 *
 * <p>Each transaction is associated with a user, an optional budget category,
 * an amount, a date, and optional notes. Transactions are used throughout
 * the application to calculate spending, balances, and budget usage.</p>
 *
 * <p>This class extends {@link Model} to leverage common database operations
 * such as insert, query, and delete.</p>
 */
public class Transaction extends Model {

    /** Unique identifier for this transaction record. */
    public int id;

    /** The monetary amount of this transaction. */
    public double amount;

    /** The ID of the user who owns this transaction. */
    public int userId;

    /** The ID of the budget category this transaction belongs to. */
    public int categoryId;

    /** Optional notes or description provided by the user for this transaction. */
    public String notes;

    /** The date and time when this transaction occurred. */
    public LocalDateTime date;

    /**
     * Indicates whether this transaction is an income ({@code true})
     * or an expense ({@code false}).
     */
    public boolean isIncome;

    /**
     * The display name of the category, lazily loaded on first access.
     * {@code null} until {@link #getCategory()} is called.
     */
    private String category = null;

    /**
     * Formatter used to parse and format date-time strings from/to the database
     * in the format {@code yyyy-MM-dd HH:mm:ss.S}.
     */
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    // ── Constructors ──────────────────────────────────────────────────────────

    /**
     * Full constructor to create a Transaction instance with all fields provided.
     * Private to enforce use of factory methods and lookup constructors.
     *
     * @param id         the unique transaction ID
     * @param amount     the transaction amount
     * @param userId     the ID of the owning user
     * @param categoryId the ID of the associated budget category
     * @param notes      optional notes about the transaction
     * @param date       the date and time of the transaction
     * @param isIncome   {@code true} if income, {@code false} if expense
     */
    private Transaction(int id, double amount, int userId, int categoryId,
                        String notes, LocalDateTime date, boolean isIncome) {
        this.id = id;
        this.amount = amount;
        this.userId = userId;
        this.categoryId = categoryId;
        this.notes = notes;
        this.date = date;
        this.isIncome = isIncome;
    }

    /**
     * Lookup constructor that fetches a transaction from the database by its ID.
     *
     * <p>Initializes all fields from the retrieved database record.</p>
     *
     * @param id the ID of the transaction to load
     * @throws RuntimeException if no transaction is found with the given ID
     */
    public Transaction(int id) {
        try {
            Transaction result = this.get(
                    new String[]{"id"},
                    new Object[]{id},
                    rs -> new Transaction(
                            rs.getInt("id"),
                            rs.getDouble("amount"),
                            rs.getInt("user_id"),
                            rs.getInt("budgeting_category_id"),
                            rs.getString("notes"),
                            LocalDateTime.parse(rs.getString("transaction_date"), formatter),
                            rs.getBoolean("is_income")
                    )
            );
            this.id = result.id;
            this.amount = result.amount;
            this.userId = result.userId;
            this.categoryId = result.categoryId;
            this.notes = result.notes;
            this.date = result.date;
            this.isIncome = result.isIncome;
        } catch (SQLException e) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
    }

    /**
     * Empty constructor required by the ORM base class for internal operations.
     */
    public Transaction() {
    }

    // ── Database Operations ───────────────────────────────────────────────────

    /**
     * Returns the name of the database table associated with this model.
     *
     * @return the string {@code "transactions"}
     */
    @Override
    protected String getTable() {
        return "transactions";
    }

    /**
     * Creates and persists a new transaction record in the database.
     *
     * @param amount     the monetary amount of the transaction
     * @param userId     the ID of the user who owns this transaction
     * @param categoryId the budget category ID (can be {@code null} for uncategorized)
     * @param date       the date and time the transaction occurred
     * @param notes      optional notes or description for the transaction
     * @param isIncome   {@code true} if this is an income, {@code false} if an expense
     * @throws RuntimeException if a database error occurs during insertion
     */
    public static void create(double amount, int userId, Integer categoryId,
                               LocalDateTime date, String notes, boolean isIncome) {
        try {
            String[] cols = {"amount", "user_id", "budgeting_category_id",
                             "transaction_date", "notes", "is_income"};
            Object[] values = {amount, userId, categoryId, date, notes, isIncome};
            new Transaction().insert(cols, values);
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred");
        }
    }

    /**
     * Retrieves all transactions for a given user from the database.
     *
     * @param userId the ID of the user whose transactions to load
     * @return a list of all {@link Transaction} objects belonging to the user
     * @throws RuntimeException if a database error occurs
     */
    public static List<Transaction> all(int userId) {
        try {
            return new Transaction().getAll(
                    new String[]{"user_id"},
                    new Object[]{userId},
                    rs -> new Transaction(
                            rs.getInt("id"),
                            rs.getDouble("amount"),
                            rs.getInt("user_id"),
                            rs.getInt("budgeting_category_id"),
                            rs.getString("notes"),
                            LocalDateTime.parse(rs.getString("transaction_date"), formatter),
                            rs.getBoolean("is_income")));
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred");
        }
    }

    /**
     * Deletes this transaction record from the database.
     *
     * <p>Uses the transaction's own ID to identify and remove the record.</p>
     *
     * @throws RuntimeException if a database error occurs during deletion
     */
    public void delete() {
        try {
            this.destroy(new String[]{"id"}, new Object[]{this.id});
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception Occurred");
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /**
     * Returns the display name of this transaction's budget category.
     *
     * <p>For expense transactions, the category name is lazily loaded from
     * the database on first call and cached for subsequent calls.</p>
     *
     * <p>For income transactions, an empty string is returned since income
     * is not associated with a spending category.</p>
     *
     * @return the category name for expenses, or an empty string for income
     */
    public String getCategory() {
        if (!isIncome || category == null) {
            // Load category name from database for expense transactions
            BudgetCategory budgetCategory = new BudgetCategory(categoryId);
            category = budgetCategory.name;
        }
        if (isIncome) {
            // Income transactions do not belong to a spending category
            category = "";
        }
        return category;
    }
}