package manager;

import model.BudgetGoal;
import resource.BudgetGoalResource;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class BudgetGoalManager {

   

    public BudgetGoalResource addGoal(int userId, String name,
                                      double targetAmount, int categoryId, Date deadline) {
        BudgetGoal goal = BudgetGoal.create(userId, name, targetAmount, categoryId, deadline);
        return toResource(goal);
    }

    public void recordProgress(int goalId, double amount) {
        BudgetGoal goal = new BudgetGoal(goalId); 
        goal.addProgress(amount);
    }

    public void deleteGoal(int goalId) {
        BudgetGoal goal = new BudgetGoal(goalId);
        goal.delete();
    }

   

    public List<BudgetGoalResource> getGoalsForUser(int userId) {
        return BudgetGoal.all(userId)
                         .stream()
                         .map(this::toResource)
                         .collect(Collectors.toList());
    }

    public BudgetGoalResource getGoalById(int goalId) {
        return toResource(new BudgetGoal(goalId));
    }

    
    private BudgetGoalResource toResource(BudgetGoal g) {
        return new BudgetGoalResource(
                g.id, g.userId, g.name,
                g.targetAmount, g.currentAmount,
                g.categoryId, g.deadline, g.isCompleted
        );
    }
}