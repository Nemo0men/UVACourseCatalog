package edu.virginia.sde.reviews.db;
import edu.virginia.sde.reviews.courseSearch.InvalidCourseException;
import edu.virginia.sde.reviews.courseReview.Review;
import edu.virginia.sde.reviews.courseSearch.Course;
import edu.virginia.sde.reviews.login.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DatabaseDriver {
    private static final String URL = "jdbc:sqlite:testDatabase.sqlite";
    public Connection connection;
    private static DatabaseDriver instance;
    // Get Connection
    public Connection getConnection() {
        return connection;
    }

    public DatabaseDriver() throws SQLException {
        connect();
//        connect(); // Establish connection when the instance is created
        initializeDatabase(); // Ensure tables are created
    }

    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            throw new IllegalStateException("The connection is already opened");
        }
        connection = DriverManager.getConnection(URL);
        //the next line enables foreign key enforcement - do not delete/comment out
        connection.createStatement().execute("PRAGMA foreign_keys = ON");
        //the next line disables auto-commit - do not delete/comment out
        connection.setAutoCommit(false);
    }
    // Provide global access to the instance
    public static DatabaseDriver getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DatabaseDriver.class) {
                if (instance == null) {
                    instance = new DatabaseDriver();
                }
            }
        }
        return instance;
    }


    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void commit() throws SQLException {
        if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
            connection.commit();
        }
    }

    public void rollback() throws SQLException {
        if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
            connection.rollback();
        }
    }

    private void initializeDatabase() throws SQLException {
        String sqlUsers = """
    CREATE TABLE IF NOT EXISTS Users (
        UserID INTEGER PRIMARY KEY AUTOINCREMENT,
        Username TEXT NOT NULL UNIQUE,
        Password TEXT NOT NULL
    );""";

        String sqlCourses = """
    CREATE TABLE IF NOT EXISTS Courses (
        CourseID INTEGER PRIMARY KEY AUTOINCREMENT,
        Subject TEXT NOT NULL,
        Number INTEGER NOT NULL,
        Title TEXT NOT NULL,
        UNIQUE(Subject, Number, Title)
    );""";

        String sqlReviews = """
    CREATE TABLE IF NOT EXISTS Reviews (
        ReviewID INTEGER PRIMARY KEY AUTOINCREMENT,
        UserID INTEGER NOT NULL,
        CourseID INTEGER NOT NULL,
        Rating INTEGER NOT NULL CHECK (Rating BETWEEN 1 AND 5),
        Comment TEXT,
        Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (UserID) REFERENCES Users(UserID),
        FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
    );""";


        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlUsers);
            stmt.execute(sqlCourses);
            stmt.execute(sqlReviews);
        }
    }


    // ----------- User Related Methods ----------
    public void addUser(String username, String password) throws  SQLException{
        String sql = "INSERT INTO Users (Username, Password) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
        }
        catch (SQLException e){
            rollback();
            throw e;
        }
    }

    public void addUserByObject(User user) throws SQLException {
        String sql = "INSERT INTO Users (Username, Password) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            rollback();
            throw e;
        }
    }

    public void updateUserPasswordByUserObject(User user, String newPassword) throws SQLException {
        String sql = "UPDATE Users SET Password = ? WHERE UserID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, user.getUserId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            rollback();
            throw e;
        }
    }

    public boolean doesUserExists(String username) throws SQLException {
        String sql = "SELECT COUNT(Username) FROM Users WHERE Username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Returns true if count is greater than 0
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            throw e;
        }
        return false;
    }

    public void updateUserPassword(int userID, String newPassword) throws SQLException {
        String sql = "UPDATE Users SET Password = ? WHERE UserID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, newPassword); // Set the first ? to the new password
            pstmt.setInt(2, userID); // Set the second ? to the userId
            pstmt.executeUpdate();
        } catch (SQLException e) {
            rollback();
            throw e;
        }
    }



    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT UserID, Username, Password FROM Users";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int userId = rs.getInt("UserID");
                String username = rs.getString("Username");
                String password = rs.getString("Password");
                users.add(new User(userId, username, password));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
            throw e;
        }
        return users;
    }



    // ----------- Review  Related Methods ----------


    //addReview(int userId, int courseId, int rating, String comment):
//Purpose: Adds a review made by a user for a specific course.
//Parameters: int userId, int courseId, int rating, String comment.
//Returns: Boolean indicating success or failure.
    public void addReview(int userId, int courseId, int rating, String comment) throws SQLException {
        String sql = """
                INSERT INTO Reviews (UserID, CourseID, Rating, Comment)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (UserID, CourseID)
                DO UPDATE SET Rating = excluded.Rating, Comment = excluded.Comment, Timestamp = CURRENT_TIMESTAMP
                """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);
            pstmt.setInt(3, rating);
            pstmt.setString(4, comment != null ? comment : "");

            pstmt.executeUpdate();
        } catch (SQLException e) {
            rollback();
            throw e;
        }
    }

    //getReviewsByCourse(int courseId):
    //Purpose: Retrieves all reviews for a specific course.
    //Parameters: int courseId.
    //Returns: List of Review objects.
    public List<Review> getReviewsByCourse(int courseId) {
        List<Review> reviews = new ArrayList<>();
        String sql = """
        SELECT r.ReviewID, r.CourseID, r.Rating, r.Comment, r.Timestamp, u.Username
        FROM Reviews r
        JOIN Users u ON r.UserID = u.UserID
        WHERE r.CourseID = ?
    """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int reviewId = rs.getInt("ReviewID");
                    int courseIdFromDb = rs.getInt("CourseID");
                    double rating = rs.getDouble("Rating");
                    String comment = rs.getString("Comment");
                    Timestamp timestamp = rs.getTimestamp("Timestamp");
                    String username = rs.getString("Username");

                    reviews.add(new Review(reviewId, courseIdFromDb, rating, comment, timestamp, username));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving reviews by course: " + e.getMessage());
        }
        return reviews;
    }


    // Get reviews list by a user
    public List<Review> getReviewsByUser(int courseId) {
        List<Review> reviews = new ArrayList<>();
        String sql = """
            SELECT r.ReviewID, r.Rating, r.Comment, r.Timestamp, u.Username
            FROM Reviews r
            JOIN Users u ON r.UserID = u.UserID
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int reviewId = rs.getInt("ReviewID");
                    int rating = rs.getInt("Rating");
                    String comment = rs.getString("Comment");
                    Timestamp timestamp = rs.getTimestamp("Timestamp");
                    String username = rs.getString("Username");
                    reviews.add(new Review(reviewId, courseId, rating, comment, timestamp, username));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving reviews by course: " + e.getMessage());
        }
        return reviews;
    }





    public void clearTables() throws SQLException {
        //TODO: implement
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM Reviews");
            stmt.execute("DELETE FROM Courses");
            stmt.execute("DELETE FROM Users");
        } catch (SQLException e) {
            rollback(); //rolls back any changes before the Exception was thrown
            throw e; //still throws the SQLException
        }


    }




    // ----------- Course Related Methods ----------


    //getAllCourses():
    //Purpose: Retrieves all courses from the database.
    //Parameters: None.
    //Returns: List of Course objects.
    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = """
            
            SELECT c.CourseID, c.Subject, c.Number, c.Title, ROUND(COALESCE(AVG(r.Rating), 0), 2) AS AvgRating
            FROM Courses c
            LEFT JOIN Reviews r ON c.CourseID = r.CourseID
            GROUP BY c.CourseID, c.Subject, c.Number, c.Title
            ORDER BY c.CourseID;
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int courseId = rs.getInt("CourseID");
                String subject = rs.getString("Subject");
                int number = rs.getInt("Number");
                String title = rs.getString("Title");
                double avgRating = rs.getDouble("AvgRating");  // Handles null with COALESCE
                Course course = new Course(courseId, subject, number, title, avgRating);
                courses.add(course);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving courses: " + e.getMessage());
            throw e;
        }
        return courses;
    }


    public double getAverageRating(int courseID) throws SQLException {
        String sql = """
                                
                SELECT ROUND(COALESCE(AVG(Rating), 0), 2) AS AvgRating
                FROM Reviews WHERE CourseID = ?
            
                        
                """;
        double avgRating = 0;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
             pstmt.setInt(1, courseID);
             ResultSet rs = pstmt.executeQuery(); {
                while (rs.next()) {
                    avgRating = rs.getDouble("AvgRating");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving courses: " + e.getMessage());
            throw e;
        }
        return avgRating;
    }





    //findCoursesByCriteria(String subject, Integer number, String title):
    //Purpose: Searches for courses based on provided criteria (any or all of subject, number, and title).
    //Parameters: String subject, Integer number, String title.
    //Returns: List of Course objects matching the criteria.
    public List<Course> findCoursesByCriteria(String subject, Integer number, String title) throws SQLException {
        List<Course> courses = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
        SELECT c.CourseID, c.Subject, c.Number, c.Title, COALESCE(AVG(r.Rating), 0) AS AvgRating
        FROM Courses c
        LEFT JOIN Reviews r ON c.CourseID = r.CourseID
        WHERE 1=1 
        """);

        // Append conditions based on input
        List<Object> parameters = new ArrayList<>();
        if (subject != null && !subject.isEmpty()) {
            sql.append(" AND c.Subject LIKE ?");
            parameters.add("%" + subject.toUpperCase() + "%");
        }
        if (number != null) {
            sql.append(" AND c.Number = ?");
            parameters.add(number);
        }
        if (title != null && !title.isEmpty()) {
            sql.append(" AND c.Title LIKE ?");
            parameters.add("%" + title + "%");
        }

        sql.append(" GROUP BY c.CourseID, c.Subject, c.Number, c.Title ORDER BY c.CourseID");

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            // Set parameters for the query
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int courseId = rs.getInt("CourseID");
                    String dbSubject = rs.getString("Subject");
                    int dbNumber = rs.getInt("Number");
                    String dbTitle = rs.getString("Title");
                    double avgRating = rs.getDouble("AvgRating"); // This matches your table column expectation
                    courses.add(new Course(courseId, dbSubject, dbNumber, dbTitle, avgRating));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding courses: " + e.getMessage());
            throw e;
        }
        return courses;
    }



    public void addCourse(String mnemonic, int courseNum, String courseName) throws SQLException, InvalidCourseException {
        String sql = "INSERT INTO Courses (Subject, Number, Title) VALUES (?, ?, ?)";
        validateCourseInput(mnemonic, courseNum, courseName);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, mnemonic.toUpperCase());
            pstmt.setInt(2, courseNum);
            pstmt.setString(3, courseName);
            pstmt.executeUpdate();
//            commit();
        }
        catch(SQLException e){
            rollback();
            throw e;
        }
    }
    public boolean doesCourseExist(String subject, int number, String title) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Courses WHERE Subject = ? AND Number = ? AND Title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, subject);
            pstmt.setInt(2, number);
            pstmt.setString(3, title);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private void validateCourseInput(String mnemonic, int courseNum, String courseName) throws InvalidCourseException {
        // Pattern matching for mnemonic and course number
        Pattern mnemonicPattern = Pattern.compile("^[A-Za-z]{2,4}$");
        Pattern courseNumPattern = Pattern.compile("^\\d{4}$");

        if (!mnemonicPattern.matcher(mnemonic).matches()) {
            throw new InvalidCourseException("Subject mnemonic must be 2-4 letters only (no numbers or symbols).");
        }
        if (!courseNumPattern.matcher(String.valueOf(courseNum)).matches()) {
            throw new InvalidCourseException("Course number must be exactly 4 digits.");
        }
        if (courseName.isEmpty() || courseName.length() > 50) {
            throw new InvalidCourseException("Course title must be between 1 and 50 characters long.");
        }
    }


    public double getAverageRatingByCourse(int courseId) {
        String sql = "SELECT AVG(Rating) FROM Reviews WHERE CourseID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double averageRating = rs.getDouble(1);
                    return Math.round(averageRating * 100.0) / 100.0; // Round to two decimal places
                }
            }
        } catch (SQLException e) {
            System.out.println("Error calculating average rating: " + e.getMessage());
        }
        return 0.0;
    }


    public void insertTestData() {
        String insertUsers = """
            INSERT INTO Users (Username, Password)
            VALUES ('testuser', 'password123'),
                   ('admin', 'adminpass');
        """;

        String insertCourses = """
            INSERT INTO Courses (Subject, Number, Title)
            VALUES ('CS', 1010, 'Introduction to Programming'),
                   ('CS', 1020, 'Data Structures'),
                   ('CS', 2010, 'Algorithms');
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(insertUsers);
            stmt.execute(insertCourses);
            commit();
        } catch (SQLException e) {
            System.out.println("Error inserting test data: " + e.getMessage());
            try {
                rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    // Check if the user has already reviewed this course


    public int getUserIdByUsername(String username) throws SQLException {
        String query = "SELECT UserID FROM Users WHERE Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserID");
                }
            }
        }
        throw new SQLException("No user found with the username: " + username);
    }
    public boolean doesCourseExist(int courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Courses WHERE CourseID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    public boolean hasUserReviewedCourse(int userId, int courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Reviews WHERE UserID = ? AND CourseID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public String getUsernameById(int userId) throws SQLException {
        String sql = "SELECT Username FROM Users WHERE UserID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Username");
                }
            }
        }
        return null; // or throw an exception if user not found
    }





}