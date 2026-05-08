package model;

import core.database.Model;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class BudgetGoal extends Model {

    public int id;
    public int userId;
    public String name;
    public double targetAmount;
    public double currentAmount;
    public int categoryId;
    public Date deadline;
    public boolean isCompleted;

    // Fake database
    private static List<BudgetGoal> goals = new ArrayList<>();

    // Static block to add some dummy data
    static {
        Date soon = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        Date later = new Date(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000);

        goals.add(new BudgetGoal(1, 1, "Save for Laptop", 5000.0, 1200.0, 0, later, false));
        goals.add(new BudgetGoal(2, 1, "Monthly Food Budget", 800.0, 300.0, 1, soon, false));
        goals.add(new BudgetGoal(3, 2, "Transport Budget", 400.0, 400.0, 2, soon, true));
        goals.add(new BudgetGoal(4, 2, "Shopping Limit", 1000.0, 750.0, 5, later, false));
        goals.add(new BudgetGoal(5, 1, "Emergency Fund", 3000.0, 3000.0, 0, later, true));
    }

    // Full constructor
    private BudgetGoal(int id, int userId, String name,
            double targetAmount, double currentAmount,
            int categoryId, Date deadline, boolean isCompleted) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.categoryId = categoryId;
        this.deadline = deadline;
        this.isCompleted = isCompleted;
    }

    @Override
    protected String getTable() {
        return "saving_goals";
    }

    // Search by ID
    public BudgetGoal(int id) {
        try {
            BudgetGoal result = new BudgetGoal().get(
                    new String[]{"user_id"},
                    new Object[]{userId},
                    rs -> new BudgetGoal(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getDouble("target"),
                            rs.getDouble("saved_amount"),
                            1,
                            Date.from(LocalDate.parse(rs.getString("expected_date")).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                            rs.getBoolean("is_completed")));
            this.id = result.id;
            this.userId = result.userId;
            this.name = result.name;
            this.targetAmount = result.targetAmount;
            this.currentAmount = result.currentAmount;
            this.categoryId = result.categoryId;
            this.deadline = result.deadline;
            this.isCompleted = result.isCompleted;
        } catch (SQLException e) {
            // Handle the exception
            throw new RuntimeException("Category not found with id: " + id);
        }
    }

    // Empty constructor
    public BudgetGoal() {
    }

    // Creation method
    public static BudgetGoal create(int userId, String name,
            double targetAmount, int categoryId, Date deadline) {
        try {
            String[] cols = {"name", "user_id", "target", "expected_date", "saved_amount", "is_completed"};
            Object[] values = {name, userId, targetAmount, deadline, 0, false};
            int id = new BudgetGoal().insertAndReturnId(cols, values);
            return new BudgetGoal(id, userId, name, targetAmount, 0.0, categoryId, deadline, false);
        } catch (SQLException e) {
            // Handle different exceptions
            e.printStackTrace();
            throw new RuntimeException("An error occurred");
        }
    }

    public static List<BudgetGoal> all(int userId) {
        try {
            return new BudgetGoal().getAll(
                    new String[]{"user_id"},
                    new Object[]{userId},
                    rs -> new BudgetGoal(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getDouble("target"),
                            rs.getDouble("saved_amount"),
                            1,
                            Date.from(LocalDate.parse(rs.getString("expected_date")).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                            rs.getBoolean("is_completed")));
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred");
        }
    }

    // Edit goal fields
    public void edit(String newName, double newTargetAmount, int newCategoryId, Date newDeadline) {
//        if (newName == null || newName.isEmpty()) {
//            throw new RuntimeException("Goal name must not be empty");
//        }
//        if (newTargetAmount <= 0) {
//            throw new RuntimeException("Target amount must be positive");
//        }
//
//        for (BudgetGoal g : goals) {
//            if (g.id == this.id) {
//                g.name = newName;
//                g.targetAmount = newTargetAmount;
//                g.categoryId = newCategoryId;
//                g.deadline = newDeadline;
//                g.isCompleted = g.currentAmount >= g.targetAmount;
//                this.name = newName;
//                this.targetAmount = newTargetAmount;
//                this.categoryId = newCategoryId;
//                this.deadline = newDeadline;
//                this.isCompleted = g.isCompleted;
//                return;
//            }
//        }
    }

    // Add progress toward the goal
    public void addProgress(double amount) {
        try {
            this.currentAmount += amount;
            if (this.currentAmount >= this.targetAmount) {
                this.isCompleted = true;
            }
            String[] cols = {"saved_amount", "is_completed"};
            Object[] values = {this.currentAmount, this.isCompleted};

            String[] whereCols = {"id"};
            Object[] whereVals = {id};
            this.update(cols, values, whereCols, whereVals);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception Occurred");
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

    // Returns progress as a value between 0.0 and 1.0
    public double progressRatio() {
        if (targetAmount == 0) {
            return 0;
        }
        return Math.min(currentAmount / targetAmount, 1.0);
    }
}
