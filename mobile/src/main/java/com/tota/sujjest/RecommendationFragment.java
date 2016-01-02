package com.tota.sujjest;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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



    private  class MyAdapter extends FragmentPagerAdapter {
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
        mAdapter = new MyAdapter(getChildFragmentManager());

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
       tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        mPager = (ViewPager) v.findViewById(R.id.recommendationPager);
        mPager.setAdapter(mAdapter);

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
    public void onResume() {
        super.onResume();
        Log.d(ID, "OnResume");

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
