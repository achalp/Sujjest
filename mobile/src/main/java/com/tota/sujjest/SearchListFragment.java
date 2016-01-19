package com.tota.sujjest;
/**
 * Created by aprabhakar on 11/27/15.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tota.sujjest.Entity.Restaurant;
import com.tota.sujjest.adapters.RestaurantArrayAdapter;

import java.util.ArrayList;

public class SearchListFragment extends Fragment {

    private static final String ID="SearchListFragment";
    Restaurant restaurant=null,r2=null,r3=null;
    private Bundle arguments;
    private View view;
    private ListView mListView;
    private RestaurantArrayAdapter mRestaurantArrayAdapter;
    private Restaurant[] mRestaurantArray;
    private Tracker mTracker;
    private ArrayList<Restaurant> m_restaurantArrayList;
    private Activity activity;
    private RequestDataListener requestDataListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(ID, "onAttach");

        try {
            requestDataListener = (RequestDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RequestDataListener");
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(ID, "in OnCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(ID, "in onPrepareOptionsMenu");

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_restaurantArrayList = new ArrayList<Restaurant>();


        setHasOptionsMenu(true);

        Log.d(ID, "OnCreate Starting");
        activity = getActivity();


        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        if(savedInstanceState != null )
        {
            Log.d(ID,"Restoring State:");
            m_restaurantArrayList = (ArrayList<Restaurant>) savedInstanceState.get("m_restaurantArrayList");
        }
        mRestaurantArrayAdapter = new RestaurantArrayAdapter(activity, R.layout.fragment_search_list_item, m_restaurantArrayList);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(ID, "OnSaveInstanceState");
        outState.putSerializable("m_restaurantArrayList", m_restaurantArrayList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(ID, "Starting onCreateView");

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_search_list, null);

        if(savedInstanceState !=null) {
            //    m_restaurantArrayList = (ArrayList<Restaurant>) savedInstanceState.get("m_restaurantArrayList");
            // setSearchResults(m_restaurantArrayList);
        } else {
            //  if(requestDataListener != null)
            //    m_restaurantArrayList = requestDataListener.onRequestData(this.ID);
        }


        this.view = view;
        mListView = (ListView)view.findViewById(R.id.searchList);
        //  if(mRestaurantArrayAdapter==null)
        //     mRestaurantArrayAdapter = new RestaurantArrayAdapter(activity,R.layout.fragment_search_list_item, m_restaurantArrayList);
        mListView.setAdapter(mRestaurantArrayAdapter);

        //      mRestaurantArrayAdapter.clear();
        //       mRestaurantArrayAdapter.addAll(m_restaurantArrayList);
        //       mRestaurantArrayAdapter.notifyDataSetChanged();


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
        if (args != null) {
            //    m_restaurantArrayList = (ArrayList<Restaurant>) args.get("RestaurantListSorted");
            //   Collections.sort(m_restaurantArrayList, Restaurant.RestScoreReverseComparator);
 /*   mRestaurantArray = new Restaurant[m_restaurantArrayList.size()];
    for (int i = 0; i < m_restaurantArrayList.size(); i++)
        mRestaurantArray[i] = m_restaurantArrayList.get(i);
}
        else
    mRestaurantArray = new Restaurant[0];
*/

        }
    }

    public void setSearchResults(ArrayList<Restaurant> args)
    {

        if(args != null ) {
            // m_restaurantArrayList = args;

           /* mRestaurantArray = new Restaurant[args.size()];
            for (int i = 0; i < args.size(); i++)
                mRestaurantArray[i] = args.get(i);
*/
          //  mRestaurantArrayAdapter.clear();
         //   mRestaurantArrayAdapter.addAll(mRestaurantArray);
            Log.d(ID, "set data for restaurantArray");
            if (mRestaurantArrayAdapter != null) {
                // mRestaurantArrayAdapter = new RestaurantArrayAdapter(activity, R.layout.fragment_search_list_item, mRestaurantArray);
                mRestaurantArrayAdapter.clear();
                mRestaurantArrayAdapter.addAll(args);

                //mListView.setAdapter(mRestaurantArrayAdapter);
                mRestaurantArrayAdapter.notifyDataSetChanged();
                //  mListView.requestLayout();
                // mListView.invalidate();

            } else {
                //   mRestaurantArrayAdapter = new RestaurantArrayAdapter(activity, R.layout.fragment_search_list_item, m_restaurantArrayList);
                //   mListView.setAdapter(mRestaurantArrayAdapter);
                //     mListView.requestLayout();
                // mListView.invalidate();
            }
            //store a reference for later saving.
            m_restaurantArrayList = args;
        }
        else {
            Log.e(ID, "setSearchResults called with a null argumnet");
            //mRestaurantArray = new Restaurant[0];

            //throw new NullPointerException();
        }

    }


}