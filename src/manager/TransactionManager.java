package manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import model.Transaction;
import resource.TransactionResource;

public class TransactionManager {

    public boolean addTransaction(double amount, int categoryId, Date date, String notes, boolean isIncome) {

        try {
            int userId = AuthenticationManager.getAuthenticationManager().getUser().getID();
            Transaction transaction = Transaction.create(amount, userId, categoryId, date, notes, isIncome);
//            return new TransactionResource(transaction);
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
