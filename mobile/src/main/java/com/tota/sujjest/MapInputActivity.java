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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    protected ArrayList<Restaurant> restaurantArrayList = null;
    protected ArrayList<Restaurant> restaurantArrayList2 = null;
    private RequestTask requestTask;
    private GoogleMap gm;
    public GoogleApiClient client;
    Double latitude, longitude;
    private LocationManager locationManager;
    private String provider;


    public MapInputActivity() {
    }


    public void addRestaurantToMap(Restaurant restaurant) {

        String address;
        address = restaurant.getAddress();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapFragment mapFragment = new MapFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.map, mapFragment, "map");
        ft.commit();

        mapFragment.getMapAsync(this);


        initLocation();



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 999 && grantResults.length > 0)
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initLocation();;

    }

    protected void initLocation()
    {
        // Get the location manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
            latitude= (location.getLatitude());
            longitude = (location.getLongitude());
        } else {
            latitude = 0.0;
            longitude = 0.0;
        }


        Log.d("latitude", latitude.toString());
        Log.d("longtitude", longitude.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude= (location.getLatitude());
        longitude = (location.getLongitude());

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
            return;
        }
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getActivity(), "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View map_input = inflater.inflate(R.layout.fragment_map_input, container,false);


        AutoCompleteTextView findWhereTextView = (AutoCompleteTextView) map_input.findViewById(R.id.findWhereTextView);
        AutoCompleteTextView findWhatTextView = (AutoCompleteTextView) map_input.findViewById(R.id.findWhatTextView);
        FrameLayout frameLayout = (FrameLayout) map_input.findViewById(R.id.map);



        findWhereTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Uri u = Uri.fromParts("http","//"+image,"");

                Snackbar.make(view, "Autocomplete Clicked...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });


        return map_input;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gm = googleMap;
        initLocation();
        gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10));


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
                            Log.d("Geocoder", "Address: " + addressList.get(0).toString());
                            r.setGoogleAddress(addressList.get(0));
                            publishProgress(r);
                        }

                    }
                    else
                    {
                        Log.d("GeoCoder","Unable to access geolocation");
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
            gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(restaurantArrayList.get(0).getGoogleAddress().getLatitude(), restaurantArrayList.get(0).getGoogleAddress().getLongitude()), 10));



        }


    }
}

