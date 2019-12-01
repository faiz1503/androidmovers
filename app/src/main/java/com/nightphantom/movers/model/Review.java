package com.nightphantom.movers.model;

public class Review {
    String rating, review;

    public Review(){

    }

    public Review(String rating, String review) {
        this.rating = rating;
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
