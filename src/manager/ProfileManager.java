package manager;

import model.User;
import resource.UserResource;

public class ProfileManager {

    // Auth manager
    private final AuthenticationManager authManager;

    public ProfileManager() {
        authManager = AuthenticationManager.getAuthenticationManager();
    }

    // Get current logged in user
    public UserResource getCurrentUser() {
        return authManager.getUser();
    }

    // Update name
    public void updateName(String newName) {
        if (newName == null || newName.isBlank())
            throw new RuntimeException("Name must not be empty");

        int userId = authManager.getUser().getID();
        User user = new User(userId);
        user.updateName(newName);
    }

    // Change password
    public void changePassword(String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.isBlank())
            throw new RuntimeException("New password must not be empty");

        int userId = authManager.getUser().getID();
        User user = new User(userId);

        if (!user.password.equals(currentPassword))
            throw new RuntimeException("Current password is incorrect");

        user.updatePassword(newPassword);
    }
}