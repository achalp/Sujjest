package com.tota.sujjest;

import android.Manifest;
import android.app.Fragment;
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
    public static final String ID ="MainActivity";
    public static ArrayList<Restaurant> restaurantArrayList = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Log.d("Main","MainActivity; OnCreate");
        MapInputActivity mapInputActivity = new MapInputActivity();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.container, mapInputActivity, "MapInput");
            ft.addToBackStack("MapInput");
            //  ft.add(R.id.map,mapFragment,"map");
            ft.commit();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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
        super.onBackPressed();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Log.d(ID,"Back button Pressed");
        getFragmentManager().popBackStackImmediate();

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }





}