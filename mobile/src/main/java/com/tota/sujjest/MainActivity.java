package com.tota.sujjest;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tota.sujjest.Entity.Restaurant;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{
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



            MapInputActivity mapInputActivity = new MapInputActivity();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.container, mapInputActivity, "main");
            ft.addToBackStack("MapInput");
            //  ft.add(R.id.map,mapFragment,"map");
            ft.commit();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MainActivityFragment ma = new MainActivityFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container, ma, "main");
                    ft.addToBackStack(null);
                    ft.commit();

                    Snackbar.make(view, "Searching...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                }
            });






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