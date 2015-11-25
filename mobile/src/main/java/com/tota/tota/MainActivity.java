package com.tota.tota;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tota.tota.Entity.Restaurant;
import com.tota.tota.Entity.Review;
import com.tota.tota.Entity.Sentiment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public final static String REST_NAME = "mediterranean-kitchen-bellevue";
    public final static String KIMONO_REVIEWS_API_END_POINT = "https://www.kimonolabs.com/api/ondemand/7kv6gyv0?kimpath2=";
    //"http://www.kimonolabs.com/api/ondemand/3bjrhisw?find_loc=Bellevue%2CWA";

    public static ArrayList<Restaurant> restaurantArrayList=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RequestTask()
                        .execute();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class RequestTask extends AsyncTask<String, String, ArrayList<Restaurant>> {
        // make a request to the specified url

        YelpProcessor yelpProcessor = new YelpProcessor();
        AlchemyProcessor alchemyProcessor = new AlchemyProcessor();

        @Override
        protected ArrayList<Restaurant> doInBackground(String... uri) {
            HttpURLConnection urlConnection;

            URL url;
            String response = null;

            String inputStr = null;
            String alchemyInputStr = null;
            //s  String reviews = null;
            StringBuilder responseStrBuilder;
            responseStrBuilder = new StringBuilder();
            StringBuilder strBuilder;
            JSONObject jObj = null;

            //Get list of restaurants from Yelp.
            Map<String, JSONObject> restaurants = null;
            ArrayList<Restaurant> restaurantArrayList = null;
            try {
                restaurantArrayList = yelpProcessor.getRestaurantsForCityState();

            }  catch (IOException e) {
                Log.e("Error", e.toString());
                //return "Error: IOExeption retrieving restaurants from Yelp";
            }


            // At this point, the Map restaurants has the first 10 results from yelps city specific page.
            // Key = Restaurants key from provider

            // For every restaurant in 'restaurants' grab the first N (page 1 for now) reviews and
            // populate the Map reviews.
            // key = restaurant key from provider#review number so for eg. restaurant-of-pallino-2#1

            strBuilder = new StringBuilder();
          //  for (Map.Entry<String, JSONObject> m : restaurants.entrySet())
            for( Restaurant r: restaurantArrayList)
            {

                String key = r.getBiz_key();
                ArrayList<Review> reviewArrayList;
                Sentiment sentiment;

                JSONObject reviews;

                //JSONObject sentiment;
                try {

                    reviewArrayList = yelpProcessor.getReviews(key);

                    Log.d("Reviews", reviewArrayList.toString());

                    r.setReviews(reviewArrayList);
                    sentiment = alchemyProcessor.getSentiment(key, reviewArrayList);
                    r.setSentiment(sentiment);

//                    strBuilder.append(m.getValue().get("biz") + " "
//                            + sentiment.getJSONObject("docSentiment").getString("score") + " "
//                            + sentiment.getJSONObject("docSentiment").getString("type") + " "
//                            + sentiment.getJSONObject("docSentiment").getString("mixed") + "\n");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Error", "Error: JSONException  retrieving restaurants from Yelp " + e.getMessage());
                    //   break;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Error", "Error: IOException  retrieving restaurants from Yelp " + e.getMessage());
                    //break;
                }


            }

            return restaurantArrayList;

            //return strBuilder.toString();
            // return reviews;
        }

        @Override
        protected void onPostExecute(ArrayList<Restaurant> restaurantArrayList) {
            super.onPostExecute(restaurantArrayList);


            // return JSON String
            //return jObj;

            MainActivity.restaurantArrayList = restaurantArrayList;

            TextView txt = (TextView) findViewById(R.id.textView);
            txt.clearComposingText();
            StringBuilder strBuilder = new StringBuilder();


            for (Restaurant m : restaurantArrayList) {

                String key = m.getBiz_key();
               // JSONObject reviews;
               // JSONObject sentiment;

                ArrayList<Review> reviews;
                Sentiment sentiment;

                    sentiment = m.getSentiment();
                    strBuilder.append(m.getBiz() + " "
                            + sentiment.getScore() + " "
                            + sentiment.getSentiment() + " "
                            + sentiment.getMixed() + "\n");

            }
            txt.setText(strBuilder.toString());


        }


    }
}