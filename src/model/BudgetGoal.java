package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * BudgetGoal model — mirrors Transaction/User pattern.
 *
 * SRP  : Only responsible for data definition and fake-DB CRUD.
 * OCP  : New query methods can be added without touching existing ones.
 */
public class BudgetGoal {

    public int    id;
    public int    userId;
    public String name;          // e.g. "Save for laptop"
    public double targetAmount;  // the goal ceiling / savings target
    public double currentAmount; // amount already spent / saved toward goal
    public int    categoryId;    // which spending category this goal tracks (0 = savings)
    public Date   deadline;      // target completion date
    public boolean isCompleted;

    // ─── Fake database ────────────────────────────────────────────────────────

    private static final List<BudgetGoal> goals = new ArrayList<>();

    static {
        Date soon = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // +30 days
        Date later = new Date(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000); // +90 days

        goals.add(new BudgetGoal(1, 1, "Save for Laptop",      5000.0, 1200.0, 0, later, false));
        goals.add(new BudgetGoal(2, 1, "Monthly Food Budget",  800.0,  300.0,  1, soon,  false));
        goals.add(new BudgetGoal(3, 2, "Transport Budget",     400.0,  400.0,  2, soon,  true));
        goals.add(new BudgetGoal(4, 2, "Shopping Limit",       1000.0, 750.0,  5, later, false));
        goals.add(new BudgetGoal(5, 1, "Emergency Fund",       3000.0, 3000.0, 0, later, true));
    }

    // ─── Constructors ─────────────────────────────────────────────────────────

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

    /** Search by ID. */
    public BudgetGoal(int id) {
        for (BudgetGoal g : goals) {
            if (g.id == id) {
                copyFrom(g);
                return;
            }
        }
        throw new RuntimeException("BudgetGoal not found with id: " + id);
    }

    /** Empty constructor. */
    public BudgetGoal() {}

    // ─── Static CRUD helpers ──────────────────────────────────────────────────

    public static BudgetGoal create(int userId, String name,
                                    double targetAmount, int categoryId, Date deadline) {
        if (name == null || name.isBlank())
            throw new RuntimeException("Goal name must not be empty");
        if (targetAmount <= 0)
            throw new RuntimeException("Target amount must be positive");
        if (deadline == null)
            throw new RuntimeException("Deadline is required");

        int newId = goals.size() + 1;
        BudgetGoal g = new BudgetGoal(newId, userId, name, targetAmount, 0.0, categoryId, deadline, false);
        goals.add(g);
        return g;
    }

    public static List<BudgetGoal> all(int userId) {
        List<BudgetGoal> result = new ArrayList<>();
        for (BudgetGoal g : goals) {
            if (g.userId == userId) result.add(g);
        }
        return result;
    }

    /** Update current progress and auto-complete when target is reached. */
    public void addProgress(double amount) {
        if (amount <= 0) throw new RuntimeException("Progress amount must be positive");
        // find and mutate the record in the list
        for (BudgetGoal g : goals) {
            if (g.id == this.id) {
                g.currentAmount = Math.min(g.currentAmount + amount, g.targetAmount);
                g.isCompleted   = g.currentAmount >= g.targetAmount;
                this.currentAmount = g.currentAmount;
                this.isCompleted   = g.isCompleted;
                return;
            }
        }
    }

    public void delete() {
        goals.removeIf(g -> g.id == this.id);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /** Returns progress as a value between 0.0 and 1.0. */
    public double progressRatio() {
        if (targetAmount == 0) return 0;
        return Math.min(currentAmount / targetAmount, 1.0);
    }

    private void copyFrom(BudgetGoal src) {
        this.id            = src.id;
        this.userId        = src.userId;
        this.name          = src.name;
        this.targetAmount  = src.targetAmount;
        this.currentAmount = src.currentAmount;
        this.categoryId    = src.categoryId;
        this.deadline      = src.deadline;
        this.isCompleted   = src.isCompleted;
    }
}