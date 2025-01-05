package edu.virginia.sde.reviews.courseSearch;

import edu.virginia.sde.reviews.db.DatabaseDriver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CourseSearchController {
    public CourseSearchModel searchModel;
    public static int courseId;


    public static void setCoursePrimaryId(int primaryId) {
        courseId = primaryId;
    }

    public static int getCoursePrimaryId() {
        return courseId;
    }




    @FXML
    private TableView<Course> tableView;
    @FXML
    private TextField subjectField, numberField, titleField, searchSubject, searchNumber, searchTitle;
    @FXML
    private Button searchButton;
    private Course selectedCourse;

    private DatabaseDriver dbDriver;

    //private CourseSearchModel model;


//    public void setMainApp(MainApp mainApp) {
//        this.mainApp = mainApp;
//    }


    public void initialize() {
        searchModel = new CourseSearchModel();
        try {
            dbDriver = DatabaseDriver.getInstance();
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage());
        }
        setupTableView();
        loadCourses();
    }

    private void setupTableView() {
        TableColumn<Course, Number> courseIdColumn = new TableColumn<>("Course ID");
        TableColumn<Course, String> subjectColumn = new TableColumn<>("Subject");
        TableColumn<Course, Number> numberColumn = new TableColumn<>("Number");
        TableColumn<Course, String> titleColumn = new TableColumn<>("Title");
        TableColumn<Course, Double> avgRatingColumn = new TableColumn<>("Average Rating");

    }

    private void loadCourses() {
        ObservableList<Course> courses = FXCollections.observableArrayList();
        try {
            courses.addAll(dbDriver.getAllCourses());
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to fetch courses: " + e.getMessage());
        }
        tableView.setItems(courses);
//        try {
//            dbDriver.disconnect();
        //       } catch (SQLException e) {
//            showAlert("Database Error", e.getMessage());
        //       }

    }

    @FXML
    private void handleSearch() {
        try {
            Integer number = searchNumber.getText().isEmpty() ? null : Integer.parseInt(searchNumber.getText());
            List<Course> courses = searchModel.findCoursesByCriteria(searchSubject.getText(), number, searchTitle.getText());
            tableView.setItems((ObservableList<Course>) courses);
        } catch (SQLException e) {
            showAlert("Search Error", "Failed to search for courses: " + e.getMessage());
        }
    }

    @FXML
    private void handleDoubleClick() {
        Course selectedCourse = tableView.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            courseId = selectedCourse.getCourseId(); // Assuming getPrimaryId() is a method in Course class
            openCourseReviewScene(selectedCourse);
        }
    }

    private void openCourseReviewScene(Course course) {
        try {
            // Set the selected course ID globally
//            CourseReviewsController.setCoursePrimaryId(course.getCourseId());

            // Load the CourseReviews scene
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CourseReviews.fxml"));
            Pane root = fxmlLoader.load();

            Scene scene = new Scene(root);
            stage.setTitle("Reviews for " + course.toString());
            stage.setScene(scene);
            stage.show();

            // Close the current course search window
            Stage currentStage = (Stage) tableView.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    private void showAlert(String title, String content) {
        Alert alert;
        if (title == "Success!") {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }
        else {
            alert = new Alert(Alert.AlertType.ERROR);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String validateInputs(String subject, String number, String title) {
        StringBuilder errors = new StringBuilder();

        // Check for null or empty fields
        if (subject == null || subject.isEmpty()) {
            errors.append("Subject must be provided.\n");
        } else if (!subject.matches("[A-Z]{2,4}")) {
            errors.append("Subject must be 2 to 4 uppercase letters.\n");
        }

        if (number == null || number.isEmpty()) {
            errors.append("Number must be provided.\n");
        } else if (!number.matches("\\d{4}")) {
            errors.append("Number must be a 4-digit integer.\n");
        }

        if (title == null || title.isEmpty()) {
            errors.append("Title must be provided.\n");
        } else if (title.length() > 50) {
            errors.append("Title must be at most 50 characters.\n");
        }

        return errors.toString();
    }

    @FXML
    public void handleAddCourse() {
        String errorMessages = validateInputs(subjectField.getText().toUpperCase(), numberField.getText(), titleField.getText());
        if (!errorMessages.isEmpty()) {
            showAlert("Input Validation Error", errorMessages);
        }
        String subject = subjectField.getText().toUpperCase();
        int number = Integer.parseInt(numberField.getText());
        String title = titleField.getText();
        try {
            dbDriver.addCourse(subject, number, title);
            dbDriver.commit();
            showAlert("Success!", "Course Added Successfully");
            loadCourses();

        } catch (SQLException e) {
            try {
                dbDriver.rollback();  // Attempt to rollback if there is an SQL exception
            } catch (SQLException rollbackEx) {
                showAlert("Critical Error", "Failed to rollback transaction: " + rollbackEx.getMessage());
            }
            showAlert("Unable to Add Course", "That course already exists");
        } catch (InvalidCourseException e) {
            showAlert("Validation Error", e.getMessage());
        }
    }


    public void SignOut(ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            Pane root = fxmlLoader.load(getClass().getResource("Login.fxml").openStream());

            Scene scene = new Scene(root);
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    public Course getSelectedCourse() {
        return this.selectedCourse;
    }


    @FXML
    public void myReviews(ActionEvent actionEvent) {
        try {
            // Close the course search window
            ((Node) actionEvent.getSource()).getScene().getWindow().hide();

            // Open the "My Reviews" scene
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyReviewsScene.fxml"));
            Pane root = fxmlLoader.load();

            Scene scene = new Scene(root);
            stage.setTitle("My Reviews");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
