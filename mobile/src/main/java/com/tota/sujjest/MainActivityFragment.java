package com.tota.sujjest;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tota.sujjest.Entity.Restaurant;
import com.tota.sujjest.Entity.Review;
import com.tota.sujjest.Entity.Sentiment;

import org.json.JSONException;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static final String ID="MainActivityFragment";
    private ViewGroup vg;
    private View view;
    private Bundle savedInstance;
    private RequestTask requestTask;
    private String what, where;
    private  RecommendationFragment recommendationFragment;

    public ArrayList<Restaurant> getRestaurantArrayList() {
        return restaurantArrayList;
    }

    protected ArrayList<Restaurant> restaurantArrayList = null;
    protected ArrayList<Restaurant> restaurantArrayList2 = null;



    public MainActivityFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        what = (String) b.get("what");
        where = (String) b.get("where");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.vg = container;
        this.view = inflater.inflate(R.layout.fragment_main, container, false);
        this.savedInstance = savedInstanceState;
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);
        pb.setProgress(0);
        return view;


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(ID, "Started");

        ProgressBar pb = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        pb.setProgress(0);
        this.requestTask = new RequestTask();

        this.requestTask.execute();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(ID,"Paused");
        if(this.requestTask != null)
        {
            this.requestTask.cancel(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(ID, "Stopped");

        if(this.requestTask != null)
        {
            this.requestTask.cancel(true);
        }
    }


    private class RequestTask extends AsyncTask<String, Restaurant, ArrayList<Restaurant>> {
        // make a request to the specified url

        private static final String ID="MainAct Result Task";
        YelpProcessor yelpProcessor = new YelpProcessor();

        AlchemyProcessor alchemyProcessor = new AlchemyProcessor();

        @Override
        protected ArrayList<Restaurant> doInBackground(String... uri) {
            //Get list of restaurants from Yelp.

            try {
                yelpProcessor.setCity(where);
                yelpProcessor.setDesc(what);

                restaurantArrayList = yelpProcessor.getRestaurantsForCityState(0);
                //next page
               restaurantArrayList2 = yelpProcessor.getRestaurantsForCityState(10);

                restaurantArrayList.addAll(restaurantArrayList2);

            } catch (IOException e) {
                Log.e("Error", e.toString());
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                 alertDialogBuilder.setTitle("Unable to access source websites");
                alertDialogBuilder.setMessage("Didn't go as expected. Most probably a network issue. " + e.getMessage());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

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

                if (isCancelled()) {Log.d(ID,"cancelled so breaking");break;}

                String key = r.getBiz_key();
                ArrayList<Review> reviewArrayList;
                Sentiment sentiment;
                publishProgress(r);
                try {

                    reviewArrayList = yelpProcessor.getReviews(key);

                  //  Log.d("Reviews", reviewArrayList.toString());

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

            TextView txt = (TextView) view.findViewById(R.id.textView);
            TextView txt1 = (TextView) view.findViewById(R.id.textView1);
            TextView txt2 = (TextView) view.findViewById(R.id.textView2);


            ImageView imageView =(ImageView) view.findViewById(R.id.imageView);
            ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);
            Integer curProgress = pb.getProgress();
            int steps = pb.getMax()/restaurantArrayList.size();
            pb.setProgress(curProgress+steps);

            String image = values[0].getImage();
            image = image.substring(2,image.length());

            Uri u = Uri.parse("http:" + "//" + URLDecoder.decode(image));

            Picasso.with(getActivity()).load(u).transform(new RoundedCornersTransformation(10, 1)).into(imageView);

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
            Restaurant r = null;

            Collections.sort(restaurantArrayList, Restaurant.RestScoreComparator);
//            Log.d("Last",restaurantArrayList.get(restaurantArrayList.size()-1).getSentiment().getScore());
            Bundle b = new Bundle();
            //get TOP 3
            for(int i=restaurantArrayList.size()-1,j=1;i>=0;i--,j++)
            {
                r = restaurantArrayList.get(i);
                Sentiment sentiment =  r.getSentiment();

                StringBuilder strBuilder = new StringBuilder();
                if(sentiment != null) {
                    strBuilder.append(r.getBiz() + " "
                            + sentiment.getScore() + " "
                            + sentiment.getSentiment() + " "
                            + sentiment.getMixed() + "\n");
                    Log.d("Sentiments", strBuilder.toString());
                }
                else
                    Log.e(ID,"Sentiment is null for Restaurant: " +r.getBiz());

                        b.putSerializable("MostRecommendedRestaurant-" + j, r);

            }

            //get Bottom 3
            for(int i=0,j=1;i<restaurantArrayList.size();i++,j++)
            {
                r = restaurantArrayList.get(i);
                Sentiment sentiment =  r.getSentiment();
                StringBuilder strBuilder = new StringBuilder();
                if(sentiment != null) {
                    strBuilder.append(r.getBiz() + " "
                            + sentiment.getScore() + " "
                            + sentiment.getSentiment() + " "
                            + sentiment.getMixed() + "\n");
                    Log.d("Sentiments", strBuilder.toString());
                }
                else
                    Log.e(ID, "Sentiment is null for Restaurant: " + r.getBiz());


                b.putSerializable("LeastRecommendedRestaurant-"+j,r);

            }



//            RecommendedFragment recommendedFragment = new RecommendedFragment();
  //          recommendedFragment.setArguments(b);

           // if(recommendationFragment == null)
             recommendationFragment = new RecommendationFragment();
            //else //else detach so you can set the arguments again
           // {
            //    getFragmentManager().beginTransaction().detach(recommendationFragment).commit();
           // }
            recommendationFragment.setRecommendations(b);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(MainActivityFragment.this);
            ft.replace(R.id.container, recommendationFragment, "Recommendation");

               ft.addToBackStack("Recommendation");
               // quite important..otherwise the back button behaves weird after the second back buton press.
               // Recommendation fragment will remain visible and will overlay the mapInputActivity
            ft.commit();
            MainActivity.appState = AppStateEnum.RECOMMENDATION_SCREEN;


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(ID,"Cancelled");
        }

        @Override
        protected void onCancelled(ArrayList<Restaurant> restaurants) {
            super.onCancelled(restaurants);
            Log.d(ID, "Cancelled with Results");

        }
    }
}
