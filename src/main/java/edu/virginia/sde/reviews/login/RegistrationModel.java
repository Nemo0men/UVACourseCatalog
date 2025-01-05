package edu.virginia.sde.reviews.login;
import edu.virginia.sde.reviews.db.SQLiteConnection;

import java.sql.*;

public class RegistrationModel {
    Connection connection;

    public RegistrationModel() {
        connection = SQLiteConnection.LoginDatabaseConnection();
        if (connection == null) {
            System.out.println("Connection Unsuccessful");
            System.exit(1);
        }
    }

    public boolean userExists(String username) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Users WHERE username = ?";
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            assert ps != null;
            ps.close();
            assert rs != null;
            rs.close();
        }
    }



    public String registerUser(String username, String password) throws SQLException {

        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters";
        } else {
            PreparedStatement ps = null;

            String query = "INSERT INTO Users (username, password) VALUES (?,?)";
            try {
                ps = connection.prepareStatement(query);
                ps.setString(1, username);
                ps.setString(2, password);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    return "Registration Successful";
                } else {
                    return "Registration Unsuccessful";
                }
            } catch (Exception e) {
                return "Registration Unsuccessful";
            } finally {
                assert ps != null;
                ps.close();
            }
        }
    }
}
