package com.tota.tota.Entity;

/**
 * Created by aprabhakar on 11/24/15.
 */
public class Review {

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(String dateEntered) {
        this.dateEntered = dateEntered;
    }

    private String review;
    private String rating;
    private String dateEntered;
}
