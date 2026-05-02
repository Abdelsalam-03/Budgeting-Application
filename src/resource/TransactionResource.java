package resource;

import java.util.Date;
import model.Transaction;

public class TransactionResource {

    public int id;
    public double amount;
    public Date date;
    public int categoryId;
    public boolean isIncome;
    public String notes;

    public TransactionResource(int id, double amount, int categoryId, String notes, Date date, boolean isIncome) {
        this.id = id;
        this.amount = amount;
        this.categoryId = categoryId;
        this.notes = notes;
        this.date = date;
        this.isIncome = isIncome;
    }

    public TransactionResource(Transaction transaction) {
        this.id = transaction.id;
        this.amount = transaction.amount;
        this.categoryId = transaction.categoryId;
        this.notes = transaction.notes;
        this.date = transaction.date;
        this.isIncome = transaction.isIncome;
    }

}
