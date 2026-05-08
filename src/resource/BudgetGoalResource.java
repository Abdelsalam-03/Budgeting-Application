package resource;

import java.util.Date;


public class BudgetGoalResource {

    public final int    id;
    public final int    userId;
    public final String name;
    public final double targetAmount;
    public final double currentAmount;
    public final int    categoryId;
    public final Date   deadline;
    public final boolean isCompleted;

    public BudgetGoalResource(int id, int userId, String name,
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

    
    public double progressRatio() {
        if (targetAmount == 0) return 0;
        return Math.min(currentAmount / targetAmount, 1.0);
    }
}