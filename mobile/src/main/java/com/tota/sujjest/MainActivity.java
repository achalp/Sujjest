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
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        Log.d("Main", "MainActivity; OnCreate");

            appState = AppStateEnum.MAPVIEW_SCREEN;

        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        // Update your UI here.
                        Log.i(ID, "Back Stack Changed Listener called");

                        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
                        switch(appState)

                        {
                            case HOME_SCREEN :
                            case MAPVIEW_SCREEN:
                            {
                                MenuItem menuItem =  myToolbar.getMenu().findItem(R.id.action_search);
                                if(menuItem !=null)
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

    public void setRefreshActionButtonState(final boolean refreshing) {

        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.action_search);
            if (refreshItem != null) {
                if (refreshing) {
                    Log.e(ID,"setting refresh view");
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    Log.e(ID,"unsetting refresh view");
                    refreshItem.setActionView(null);
                }
            }
            else
                Log.e(ID,"refreshItem menuitem is null");
        }
        else
            Log.e(ID,"optionsMenu is null");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       Log.e(ID,"OnCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        optionsMenu = menu;

        return super.onCreateOptionsMenu(menu);
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

        if (id == R.id.action_search) {
            CardView cardView = (CardView) findViewById(R.id.searchCard);
            FrameLayout fragment = (FrameLayout) findViewById(R.id.container);
            if( cardView!= null && fragment != null )
                if(cardView.getVisibility() == View.VISIBLE)
                {
                    //do nothing
                   cardView.setVisibility(View.GONE);
                    fragment.invalidate();
                }
                else
                {
                    //show
                   cardView.setVisibility(View.VISIBLE);
                    fragment.invalidate();

                }
             return true;
        }

        return super.onOptionsItemSelected(item);
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
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }

        }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(ID,"onResume starting");
    if (m_mapInputActivity == null) {
        MapInputActivity mapInputActivity = new MapInputActivity();
        m_mapInputActivity = mapInputActivity;
    }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, m_mapInputActivity, "MapInput");
        ft.addToBackStack("MapInput");
        //  ft.add(R.id.map,mapFragment,"map");
        ft.commit();

    }
}