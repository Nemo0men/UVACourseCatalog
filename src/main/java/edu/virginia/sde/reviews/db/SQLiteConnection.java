package edu.virginia.sde.reviews.db;

import java.sql.*;

public class SQLiteConnection {

    public static Connection LoginDatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:testDatabase.sqlite");
            return conn;
        }
        catch (Exception e) {
            return null;
        }
    }


    public static Connection CourseReviewsDatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:Reviews.sqlite");
            return conn;
        }
        catch (Exception e) {
            return null;
        }
    }
}
