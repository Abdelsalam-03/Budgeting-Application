package model;

import core.database.Model;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class BudgetCategory extends Model {

    public int id;
    public Integer userId;
    public String name;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    // Full constructor
    private BudgetCategory(int id, String name, Integer userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    // Search by ID
    public BudgetCategory(int id) {
        try {
            BudgetCategory result = this.get(
                    new String[]{"id"},
                    new Object[]{id},
                    rs -> new BudgetCategory(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("user_id")
                    )
            );
            this.id = result.id;
            this.name = result.name;
            this.userId = result.userId;
        } catch (SQLException e) {
            // Handle the exception
            throw new RuntimeException("Category not found with id: " + id);
        }
    }

    // Empty constructor
    public BudgetCategory() {
    }

    @Override
    protected String getTable() {
        return "budgeting_categories";
    }

    // Creation method
    public static void create(String name, Integer userId) {
        try {
            String[] cols = {"name", "user_id"};
            Object[] values = {name, userId};
            new BudgetCategory().insert(cols, values);
        } catch (SQLException e) {
            // Handle different exceptions
            throw new RuntimeException("An error occurred");
        }
    }

    // Optional: get all user transactions
    public static List<BudgetCategory> all(int userId) {
        try {
            return new BudgetCategory().getAll(
                    new String[]{"user_id"},
                    new Object[]{null},
                    rs -> new BudgetCategory(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("user_id")
                    ));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred");
        }
    }

    public void delete() {
        try {
            this.destroy(new String[]{"id"}, new Object[]{this.id});
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception Occurred");
        }
    }
    
    @Override
    public String toString(){
        return this.name;
    }
}
