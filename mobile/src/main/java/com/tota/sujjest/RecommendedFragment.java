package com.tota.sujjest;
/**
 * Created by aprabhakar on 11/27/15.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tota.sujjest.Entity.ApplicationState;
import com.tota.sujjest.Entity.Options;
import com.tota.sujjest.Entity.Restaurant;
import com.tota.sujjest.Entity.Sentiment;
import com.tota.sujjest.adapters.RestaurantArrayAdapter;

import java.util.ArrayList;

public class RecommendedFragment extends Fragment {

    private static final String ID="RecommendedFragment";
    private ListView mListView;
    private RestaurantArrayAdapter mRestaurantArrayAdapter;
    private Restaurant[] mRestaurantArray;
    private Tracker mTracker;
    private ApplicationState applicationState;
    private Options options;
    private ArrayList<Restaurant> restaurantArrayList;
    private Activity activity;
    private ArrayList<Restaurant> mRestaurantArrayListTopN;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        this.applicationState = ApplicationState.getInstance();
        this.options = applicationState.getOptions();
        this.activity = getActivity();

        if(savedInstanceState != null) {
            Log.d(ID,"Restoring State");
            mRestaurantArray = (Restaurant[]) savedInstanceState.get("RestaurantArray");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(ID, "Starting onCreateView");

        View view = inflater.inflate(R.layout.fragment_recommended_list, null);
        mListView = (ListView)view.findViewById(R.id.recommendationList);
        if(mRestaurantArray == null) {
            Log.e(ID,"Restaurant Array null in OnCreateView: Why?");
            mRestaurantArray = new Restaurant[0];
        }
        mRestaurantArrayAdapter = new RestaurantArrayAdapter(getActivity().getApplicationContext(), R.layout.fragment_recommended_list_item, mRestaurantArrayListTopN);
        mListView.setAdapter(mRestaurantArrayAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(ID, "Item Clicked at position: " + position + " with Id= " + id);
                Log.e(ID, "Item is: " + mListView.getItemAtPosition(position));

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Recommended Restaurant Clicked")
                        .setLabel(((Restaurant) mListView.getItemAtPosition(position)).getBiz_key())
                        .build());

                String uriString = "http://yelp.com/biz/" + ((Restaurant) mListView.getItemAtPosition(position)).getBiz_key();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
            }


        });

        mListView.setOnItemSelectedListener(new ListView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(ID, "Item Selected at position: " + position + " with Id= " + id);
                Log.e(ID, "Item is: " + mListView.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;


    }


    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName("Recommended Tab");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public void setArguments(Bundle args) {
       // super.setArguments(args);
        restaurantArrayList = (ArrayList<Restaurant>) args.get("RestaurantListSorted");
        mRestaurantArrayListTopN = new ArrayList<Restaurant>();
        int size = ApplicationState.getInstance().getOptions().getShowN();

        //mRestaurantArray = new Restaurant[ApplicationState.getInstance().getOptions().getShowN()];
        for (int i = restaurantArrayList.size() - 1, j = 0; i >= 0 && j < ApplicationState.getInstance().getOptions().getShowN(); i--) {
            //  mRestaurantArray[j] = restaurantArrayList.get(i);
            Sentiment sentiment = restaurantArrayList.get(i).getSentiment();
            if (sentiment != null && sentiment.getSentiment() != null && "positive".equals(sentiment.getSentiment())) {
                mRestaurantArrayListTopN.add(restaurantArrayList.get(i));
                j++;
            }
        }

    }

    public void refreshOnShowNOptionChanged()
    {

        //   mRestaurantArrayAdapter = new RestaurantArrayAdapter(activity, R.layout.fragment_recommended_list_item, mRestaurantArray);
        mRestaurantArrayAdapter = new RestaurantArrayAdapter(activity, R.layout.fragment_recommended_list_item, mRestaurantArrayListTopN);
        mListView.setAdapter(mRestaurantArrayAdapter);
        mListView.requestLayout();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("RestaurantArray", mRestaurantArrayListTopN);
    }
}