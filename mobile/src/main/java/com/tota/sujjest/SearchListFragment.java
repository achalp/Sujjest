package com.tota.sujjest;
/**
 * Created by aprabhakar on 11/27/15.
 */

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
import com.tota.sujjest.Entity.Restaurant;
import com.tota.sujjest.adapters.RestaurantArrayAdapter;

import java.util.ArrayList;

public class SearchListFragment extends Fragment {

    private Bundle arguments;
    private View  view;
    private static final String ID="SearchListFragment";
    Restaurant restaurant=null,r2=null,r3=null;
    private ListView mListView;
    private RestaurantArrayAdapter mRestaurantArrayAdapter;
    private Restaurant[] mRestaurantArray;
    private Tracker mTracker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(ID, "Starting onCreateView");

        View view = inflater.inflate(R.layout.fragment_search_list, null);
        this.view = view;
        mListView = (ListView)view.findViewById(R.id.searchList);
        mRestaurantArrayAdapter = new RestaurantArrayAdapter(getActivity().getApplicationContext(),R.layout.fragment_search_list_item, mRestaurantArray);
        mListView.setAdapter(mRestaurantArrayAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(ID, "Item Clicked at position: " + position + " with Id= " + id);
                Log.e(ID, "Item is: " + mListView.getItemAtPosition(position));

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Search Restaurant Clicked")
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
        mTracker.setScreenName("Search List Tab");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
if(args != null ) {
    ArrayList<Restaurant> restaurantArrayList = (ArrayList<Restaurant>) args.get("RestaurantListSorted");
    //   Collections.sort(restaurantArrayList, Restaurant.RestScoreReverseComparator);
    mRestaurantArray = new Restaurant[restaurantArrayList.size()];
    for (int i = 0; i < restaurantArrayList.size(); i++)
        mRestaurantArray[i] = restaurantArrayList.get(i);
}
        else
    mRestaurantArray = new Restaurant[0];


    }

    public void setSearchResults(ArrayList<Restaurant> args)
    {

        if(args != null ) {
            //   Collections.sort(restaurantArrayList, Restaurant.RestScoreReverseComparator);

            mRestaurantArray = new Restaurant[args.size()];
            for (int i = 0; i < args.size(); i++)
                mRestaurantArray[i] = args.get(i);

          //  mRestaurantArrayAdapter.clear();
         //   mRestaurantArrayAdapter.addAll(mRestaurantArray);
            Log.d(ID, "set data for restaurantArray");
            if(mRestaurantArrayAdapter != null) {
                mRestaurantArrayAdapter = new RestaurantArrayAdapter(getActivity().getApplicationContext(), R.layout.fragment_search_list_item, mRestaurantArray);
               mListView.setAdapter(mRestaurantArrayAdapter);
                mListView.requestLayout();

            }
        }
        else {
            Log.e(ID, "setSearchResults called with a null argumnet");
            mRestaurantArray = new Restaurant[0];

            //throw new NullPointerException();
        }

    }
}