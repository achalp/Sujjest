package com.tota.sujjest;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.tota.sujjest.Entity.AppStateEnum;
import com.tota.sujjest.Entity.ApplicationState;
import com.tota.sujjest.Entity.Restaurant;
import com.tota.sujjest.Entity.Review;
import com.tota.sujjest.Entity.Sentiment;
import com.tota.sujjest.processors.AlchemyProcessor;
import com.tota.sujjest.processors.TotaProcessor;
import com.tota.sujjest.processors.YelpProcessor;

import org.json.JSONException;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProcessFragment extends Fragment {

    public static final String ID="ProcessFragment";
    protected ArrayList<Restaurant> restaurantArrayList = null;
    protected ArrayList<Restaurant> restaurantArrayList2 = null;
    private ViewGroup vg;
    private View view;
    private Bundle savedInstance;
    private RequestTask requestTask;
    private String what, where;
    private  RecommendationFragment recommendationFragment;
    private ProgressBar mProgressBar;
    private int mProgressPercent;
    private int mLastRestaurantProcessedPosition;
    private Tracker mTracker;
    private ArrayList<Restaurant> mRestaurantList;

    public ProcessFragment() {
    }

    public ArrayList<Restaurant> getmRestaurantList() {
        return mRestaurantList;
    }

    public void setmRestaurantList(ArrayList<Restaurant> mRestaurantList) {
        this.mRestaurantList = mRestaurantList;
    }

    public ArrayList<Restaurant> getRestaurantArrayList() {
        return restaurantArrayList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        Bundle b = getArguments();
        what = (String) b.get("what");
        where = (String) b.get("where");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
   /*     Log.e(ID, "OnCreateOptionsMenu");
        inflater.inflate(R.menu.menu_home, menu);
      //  optionsMenu = menu;*/

         super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("ProgressPercent", mProgressPercent);
        outState.putInt("LastRestaurantProcessedPosition", mLastRestaurantProcessedPosition);
        outState.putString("what",this.what);
        outState.putString("Where", this.where);
        outState.putSerializable("RestaurantList", restaurantArrayList);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(ID,"OnCreateView");

        setHasOptionsMenu(false);

        this.vg = container;
        this.view = inflater.inflate(R.layout.fragment_main, container, false);
        this.savedInstance = savedInstanceState;

        if(savedInstanceState !=null)
        {
            Log.d(ID,"Restoring saved Instance");
            // Fragment was restored. Either a back button press or a configuration change
            // Restore state if any and go ahead.
            String savedWhat=null, savedWhere=null;
            int savedProgressPercent, savedLastRestaurantProcessedPosition;
            ArrayList<Restaurant> savedRestaurantList;

            savedWhat = savedInstanceState.getString("what");
            savedWhere = savedInstanceState.getString("where");

            savedProgressPercent= savedInstanceState.getInt("ProgressPercent");
            savedLastRestaurantProcessedPosition = savedInstanceState.getInt("LastRestaurantProcessedPosition");

            savedRestaurantList = (ArrayList<Restaurant>) savedInstanceState.get("RestaurantList");

            if(savedWhat !=null)
                this.what =  savedWhat;

            if(savedWhere != null)
                this.where = savedWhere;

            mProgressPercent = savedProgressPercent;
            mLastRestaurantProcessedPosition = savedLastRestaurantProcessedPosition;

            if(savedRestaurantList != null)
            mRestaurantList = savedRestaurantList;

        }
        else
        {
            //no state to set. This is a new view with no state.
            Log.d(ID,"No saved Instance");
            mProgressPercent=0;
            mLastRestaurantProcessedPosition=-1;
        }


        return view;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar  = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setProgress(mProgressPercent);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(ID, "Started");

        //set app state;
        ApplicationState.getInstance().setAppState(AppStateEnum.REREIVING_REVIEWS_SCREEN);

        this.requestTask = new RequestTask();
        this.requestTask.execute();

    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName("Process Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

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

        private static final String ID="ProcessFragmentTask";
        YelpProcessor yelpProcessor = new YelpProcessor();

        AlchemyProcessor alchemyProcessor = new AlchemyProcessor();
        TotaProcessor totaProcessor = new TotaProcessor();

        @Override
        protected ArrayList<Restaurant> doInBackground(String... uri) {
            //Get list of restaurants from Yelp.

            if(mRestaurantList==null) {
                try {
                    yelpProcessor.setCity(where);
                    yelpProcessor.setDesc(what);

                    restaurantArrayList = yelpProcessor.getRestaurantsForCityState(0);
                    //next page
                    restaurantArrayList2 = yelpProcessor.getRestaurantsForCityState(10);

                    restaurantArrayList.addAll(restaurantArrayList2);
                    mRestaurantList = restaurantArrayList;
                } catch (IOException e) {
                    Log.e("Error", e.toString());
                    return null;
                    //return "Error: IOExeption retrieving restaurants from Yelp";
                }
            }
            else {
                Log.d(ID,"mRestaurantList is not null. So probably a restored instance or set by parent activity/fragment");
                restaurantArrayList=mRestaurantList;
            }

            // At this point, the Map restaurants has the first 10 or 20 results from yelps city specific page.
            // Key = Restaurants key from provider

            // For every restaurant in 'restaurants' grab the first N (page 1 for now) reviews and
            // populate the Map reviews.
            // key = restaurant key from provider#review number so for eg. restaurant-of-pallino-2#1

            StringBuilder strBuilder = new StringBuilder();
            //  for (Map.Entry<String, JSONObject> m : restaurants.entrySet())
            int start=0;
            if(mLastRestaurantProcessedPosition >0 ) {
                start = mLastRestaurantProcessedPosition;
                Log.d(ID,"Starting at Index: " + start);
            }
            for(int i=start;i<restaurantArrayList.size();i++)
            {
                Restaurant r = restaurantArrayList.get(i);
          //  for (Restaurant r : restaurantArrayList) {

                ArrayList<Review> reviewArrayList;
                Sentiment sentiment;

                if (isCancelled()) {Log.d(ID,"cancelled so breaking");break;}

                String key = r.getBiz_key();

                publishProgress(r);

                try {

                    reviewArrayList = yelpProcessor.getReviews(key);

                  //  Log.d("Reviews", reviewArrayList.toString());

                    r.setReviews(reviewArrayList);
                    sentiment = alchemyProcessor.getSentiment(key, reviewArrayList);
                    //sentiment = totaProcessor.getSentiment(key, reviewArrayList);
                    r.setSentiment(sentiment);
                    mLastRestaurantProcessedPosition = i;

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
            mProgressPercent = curProgress+steps;

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
            b.putSerializable("RestaurantListSorted",restaurantArrayList);





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
               ft.remove(ProcessFragment.this);
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
