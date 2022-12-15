package jdbc;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SimpleJDBCRepository {
    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES(?, ?, ?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";

    public Long createUser(User user) {
        Long result = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();

            // Retrieve the ID of the newly inserted row
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                result = rs.getLong(1);
            }
        } catch (SQLException throwables) {
            throwRuntimeException(throwables);
        }
        return result;
    }

    public User findUserById(Long userId) {
        User user = null;
        ResultSet rs;
        try (Connection connection = CustomDataSource.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(findUserByIdSQL);) {
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            if (!rs.next()) throw new SQLException("No such users");

            String firstname = rs.getString("firstname");
            String lastname = rs.getString("lastname");
            int age = rs.getInt("age");

            user = new User(userId, firstname, lastname, age);
        } catch (SQLException throwables) {
            throwRuntimeException(throwables);
        }
        return user;
    }

    public User findUserByName(String userName) {
        ResultSet rs = null;
        User user = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(findUserByNameSQL);) {
            ps.setString(1, userName);
            rs = ps.executeQuery();
            if (!rs.next()) throw new SQLException("No such users");

            String firstname = rs.getString("firstname");
            String lastname = rs.getString("lastname");
            int age = rs.getInt("age");
            Long id = Long.parseLong(rs.getString("id"));

            user = new User(id, firstname, lastname, age);
        } catch (SQLException throwables) {
            throwRuntimeException(throwables);
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = null;
        ResultSet rs = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection(); Statement st = connection.createStatement()) {
            rs = st.executeQuery(findAllUserSQL);
            users = new ArrayList<User>();
            while (rs.next()) {
                Long id = Long.parseLong(rs.getString("id"));
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                int age = rs.getInt("age");
                users.add(new User(id, firstName, lastName, age));
            }
        } catch (SQLException throwables) {
            throwRuntimeException(throwables);
        }
        return users;
    }

    public User updateUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(updateUserSQL);) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            if (ps.executeUpdate() == 0) throw new SQLException("No such user exists");
        } catch (SQLException throwables) {
            throwRuntimeException(throwables);
        }
        return user;
    }

    public void deleteUser(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(deleteUser);) {
            ps.setLong(1, userId);
            if (ps.executeUpdate() == 0) throw new SQLException("No such user exists");
        } catch (SQLException throwables) {
            throwRuntimeException(throwables);
        }
    }
    public void throwRuntimeException(Exception e) {
        String message = String.format("%s: %s", e.getClass().getName(), e.getMessage());
        if (e.getCause() != null) {
            message += String.format("\nCause: %s: %s", e.getCause().getClass().getName(), e.getCause().getMessage());
        }
        throw new RuntimeException(message);
    }

}