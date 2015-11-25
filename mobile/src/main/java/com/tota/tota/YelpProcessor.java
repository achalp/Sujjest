package com.tota.tota;

import android.util.Log;

import com.tota.tota.Entity.Restaurant;
import com.tota.tota.Entity.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aprabhakar on 11/23/15.
 */
public class YelpProcessor {


    public final static String YELP_LIST_OF_RESTAURANTS_END_POINT = "http://www.yelp.com/search";
            //?find_desc=Restaurants
            // &cflt=indpak
            // &find_loc=Issaquah,+WA
            // &start=0
            // &sortby=review_count"
            // ;//&sortby=rating";

    private static final String ID="YELPPROCESSOR";

    public final static String YELP_BIZ_BASE_URL = "http://yelp.com/biz/";

    private final static String CITY="Bellevue";
    private final static String STATE="WA" ;
    private final static String SORTBY="review_count";
    private final static String CUISINE = "indpak";
    private final static String DESC = "Restaurants";

    private String city;
    private String state;
    private String sortBy;
    private String cuisine;
    private String desc;

    private String formURL()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(YELP_LIST_OF_RESTAURANTS_END_POINT)
                .append("?find_desc=")
                .append(desc)
                .append("&cflt=")
                .append(cuisine)
                .append("&find_loc=")
                .append(city)
                .append(",")
                .append(state)
                .append("&sortby=")
                .append(sortBy);

        return sb.toString();

    }

    public YelpProcessor() {
        city=CITY;
        state=STATE;
        sortBy=SORTBY;
        cuisine=CUISINE;
        desc=DESC;
        Log.d(this.ID,"YELP SEARCH URL: "+ this.formURL());

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cusine) {
        this.cuisine = cuisine;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArrayList<Restaurant> getRestaurantsForCityState() throws  IOException

    {

        //Map<String, JSONObject> restaurants = new HashMap<String, JSONObject>();

        ArrayList<Restaurant> restaurantArrayList = new ArrayList<Restaurant>();

        //Get the list of top 10 restaurants

        try {
            Document doc = Jsoup.connect(this.formURL()).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").get();
            int minCount = 0;
            int len;

            Elements address = doc.select("li > div > div > div > address");
            minCount = address.toArray().length;

            Elements biz = doc.select("div > h3 > span > a > span");
            len = biz.toArray().length;
            if (minCount > len) minCount = len;

            Elements phone = doc.select("li > div > div > div > span.biz-phone");
            len = phone.toArray().length;
            if (minCount > len) minCount = len;

            Elements cost = doc.select("div > div > div > div > span > span");
            Log.d("RestaurantsForCityState", "Cost=" + cost.toString());
            len = cost.toArray().length;
            if (minCount > len) minCount = len;

            Elements numReviews = doc.select("div > div > div > div > span.review-count.rating-qualifier");
            len = numReviews.toArray().length;
            if (minCount > len) minCount = len;

            Elements image = doc.select("div > div > div.photo-box.pb-90s > a > img.photo-box-img");
            len = image.toArray().length;
            if (minCount > len) minCount = len;

            Log.d("RestaurantsForCityState", "MinCount " + Integer.toString(minCount));
            Log.d("RestaurantsForCityState", "Biz " + biz.first().text());
            Log.d("RestaurantsForCityState", "Biz Href" + biz.first().parents().first().attr("href"));

            Log.d("RestaurantsForCityState", "address" + address.text());

            Restaurant r = new Restaurant();
            //int i = 0;
            for (int i = 0; i < minCount; i++) {
                Element e = biz.get(i);
                String href = e.parents().first().attr("href");
                String biz_key = href.substring(href.lastIndexOf('/') + 1, href.length());

                r.setBiz(e.text());
                r.setAddress(address.get(i).text());
                r.setHref(href);
                r.setBiz_key(biz_key);
                r.setPhone(phone.get(i).text());
                r.setNumReviews(numReviews.get(i).text());
                r.setImage(image.get(i).attr("src"));

                restaurantArrayList.add(r);

                r = new Restaurant();

            }
            return restaurantArrayList;

        } catch (IOException e) {
            Log.d("RestaurantsForCityState", "Exception:" + e.toString());
            throw e;
        }

    }


    public ArrayList<Review> getReviews(String key) throws JSONException, IOException

    {

        //   Map<String, JSONObject> restaurantReviews = new HashMap<String,JSONObject>();

        //Get the list of top 10 restaurants

        try {
            String url = YELP_BIZ_BASE_URL + key;
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").get();
            int minCount = 0;
            int len;

            Elements reviews = doc.select("li > div > div > div > p");
            minCount = reviews.toArray().length;

            Elements ratings = doc.select("li > div > div > div.review-content > div > div > div.rating-very-large > meta[itemprop=\"ratingValue\"]");
            len = ratings.toArray().length;
            if (minCount > len) minCount = len;


            Log.d("Reviews", "MinCount " + Integer.toString(minCount));
            Log.d("Reviews", "Key: " + key);
            Log.d("Reviews", "first review: " + reviews.first().text());

            Log.d("Reviews", "first rating: " + ratings.first().attr("content"));

            JSONObject reviewsForRestaurant = new JSONObject();
            JSONObject j = new JSONObject();
            JSONArray ja = new JSONArray();

            Review r = new Review();
            ArrayList<Review> reviewArrayList = new ArrayList<Review>();


            //int i = 0;
            for (int i = 0; i < minCount; i++) {

                    Element e = reviews.get(i);

                    //j.put("biz-key", key);
                    r.setReview(reviews.get(i).text());
                    r.setRating(ratings.attr("content"));

                    reviewArrayList.add(r);

                    r = new Review();

            }

            return reviewArrayList;

        } catch (IOException e) {
            Log.d("Reviews", "Exception:" + e.toString());
            throw e;
        }

    }
}
