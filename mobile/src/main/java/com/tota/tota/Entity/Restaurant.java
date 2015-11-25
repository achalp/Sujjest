package com.tota.tota.Entity;

import java.util.ArrayList;

/**
 * Created by aprabhakar on 11/24/15.
 */
public class Restaurant {

    public Restaurant() {
        //nothing
    }


    public String getBiz_key() {
        return biz_key;
    }

    public void setBiz_key(String biz_key) {
        this.biz_key = biz_key;
    }

    public String getBiz() {
        return biz;
    }

    public void setBiz(String biz) {
        this.biz = biz;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(String numReviews) {
        this.numReviews = numReviews;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String biz_key;
    private String biz;
    private String address;
    private String href;

    private String cost;
    private String phone;
    private String numReviews;
    private String image;

    public ArrayList<Review> getReviews() {

        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    private ArrayList<Review> reviews;
    private Sentiment sentiment;






}
