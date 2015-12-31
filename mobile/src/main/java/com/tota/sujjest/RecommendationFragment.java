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

          if(position == 0) {
              if(rec == null) {
                  rec = new RecommendedFragment();
               }
              rec.setArguments(mBundle);

              return rec;
          }
            else
          {
              if(lrec == null)
                lrec = new LeastRecommendedFragment();

              lrec.setArguments(mBundle);
              return lrec;
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
        mBundle = args;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       Log.d(ID, "Starting onCreateView");
        View v = inflater.inflate(R.layout.fragment_recommended,container,false);
        mAdapter = new MyAdapter(getFragmentManager());

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Recommended"));
        tabLayout.addTab(tabLayout.newTab().setText("Not Recommended"));
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

        return v;

    }
}
