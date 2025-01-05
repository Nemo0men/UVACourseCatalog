package edu.virginia.sde.reviews.courseReview;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

import static edu.virginia.sde.reviews.courseReview.CourseReviewsController.commentG;

public class CommentController implements Initializable {

    @FXML
    private Label commentTextArea;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        commentTextArea.setWrapText(true);
        commentTextArea.setMaxWidth(400);
        commentTextArea.setText(commentG);

    }
}
