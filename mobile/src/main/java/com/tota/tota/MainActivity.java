package com.tota.tota;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
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
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;
import com.tota.tota.Entity.Restaurant;
import com.tota.tota.Entity.Review;
import com.tota.tota.Entity.Sentiment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public final static String REST_NAME = "mediterranean-kitchen-bellevue";
    public final static String KIMONO_REVIEWS_API_END_POINT = "https://www.kimonolabs.com/api/ondemand/7kv6gyv0?kimpath2=";
    //"http://www.kimonolabs.com/api/ondemand/3bjrhisw?find_loc=Bellevue%2CWA";

    public static ArrayList<Restaurant> restaurantArrayList = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityFragment ma = new MainActivityFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.container,ma,"main");
        ft.commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Uri u = Uri.fromParts("http","//"+image,"");


                new RequestTask()
                        .execute();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.tota.tota/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.tota.tota/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private class RequestTask extends AsyncTask<String, Restaurant, ArrayList<Restaurant>> {
        // make a request to the specified url

        YelpProcessor yelpProcessor = new YelpProcessor();
        AlchemyProcessor alchemyProcessor = new AlchemyProcessor();

        @Override
        protected ArrayList<Restaurant> doInBackground(String... uri) {
            //Get list of restaurants from Yelp.
            ArrayList<Restaurant> restaurantArrayList = null;
            ArrayList<Restaurant> restaurantArrayList2 = null;
            try {
                restaurantArrayList = yelpProcessor.getRestaurantsForCityState(0);
                //next page
              //  restaurantArrayList2 = yelpProcessor.getRestaurantsForCityState(10);
                //restaurantArrayList.addAll(restaurantArrayList2);
                MainActivity.restaurantArrayList = restaurantArrayList;

            } catch (IOException e) {
                Log.e("Error", e.toString());
                //return "Error: IOExeption retrieving restaurants from Yelp";
            }


            // At this point, the Map restaurants has the first 10 results from yelps city specific page.
            // Key = Restaurants key from provider

            // For every restaurant in 'restaurants' grab the first N (page 1 for now) reviews and
            // populate the Map reviews.
            // key = restaurant key from provider#review number so for eg. restaurant-of-pallino-2#1

            StringBuilder strBuilder = new StringBuilder();
            //  for (Map.Entry<String, JSONObject> m : restaurants.entrySet())
            for (Restaurant r : restaurantArrayList) {

                String key = r.getBiz_key();
                ArrayList<Review> reviewArrayList;
                Sentiment sentiment;
                publishProgress(r);
                try {

                    reviewArrayList = yelpProcessor.getReviews(key);

                    Log.d("Reviews", reviewArrayList.toString());

                    r.setReviews(reviewArrayList);
                    sentiment = alchemyProcessor.getSentiment(key, reviewArrayList);
                    r.setSentiment(sentiment);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Error", "Error: JSONException  retrieving restaurants from Yelp " + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Error", "Error: IOException  retrieving restaurants from Yelp " + e.getMessage());
                }


            }

            return restaurantArrayList;

        }

        @Override
        protected void onProgressUpdate(Restaurant... values) {
            super.onProgressUpdate(values);
            Log.d("Progress",values.toString());

            TextView txt = (TextView) findViewById(R.id.textView);
            TextView txt1 = (TextView) findViewById(R.id.textView1);
            TextView txt2 = (TextView) findViewById(R.id.textView2);


            ImageView imageView =(ImageView) findViewById(R.id.imageView);
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
            Integer curProgress = pb.getProgress();
            int steps = pb.getMax()/MainActivity.restaurantArrayList.size();
            pb.setProgress(curProgress+steps);

            String image = values[0].getImage();
            image = image.substring(2,image.length());

            Uri u = Uri.parse("http:" + "//" + URLDecoder.decode(image));

            Picasso.with(getApplicationContext()).load(u).into(imageView);

            txt.clearComposingText();

            StringBuilder strBuilder = new StringBuilder();

            Restaurant m = values[0];


                String key = m.getBiz_key();
                // JSONObject reviews;
                // JSONObject sentiment;

                ArrayList<Review> reviews;
                Sentiment sentiment;

                sentiment = m.getSentiment();
            strBuilder.append(m.getBiz() + "\n");


            txt.setText(strBuilder.toString());
            txt1.setText(m.getNumReviews());
            txt2.setText(m.getCost());


        }

        @Override
        protected void onPostExecute(ArrayList<Restaurant> restaurantArrayList) {
            super.onPostExecute(restaurantArrayList);


            // return JSON String
            //return jObj;
            int mMixedInt;
            String mType;
            Double mScore = 0.0;
            String maxKey=null;
            Double maxSCore = 0.0;
            for (Restaurant m : restaurantArrayList) {

                String key = m.getBiz_key();
                Sentiment sentiment;
                int mixedInt;
                String type;
                Double score;

                StringBuilder strBuilder = new StringBuilder();
                sentiment = m.getSentiment();

                mixedInt = Integer.parseInt(sentiment.getMixed());
                score = Double.parseDouble(sentiment.getScore());
                type = sentiment.getSentiment();

                if (score > maxSCore) {
                    maxSCore = score;
                    maxKey = key;
                }


                strBuilder.append(m.getBiz() + " "
                        + sentiment.getScore() + " "
                        + sentiment.getSentiment() + " "
                        + sentiment.getMixed() + "\n");
                Log.d("Sentiments", strBuilder.toString());

            }


            Log.d("Sentiment","Max Score and Key " + maxKey +" " + maxSCore);

            RecommendedFragment recommendedFragment = new RecommendedFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.container, recommendedFragment);
//            ft.add(R.id.fragment,recommendedFragment);
          //  ft.hide(MainActivityFragment);
    //        ft.hide(getFragmentManager().)
            ft.addToBackStack("Recommendation");
            ft.commit();


        }


    }
}