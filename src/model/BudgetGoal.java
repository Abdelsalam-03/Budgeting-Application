package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;


public class BudgetGoal {

    public int    id;
    public int    userId;
    public String name;          
    public double targetAmount;  
    public double currentAmount; 
    public int    categoryId;    
    public Date   deadline;      
    public boolean isCompleted;

    

    private static final List<BudgetGoal> goals = new ArrayList<>();

    static {
        Date soon = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); 
        Date later = new Date(System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000); 

        goals.add(new BudgetGoal(1, 1, "Save for Laptop",      5000.0, 1200.0, 0, later, false));
        goals.add(new BudgetGoal(2, 1, "Monthly Food Budget",  800.0,  300.0,  1, soon,  false));
        goals.add(new BudgetGoal(3, 2, "Transport Budget",     400.0,  400.0,  2, soon,  true));
        goals.add(new BudgetGoal(4, 2, "Shopping Limit",       1000.0, 750.0,  5, later, false));
        goals.add(new BudgetGoal(5, 1, "Emergency Fund",       3000.0, 3000.0, 0, later, true));
    }

    

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

    
    public BudgetGoal(int id) {
        for (BudgetGoal g : goals) {
            if (g.id == id) {
                copyFrom(g);
                return;
            }
        }
        throw new RuntimeException("BudgetGoal not found with id: " + id);
    }

    
    public BudgetGoal() {}

    

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

    
    public void addProgress(double amount) {
        if (amount <= 0) throw new RuntimeException("Progress amount must be positive");
        
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