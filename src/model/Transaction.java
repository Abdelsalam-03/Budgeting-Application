package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Transaction {

    public int id;
    public double amount;
    public int userId;
    public int categoryId;
    public String notes;
    public Date date;
    public boolean isIncome;

    // Fake database
    private static List<Transaction> transactions = new ArrayList<>();

    // Static block to add some dummy data
    static {
        transactions.add(new Transaction(1, 150.0, 1, 1, "Lunch at restaurant", new Date(), false));
        transactions.add(new Transaction(2, 50.0, 1, 2, "Uber ride", new Date(), false));
        transactions.add(new Transaction(3, 300.0, 2, 4, "Electricity bill", new Date(), false));
        transactions.add(new Transaction(4, 120.0, 2, 3, "Cinema tickets", new Date(), false));
        transactions.add(new Transaction(5, 500.0, 1, 5, "Clothes shopping", new Date(), false));

        transactions.add(new Transaction(6, 2000.0, 1, 0, "Salary", new Date(), true));
        transactions.add(new Transaction(7, 1500.0, 2, 0, "Freelance project", new Date(), true));
        transactions.add(new Transaction(8, 250.0, 1, 0, "Sold old items", new Date(), true));

        transactions.add(new Transaction(9, 80.0, 2, 1, "Groceries", new Date(), false));
        transactions.add(new Transaction(10, 40.0, 1, 2, "Bus ticket", new Date(), false));
        transactions.add(new Transaction(11, 220.0, 2, 5, "New shoes", new Date(), false));
        transactions.add(new Transaction(12, 600.0, 1, 4, "Internet bill", new Date(), false));

        transactions.add(new Transaction(13, 3000.0, 2, 0, "Monthly salary", new Date(), true));
        transactions.add(new Transaction(14, 100.0, 1, 3, "Game purchase", new Date(), false));
        transactions.add(new Transaction(15, 75.0, 2, 1, "Dinner", new Date(), false));
    }

    // Full constructor
    private Transaction(int id, double amount, int userId, int categoryId, String notes, Date date, boolean isIncome) {
        this.id = id;
        this.amount = amount;
        this.userId = userId;
        this.categoryId = categoryId;
        this.notes = notes;
        this.date = date;
        this.isIncome = isIncome;
    }

    // Search by ID
    public Transaction(int id) {
        for (Transaction transaction : transactions) {
            if (transaction.id == id) {
                this.id = transaction.id;
                return;
            }
        }
        throw new RuntimeException("Transaction not found with id: " + id);
    }

    // Empty constructor
    public Transaction() {
    }

    // Creation method
    public static Transaction create(double amount, int userId, int categoryId, Date date, String notes, boolean isIncome) {
        int newId = transactions.size() + 1;

        if (amount <= 0) {
            throw new RuntimeException("Transaction amount is not valid");
        }

        Transaction newTransaction = new Transaction(newId, amount, userId, categoryId, notes, date, isIncome);
        transactions.add(newTransaction);
        return newTransaction;
    }

    // Optional: get all user transactions
    public static List<Transaction> all(int userId) {
        List<Transaction> newList = new ArrayList<>();
        transactions.forEach(tr -> {
            if (tr.userId == userId) {
                newList.add(tr);
            }
        });
        return newList;
    }
    
    public void delete() {
        transactions.removeIf(t -> t.id == this.id);
    }
}
