package com.tota.sujjest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tota.sujjest.Entity.Restaurant;

import java.util.ArrayList;

/**
 * Created by aprabhakar on 12/31/15.
 */
public class SearchFragment extends Fragment {

    public static final String ID = "SearchFragment";
    private MyAdapter mAdapter;
    private ViewPager mPager;
    private RecommendedFragment mRecommendedFragment;
    private Bundle mBundle;
    private ArrayList<Restaurant> restaurantArrayList;
    private Tracker mTracker;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.menu_main,menu);
        Log.d(ID, "in OnCreateOptionsMenu");
        Log.d(ID, "Current Item:" + this.mPager.getCurrentItem());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(ID, "in onPrepareOptionsMenu");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            Log.d(ID, "Saved instance - restoring data in OnCreate");
            restaurantArrayList = (ArrayList<Restaurant>) savedInstanceState.get("restaurantList");
            //  mAdapter = (MyAdapter) savedInstanceState.get("MyAdapter");
        }
        //  else
        //    mAdapter = new MyAdapter(getChildFragmentManager());


        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();


    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        //   mBundle = args;
    }

    public void setSearchResults(ArrayList<Restaurant> args) {
        //  super.setArguments(args);
        restaurantArrayList = new ArrayList<Restaurant>();
        for (Restaurant r : args)
            restaurantArrayList.add(r);
    }

    public void refreshSearchResults(ArrayList<Restaurant> args) {
        setSearchResults(args);
//        if(mAdapter)

        SearchListFragment searchListFragment = (SearchListFragment) mAdapter.getRegisteredFragment(1);
        searchListFragment.setSearchResults(args);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(ID, "Starting onCreateView");

        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.fragment_search, container, false);


        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tab_layout_search);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Note: If adapter moves from onCreateView to OnCreate then the optionsMenu is not rendedered after back pressed event.
        mAdapter = new MyAdapter(getChildFragmentManager());

        mPager = (ViewPager) v.findViewById(R.id.searchPager);


        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(0);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                Log.d(ID, "ViewPager ka onPageSelected was called");
                // refreshSearchResults(restaurantArrayList);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
                Log.d(ID, "Page Selected");


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(ID, "Page RE - Selected");

            }
        });

        tabLayout.setupWithViewPager(mPager);
        tabLayout.getTabAt(0).setText("Map");
        tabLayout.getTabAt(1).setText("List");

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(ID, "OnResume");

        mTracker.setScreenName("Search Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(ID, "OnPause");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(ID, "OnStop");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(ID, "OnStart");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(ID, "OnSaveInstanceState");
        outState.putSerializable("RestaurantList", restaurantArrayList);
        //  outState.putSerializable("MyAdapter", mAdapter);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(ID, "OnDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(ID, "OnDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(ID, "OnDestroyView");
    }

    private class MyAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public MyAdapter(FragmentManager fm) {
            super(fm);
            Log.d(ID, "MyAdapter: Constructed");

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {

            Log.d(ID, "MyAdapter: getItem called");

            if (position == 0) {
                return new MapInputActivity();
            }
            if (position == 1) {
                return new SearchListFragment();
            } else {
                Log.e(ID, "MyAdapter: Position returned is not 0 or 1");
                return null;
            }
        }
    }
}
