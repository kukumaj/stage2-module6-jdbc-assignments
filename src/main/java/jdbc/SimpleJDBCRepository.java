package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
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
        try (Connection connection = CustomDataSource.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(createUserSQL);) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, String.valueOf(user.getAge()));
            result = (long) ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public User findUserById(Long userId) {
        User user = null;
        ResultSet rs;
        try (Connection connection = CustomDataSource.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(findUserByIdSQL);) {
            ps.setString(1, String.valueOf(userId));
            rs = ps.executeQuery();
            if (!rs.next()) throw new SQLException("No such users");

            String firstname = rs.getString("firstname");
            String lastname = rs.getString("lastname");
            int age = Integer.parseInt(rs.getString("age"));

            user = new User(userId, firstname, lastname, age);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
            int age = Integer.parseInt(rs.getString("age"));
            Long id = Long.parseLong(rs.getString("id"));

            user = new User(id, firstname, lastname, age);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = null;
        ResultSet rs = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection(); Statement st = connection.createStatement()) {
            rs = st.executeQuery(findAllUserSQL);
            if (!rs.next()) throw new SQLException("There are no users!");
            users = new ArrayList<User>();
            while (rs.next()) {
                Long id = Long.parseLong(rs.getString("id"));
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                int age = Integer.parseInt("age");
                users.add(new User(id, firstName, lastName, age));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return users;
    }

    public User updateUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(updateUserSQL);) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, String.valueOf(user.getAge()));
            ps.setString(4, String.valueOf(user.getId()));
            if (ps.executeUpdate() == 0) throw new SQLException("No such user exists");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

    public void deleteUser(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(deleteUser);) {
            ps.setString(1, String.valueOf(userId));
            if (ps.executeUpdate() == 0) throw new SQLException("No such user exists");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}