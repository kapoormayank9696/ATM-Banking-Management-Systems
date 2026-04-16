package Banking_ATM_Simulator.src.DAO;

import Banking_ATM_Simulator.src.model.Users;
import Banking_ATM_Simulator.src.util.DBConnection;
import java.sql.*;

public class UserDAO {

    public boolean createUser(Users user) {
        String query = "INSERT INTO users(username, password) VALUES(?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    user.setUserId(generatedId);

                    // 🔥 IMPORTANT: set ID back to object
                    // (you need setter or constructor update)
                    System.out.println("Generated User ID: " + generatedId);
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
        return false;
    }

    public Users getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Use the constructor with the isAlreadyHashed = true flag
                    return new Users(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            true
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updated login logic to return the User object instead of just a boolean.
     * This allows the Main class to know WHICH user logged in.
     */
    public Users loginUser(String username, String password) {
        Users user = getUserByUsername(username);
        if (user != null && user.verifyPassword(password)) {
            return user;
        }
        return null;
    }
}