package model;

import core.database.Model;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Model class representing a monthly budget for a spending category.
 */
public class Budget extends Model {

    /*public int id;
    public int userId;
    public int categoryId;
    public double amount;
     */
    public int id;
    public int categoryId;
    public int userId;
    public double amount;
    public Double spent = null;
    public YearMonth period;
    private String categoryName = null;
    
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Budget(int id, int categoryId, int userId, double amount, YearMonth period) {
        this.id = id;
        this.categoryId = categoryId;
        this.userId = userId;
        this.amount = amount;
        this.period = period;
    }

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
            // Handle the exception
            throw new RuntimeException("Category not found with id: " + id);
        }
    }

    // Empty constructor
    public Budget() {
    }

// Creation method
    public static Budget create(int categoryId, int userId, double limit, YearMonth date) {
        try {
            String[] cols = {"category_id", "user_id", "limit_amount", "period"};
            Object[] values = {categoryId, userId, limit, date.atDay(1)};
            int id = new Budget().insertAndReturnId(cols, values);
            return new Budget(id, categoryId, userId, limit, date);
        } catch (SQLException e) {
            // Handle different exceptions
            e.printStackTrace();
            throw new RuntimeException("An error occurred");
        }
    }

    // Update method
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

    @Override
    protected String getTable() {
        return "budgets";
    }

    public boolean isOverLimit() {
        return getSpent() >= amount;
    }

    public boolean isNearLimit() {
        return !isOverLimit() && (getSpent() / amount) >= 0.80;
    }

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

    public String getCategory() {
        if (categoryName == null) {
            BudgetCategory category = new BudgetCategory(categoryId);
            categoryName = category.name;
        }
        return categoryName;
    }
    
    public Double getSpent() {
        if (spent == null) {
            List<Transaction> transactions = Transaction.all(userId);
            spent = 0.0;
            spent = transactions.stream()
                .filter(t -> YearMonth.from(t.date).equals(period)
                && !t.isIncome
                && t.categoryId == categoryId)
                .mapToDouble(t -> t.amount).sum();
        }
        return spent;
    }
    
    public Double getRemaining() {
        return amount - getSpent();
    }
}
