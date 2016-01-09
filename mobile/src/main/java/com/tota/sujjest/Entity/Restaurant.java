package com.tota.sujjest.Entity;

import android.location.Address;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by aprabhakar on 11/24/15.
 */
public class Restaurant implements Serializable {

    public Restaurant() {
        //nothing
         hasSentiment=false;
       hasReviews=false;
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

    public boolean isHasSentiment() {
        return hasSentiment;
    }

    public void setHasSentiment(boolean hasSentiment) {
        this.hasSentiment = hasSentiment;
    }

    public boolean isHasReviews() {
        return hasReviews;
    }

    public void setHasReviews(boolean hasReviews) {
        this.hasReviews = hasReviews;
    }

    private boolean hasSentiment;
    private boolean hasReviews;


    public Address getGoogleAddress() {
        return googleAddress;
    }

    public void setGoogleAddress(Address googleAddress) {
        this.googleAddress = googleAddress;
    }

    private Address googleAddress;

    public ArrayList<Review> getReviews() {

        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {

        this.reviews = reviews;
        if(reviews!=null)
            this.setHasReviews(false);
        else
            this.setHasReviews(true);


    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {

        this.sentiment = sentiment;
        if(sentiment!=null)
            this.setHasSentiment(true);
        else
            this.setHasSentiment(false);
    }

    private ArrayList<Review> reviews;
    private Sentiment sentiment;

    public static Comparator<Restaurant> RestScoreComparator = new Comparator<Restaurant>() {
        @Override
        public int compare(Restaurant restaurant, Restaurant t1) {
            Sentiment s1, s2;
            Double d1, d2;
            if (t1 != null && restaurant != null) {
                s1 = restaurant.getSentiment();
                s2 = t1.getSentiment();
                if(s1 !=null && s2 !=null) {
                    d1 = Double.parseDouble(s1.getScore());
                    d2 = Double.parseDouble(s2.getScore());

                    if (d1 > d2) return 1;
                    if (d1 < d2) return -1;
                    else return 0;

                }

            }
            return 0;
        }
    };

    public static Comparator<Restaurant> RestScoreReverseComparator = new Comparator<Restaurant>() {
        @Override
        public int compare(Restaurant restaurant, Restaurant t1) {
            Sentiment s1, s2;
            Double d1, d2;
            if (t1 != null && restaurant != null) {
                s1 = restaurant.getSentiment();
                s2 = t1.getSentiment();
                if(s1 !=null && s2 !=null) {
                    d1 = Double.parseDouble(s1.getScore());
                    d2 = Double.parseDouble(s2.getScore());

                    if (d1 < d2) return 1;
                    if (d1 > d2) return -1;
                    else return 0;

                }

            }
            return 0;
        }
    };


}
