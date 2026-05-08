package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class BudgetGoal {

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
        Date soon  = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        Date later = new Date(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000);

        goals.add(new BudgetGoal(1, 1, "Save for Laptop",     5000.0, 1200.0, 0, later, false));
        goals.add(new BudgetGoal(2, 1, "Monthly Food Budget", 800.0,  300.0,  1, soon,  false));
        goals.add(new BudgetGoal(3, 2, "Transport Budget",    400.0,  400.0,  2, soon,  true));
        goals.add(new BudgetGoal(4, 2, "Shopping Limit",      1000.0, 750.0,  5, later, false));
        goals.add(new BudgetGoal(5, 1, "Emergency Fund",      3000.0, 3000.0, 0, later, true));
    }

    // Full constructor
    private BudgetGoal(int id, int userId, String name,
                       double targetAmount, double currentAmount,
                       int categoryId, Date deadline, boolean isCompleted) {
        this.id            = id;
        this.userId        = userId;
        this.name          = name;
        this.targetAmount  = targetAmount;
        this.currentAmount = currentAmount;
        this.categoryId    = categoryId;
        this.deadline      = deadline;
        this.isCompleted   = isCompleted;
    }

    // Search by ID
    public BudgetGoal(int id) {
        for (BudgetGoal goal : goals) {
            if (goal.id == id) {
                this.id            = goal.id;
                this.userId        = goal.userId;
                this.name          = goal.name;
                this.targetAmount  = goal.targetAmount;
                this.currentAmount = goal.currentAmount;
                this.categoryId    = goal.categoryId;
                this.deadline      = goal.deadline;
                this.isCompleted   = goal.isCompleted;
                return;
            }
        }
        throw new RuntimeException("BudgetGoal not found with id: " + id);
    }

    // Empty constructor
    public BudgetGoal() {}

    // Creation method
    public static BudgetGoal create(int userId, String name,
                                    double targetAmount, int categoryId, Date deadline) {
        int newId = goals.size() + 1;

        if (name == null || name.isEmpty())
            throw new RuntimeException("Goal name must not be empty");
        if (targetAmount <= 0)
            throw new RuntimeException("Target amount must be positive");

        BudgetGoal newGoal = new BudgetGoal(newId, userId, name, targetAmount, 0.0, categoryId, deadline, false);
        goals.add(newGoal);
        return newGoal;
    }

    // Optional: get all user goals
    public static List<BudgetGoal> all(int userId) {
        List<BudgetGoal> newList = new ArrayList<>();
        goals.forEach(g -> {
            if (g.userId == userId) newList.add(g);
        });
        return newList;
    }

    // Edit goal fields
    public void edit(String newName, double newTargetAmount, int newCategoryId, Date newDeadline) {
        if (newName == null || newName.isEmpty())
            throw new RuntimeException("Goal name must not be empty");
        if (newTargetAmount <= 0)
            throw new RuntimeException("Target amount must be positive");

        for (BudgetGoal g : goals) {
            if (g.id == this.id) {
                g.name          = newName;
                g.targetAmount  = newTargetAmount;
                g.categoryId    = newCategoryId;
                g.deadline      = newDeadline;
                g.isCompleted   = g.currentAmount >= g.targetAmount;
                this.name          = newName;
                this.targetAmount  = newTargetAmount;
                this.categoryId    = newCategoryId;
                this.deadline      = newDeadline;
                this.isCompleted   = g.isCompleted;
                return;
            }
        }
    }

    // Add progress toward the goal
    public void addProgress(double amount) {
        if (amount <= 0) throw new RuntimeException("Progress amount must be positive");
        for (BudgetGoal g : goals) {
            if (g.id == this.id) {
                g.currentAmount    = Math.min(g.currentAmount + amount, g.targetAmount);
                g.isCompleted      = g.currentAmount >= g.targetAmount;
                this.currentAmount = g.currentAmount;
                this.isCompleted   = g.isCompleted;
                return;
            }
        }
    }

    public void delete() {
        goals.removeIf(g -> g.id == this.id);
    }

    // Returns progress as a value between 0.0 and 1.0
    public double progressRatio() {
        if (targetAmount == 0) return 0;
        return Math.min(currentAmount / targetAmount, 1.0);
    }
}