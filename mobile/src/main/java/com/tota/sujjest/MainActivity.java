package com.tota.sujjest;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tota.sujjest.Entity.Restaurant;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{

    public static AppStateEnum   appState = AppStateEnum.HOME_SCREEN;

    public static final String ID ="MainActivity";
    public static ArrayList<Restaurant> restaurantArrayList = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private MapInputActivity m_mapInputActivity;
    private MainActivityFragment m_mainActivityFragment;
    private RecommendedFragment m_recommendedFragment;
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(android.R.drawable.arrow_up_float);
        actionBar.setDisplayShowHomeEnabled(true);

        Log.d(ID, "MainActivity; OnCreate");

            appState = AppStateEnum.MAPVIEW_SCREEN;

        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        // Update your UI here.
                        Log.i(ID, "Back Stack Changed Listener called");

                        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
                        switch (appState)

                        {
                            case HOME_SCREEN:
                            case MAPVIEW_SCREEN: {
                                MenuItem menuItem = myToolbar.getMenu().findItem(R.id.action_search);
                                if (menuItem != null)
                                    menuItem.setVisible(true);

                                break;
                            }
                            default: {
                                MenuItem menuItem = myToolbar.getMenu().findItem(R.id.action_search);
                                if (menuItem != null)
                                    menuItem.setVisible(false);
                            }
                        }

                    }
                });



    }







    @Override
    public void onBackPressed() {

      Log.d(ID, "Back button Pressed");
        Log.d(ID, "Backstack count: " + getFragmentManager().getBackStackEntryCount());

        // set the next state
        switch (appState)
        {
            case RECOMMENDATION_SCREEN:
            case REREIVING_REVIEWS_SCREEN:
                appState = AppStateEnum.MAPVIEW_SCREEN;
                break;
            case MAPVIEW_SCREEN:
                appState = AppStateEnum.HOME_SCREEN;
                break;
            default:
                appState = AppStateEnum.HOME_SCREEN;
        }

                if(getFragmentManager().getBackStackEntryCount() != 0) {

                    Log.d(ID, "about to popstack");
/*
                    MapInputActivity mapInputActivity = new MapInputActivity();
                    m_mapInputActivity = mapInputActivity;
                    FragmentTransaction ft = getFragmentManager().beginTransaction();

                    ft.replace(R.id.container, m_mapInputActivity, "MapInput"); //was add

                    // ft.addToBackStack("Main");
                    //  ft.add(R.id.map,mapFragment,"map");

                    ft.commit();*/

                getFragmentManager().popBackStack("MapInput",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                  else
                      super.onBackPressed();


        }


    @Override
    public void onStart() {
        super.onStart();

        Log.d(ID, "onStart");

    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(ID, "onStop");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(ID,"onPause");

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(ID, "onResume starting");
      //  if (m_mapInputActivity == null) {
            MapInputActivity mapInputActivity = new MapInputActivity();
            m_mapInputActivity = mapInputActivity;
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            ft.replace(R.id.container, m_mapInputActivity, "MapInput"); //was add

            // ft.addToBackStack("Main");
            //  ft.add(R.id.map,mapFragment,"map");

            ft.commit();
              getFragmentManager().executePendingTransactions();

            Log.d(ID,"backstack count: " + getFragmentManager().getBackStackEntryCount());


    }
}