package edu.virginia.sde.reviews.courseReview;

import edu.virginia.sde.reviews.courseSearch.Course;
import edu.virginia.sde.reviews.courseSearch.CourseSearchController;
import edu.virginia.sde.reviews.db.DatabaseDriver;
import edu.virginia.sde.reviews.login.LoginController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class CourseReviewsController implements Initializable {
    private DatabaseDriver dbDriver;
    private Connection connection;
    private int courseId;
    private String username = LoginController.loggedInUsername;


    public CourseReviewsController() {
        try {
            dbDriver = DatabaseDriver.getInstance();
//            Connection connection = SQLiteConnection.CourseReviewsDatabaseConnection(); // Initializes and connects to the database
            connection = dbDriver.getConnection();
        } catch (SQLException e) {
            showAlert("Connection Unsuccessful: ", e.getMessage());
            System.exit(1);
        }
    }

    @FXML
    private TableView<CourseReviewModel> reviewTable;

    @FXML
    private TableColumn<CourseReviewModel, Integer> ratingsColumn;
    @FXML
    private TableColumn<CourseReviewModel, String> commentsColumn;
    @FXML
    private TableColumn<CourseReviewModel, Timestamp> timestampsColumn;

    @FXML
    private TextField ratingInput;
    @FXML
    private TextField commentInput;

    @FXML
    private Label averageRatingLabel;

    ObservableList<CourseReviewModel> reviewsObservableList = FXCollections.observableArrayList();
    public FilteredList<CourseReviewModel> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        courseId = CourseSearchController.getCoursePrimaryId();

        try {
            refreshAvgRating();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        dbDriver.insertTestData();
        String query = "SELECT ReviewID, UserID, CourseID, Rating, Comment, Timestamp FROM Reviews";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Integer reviewID = rs.getInt("ReviewID");
                Integer userID = rs.getInt("UserID");
                Integer courseID = rs.getInt("CourseID");
                Integer rating = rs.getInt("Rating");
                String comment = rs.getString("Comment");
                Timestamp timestamp = rs.getTimestamp("Timestamp");

                reviewsObservableList.add(new CourseReviewModel(reviewID, userID, courseID, rating, comment, timestamp));
            }

            ratingsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRating()).asObject());
            commentsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComments()));
            timestampsColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTimestamp()));

            reviewTable.setItems(reviewsObservableList);


            //Initial filtered list
            filteredList = new FilteredList<>(reviewsObservableList, b -> b.getCourseID() == courseId);

            reviewTable.setItems(filteredList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshAvgRating() throws SQLException {
        averageRatingLabel.setText("Average Rating: " + dbDriver.getAverageRating(courseId));
    }

    private void loadReviews() {
//        ObservableList<CourseReviewModel> reviews = FXCollections.observableArrayList();
//        try {
//            String query = "SELECT ReviewID, UserID, CourseID, Rating, Comment, Timestamp FROM Reviews";
//            try (PreparedStatement ps = connection.prepareStatement(query);
//                 ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    Integer reviewID = rs.getInt("ReviewID");
//                    Integer userID = rs.getInt("UserID");
//                    Integer courseID = rs.getInt("CourseID");
//                    Integer rating = rs.getInt("Rating");
//                    String comment = rs.getString("Comment");
//                    Timestamp timestamp = rs.getTimestamp("Timestamp");
//
//                    reviews.add(new CourseReviewModel(reviewID, userID, courseID, rating, comment, timestamp));
//                }
//            } catch (SQLException e) {
//                // showAlert("Database Error", "Failed to fetch courses: " + e.getMessage());
//            }
        reviewTable.setItems(filteredList);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    @FXML
    public void addReviewButton(ActionEvent event) throws SQLException {
        String ratingText = ratingInput.getText().trim();
        String comment = commentInput.getText().trim();
        int userID = dbDriver.getUserIdByUsername(username); // Replace with actual user ID that exists in the Users table
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (reviewByUserExists()) {
            showAlert("Error", "You may only submit one review per course.");
            return;
        }

        if (ratingText.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        Integer rating;
        try {
            rating = Integer.parseInt(ratingText);
            if (rating < 1 || rating > 5) {
                showAlert("Error", "Rating should be between 1 and 5.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid rating input");
            return;
        }

        String insertQuery = "INSERT INTO Reviews (UserID, CourseID, Rating, Comment, Timestamp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setInt(1, userID);
            ps.setInt(2, courseId);
            ps.setInt(3, rating);
            ps.setString(4, comment);
            ps.setTimestamp(5, timestamp);
            ps.executeUpdate();

            // Fetch the last inserted ReviewID using LAST_INSERT_ROWID
            String lastInsertIdQuery = "SELECT LAST_INSERT_ROWID()";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(lastInsertIdQuery)) {
                if (rs.next()) {
                    Integer reviewID = rs.getInt(1);
                    reviewsObservableList.add(new CourseReviewModel(reviewID, userID, courseId, rating, comment, timestamp));
                    reviewTable.refresh(); // Refresh TableView
                }
                dbDriver.commit();
                loadReviews();
                refreshAvgRating();
            }
        } catch (SQLException e) {
            showAlert("Error", "e.getMessage()");
            e.printStackTrace();
        }
    }

    public boolean reviewByUserExists() throws SQLException{
        PreparedStatement ps = null;
        ResultSet rs = null;
        int userID = dbDriver.getUserIdByUsername(username);
        String query = "SELECT * FROM Reviews WHERE UserID = ? and CourseID = ?";
        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, userID);
            ps.setInt(2, courseId);
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

    @FXML
    public void deleteReview(ActionEvent event) throws SQLException {
        CourseReviewModel selectedReview = reviewTable.getSelectionModel().getSelectedItem();
        if (selectedReview != null) {
            int reviewID = selectedReview.getReviewID();

            if (!reviewIsByUser(reviewID)) {
                showAlert("Error", "This is not your review");
                return;
            }

            String deleteQuery = "DELETE FROM Reviews WHERE ReviewID = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
                ps.setInt(1, reviewID);
                ps.executeUpdate();

                reviewsObservableList.remove(selectedReview); // Remove from ObservableList
                reviewTable.refresh();
                dbDriver.commit();// Refresh TableView
                refreshAvgRating();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            showAlert("Error", "Please choose a review to delete.");
        }

    }

    public boolean reviewIsByUser(int reviewID) throws SQLException{
        PreparedStatement ps = null;
        ResultSet rs = null;
        int userID = dbDriver.getUserIdByUsername(username);
        String query = "SELECT UserID FROM Reviews WHERE ReviewID = ?";
        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, reviewID);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("UserID") == userID;
            } else {
                return false;
            }
        } catch (SQLException e) {
        System.out.println("Error checking review ownership: " + e.getMessage());
        throw e;
    }
    }

    @FXML
    public void updateReview(ActionEvent event) throws SQLException {
        CourseReviewModel selectedReview = reviewTable.getSelectionModel().getSelectedItem();
        if (selectedReview == null) {showAlert("Error", "Please choose a review to update.");}
        int reviewID = selectedReview.getReviewID();

        if (!reviewIsByUser(reviewID)) {
            showAlert("Error", "This is not your review");
            return;
        }

        String ratingText = ratingInput.getText().trim();
        String comment = commentInput.getText().trim();

        if (ratingText.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        Integer rating;
        try {
            rating = Integer.parseInt(ratingText);
            if (rating < 1 || rating > 5) {
                showAlert("Error", "Rating should be between 1 and 5.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid rating input");
            return;
        }

        String updateQuery = "UPDATE Reviews SET Rating = ?, Comment = ?, Timestamp = CURRENT_TIMESTAMP WHERE ReviewID = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, selectedReview.getReviewID());
            ps.executeUpdate();

            // Update the TableView
            selectedReview.setRating(rating);
            selectedReview.setComments(comment);
            selectedReview.setTimestamp(new Timestamp(System.currentTimeMillis()));
            dbDriver.commit();
            reviewTable.refresh(); // Refresh TableView
            refreshAvgRating();

        } catch (SQLException e) {
            showAlert("Error updating review:", e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    public void backToCourseSearch(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CourseSearch.fxml"));
            Pane root = fxmlLoader.load();

            Scene scene = new Scene(root);
            stage.setTitle("Course Search");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Close the current course review window
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    @FXML
    public void myReviews(ActionEvent actionEvent) {
        try {
            // Close the course search window
            ((Node) actionEvent.getSource()).getScene().getWindow().hide();

            // Open the "My Reviews" scene
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyReviews.fxml"));
            Pane root = fxmlLoader.load();

            Scene scene = new Scene(root);
            stage.setTitle("My Reviews");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert;
        if (Objects.equals(title, "Comment")) {
            alert = new Alert(Alert.AlertType.INFORMATION);
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    static String commentG;

    @FXML
    private void handleDoubleClick() {
        CourseReviewModel selectedReview = reviewTable.getSelectionModel().getSelectedItem();
        commentG = selectedReview.getComments();


        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Comment.fxml"));
            ScrollPane root = fxmlLoader.load();

            Scene scene = new Scene(root);
            stage.setTitle("Comment");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}