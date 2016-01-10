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

import com.tota.sujjest.Entity.ApplicationState;
import com.tota.sujjest.Entity.Options;
import com.tota.sujjest.Entity.Restaurant;
import com.tota.sujjest.adapters.RestaurantArrayAdapter;

import java.util.ArrayList;

public class LeastRecommendedFragment extends Fragment {

    private Bundle arguments;
    private View  view;
    private static final String ID="LeastRecommendFragment";
    private Restaurant restaurant=null,r2=null,r3=null;
    private ListView mListView;
    private RestaurantArrayAdapter mRestaurantArrayAdapter;
    private Restaurant[] mRestaurantArray;
    private ApplicationState applicationState;
    private Options options;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.applicationState = ApplicationState.getInstance();
        this.options = applicationState.getOptions();


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(ID, "Starting onCreateView");

        View view = inflater.inflate(R.layout.fragment_recommended_list, null);
        this.view = view;

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.applicationState = ApplicationState.getInstance();
        this.options = applicationState.getOptions();

        mListView = (ListView) view.findViewById(R.id.recommendationList);
        mRestaurantArrayAdapter = new RestaurantArrayAdapter(getActivity().getApplicationContext(), R.layout.fragment_recommended_list_item, mRestaurantArray);
        mListView.setAdapter(mRestaurantArrayAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(ID, "Item Clicked at position: " + position + " with Id= " + id);
                Log.d(ID, "Item is: " + mListView.getItemAtPosition(position));
                String uriString = "http://yelp.com/biz/" + ((Restaurant) mListView.getItemAtPosition(position)).getBiz_key();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
            }


        });

        mListView.setOnItemSelectedListener(new ListView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(ID, "Item Selected at position: " + position + " with Id= " + id);
                Log.d(ID, "Item is: " + mListView.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Restaurant r = (Restaurant)args.get("LeastRecommendedRestaurant-1");
        r2 = (Restaurant)args.get("LeastRecommendedRestaurant-2");
        r3 = (Restaurant)args.get("LeastRecommendedRestaurant-3");
       // mRestaurantArray = new Restaurant[3];
       // mRestaurantArray[0] = r;
       // mRestaurantArray[1] = r2;
       // mRestaurantArray[2]=r3;
        ArrayList<Restaurant> restaurantArrayList = (ArrayList<Restaurant>) args.get("RestaurantListSorted");
       // Collections.sort(restaurantArrayList, Restaurant.RestScoreReverseComparator);
        int showN = ApplicationState.getInstance().getOptions().getShowN();

        mRestaurantArray = new Restaurant[showN];
        for(int i=0, j=0;i<=restaurantArrayList.size()-1 && j< showN && restaurantArrayList.size()> showN;i++,j++)
            mRestaurantArray[j] = restaurantArrayList.get(i);


    }


}