package jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        SimpleJDBCRepository repo = new SimpleJDBCRepository();

        // Create a new user
        User user = new User(0l,"John", "Doe", 23);
        Long userId = repo.createUser(user);
        System.out.println("Created new user with ID: {userId}");

        // Find the user by ID
        User foundUser = repo.findUserById(userId);
        System.out.println("Found user by ID: {foundUser}");

        // Find the user by name
        User users = repo.findUserByName("John");
        System.out.println("Found {users.size()} users with name 'John Doe': {users}");

        // Find all users
        List<User> allUsers = repo.findAllUser();
        System.out.println("Found {allUsers.size()} users: {allUsers}");

        // Update the user
        foundUser.setAge(24);
        User updatedUser = repo.updateUser(foundUser);
        System.out.println("Updated user: {updatedUser}");

        // Delete the user
        repo.deleteUser(userId);
        System.out.println("Deleted user with ID: {userId}");
    }
}
//        User user = new User(1l, "Tom", "Cruise", 23);
//        long ageUser = user.getAge();
//        String firstName = user.getFirstName();
//        String lastName = user.getLastName();
//        logger.log(Level.INFO,firstName + " " +lastName + " " + ageUser);