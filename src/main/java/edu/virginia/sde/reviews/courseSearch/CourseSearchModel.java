package edu.virginia.sde.reviews.courseSearch;

import edu.virginia.sde.reviews.db.DatabaseDriver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.SQLException;

public class CourseSearchModel {
    private DatabaseDriver dbDriver;
    Connection connection;

    public CourseSearchModel() {
        try{
            dbDriver = DatabaseDriver.getInstance();
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }



    public ObservableList<Course> getAllCourses() throws SQLException {
        return FXCollections.observableArrayList(dbDriver.getAllCourses());
    }

    public ObservableList<Course> findCoursesByCriteria(String subject, Integer number, String title) throws SQLException {
        return FXCollections.observableArrayList(dbDriver.findCoursesByCriteria(subject, number, title));
    }

    public void addCourse(String subject, int number, String title) throws SQLException, InvalidCourseException {
        dbDriver.addCourse(subject, number, title);
        //dbDriver.commit();
    }

    public boolean doesCourseExist(String subject, int number, String title){
        boolean bool = false;
        try {
            bool = dbDriver.doesCourseExist(subject, number, title);
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return bool;
    }
}
