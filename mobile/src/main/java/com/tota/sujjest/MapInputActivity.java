package com.tota.sujjest;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.tota.sujjest.Entity.Restaurant;
import com.tota.sujjest.Entity.Review;
import com.tota.sujjest.Entity.Sentiment;

import org.json.JSONException;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapInputActivity extends Fragment
        implements OnMapReadyCallback, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {


    public static final String ID="MapInputActivity";

    protected ArrayList<Restaurant> restaurantArrayList = null;
    protected ArrayList<Restaurant> restaurantArrayList2 = null;
    private RequestTask requestTask;
    private GoogleMap gm;
    public GoogleApiClient client;
    Double latitude, longitude;
    private LocationManager locationManager;
    private String provider;
    protected String where;
    protected String what;



    public MapInputActivity() {
    }


    public void addRestaurantToMap(Restaurant restaurant) {

        String address;
        address = restaurant.getAddress();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        Log.d(ID, "OnCreate Starting");

        initLocation();



        MapFragment mapFragment = new MapFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.map, mapFragment, "map");
        ft.commit();

        mapFragment.getMapAsync(this);








    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 999 && grantResults.length > 0)
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initLocation();;

    }


    protected void refreshLocation()
    {
        Log.d(ID, "Starting refreshLocation");
        initLocation();
    }

    protected void initLocation()
    {
        Geocoder geocoder;
        List<Address> addressList;

        // Get the location manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManager.getBestProvider(criteria, false);

        //if no permission, then prompt user to give permission
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] a = new String[2];
            a[0]=Manifest.permission.ACCESS_FINE_LOCATION;
            a[1]=Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(getActivity(),a,999);
            return;
        }

        locationManager.requestSingleUpdate(provider,this,null);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        // Initialize the location fields
        if (location != null) {
            Log.d(ID,"Provider " + provider + " has been selected.");
            onLocationChanged(location);
            latitude= (location.getLatitude());
            longitude = (location.getLongitude());

            geocoder = new Geocoder(getActivity());

            try {
                addressList = geocoder.getFromLocation(latitude, longitude, 1);
                if (addressList != null) {
                    if (addressList.size() > 0) {
                        Log.d(ID, "Geocoder Address: " + addressList.get(0).toString());
                        Address address = addressList.get(0);
                        if (address!=null)
                        {
                            String city_state = address.getLocality()+","+address.getAdminArea();
                            where = city_state;
                            Log.d(ID,"Found that we are here: " + where);
                        }
                    }
                    else
                    {
                        Log.e(ID, "GeoCoder: Unable to resolve location into a valid address. No address entries returned from Google Places");

                    }

                } else {
                    Log.e(ID, "GeoCoder: Resolving location into Address using Google failed - geocoder.getLocation returned null");

                }
            }
            catch (IOException e)
            {
                Log.e(ID,"Exception thrown while geoCoding current location: "+ e.toString());
            }


        } else {
            Log.e(ID,"getLastKnownLocation returned null location");

            latitude = 0.0;
            longitude = 0.0;
        }


        Log.i(ID,"latitude "+ latitude.toString());
        Log.i(ID,"longtitude " + longitude.toString());
    }

    protected void initLocationFromName(String city_state)
    {
        Geocoder geocoder;
        List<Address> addressList;

        // Get the location manager

            geocoder = new Geocoder(getActivity());

            try {
                addressList = geocoder.getFromLocationName(city_state,1);
          //      addressList = geocoder.getFromLocation(latitude, longitude, 1);
                if (addressList != null) {
                    if (addressList.size() > 0) {
                        Log.d(ID, "Geocoder Address: " + addressList.get(0).toString());
                        Address address = addressList.get(0);
                        if (address!=null)
                        {
                             city_state = address.getLocality()+","+address.getAdminArea();
                            where = city_state;
                            if(address.hasLongitude())
                            longitude = address.getLongitude();

                            if(address.hasLatitude())
                            latitude = address.getLatitude();

                            Log.d(ID,"Found that we are here: " + where);
                            if(gm != null)
                                gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11));
                        }
                    }
                    else
                    {
                        Log.e(ID, "GeoCoder: Unable to resolve location into a valid address. No address entries returned from Google Places");

                    }

                } else {
                    Log.e(ID, "GeoCoder: Resolving location into Address using Google failed - geocoder.getLocation returned null");

                }
            }
            catch (IOException e)
            {
                Log.e(ID,"Exception thrown while geoCoding current location: "+ e.toString());
            }



        Log.i(ID,"latitude "+ latitude.toString());
        Log.i(ID,"longtitude " + longitude.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude= (location.getLatitude());
        longitude = (location.getLongitude());

        if(gm != null)
        gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        initLocation();
    }

    /* Request updates at startup */
    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String[] a = new String[2];
            a[0]=Manifest.permission.ACCESS_FINE_LOCATION;
            a[1]=Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(getActivity(),a,999);
            return;
        }
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    public void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String[] a = new String[2];
            a[0]=Manifest.permission.ACCESS_FINE_LOCATION;
            a[1]=Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(getActivity(),a,999);
            return;
        }
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }



    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getActivity(), "Location is now available through: " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Location is now available through: " + provider,
                Toast.LENGTH_SHORT).show();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View map_input = inflater.inflate(R.layout.fragment_map_input, container,false);


        final ClearableAutoCompleteTextView findWhereTextView = (ClearableAutoCompleteTextView) map_input.findViewById(R.id.findWhereTextView);
        final ClearableAutoCompleteTextView findWhatTextView = (ClearableAutoCompleteTextView) map_input.findViewById(R.id.findWhatTextView);


       // ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_selectable_list_item,YelpProcessor.categories);
        //findWhatTextView.setAdapter(arrayAdapter);
         CuisineArrayAdapter cuisineArrayAdapter = new CuisineArrayAdapter(getActivity().getApplicationContext(),R.layout.cuiseine_type_ahead_item,YelpProcessor.categories);
        findWhatTextView.setAdapter(cuisineArrayAdapter);
        findWhatTextView.setThreshold(1);


//        where = findWhereTextView.getText().toString();


        findWhereTextView.setText(where);
        what = findWhatTextView.getText().toString();

        FrameLayout frameLayout = (FrameLayout) map_input.findViewById(R.id.map);



        findWhatTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, "Autocomplete Clicked...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                String data = (String) parent.getItemAtPosition(position);
                Log.d(ID, "Item Selected: " + data);
                findWhatTextView.setText(data);
                what = data;

                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

                // if a new text was entered then re-run the search for restaurants using the latest info.
                if (what.length() > 0) {
                    if (gm != null)
                        gm.clear();
                    requestTask = new RequestTask();
                    requestTask.execute();
                }

            }
        });

        findWhatTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    findWhatTextView.showClearButton();
                } else
                    findWhatTextView.hideClearButton();


                what = s.toString();

            }
        });


        findWhereTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    where = findWhereTextView.getText().toString();
                    initLocationFromName(where);

                    if(gm != null)
                        gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11));

                    requestTask = new RequestTask();
                    requestTask.execute();
                    return true;

                }
                return false;
            }
        });


        findWhereTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length() >0 )
                {
                    findWhereTextView.showClearButton();
                }
                else
                    findWhereTextView.hideClearButton();


                where = s.toString();

            }
        });




        FloatingActionButton fab = (FloatingActionButton) map_input.findViewById(R.id.fab);
        FloatingActionButton fab2 = (FloatingActionButton) map_input.findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        refreshLocation();
                                    }
                                });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               AutoCompleteTextView t1 = (AutoCompleteTextView) getActivity().findViewById(R.id.findWhatTextView);
                Log.d(ID,t1.getText().toString());
               AutoCompleteTextView t2 = (AutoCompleteTextView) getActivity().findViewById(R.id.findWhereTextView);
               Log.d(ID, t2.getText().toString());

                MainActivityFragment ma = new MainActivityFragment();

                Bundle b = new Bundle();
                b.putSerializable("what",what);
                b.putSerializable("where", where);
                ma.setArguments(b);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, ma, "main");
                ft.addToBackStack("main");
                ft.commit();

                Snackbar.make(view, "Searching...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });

        return map_input;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gm = googleMap;
       // initLocation();
        gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11));


        //    gm.setMyLocationEnabled(true);
    //    gm.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gm.getMyLocation().getLatitude(), gm.getMyLocation().getLongitude()),15));
        requestTask = new RequestTask();
        requestTask.execute();
    }





    private class RequestTask extends AsyncTask<String, Restaurant, ArrayList<Restaurant>> {
        // make a request to the specified url

        YelpProcessor yelpProcessor = new YelpProcessor();

        @Override
        protected ArrayList<Restaurant> doInBackground(String... uri) {
            //Get list of restaurants from Yelp.

            Geocoder geocoder=null;
            try {


                yelpProcessor.setCity(where);
                yelpProcessor.setDesc(what);
                restaurantArrayList = yelpProcessor.getRestaurantsForCityState(0);
                //next page
               restaurantArrayList2 = yelpProcessor.getRestaurantsForCityState(10);
               restaurantArrayList.addAll(restaurantArrayList2);
                //  MainActivity.restaurantArrayList = restaurantArrayList;


                for(Restaurant r: restaurantArrayList)
                {
                    List<Address> addressList;

                    geocoder = new Geocoder(getActivity());
                    addressList = geocoder.getFromLocationName(r.getAddress(),1);
                    if(addressList!=null)
                    {
                        if(addressList.size() >0) {
                            Log.d(ID,"Geocoder Address: " + addressList.get(0).toString());
                            r.setGoogleAddress(addressList.get(0));
                            publishProgress(r);
                        }

                    }
                    else
                    {
                        Log.e(ID, "GeoCoder Unable to access geolocation");
                        r.setGoogleAddress(null);
                    }

                }


            } catch (IOException e) {
                Log.e("Error", e.toString());
                //return "Error: IOExeption retrieving restaurants from Yelp";
            }

            return restaurantArrayList;

        }

        @Override
        protected void onProgressUpdate(Restaurant... values) {
            super.onProgressUpdate(values);

               // MapFragment m = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
               // GoogleMap gm = m.getMap();
            if(gm!=null &&
                    values.length>0 &&
                    values[0]!=null &&
                    values[0].getGoogleAddress() !=null )
            gm.addMarker(new MarkerOptions()
                            .position(new LatLng(values[0].getGoogleAddress().getLatitude(), values[0].getGoogleAddress().getLongitude()))
                            .title(values[0].getBiz())
                            .snippet(values[0].getAddress() + "\n\r" + values[0].getPhone())
            );
          //  gm.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(values[0].getGoogleAddress().getLatitude(), values[0].getGoogleAddress().getLongitude()), 10));
        }

        @Override
        protected void onPostExecute(ArrayList<Restaurant> restaurantArrayList) {
            super.onPostExecute(restaurantArrayList);


            if(gm!=null &&
                    restaurantArrayList.size() > 0 &&
                    restaurantArrayList.get(0)!=null &&
                    restaurantArrayList.get(0).getGoogleAddress() !=null )
            gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(restaurantArrayList.get(0).getGoogleAddress().getLatitude(), restaurantArrayList.get(0).getGoogleAddress().getLongitude()), 11));



        }


    }
}

