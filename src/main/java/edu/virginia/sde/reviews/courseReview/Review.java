package edu.virginia.sde.reviews.courseReview;

import java.sql.Timestamp;

public class Review {
    private int reviewId;
    private String username;
    private int courseId;
    private double rating;
    private String comment;
    private Timestamp timestamp;

    // Constructor
    public Review(int reviewId, int courseId, double rating, String comment, Timestamp timestamp, String username) {
        this.reviewId = reviewId;
        this.courseId = courseId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
        this.username = username;
    }

    // Getters and Setters
    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("Review by %s: %d stars. Comment: %s", username, rating, comment);
    }
}
