package edu.virginia.sde.reviews.courseReview;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CourseReviewApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CourseReviews.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Course Review");
        stage.setScene(scene);
        stage.show();
    }
}
