package com.tota.sujjest;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tota.sujjest.Entity.ApplicationState;
import com.tota.sujjest.Entity.Restaurant;

/**
 * Created by aprabhakar on 12/31/15.
 */
public class RecommendationFragment extends Fragment {

    public static final String ID ="RecommendationFragment";
    private MyAdapter mAdapter;
    private ViewPager mPager;
    private RecommendedFragment mRecommendedFragment;
    private Bundle mBundle;
    private Tracker mTracker;
    private Menu optionsMenu;


    private  class MyAdapter extends FragmentStatePagerAdapter {
        LeastRecommendedFragment lrec;
        RecommendedFragment rec;

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                if (rec == null) {
                    rec = new RecommendedFragment();
                    Log.d(ID,"Setting Arguments for RecommendedFragment");
                    rec.setArguments(mBundle);

                } else {
                    Log.d(ID, "Re-using fragment");
                    rec.setArguments(mBundle);

                }

                return rec;
            }
            if (position == 1) {
                if (lrec == null)
                    lrec = new LeastRecommendedFragment();

                lrec.setArguments(mBundle);
                return lrec;
            } else {
                Log.e(ID,"Position returned is not 0 or 1" );
                return null;
            }
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        if(savedInstanceState !=null) {
            Log.d(ID,"Saved instance - restoring data in OnCreate");
            mBundle = (Bundle) savedInstanceState.getBundle("RestaurantListBundle");
            //  mAdapter = (MyAdapter) savedInstanceState.get("MyAdapter");
        }

        mAdapter = new MyAdapter(getChildFragmentManager());

    }


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
     //   mBundle = args;
    }

    public void setRecommendations(Bundle args) {
      //  super.setArguments(args);
        mBundle = args;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       Log.d(ID, "Starting onCreateView");
        View v = inflater.inflate(R.layout.fragment_recommended,container,false);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
       tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        mPager = (ViewPager) v.findViewById(R.id.recommendationPager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(0);

        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.setupWithViewPager(mPager);
        tabLayout.getTabAt(0).setText("Recommended");
        tabLayout.getTabAt(1).setText("Not Recommended");

        return v;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
        Log.e(ID, "OnCreateOptionsMenu");
        inflater.inflate(R.menu.menu_recommendation, menu);
        optionsMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(ApplicationState.getInstance().getOptions().getShowN() == 5)
            menu.findItem(R.id.action_showN5).setChecked(true);
        else
            menu.findItem(R.id.action_showN5).setChecked(false);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_settings) {
            return true;
        }*/

        if (id == R.id.action_showN5) {
            if (item.isChecked()) {
                ApplicationState.getInstance().getOptions().setShowN(3);
                item.setChecked(false);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Show Three Results")
                        .build());
               RecommendedFragment recommendedFragment= (RecommendedFragment) mAdapter.getItem(0);
                recommendedFragment.setArguments(mBundle);
                recommendedFragment.refreshOnShowNOptionChanged();

                LeastRecommendedFragment leastRecommendedFragment = (LeastRecommendedFragment) mAdapter.getItem(1);
                leastRecommendedFragment.setArguments(mBundle);
                leastRecommendedFragment.refreshOnShowNOptionChanged();




            } else {
                ApplicationState.getInstance().getOptions().setShowN(5);

                item.setChecked(true);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Show Five Results")
                        .build());

                RecommendedFragment recommendedFragment= (RecommendedFragment) mAdapter.getItem(0);
                recommendedFragment.setArguments(mBundle);
                recommendedFragment.refreshOnShowNOptionChanged();

                LeastRecommendedFragment leastRecommendedFragment = (LeastRecommendedFragment) mAdapter.getItem(1);
                leastRecommendedFragment.setArguments(mBundle);
                leastRecommendedFragment.refreshOnShowNOptionChanged();


            }


        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(ID, "OnResume");

        mTracker.setScreenName("Recommendation Screen");
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
        outState.putBundle("RestaurantListBundle", mBundle);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(ID,"OnDetach");
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
}
