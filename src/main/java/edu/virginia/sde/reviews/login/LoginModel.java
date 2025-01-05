package edu.virginia.sde.reviews.login;
import edu.virginia.sde.reviews.db.SQLiteConnection;

import java.sql.*;

public class LoginModel {
    public LoginModel() {
        connection = SQLiteConnection.LoginDatabaseConnection();
        if (connection == null) {
            System.out.println("Connection Unsuccessful");
            System.exit(1);
        }
    }Connection connection;


    public boolean isDatabaseConnected() {
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean loginExists(String username, String password) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
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

}
