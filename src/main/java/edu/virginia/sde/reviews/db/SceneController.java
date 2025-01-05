package edu.virginia.sde.reviews.db;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController{
    private Stage stage;
    private Scene scene;

    public SceneController(Stage stage) {
        this.stage = stage;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        this.stage.setScene(scene);
        this.stage.show();
    }

    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoginScene() {
        loadScene("Login.fxml");
    }

    public void showRegistrationScene() {
        loadScene("Registration.fxml");
    }

    public void showCourseSearchScene() {
        loadScene("CourseSearch.fxml");
    }

    public void showCourseReviewsScene() {
        loadScene("CourseReviews.fxml");
    }

    public void showMyReviewsScene() {
        loadScene("MyReviewsScene.fxml");
    }


    /*@Override
    public void start(Stage primaryStage) throws IOException {
        stg = primaryStage;

        primaryStage.setResizable(false); //user cant change the size of the screen
        FXMLLoader fxmlLoader = new FXMLLoader(showLoginScene());
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
        }

        public void changeScene(String fxml) throws IOException{
            FXMLLoader fxmlLoader = new FXMLLoader(CourseReviewApplication.class.getResource(fxml));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
//        Login loginControl = (Login)fxmlLoader.getController();
//        if (fxml.equals("writeReview.fxml")) {
//
//            Menu menuControl = fxmlLoader.getController();
//            menuControl.setStudent(loginControl.getStudent());
//        }

            stg.setTitle("Class Reviews");
            stg.setScene(scene);
            stg.show();
        }*/

        /*public static void run() {
            DatabaseManager db = new DatabaseManager();
            launch();
        }*/
    }
