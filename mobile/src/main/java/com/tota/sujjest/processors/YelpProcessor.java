package com.tota.sujjest.processors;

import android.util.Log;

import com.tota.sujjest.Entity.ApplicationState;
import com.tota.sujjest.Entity.Options;
import com.tota.sujjest.Entity.Restaurant;
import com.tota.sujjest.Entity.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by aprabhakar on 11/23/15.
 */
public class YelpProcessor {

    public final static String[] categories = {
            "Afghan" ,
            "African" ,
            "American (New)" ,
            "American (Traditional)" ,
            "Arabian" ,
            "Argentine" ,
            "Armenian" ,
            "Asian Fusion" ,
            "Australian" ,
            "Austrian" ,
            "Bangladeshi" ,
            "Barbeque" ,
            "Basque" ,
            "Belgian" ,
            "Brasseries" ,
            "Brazilian" ,
            "Breakfast & Brunch" ,
            "British" ,
            "Buffets" ,
            "Burgers" ,
            "Burmese" ,
            "Cafes" ,
            "Cafeteria" ,
            "Cajun/Creole" ,
            "Cambodian" ,
            "Caribbean" ,
            "Catalan" ,
            "Cheesesteaks" ,
            "Chicken Shop" ,
            "Chicken Wings" ,
            "Chinese" ,
            "Comfort Food" ,
            "Creperies" ,
            "Cuban" ,
            "Czech" ,
            "Delis" ,
            "Diners" ,
            "Ethiopian" ,
            "Fast Food" ,
            "Filipino" ,
            "Fish & Chips" ,
            "Fondue" ,
            "Food Court" ,
            "Food Stands" ,
            "French" ,
            "Gastropubs" ,
            "German" ,
            "Gluten-Free" ,
            "Greek" ,
            "Halal" ,
            "Hawaiian" ,
            "Himalayan/Nepalese" ,
            "Hot Dogs" ,
            "Hot Pot" ,
            "Hungarian" ,
            "Iberian" ,
            "Indian" ,
            "Indonesian" ,
            "Irish" ,
            "Italian" ,
            "Japanese" ,
            "Korean" ,
            "Kosher" ,
            "Laotian" ,
            "Latin American" ,
            "Live/Raw Food" ,
            "Malaysian" ,
            "Mediterranean" ,
            "Mexican" ,
            "Middle Eastern" ,
            "Modern European" ,
            "Mongolian" ,
            "Moroccan" ,
            "Pakistani" ,
            "Persian/Iranian" ,
            "Peruvian" ,
            "Pizza" ,
            "Polish" ,
            "Portuguese" ,
            "Poutineries" ,
            "Russian" ,
            "Salad" ,
            "Sandwiches" ,
            "Scandinavian" ,
            "Scottish" ,
            "Seafood" ,
            "Singaporean" ,
            "Slovakian" ,
            "Soul Food" ,
            "Soup" ,
            "Southern" ,
            "Spanish" ,
            "Sri Lankan" ,
            "Steakhouses" ,
            "Supper Clubs" ,
            "Sushi Bars" ,
            "Syrian" ,
            "Taiwanese" ,
            "Tapas Bars" ,
            "Tapas/Small Plates" ,
            "Tex-Mex" ,
            "Thai" ,
            "Turkish" ,
            "Ukrainian" ,
            "Uzbek" ,
            "Vegan" ,
            "Vegetarian" ,
            "Vietnamese"};

    public final static String YELP_LIST_OF_RESTAURANTS_END_POINT = "http://www.yelp.com/search";
            //?find_desc=Restaurants
            // &cflt=indpak
            // &find_loc=Issaquah,+WA
            // &start=0
            // &sortby=review_count"
            // ;//&sortby=rating";

    private static final String ID="YELPPROCESSOR";

    public final static String YELP_BIZ_BASE_URL = "http://yelp.com/biz/";

    private final static String CITY="";
    private final static String STATE="" ;
    private final static String SORTBY="review_count";
    private final static String CUISINE = "";
    private final static String DESC = "Restaurants";
    private final static String ATTRS = "";

    private String city;
    private String state;
    private String sortBy;
    private String cuisine;
    private String desc;
    private ApplicationState applicationState;
    private Options options;

    private String formURL()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(YELP_LIST_OF_RESTAURANTS_END_POINT)
                .append("?find_desc=")
                .append(desc)
                .append("&cflt=restaurants")
                .append(cuisine)
                .append("&find_loc=")
                .append(city.trim())
             //   .append(",")
               // .append(state)
                .append("&sortby=")
                .append(sortBy);


        String attrs = "";
        if(this.options.isPrice$()) {
            if (attrs.length() > 0)
                attrs = attrs + ",RestaurantsPriceRange2.1";
            else
                attrs = attrs + "RestaurantsPriceRange2.1";
        }

        if(this.options.isPrice$$()) {
            if(attrs.length() >0 )
            attrs = attrs + ",RestaurantsPriceRange2.2";
            else
                attrs = attrs + "RestaurantsPriceRange2.2";
        }
            if(this.options.isPrice$$$()) {
                if(attrs.length() >0 )
                    attrs = attrs + ",RestaurantsPriceRange2.3";
                else
                    attrs = attrs + "RestaurantsPriceRange2.3";
            }
            if(this.options.isPrice$$$$()) {
                if(attrs.length() >0 )
                    attrs = attrs + ",RestaurantsPriceRange2.4";
                else
                    attrs = attrs + "RestaurantsPriceRange2.4";
            }

if(attrs.length() > 0)
    sb.append("&attrs=").append(attrs);

        String returnval = sb.toString();
        Log.d(ID,"Yelp URL: " + returnval);
        return returnval;

    }

    public YelpProcessor() {


        city=CITY;
        state=STATE;
        sortBy=SORTBY;
        cuisine=CUISINE;
        desc=DESC;

        this.applicationState = ApplicationState.getInstance();
        this.options = this.applicationState.getOptions();

        Log.d(this.ID,"Constructor: Initial  - YELP SEARCH URL: "+ this.formURL());

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

    public ArrayList<Restaurant> getRestaurantsForCityState(int start) throws  IOException

    {

        //Map<String, JSONObject> restaurants = new HashMap<String, JSONObject>();

        ArrayList<Restaurant> restaurantArrayList = new ArrayList<Restaurant>();

        //Get the list of top 10 restaurants

        try {
            Document doc = Jsoup.connect(this.formURL()+"&start="+Integer.toString(start)).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").get();
            int minCount = 0;
            int len;

            Elements address = doc.select("li.regular-search-result > div > div > div > address");
            minCount = address.toArray().length;

            Elements biz = doc.select("div > h3 > span.indexed-biz-name > a > span");
            len = biz.toArray().length;
            if (minCount > len) minCount = len;

            Elements phone = doc.select("li.regular-search-result > div > div > div > span.biz-phone");
            len = phone.toArray().length;
            if (minCount > len) minCount = len;

            Elements cost = doc.select("li.regular-search-result > div > div  > div > div > div > div > span > span.business-attribute.price-range");
            Log.d("RestaurantsForCityState", "Cost=" + cost.toString());
            len = cost.toArray().length;
            if (minCount > len) minCount = len;

            Elements numReviews = doc.select("li > div.natural-search-result > div > div > div >div > div > span.review-count.rating-qualifier");
            len = numReviews.toArray().length;
            if (minCount > len) minCount = len;

            Elements image = doc.select("li > div.natural-search-result > div > div > div > div > div.photo-box.pb-90s > a > img.photo-box-img");
            len = image.toArray().length;
            if (minCount > len) minCount = len;

            Log.d("RestaurantsForCityState", "MinCount " + Integer.toString(minCount));
            Log.d("RestaurantsForCityState", "Biz " + biz.first()==null?biz.first().text():"nothing ");
            Log.d("RestaurantsForCityState", "Biz Href" + biz.first()==null?biz.first().parents().first().attr("href"):"nothing ");

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
                r.setCost(cost.get(i).text());
                r.setPhone(phone.get(i).text());
                r.setNumReviews(numReviews.get(i).text());
                String imgSrc = image.get(i).attr("src");
               imgSrc = imgSrc.replaceFirst("90s","258s");
                r.setImage(imgSrc);


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
            //Log.d("Reviews", "first review: " + reviews.first().text());

           // Log.d("Reviews", "first rating: " + ratings.first().attr("content"));

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
            Log.e(ID, "Exception:" + e.toString());
            throw e;
        }

    }
}
