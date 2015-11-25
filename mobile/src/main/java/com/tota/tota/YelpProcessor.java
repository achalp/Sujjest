package com.tota.tota;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aprabhakar on 11/23/15.
 */
public class YelpProcessor {

    public final static String YELP_LIST_OF_RESTAURANTS_END_POINT = "http://www.yelp.com/search?find_desc=Restaurants&cflt=indpak&find_loc=Bellevue,+WA&start=0&sortby=review_count";//&sortby=rating";
    public final static String YELP_BIZ_BASE_URL = "http://yelp.com/biz/";

    public Map<String, JSONObject> getRestaurantsForCityState() throws JSONException, IOException

    {

        Map<String, JSONObject> restaurants = new HashMap<String, JSONObject>();

        //Get the list of top 10 restaurants

        try {
            Document doc = Jsoup.connect(YELP_LIST_OF_RESTAURANTS_END_POINT).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").get();
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

            JSONObject j = new JSONObject();
            //int i = 0;
            for (int i = 0; i < minCount; i++) {
                try {
                    Element e = biz.get(i);
                    String href = e.parents().first().attr("href");
                    String biz_key = href.substring(href.lastIndexOf('/') + 1, href.length());
                    j.put("biz", e.text());
                    j.put("address", address.get(i).text());
                    j.put("href", href);
                    j.put("biz-key", biz_key);
                    j.put("phone", phone.get(i).text());
                    //   j.put("cost", cost.get(i).text());
                    j.put("numReviews", numReviews.get(i).text());
                    j.put("image", image.get(i).attr("src"));
                    //i++;

                    restaurants.put(biz_key, j);

                    //j=null;
                    j = new JSONObject();

                } catch (JSONException ex) {
                    Log.e("RestaurantsForCityState", ex.getMessage());
                    throw ex;
                }

            }
            return restaurants;

        } catch (IOException e) {
            Log.d("RestaurantsForCityState", "Exception:" + e.toString());
            throw e;
        }

    }


    public JSONObject getReviews(String key) throws JSONException, IOException

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
            Log.d("Reviews", "review: " + reviews.first().text());

            Log.d("Reviews", "rating: " + ratings.first().attr("content"));

            JSONObject reviewsForRestaurant = new JSONObject();
            JSONObject j = new JSONObject();
            JSONArray ja = new JSONArray();
            //int i = 0;
            for (int i = 0; i < minCount; i++) {
                try {
                    Element e = reviews.get(i);

                    //j.put("biz-key", key);
                    j.put("review", reviews.get(i).text());
                    j.put("rating", ratings.attr("content"));

                    ja.put(j);
                    //restaurantReviews.put(key+"#"+Integer.toString(i),j);

                    //j=null;
                    j = new JSONObject();

                } catch (JSONException ex) {
                    Log.e("Reviews", ex.getMessage());
                    throw ex;
                }

            }
            reviewsForRestaurant.put("biz-key", key);
            reviewsForRestaurant.put("reviews", ja);

            return reviewsForRestaurant;

        } catch (IOException e) {
            Log.d("Reviews", "Exception:" + e.toString());
            throw e;
        }

    }
}
