package edu.virginia.sde.reviews.login;

import edu.virginia.sde.reviews.db.SceneController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    public LoginModel loginModel = new LoginModel();



    public static String loggedInUsername;



    @FXML
    private Label connected;

    @FXML
    private TextField userInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private Label errorMsg;

    private SceneController sceneController;

    private MainApp mainApp;

    void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    public void setSceneController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (loginModel.isDatabaseConnected()) {
            connected.setText("Connected");
        } else {
            connected.setText("Disconnected");
        }
    }


    public void login(ActionEvent event) {
        try {
            if (loginModel.loginExists(userInput.getText(), passwordInput.getText())) {
                errorMsg.setText("Login Successful");
                loggedInUsername = userInput.getText();

                ((Node)event.getSource()).getScene().getWindow().hide();
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader();
                Pane root = fxmlLoader.load(getClass().getResource("CourseSearch.fxml").openStream());

                Scene scene = new Scene(root);
                stage.setTitle("Username and Password Registration");
                stage.setScene(scene);
                stage.show();

            } else {
                errorMsg.setText("Username/Password is incorrect.");
            }
        } catch (SQLException e) {
            errorMsg.setText("Username/Password is incorrect.");;
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void navigateToRegistration(ActionEvent event) {
        try {
            ((Node)event.getSource()).getScene().getWindow().hide();

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            Pane root = fxmlLoader.load(getClass().getResource("Registeration.fxml").openStream());

            Scene scene = new Scene(root);
            stage.setTitle("Username and Password Registration");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void close(ActionEvent event) {
        Platform.exit();
    }
}