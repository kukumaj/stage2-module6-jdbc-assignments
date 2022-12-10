package jdbc;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws SQLException {
        Logger logger = Logger.getLogger(Main.class.getName());
        User user = new User(1l, "Tom", "Cruise", 23);
        long ageUser = user.getAge();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        logger.log(Level.INFO,firstName + " " +lastName + " " + ageUser);
    }
}