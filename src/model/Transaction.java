package model;

import java.time.LocalDate;

/**
 * Model class representing a single financial transaction.
 */
public class Transaction {

    public enum Type { INCOME, EXPENSE }

    private int       id;
    private String    description;
    private double    amount;
    private Type      type;
    private String    category;
    private LocalDate date;

    public Transaction(int id, String description, double amount,
                       Type type, String category, LocalDate date) {
        this.id          = id;
        this.description = description;
        this.amount      = amount;
        this.type        = type;
        this.category    = category;
        this.date        = date;
    }

    public int       getId()          { return id; }
    public String    getDescription() { return description; }
    public double    getAmount()      { return amount; }
    public Type      getType()        { return type; }
    public String    getCategory()    { return category; }
    public LocalDate getDate()        { return date; }
}