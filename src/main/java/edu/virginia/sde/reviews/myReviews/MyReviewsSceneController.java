package edu.virginia.sde.reviews.myReviews;


import edu.virginia.sde.reviews.courseSearch.Course;
import edu.virginia.sde.reviews.courseSearch.CourseSearchController;
import edu.virginia.sde.reviews.db.DatabaseDriver;
import edu.virginia.sde.reviews.login.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.ResourceBundle;

public class MyReviewsSceneController implements Initializable {
    private DatabaseDriver dbDriver;
    private Connection connection;
    private String username = LoginController.loggedInUsername;
    private int globalCourseId;


    public MyReviewsSceneController() {
        try {
            dbDriver = DatabaseDriver.getInstance();
            connection = dbDriver.getConnection();
        } catch (SQLException e) {
            showAlert("Connection Unsuccessful: ", e.getMessage());
            System.exit(1);
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

    @FXML
    private TableView<myReviewsModel> reviewTable;

    @FXML
    private TableColumn<myReviewsModel, String> subjectColumn;
    @FXML
    private TableColumn<myReviewsModel, Integer> numberColumn;
    @FXML
    private TableColumn<myReviewsModel, Double> ratingColumn;


    ObservableList<myReviewsModel> reviewsObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int userID;
        try {
            userID = dbDriver.getUserIdByUsername(username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


      String query = "SELECT c.Subject, c.Number, r.Rating, r.CourseID FROM Reviews r JOIN Courses c ON r.CourseID = c.CourseID WHERE r.UserID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            // Set the parameter before executing the query
            ps.setInt(1, userID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                String subject = rs.getString("Subject");
                Integer courseNumber = rs.getInt("Number");
                Integer rating = rs.getInt("Rating");
                Integer courseId = rs.getInt("CourseID");

                    reviewsObservableList.add(new myReviewsModel(subject, courseNumber, rating, courseId));
                }

//                System.out.println("Number of reviews fetched: " + reviewsObservableList.size());

                subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
                numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
                ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));


                reviewTable.setItems(reviewsObservableList);


            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


        @FXML
    private void handleBackAction(javafx.event.ActionEvent event) {
        try {
            // Close the current window
            Node source = (Node) event.getSource();
            Stage oldStage = (Stage) source.getScene().getWindow();
            oldStage.close();

            // Load and display the Course Search scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseSearch.fxml")); // Ensure the path is correct
            Pane root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Course Search");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    @FXML
    private void handleDoubleClick() {
        myReviewsModel selectedCourse = reviewTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
//            courseId = selectedCourse.getCourseId(); // Assuming getPrimaryId() is a method in Course class
            openCourseReviewScene(selectedCourse);
        }
    }

    private void openCourseReviewScene(myReviewsModel course) {
        try {
            // Set the selected course ID globally
            CourseSearchController.setCoursePrimaryId(course.getCourseId());

            // Load the CourseReviews scene
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CourseReviews.fxml"));
            Pane root = fxmlLoader.load();

            Scene scene = new Scene(root);
            stage.setTitle("Reviews for " + course.toString());
            stage.setScene(scene);
            stage.show();

            // Close the current course search window
            Stage currentStage = (Stage) reviewTable.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}