package manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Transaction;
import resource.TransactionResource;

public class TransactionManager {

    public boolean addTransaction(double amount, Integer categoryId, LocalDateTime date, String notes, boolean isIncome) {

        try {
            int userId = AuthenticationManager.getAuthenticationManager().getUser().getID();
            Transaction.create(amount, userId, categoryId, date, notes, isIncome);

            // Auto-update matching budget goals by category
            if (isIncome) {
                BudgetGoalManager budgetGoalManager = new BudgetGoalManager();
                budgetGoalManager.updateGoalsForTransaction(userId, 1, amount);
            }

        } catch (Exception e) {
            throw e;
        }

        return true;
    }

    public TransactionResource getTransaction(int transactionID) {
        try {
            return new TransactionResource(new Transaction(transactionID));
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean deleteTransaction(int transactionID) {
        try {
            Transaction transaction = new Transaction(transactionID);
            transaction.delete();
        } catch (Exception e) {
            throw e;
        }
        return true;
    }

    public List<TransactionResource> getTransactions() {
        try {
            int userID = AuthenticationManager.getAuthenticationManager().getUser().getID();
            List<TransactionResource> transactions = new ArrayList<>();
            Transaction.all(userID).forEach(tr -> {
                transactions.add(new TransactionResource(tr));
            });
            return transactions;
        } catch (Exception e) {
            throw e;
        }
    }

}