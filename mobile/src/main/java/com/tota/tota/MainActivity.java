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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public final static String REST_NAME = "mediterranean-kitchen-bellevue";
    public final static String KIMONO_REVIEWS_API_END_POINT = "https://www.kimonolabs.com/api/ondemand/7kv6gyv0?kimpath2=";
    //"http://www.kimonolabs.com/api/ondemand/3bjrhisw?find_loc=Bellevue%2CWA";

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


    private class RequestTask extends AsyncTask<String, String, Map<String, JSONObject>> {
        // make a request to the specified url

        YelpProcessor yelpProcessor = new YelpProcessor();
        AlchemyProcessor alchemyProcessor = new AlchemyProcessor();

        @Override
        protected Map<String, JSONObject> doInBackground(String... uri) {
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
            try {
                restaurants = yelpProcessor.getRestaurantsForCityState();

            } catch (JSONException e) {
                Log.e("Error", e.toString());
                // return "Error: JSONException  retrieving restaurants from Yelp";
            } catch (IOException e) {
                Log.e("Error", e.toString());
                //return "Error: IOExeption retrieving restaurants from Yelp";
            }


            // At this point, the Map restaurants has the first 10 results from yelps city specific page.
            // Key = Restaurants key from provider
            // Value - JSON object


            // For every restaurant in 'restaurants' grab the first N (page 1 for now) reviews and
            // populate the Map reviews.
            // key = restaurant key from provider#review number so for eg. restaurant-of-pallino-2#1
            // Value = JSON object

            strBuilder = new StringBuilder();
            for (Map.Entry<String, JSONObject> m : restaurants.entrySet()) {

                String key = m.getKey();
                JSONObject reviews;
                JSONObject sentiment;
                try {

                    reviews = yelpProcessor.getReviews(key);
                    Log.d("Reviews", reviews.toString());
                    m.getValue().put("reviews", reviews);
                    sentiment = alchemyProcessor.getSentiment(key, reviews);
                    m.getValue().put("sentiment", sentiment);
                    strBuilder.append(m.getValue().get("biz") + " "
                            + sentiment.getJSONObject("docSentiment").getString("score") + " "
                            + sentiment.getJSONObject("docSentiment").getString("type") + " "
                            + sentiment.getJSONObject("docSentiment").getString("mixed") + "\n");

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

            return restaurants;

            //return strBuilder.toString();
            // return reviews;
        }

        @Override
        protected void onPostExecute(Map<String, JSONObject> response) {
            super.onPostExecute(response);


            // return JSON String
            //return jObj;


            TextView txt = (TextView) findViewById(R.id.textView);
            txt.clearComposingText();
            StringBuilder strBuilder = new StringBuilder();


            for (Map.Entry<String, JSONObject> m : response.entrySet()) {

                String key = m.getKey();
                JSONObject reviews;
                JSONObject sentiment;
                try {

                    reviews = yelpProcessor.getReviews(key);
                    Log.d("Reviews", reviews.toString());
                    m.getValue().put("reviews", reviews);
                    sentiment = m.getValue().getJSONObject("sentiment");
                    strBuilder.append(m.getValue().get("biz") + " "
                            + sentiment.getJSONObject("docSentiment").getString("score") + " "
                            + sentiment.getJSONObject("docSentiment").getString("type") + " "
                            + sentiment.getJSONObject("docSentiment").getString("mixed") + "\n");

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
            txt.setText(strBuilder.toString());


        }


    }
}