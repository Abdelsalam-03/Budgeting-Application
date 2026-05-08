package model;

import core.database.Model;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class Transaction extends Model {

    public int id;
    public double amount;
    public int userId;
    public int categoryId;
    public String notes;
    public LocalDateTime date;
    public boolean isIncome;
    private String category = null;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    // Full constructor
    private Transaction(int id, double amount, int userId, int categoryId, String notes, LocalDateTime date, boolean isIncome) {
        this.id = id;
        this.amount = amount;
        this.userId = userId;
        this.categoryId = categoryId;
        this.notes = notes;
        this.date = date;
        this.isIncome = isIncome;
    }

    // Search by ID
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
            // Handle the exception
            throw new RuntimeException("Transaction not found with id: " + id);
        }
    }

    // Empty constructor
    public Transaction() {
    }

    @Override
    protected String getTable() {
        return "transactions";
    }

    // Creation method
    public static void create(double amount, int userId, Integer categoryId, LocalDateTime date, String notes, boolean isIncome) {
        try {
            String[] cols = {"amount", "user_id", "budgeting_category_id", "transaction_date", "notes", "is_income"};
            Object[] values = {amount, userId, categoryId, date, notes, isIncome};
            new Transaction().insert(cols, values);
        } catch (SQLException e) {
            // Handle different exceptions
            throw new RuntimeException("An error occurred");
        }
    }

    // Optional: get all user transactions
    public static List<Transaction> all(int userId) {
        try {
            return new Transaction().getAll(
                    new String[]{"user_id"},
                    new Object[]{userId},
                    rs
                    -> new Transaction(
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

    public void delete() {
        try {
            this.destroy(new String[]{"id"}, new Object[]{this.id});
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception Occurred");
        }
    }

    public String getCategory(){
        if (!isIncome || category == null) {
            BudgetCategory budgetCategory = new BudgetCategory(categoryId);
            category = budgetCategory.name;
        }
        if (isIncome) {
            category = "";
        }
        return category;
    }
}
