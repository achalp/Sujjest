package com.tota.sujjest;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tota.sujjest.Entity.AppStateEnum;
import com.tota.sujjest.Entity.Restaurant;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MapInputActivity.SearchResultsListener {
    public static final String ID = "MainActivity";
    public static AppStateEnum appState = AppStateEnum.HOME_SCREEN;
    public static ArrayList<Restaurant> restaurantArrayList = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private MapInputActivity m_mapInputActivity;
    private SearchFragment m_searchFragment;
    private ProcessFragment m_mainActivityFragment;
    private RecommendedFragment m_recommendedFragment;
    private Menu optionsMenu;
    private Tracker mTracker;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        Log.e(ID, "OnCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        optionsMenu = menu;
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
        return true;
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


        if (id == R.id.action_about) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Show About")
                    .build());

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.aboutTitle);
            alertDialogBuilder.setMessage(R.string.aboutText);
            alertDialogBuilder.setPositiveButton(R.string.dismissTitle, null);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //      Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //      setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(android.R.drawable.arrow_up_float);
        actionBar.setDisplayShowHomeEnabled(true);

        Log.d(ID, "MainActivity; OnCreate");


        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();



        appState = AppStateEnum.HOME_SCREEN;

        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        // Update your UI here.
                        Log.i(ID, "Back Stack Changed Listener called");
                        Log.d(ID, "invalidating menu");
                        invalidateOptionsMenu();

                      /*  Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
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
                        }*/

                    }
                });

//copied from onResume. If doesnt work move it back:
        //  if (m_mapInputActivity == null) {
     /*   MapInputActivity mapInputActivity = new MapInputActivity();
        m_mapInputActivity = mapInputActivity;

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.replace(R.id.container, m_mapInputActivity, MapInputActivity.ID); //was add
        ft.commit();
*/

        if(savedInstanceState == null) {
            m_searchFragment = new SearchFragment();

            FragmentTransaction ft = getFragmentManager().beginTransaction();

            ft.replace(R.id.container, m_searchFragment, SearchFragment.ID); //was add
            ft.commit();
        }
  //  commented on Jan 8 - looks like not needed
  //    getFragmentManager().executePendingTransactions();

        Log.d(ID, "backstack count: " + getFragmentManager().getBackStackEntryCount());


    }


    @Override
    public void onBackPressed() {

        Log.d(ID, "Back button Pressed");
        Log.d(ID, "Backstack count: " + getFragmentManager().getBackStackEntryCount());

        // set the next state
        switch (appState) {
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

        if (getFragmentManager().getBackStackEntryCount() != 0) {

            Log.d(ID, "about to popstack");
/*
                    MapInputActivity mapInputActivity = new MapInputActivity();
                    m_mapInputActivity = mapInputActivity;
                    FragmentTransaction ft = getFragmentManager().beginTransaction();

                    ft.replace(R.id.container, m_mapInputActivity, "MapInput"); //was add

                    // ft.addToBackStack("Main");
                    //  ft.add(R.id.map,mapFragment,"map");

                    ft.commit();*/
            getFragmentManager().popBackStack();
//            getFragmentManager().popBackStack("MapInput", FragmentManager.POP_BACK_STACK_INCLUSIVE);


        } else
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
        Log.d(ID, "onPause");

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(ID, "onResume starting");
        Log.i(ID, "Setting screen name: " + "Home Screen");
        mTracker.setScreenName("Home Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSearchResultsAvailable(ArrayList<Restaurant> restaurants) {
        Log.d(ID,"onSearchResultsAvailable called: Must Re Fr eSh Data");
        //start to communicate with fragments and tell them you have new data for them.
        // at the moment, this is really for telling the list of search results fragment that data is available.
        restaurantArrayList = restaurants;
        if(m_searchFragment != null)
        m_searchFragment.refreshSearchResults(restaurants);
    }

    @Override
    public void onAnalyzeReviews(Bundle args) {
        ProcessFragment ma = new ProcessFragment();

          if(restaurantArrayList != null)
            ma.setmRestaurantList(restaurantArrayList);
        ma.setArguments(args);


        FragmentTransaction ft = getFragmentManager().beginTransaction();
      //ft.remove(m_searchFragment);
        ft.replace(R.id.container, ma, "main"); //last change was .replace
        ft.addToBackStack("MapInput");

        //  ft.addToBackStack("main");
        ft.commit();
    }
}