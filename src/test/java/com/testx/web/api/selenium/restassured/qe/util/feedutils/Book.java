
package com.testx.web.api.selenium.restassured.qe.util.feedutils;
import lombok.ToString;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "book")
@ToString
public class Book {
    private int id;
    private String email;
    private String timestamp;
    private int rating;
    private int reviewId;
    private String review;

    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlElement
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @XmlElement
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @XmlElement
    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    @XmlElement
    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}

