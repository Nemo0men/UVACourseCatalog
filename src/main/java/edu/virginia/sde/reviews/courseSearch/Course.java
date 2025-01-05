package edu.virginia.sde.reviews.courseSearch;

public class Course {
    private int courseId;
    private String subject;
    private int number;
    private String title;
    private double avgRating;

    public Course(int courseId, String subject, int number, String title, double avgRating) {
        this.courseId = courseId;
        this.subject = subject;
        this.number = number;
        this.title = title;
        this.avgRating = avgRating;
    }

    // Getters and Setters
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    @Override
    public String toString() {
        return subject + " " + number + ": " + title;
    }
}
