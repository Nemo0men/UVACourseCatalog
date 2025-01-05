package edu.virginia.sde.reviews.myReviews;

public class myReviewsModel {
    private String subject;
    private int number;
    private double rating;
    private int courseId;

    // Constructor with all parameters
    public myReviewsModel(String subject, int number, double rating, int courseId) {
        this.subject = subject;
        this.number = number;
        this.rating = rating;
        this.courseId = courseId;
    }

    // Constructor with 'courseId' as an optional parameter
    public myReviewsModel(String subject, int number, double rating) {
        this(subject, number, rating, -1); // Set 'courseId' to -1 by default to indicate it is not set
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "Subject: " + subject + ", Number: " + number + ", Rating: " + rating +
                (courseId != -1 ? ", CourseId: " + courseId : "");
    }
}
