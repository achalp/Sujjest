package com.tota.tota.Entity;

/**
 * Created by aprabhakar on 11/24/15.
 */
public class Sentiment {

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getMixed() {
        return mixed;
    }

    public void setMixed(String mixed) {
        this.mixed = mixed;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    private String sentiment;
    private String mixed;
    private String score;

}

