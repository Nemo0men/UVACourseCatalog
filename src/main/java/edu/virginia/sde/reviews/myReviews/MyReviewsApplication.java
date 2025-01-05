package edu.virginia.sde.reviews.myReviews;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MyReviewsApplication extends Application {
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML document for My Reviews Scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyReviewsScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());  // Ensure that MyReviewsScene.fxml exists in the correct directory

        // Set the scene on the stage
        stage.setTitle("My Reviews");
        stage.setScene(scene);

        // Display the primary stage
        stage.show();
    }
}
