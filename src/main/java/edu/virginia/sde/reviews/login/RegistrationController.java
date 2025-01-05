package edu.virginia.sde.reviews.login;

import edu.virginia.sde.reviews.db.DatabaseDriver;
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

public class RegistrationController implements Initializable {
    public RegistrationModel registrationModel = new RegistrationModel();

    @FXML
    private TextField registeringUsername;

    @FXML
    private TextField registeringPassword;

    @FXML
    private Label registrationError;
    private MainApp mainApp;
    DatabaseDriver dbDriver;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    public void register(ActionEvent event) {
        try {
            if (registrationModel.userExists(registeringUsername.getText())) {
                registrationError.setText("This Username is Taken.");
            }
            else {
                registrationError.setText(registrationModel.registerUser(registeringUsername.getText(), registeringPassword.getText()));
                dbDriver = DatabaseDriver.getInstance();
                dbDriver.addUser(registeringUsername.getText(), registeringPassword.getText());
                dbDriver.commit();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


        public void returnToLogin(ActionEvent event) {
        try {
            ((Node)event.getSource()).getScene().getWindow().hide();

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



}
