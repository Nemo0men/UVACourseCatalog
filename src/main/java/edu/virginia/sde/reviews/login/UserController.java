package edu.virginia.sde.reviews.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class UserController implements Initializable {
    @FXML
    private Label username;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void getUser(String user) {
        username.setText("Welcome " + user + "!");
    }

    public void signout(ActionEvent event) throws IOException {
        ((Node)event.getSource()).getScene().getWindow().hide();

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        Pane root = fxmlLoader.load(Objects.requireNonNull(getClass().getResource("Login.fxml")).openStream());


        Scene scene = new Scene(root);
        stage.setTitle("Course Search");
        stage.setScene(scene);
        stage.show();
    }
}
