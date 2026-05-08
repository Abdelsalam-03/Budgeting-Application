package model;

import java.util.ArrayList;
import java.util.List;

public class User {

    public int id;
    public String name;
    public String email;
    public String password;

    // Fake database
    private static List<User> users = new ArrayList<>();

    // Static block to add some dummy data
    static {
        users.add(new User(1, "Ahmed", "ahmed@gmail.com", "1234"));
        users.add(new User(2, "Ali",   "ali@gmail.com",   "abcd"));
    }

    // Full constructor
    private User(int id, String name, String email, String password) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.password = password;
    }

    // Search by ID
    public User(int id) {
        for (User user : users) {
            if (user.id == id) {
                this.id       = user.id;
                this.name     = user.name;
                this.email    = user.email;
                this.password = user.password;
                return;
            }
        }
        throw new RuntimeException("User not found with id: " + id);
    }

    // Search by email
    public User(String email) {
        for (User user : users) {
            if (user.email.equals(email)) {
                this.id       = user.id;
                this.name     = user.name;
                this.email    = user.email;
                this.password = user.password;
                return;
            }
        }
        throw new RuntimeException("User not found with email: " + email);
    }

    // Empty constructor
    public User() {}

    // Signup method
    public static User create(String name, String email, String password) {
        int newId = users.size() + 1;

        // check if email already exists
        for (User user : users) {
            if (user.email.equals(email)) {
                throw new RuntimeException("Email already exists");
            }
        }

        User newUser = new User(newId, name, email, password);
        users.add(newUser);
        return newUser;
    }

    // Optional: get all users
    public static List<User> all() {
        return users;
    }

    // Update name
    public void updateName(String newName) {
        for (User user : users) {
            if (user.id == this.id) {
                user.name = newName;
                this.name = newName;
                return;
            }
        }
    }

    // Update password
    public void updatePassword(String newPassword) {
        for (User user : users) {
            if (user.id == this.id) {
                user.password = newPassword;
                this.password = newPassword;
                return;
            }
        }
    }
}