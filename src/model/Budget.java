package model;

import java.time.YearMonth;

/**
 * Model class representing a monthly budget for a spending category.
 */
public class Budget {

    private int id;
    private String category;
    private double amount;
    private double spent;
    private YearMonth period;

    public Budget(int id, String category, double amount, YearMonth period) {
        this.id       = id;
        this.category = category;
        this.amount   = amount;
        this.period   = period;
        this.spent    = 0.0;
    }

    public int       getId()        { return id; }
    public String    getCategory()  { return category; }
    public double    getAmount()    { return amount; }
    public double    getSpent()     { return spent; }
    public YearMonth getPeriod()    { return period; }
    public double    getRemaining() { return amount - spent; }
    public boolean   isOverLimit()  { return spent >= amount; }
    public boolean   isNearLimit()  { return !isOverLimit() && (spent / amount) >= 0.80; }

    public void setCategory(String category) { this.category = category; }
    public void setAmount(double amount)     { this.amount   = amount; }
    public void setPeriod(YearMonth period)  { this.period   = period; }
    public void setSpent(double spent)       { this.spent    = spent; }
}