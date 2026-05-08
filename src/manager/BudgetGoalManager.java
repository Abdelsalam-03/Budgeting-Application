package manager;

import model.BudgetGoal;
import resource.BudgetGoalResource;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BudgetGoalManager {

    // Add a new goal
    public BudgetGoalResource addGoal(int userId, String name,
                                      double targetAmount, int categoryId, Date deadline) {
        BudgetGoal goal = BudgetGoal.create(userId, name, targetAmount, categoryId, deadline);
        return toResource(goal);
    }

    // Edit an existing goal
    public void editGoal(int goalId, String newName, double newTargetAmount,
                         int newCategoryId, Date newDeadline) {
        BudgetGoal goal = new BudgetGoal(goalId);
        goal.edit(newName, newTargetAmount, newCategoryId, newDeadline);
    }

    // Record progress toward a goal manually
    public void recordProgress(int goalId, double amount) {
        BudgetGoal goal = new BudgetGoal(goalId);
        goal.addProgress(amount);
    }

    // Delete a goal
    public void deleteGoal(int goalId) {
        BudgetGoal goal = new BudgetGoal(goalId);
        goal.delete();
    }

    // Get all goals for a user
    public List<BudgetGoalResource> getGoalsForUser(int userId) {
        return BudgetGoal.all(userId)
                         .stream()
                         .map(this::toResource)
                         .collect(Collectors.toList());
    }

    // Get a single goal by ID
    public BudgetGoalResource getGoalById(int goalId) {
        return toResource(new BudgetGoal(goalId));
    }

    // Auto-update goals when a transaction is added
    // Matches transaction categoryId to goal categoryId — works for both income and expense
    public void updateGoalsForTransaction(int userId, int categoryId, double amount) {
        List<BudgetGoal> userGoals = BudgetGoal.all(userId);

        for (BudgetGoal goal : userGoals) {
            if (goal.isCompleted) continue;

            // Only update goals whose category matches the transaction category
            if (goal.categoryId == categoryId) {
                goal.addProgress(amount);
            }
        }
    }

    // Map model to resource
    private BudgetGoalResource toResource(BudgetGoal g) {
        return new BudgetGoalResource(
                g.id, g.userId, g.name,
                g.targetAmount, g.currentAmount,
                g.categoryId, g.deadline, g.isCompleted
        );
    }
}