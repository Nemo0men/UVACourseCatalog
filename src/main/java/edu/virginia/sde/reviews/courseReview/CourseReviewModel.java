package edu.virginia.sde.reviews.courseReview;

import java.sql.Timestamp;

public class CourseReviewModel {
    private Integer reviewID; // ReviewID as the primary key
    private Integer userID;   // Foreign key reference to User
    private Integer courseID; // Foreign key reference to Course
    private Integer rating;
    private String comments;
    private Timestamp timestamp;

    private String username;

    public CourseReviewModel(Integer reviewID, Integer userID, Integer courseID, Integer rating, String comments, Timestamp timestamp) {
        this.reviewID = reviewID;
        this.userID = userID;
        this.courseID = courseID;
        this.rating = rating;
        this.comments = comments;
        this.timestamp = timestamp;
    }

    public Integer getReviewID() {
        return reviewID;
    }

    public void setReviewID(Integer reviewID) {
        this.reviewID = reviewID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getCourseID() {
        return courseID;
    }

    public void setCourseID(Integer courseID) {
        this.courseID = courseID;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }
}
