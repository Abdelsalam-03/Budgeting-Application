package model;

import core.database.Model;
import java.sql.SQLException;

public class User extends Model {

    public int id;
    public String name;
    public String email;
    public String password;

    @Override
    protected String getTable() {
        return "users";
    }

    // Existing user Full constructor
    private User(int id, String name, String email, String password) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.password = password;
    }

    // Non-Existing user Full constructor
    private User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Search by ID
    public User(int id) {
        try {
            User result = new User().get(
                    new String[]{"id"},
                    new Object[]{id},
                    rs -> new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password")
                    )
            );
            this.id = result.id;
            this.name = result.name;
            this.email = result.email;
            this.password = result.password;
        } catch (SQLException e) {
            // Handle the exception
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    // Search by email
    public User(String email) {
        try {
            User result = new User().get(
                    new String[]{"email"},
                    new Object[]{email},
                    rs -> new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password")
                    )
            );
            this.id = result.id;
            this.name = result.name;
            this.email = result.email;
            this.password = result.password;
        } catch (SQLException e) {
            // Handle the exception
            throw new RuntimeException("User not found with email: " + email);
        }
    }

    // Empty constructor
    public User() {
    }

    // Signup method
    public static User create(String name, String email, String password) {

        try {
            String[] cols = {"name", "email", "password"};
            Object[] values = {name, email, password};
            new User().insert(cols, values);
            return new User(email);
        } catch (SQLException e) {
            // Handle different exceptions
            throw new RuntimeException("Email already exists");
        }

    }

    // Update method
    public void save() {
        try {
            String[] cols = {"name", "password"};
            Object[] values = {name, password};

            String[] whereCols = {"id"};
            Object[] whereVals = {id};

            new Budget().update(cols, values, whereCols, whereVals);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred");
        }
    }


    // Update name
    public void updateName(String newName) {
        this.name = newName;
        this.save();
    }

    // Update password
    public void updatePassword(String newPassword) {
        this.password = newPassword;
        this.save();
    }
}
