package com.tota.sujjest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tota.sujjest.Entity.ApplicationState;
import com.tota.sujjest.Entity.Options;
import com.tota.sujjest.Entity.Restaurant;
import com.tota.sujjest.adapters.CuisineArrayAdapter;
import com.tota.sujjest.adapters.PlaceAutoCompleteAdapter;
import com.tota.sujjest.processors.YelpProcessor;
import com.tota.sujjest.widgets.ClearableAutoCompleteTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapInputActivity extends Fragment
        implements OnMapReadyCallback, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {


    public static final String ID = "MapInputActivity";
    public GoogleApiClient client;
    protected ArrayList<Restaurant> restaurantArrayList = null;
    protected ArrayList<Restaurant> restaurantArrayList2 = null;
    protected String where;
    protected String what;
    protected MapFragment mapFragment;
    Double latitude = 0.0, longitude = 0.0;
    ProcessFragment ma;
    private RequestTask requestTask;
    private GoogleMap gm;
    private LocationManager locationManager;
    private String provider;
    private boolean mlocationChanged;
    private Menu optionsMenu;
    private Activity activity;
    private ApplicationState applicationState;
    private Options options;
    private Tracker mTracker;
    private SearchResultsListener searchResultsListener;

    public MapInputActivity() {
        this.applicationState = ApplicationState.getInstance();
        this.options = this.applicationState.getOptions();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(ID, "onAttach");
        activity = getActivity();

        try {
            searchResultsListener = (SearchResultsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SearchResultsListener");
        }

        initLocation();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(ID, "onAttach -- the deprecated one");

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            searchResultsListener = (SearchResultsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SearchResultsListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(ID, "Starting onSaveInstanceState");
        // Save the user's current game state
        savedInstanceState.putString("what", what);
        savedInstanceState.putString("where", where);
        savedInstanceState.putDouble("latitude", latitude);
        savedInstanceState.putDouble("longitude", longitude);

        savedInstanceState.putSerializable("RestaurantList", restaurantArrayList);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        Log.e(ID, "OnCreateOptionsMenu");
        inflater.inflate(R.menu.menu_main, menu);
        optionsMenu = menu;

        //      return super.onCreateOptionsMenu(menu);
    }


   /* public void addRestaurantToMap(Restaurant restaurant) {

        String address;
        address = restaurant.getAddress();


    }
    */

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.e(ID, "in opPrepareOptionsMenu");
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

        if (id == R.id.action_price$) {
            if (item.isChecked()) {
                this.options.setPrice$(false);
                item.setChecked(false);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("UnCheck-price$")
                        .build());
            } else {
                this.options.setPrice$(true);
                item.setChecked(true);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Check-price$")
                        .build());

            }


        }
        if (id == R.id.action_price$$) {
            if (item.isChecked()) {
                this.options.setPrice$$(false);
                item.setChecked(false);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("UnCheck-price$$")
                        .build());
            } else {
                this.options.setPrice$$(true);
                item.setChecked(true);

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Check-price$$")
                        .build());

            }
        }
        if (id == R.id.action_price$$$) {
            if (item.isChecked()) {
                this.options.setPrice$$$(false);
                item.setChecked(false);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("UnCheck-price$$$")
                        .build());
            } else {
                this.options.setPrice$$$(true);
                item.setChecked(true);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Check-price$$$")
                        .build());

            }
        }
        if (id == R.id.action_price$$$$) {
            if (item.isChecked()) {
                this.options.setPrice$$$$(false);
                item.setChecked(false);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("UnCheck-price$$$$")
                        .build());
            } else {
                this.options.setPrice$$$$(true);
                item.setChecked(true);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Check-price$$$$")
                        .build());
            }
        }

        if ((id == R.id.action_price$) || (id == R.id.action_price$$) || (id == R.id.action_price$$$) || (id == R.id.action_price$$$$)) {
            refreshResults();
            return true;
        }

        if (id == R.id.action_search) {
            CardView cardView = (CardView) getActivity().findViewById(R.id.searchCard);
            FrameLayout fragment = (FrameLayout) getActivity().findViewById(R.id.container);
            if (cardView != null && fragment != null)
                if (cardView.getVisibility() == View.VISIBLE) {
                    //do nothing

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("searchFilterInvisible")
                            .build());

                    cardView.setVisibility(View.GONE);
                    fragment.invalidate();

                } else {
                    //show
                    //send GA event
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("searchFilterVisible")
                            .build());


                    if(gm!=null)
                    {
                        LatLng latLng = gm.getCameraPosition().target;
                        Geocoder geocoder = new Geocoder(activity);
                        List<Address> addresses;
                        Address selectedAddress=null;
                        try {
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10);

                            if(addresses.size() >0) {
                                Log.d(ID, "Geocoding returned:");
                                for (Address address : addresses) {
                                        if(address.getSubLocality() != null)
                                        {
                                            selectedAddress = address;
                                            break;
                                        }
                                }
                                if(selectedAddress == null) {
                                    selectedAddress = addresses.get(0);
                                    where = selectedAddress.getLocality() +"," + selectedAddress.getAdminArea()+"," +selectedAddress.getCountryCode();
                                }
                                else
                                {
                                    where = selectedAddress.getSubLocality()+"," +selectedAddress.getLocality() +"," + selectedAddress.getAdminArea()+"," +selectedAddress.getCountryCode();

                                }
                                ClearableAutoCompleteTextView whereTextView = (ClearableAutoCompleteTextView)getActivity().findViewById(R.id.findWhereTextView);
                                if(whereTextView!= null)
                                    whereTextView.setText(where);


                            }
                            else
                                Log.e(ID,"Geocding did not return any results...the where field will not be set");


                        }catch(IOException e)
                        {
                            Log.e(ID,"Error Geocoding the current Map center");
                        }

                    }

                    cardView.setVisibility(View.VISIBLE);
                    fragment.invalidate();

                }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {

        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.action_search);
            if (refreshItem != null) {
                if (refreshing) {
                    Log.e(ID, "setting refresh view");
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    Log.e(ID, "unsetting refresh view");
                    refreshItem.setActionView(null);
                }
            } else
                Log.e(ID, "refreshItem menuitem is null");
        } else
            Log.e(ID, "optionsMenu is null");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Log.d(ID, "OnCreate Starting");
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();


        activity = getActivity();
        if (savedInstanceState != null) {
            Log.d(ID, "Bundle:What: " + savedInstanceState.getString("what"));

            Log.d(ID, "Bundle:Where: " + savedInstanceState.getString("where"));

            what = savedInstanceState.getString("what");
            where = savedInstanceState.getString("where");
            latitude = savedInstanceState.getDouble("latitude");
            longitude = savedInstanceState.getDouble("longitude");

            restaurantArrayList = (ArrayList<Restaurant>) savedInstanceState.get("RestaurantList");


        } else {
            Log.e(ID, "saved instance in OnCreate is null");
        }





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(ID, "onCreateView Starting");

        setHasOptionsMenu(true);
        setMenuVisibility(true);
        View map_input = inflater.inflate(R.layout.fragment_map_input, container, false);


        mapFragment = MapFragment.newInstance();
        FragmentTransaction ft;
        ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.map, mapFragment, "map");
        ft.commit();
        mapFragment.getMapAsync(this);


        return map_input;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(ID, "OnViewcreated Starting");
        final ClearableAutoCompleteTextView findWhereTextView = (ClearableAutoCompleteTextView) view.findViewById(R.id.findWhereTextView);
        final ClearableAutoCompleteTextView findWhatTextView = (ClearableAutoCompleteTextView) view.findViewById(R.id.findWhatTextView);
        final Button buttonSearch = (Button) view.findViewById(R.id.btnSearch);

        // ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_selectable_list_item,YelpProcessor.categories);
        //findWhatTextView.setAdapter(arrayAdapter);
        CuisineArrayAdapter cuisineArrayAdapter = new CuisineArrayAdapter(getActivity().getApplicationContext(), R.layout.cuiseine_type_ahead_item, YelpProcessor.categories);
        findWhatTextView.setAdapter(cuisineArrayAdapter);
        findWhatTextView.setThreshold(1);


//        where = findWhereTextView.getText().toString();

        buttonSearch.setOnClickListener(new
                                                View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        //track in GA
                                                        mTracker.send(new HitBuilders.EventBuilder()
                                                                .setCategory("Action")
                                                                .setAction("searchButtonClicked")
                                                                .setLabel(what+","+where)
                                                                .build());

                                                        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        in.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                                                        /*if (what.length() > 0)*/ {
                                                            if (gm != null)
                                                                gm.clear();
                                                            setRefreshActionButtonState(true);

                                                            if (requestTask != null)
                                                                requestTask.cancel(true);

                                                            searchRestaurants(true);
                                                        }


                                                    }
                                                });

        findWhatTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String data = (String) parent.getItemAtPosition(position);
                Log.d(ID, "Item Selected: " + data);
                findWhatTextView.setText(data);
                what = data;

                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

                // if a new text was entered then re-run the search for restaurants using the latest info.
            /*    if (what.length() > 0) {
                    if (gm != null)
                        gm.clear();
                    setRefreshActionButtonState(true);

                    searchRestaurants(true);
                }
*/
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("whatItemSelected")
                        .setLabel(what)
                        .build());
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

        findWhatTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    //hide hte keyboard
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    //if th
                    //ey entered something useful then attempt to resolve.
                  /*  if (what != null) {

                        if (gm != null)
                            gm.clear();

                        setRefreshActionButtonState(true);

                        if (requestTask != null)
                            requestTask.cancel(true);

                  //      searchRestaurants(true);

                    }*/
                    handled = true;
                }


                return handled;
            }
        });



        PlaceAutoCompleteAdapter placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(getActivity().getApplicationContext());
        findWhereTextView.setAdapter(placeAutoCompleteAdapter);
        findWhereTextView.setThreshold(4);

        //will work in newer versions of Android and is the recommended way.
        findWhereTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {



                    //hide hte keyboard
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    //if they entered something useful then attempt to resolve.
                    if (where.length() > 0) {

                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("whereChanged")
                                .setLabel(where)
                                .build());

                        initLocationFromName(where);
                        //  ClearableAutoCompleteTextView whereTextView = (ClearableAutoCompleteTextView) MapInputActivity.this.getActivity().findViewById(R.id.findWhereTextView);
                        //  if(whereTextView != null)
                        v.setText(where);
                        //  else
                        //    Log.e(ID,"WhereTextView is null");
                      /*  if (mlocationChanged) {
                            if (gm != null)
                                gm.clear();
                            setRefreshActionButtonState(true);

                            searchRestaurants(true);
                        }*/
                    }


                    handled = true;
                }
                return handled;
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

                if (s.length() > 0) {
                    findWhereTextView.showClearButton();
                } else
                    findWhereTextView.hideClearButton();


                where = s.toString();

            }
        });

        findWhereTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String data = (String) parent.getItemAtPosition(position);
                Log.d(ID, "Item Selected: " + data);
                findWhereTextView.setText(data);
                where = data;

                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

                // if a new text was entered then re-run the search for restaurants using the latest info.
                if (where.length() > 0) {

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("whereItemSelected")
                            .setLabel(where)
                            .build());

                /*    if (gm != null)
                        gm.clear();
                    setRefreshActionButtonState(true);

                    searchRestaurants(true);
                    */
                }

            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        FloatingActionButton fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("MyLocation")
                        .build());

                refreshLocation();
                ClearableAutoCompleteTextView whereTextView = (ClearableAutoCompleteTextView) MapInputActivity.this.getActivity().findViewById(R.id.findWhereTextView);
                if (whereTextView != null)
                    whereTextView.setText(where);
                else
                    Log.e(ID, "WhereTextView is null");

                if (gm != null)
                    gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11));


            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("AnalyseReviews")
                        .build());

                AutoCompleteTextView t1 = (AutoCompleteTextView) getActivity().findViewById(R.id.findWhatTextView);
                //  Log.d(ID,t1.getText().toString());
                AutoCompleteTextView t2 = (AutoCompleteTextView) getActivity().findViewById(R.id.findWhereTextView);
                //  Log.d(ID, t2.getText().toString());

                //  if (ma == null)
      /*          ma = new ProcessFragment();

                Bundle b = new Bundle();
                b.putSerializable("what", what);
                b.putSerializable("where", where);
                if(restaurantArrayList != null)
                    ma.setmRestaurantList(restaurantArrayList);
                ma.setArguments(b);


                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(MapInputActivity.this);
                ft.replace(R.id.container, ma, "main"); //last change was .replace
                ft.addToBackStack("MapInput");

                //  ft.addToBackStack("main");
                ft.commit();
*/
                Bundle b = new Bundle();
                b.putSerializable("what", what);
                b.putSerializable("where", where);
                searchResultsListener.onAnalyzeReviews(b);

            }
        });

        Log.d(ID, "Restoring saved state:");
        if (savedInstanceState != null) {
            what = savedInstanceState.getString("what");
            where = savedInstanceState.getString("where");
            latitude=savedInstanceState.getDouble("latitude");
            longitude=savedInstanceState.getDouble("longitude");

            restaurantArrayList=(ArrayList<Restaurant>)savedInstanceState.get("RestaurantList");

            findWhatTextView.setText(what);
            findWhereTextView.setText(where);

        } else {
            findWhereTextView.setText(where);
            what = findWhatTextView.getText().toString();

        }

    }

    private void searchRestaurants(boolean force) {

        setRefreshActionButtonState(true);
        //regardless of force or not..reload.
        if(restaurantArrayList == null) {
            requestTask = new RequestTask();
            requestTask.execute(true);
        }
        else
        {
            if(force) {
                //do force reload
                restaurantArrayList = null;
                requestTask = new RequestTask();
                requestTask.execute(false);
            }
            else {
                // not null and not forced to reload. Just call the thread to redraw the markers.
                Log.d(ID, "RestaurantList is not null..I am not going to reload. Make it null if you intended to refresh or better call refreshResults");
                requestTask = new RequestTask();
                requestTask.execute(false);
            }
        }
        if (restaurantArrayList != null && restaurantArrayList.size() > 0) {
            //ultimately, do move the camera to results.
            float zoom;
            zoom = gm.getCameraPosition().zoom;
            Log.d(ID, "Zoom:=" + zoom);
            if (zoom < 11) zoom = 11;

            gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(restaurantArrayList.get(0).getLatitude(), restaurantArrayList.get(0).getLongitude()), zoom));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initLocation();

    }

    protected void refreshResults() {
        Log.d(ID, "Starting refreshResults");
       // initLocation();
        gm.clear();

        setRefreshActionButtonState(true);

       searchRestaurants(true);
    }

    protected void refreshLocation() {
        Log.d(ID, "Starting refreshLocation");
        initLocation();
        if (mlocationChanged) //yes it did. get the list again.
        {
            gm.clear();
            setRefreshActionButtonState(true);
            searchRestaurants(true);
        }
    }

    protected void initLocation() {
        Geocoder geocoder;
        List<Address> addressList;
        Double tlatitude;
        //
        // //= latitude;
        //
        Double tlongitude;
        // //= longitude;
        Location location;

        // Get the location manager
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManager.getBestProvider(criteria, false);


        //if no permission, then prompt user to give permission
        // 01-09 achal
        // changed getActivity() to activity as it was exception'ing at onStatusChanged. Has to be a better way to do this.
        // Perhaps check to see if the app is the active activity before doing any update that requires user action
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] a = new String[2];
            a[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            a[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(activity, a, 1);
            return;
        }

        locationManager.requestSingleUpdate(provider, this, null);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 100, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100, this);

        location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            Log.d(ID, "Provider " + provider + " has been selected.");
            onLocationChanged(location);
            tlatitude = (location.getLatitude());
            tlongitude = (location.getLongitude());

            geocoder = new Geocoder(activity);

            try {
                addressList = geocoder.getFromLocation(tlatitude, tlongitude, 10);
                if (addressList != null) {
                    if (addressList.size() > 0) {
                        Log.d(ID, "Geocoder Address: " + addressList.get(0).toString());
                        Address address = addressList.get(0);
                        if (address != null) {
                            where = address.getLocality() + "," + address.getAdminArea();

                            Log.d(ID, "Found that we are here: " + where);
                        }
                    } else {
                        Log.e(ID, "GeoCoder: Unable to resolve location into a valid address. No address entries returned from Google Places");

                    }

                } else {
                    Log.e(ID, "GeoCoder: Resolving location into Address using Google failed - geocoder.getLocation returned null");

                }
            } catch (IOException e) {
                Log.e(ID, "Exception thrown while geoCoding current location: " + e.toString());
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Can't figure out your physical location");
                alertDialogBuilder.setMessage("Didn't go as expected. Most probably a network issue. \n Try again by hitting the locate me button or choosing a different location manually.\n" + e.getMessage());
                alertDialogBuilder.setPositiveButton("Dismiss", null);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }


        } else {
            Log.e(ID, "getLastKnownLocation returned null location");

            //if user entered a city,state then go there.
            if (where != null && where.length() > 0) {
                initLocationFromName(where);
                tlatitude=latitude;
                tlongitude=longitude;
            }
            else {
                //no option, keep it at whatever is in latitude and longitude
                tlatitude = latitude;
                tlongitude = longitude;
            }
        }

//did location of interest change?
        if ((latitude != 0.0 && longitude != 0.0) && (tlatitude != latitude || tlongitude != longitude)) {
            float[] results = new float[1];
            Location.distanceBetween(latitude, longitude, tlatitude, tlongitude, results);
            if (results.length > 0)
                if (results[0] > 1609) //more than a mile
                {
                    Log.d(ID, "Location delta : " + results[0] + ". Greater than 1609 so location changed");
                    latitude = tlatitude;
                    longitude = tlongitude;
                    mlocationChanged = true;
                } else
                    Log.d(ID, "Location delta : " + results[0] + ". Less than 1609 so location changed");
            else
                Log.d(ID, "distanceBetween did not return an array with length > 0");

        } else {
            if ((latitude == 0.0 && longitude == 0.0)) {
                //first time so never initialized. Just set it to the latest ( could be zero again).
                Log.d(ID, "Looks like an init situation..setting to initial");
                Log.d(ID, "tLatitude: " + tlatitude
                        +
                        " tLongitude: " + tlongitude +
                        "latitude: " + latitude +
                        " longitude: " + longitude);
                latitude = tlatitude;
                longitude = tlongitude;
                mlocationChanged = true;
            } else {
                Log.d(ID, "tLatitude: " + tlatitude
                        +
                        " tLongitude: " + tlongitude +
                        "latitude: " + latitude +
                        " longitude: " + longitude);

                mlocationChanged = false;
            }
        }

        Log.i(ID, "latitude " + latitude.toString());
        Log.i(ID, "longtitude " + longitude.toString());
    }

    protected void initLocationFromName(String city_state) {
        Geocoder geocoder;
        List<Address> addressList;
        Double tlongitude = longitude;
        Double tlatitude = latitude;
        // Get the location manager
        mlocationChanged = false;
        geocoder = new Geocoder(getActivity());

        try {
            addressList = geocoder.getFromLocationName(city_state, 1);
            //      addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null) {
                if (addressList.size() > 0) {
                    Log.d(ID, "Geocoder Address: " + addressList.get(0).toString());
                    Address address = addressList.get(0);
                    if (address != null) {
                        city_state = address.getLocality() + "," + address.getAdminArea();
                        where = city_state;
                        if (address.hasLongitude())
                            tlongitude = address.getLongitude();

                        if (address.hasLatitude())
                            tlatitude = address.getLatitude();
                        if (tlatitude != latitude || tlongitude != longitude) {
                            float[] results = new float[1];
                            Location.distanceBetween(latitude, longitude, tlatitude, tlongitude, results);
                            if (results.length > 0)
                                if (results[0] > 1609) //more than a mile
                                {
                                    Log.d(ID, "Location delta : " + results[0] + ". Greater than 1609 so location changed");
                                    latitude = tlatitude;
                                    longitude = tlongitude;
                                    mlocationChanged = true;
                                } else
                                    Log.d(ID, "Location delta : " + results[0] + ". Less than 1609 so no location changed");
                            else
                                Log.d(ID, "distanceBetween did not return an array with length > 0");

                        } else {
                            Log.d(ID, "tLatitude: " + tlatitude
                                    +
                                    " tLongitude: " + tlongitude +
                                    "latitude: " + latitude +
                                    " longitude: " + longitude);

                            mlocationChanged = false;
                        }
                        Log.d(ID, "Found that we are here: " + where);

                    }
                } else {
                    Log.e(ID, "GeoCoder: Unable to resolve location into a valid address. No address entries returned from Google Places");

                }

            } else {
                Log.e(ID, "GeoCoder: Resolving location into Address using Google failed - geocoder.getLocation returned null");

            }

        } catch (IOException e) {
            Log.e(ID, "Exception thrown while geoCoding current location: " + e.toString());
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Unable to access source websites");
            alertDialogBuilder.setMessage("Didn't go as expected. Most probably a network issue. " + e.getMessage());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (gm != null)
            gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11));
        Log.i(ID, "latitude " + latitude.toString());
        Log.i(ID, "longtitude " + longitude.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(ID, "onLocationChanged");
        double tlatitude, tlongitude;

        tlatitude = (location.getLatitude());
        tlongitude = (location.getLongitude());

        if (tlatitude != latitude || tlongitude != longitude) {
            float[] results = new float[1];
            Location.distanceBetween(latitude, longitude, tlatitude, tlongitude, results);
            if (results.length > 0)
                if (results[0] > 1609) //more than a mile
                {
                    Log.d(ID, "Location delta : " + results[0] + ". Greater than 1609 so location changed");
                    latitude = tlatitude;
                    longitude = tlongitude;
                    mlocationChanged = true;
                } else
                    Log.d(ID, "Location delta : " + results[0] + ". Less than 1609 so no location changed");
            else
                Log.d(ID, "distanceBetween did not return an array with length > 0");
            // if(gm != null)
            //  gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11));

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(ID, "onStatusChanged");
        initLocation();
    }

    /* Request updates at startup */
    @Override
    public void onResume() {
        Log.d(ID, "Resume Starting");

        mTracker.setScreenName("Map Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        super.onResume();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] a = new String[2];
            a[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            a[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(getActivity(), a, 1);
            return;
        } else {
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

            // Define the criteria how to select the locatioin provider -> use
            // default
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            provider = locationManager.getBestProvider(criteria, false);
            if (provider != null)
                locationManager.requestLocationUpdates(provider, 400, 100, this);
            else
                Log.e(ID, "No location provider available");
        }

        Log.d(ID, "backstack count at end of onResume: " + getFragmentManager().getBackStackEntryCount());
        Log.d(ID, "OnResume Done");

    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    public void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] a = new String[2];
            a[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            a[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(getActivity(), a, 1);
            return;
        }
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(ID, "Location is NOW available through: " + provider);
        if (getActivity() != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                String[] a = new String[2];
                a[0] = Manifest.permission.ACCESS_FINE_LOCATION;
                a[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
                ActivityCompat.requestPermissions(getActivity(), a, 1);
                return;
            }
            locationManager.requestLocationUpdates(provider, 400, 100, this);
        }
    }
    @Override
    public void onProviderDisabled(String provider) {
        Log.i(ID, "Location is NO LONGER available through: " + provider);
        if (getActivity() != null) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                String[] a = new String[2];
                a[0] = Manifest.permission.ACCESS_FINE_LOCATION;
                a[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
                ActivityCompat.requestPermissions(activity, a, 1);
                return;
            }
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(ID, "onMapReady Starting");
        gm = googleMap;
        // initLocation();
        gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11));


        //    gm.setMyLocationEnabled(true);
        //    gm.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gm.getMyLocation().getLatitude(), gm.getMyLocation().getLongitude()),15));

        setRefreshActionButtonState(true);
        searchRestaurants(false);
    }


    public interface SearchResultsListener {
        void onSearchResultsAvailable(ArrayList<Restaurant> restaurants);

        void onAnalyzeReviews(Bundle args);
    }

    private class RequestTask extends AsyncTask<Boolean, Restaurant, ArrayList<Restaurant>> {
        // make a request to the specified url

        private static final String ID="MapInputActResultTask";
        YelpProcessor yelpProcessor = new YelpProcessor();

        @Override
        protected ArrayList<Restaurant> doInBackground(Boolean... forceReload) {
            //Get list of restaurants from Yelp.



            Geocoder geocoder;

            if(forceReload[0].booleanValue() == true || restaurantArrayList == null) {
                try {


                    yelpProcessor.setCity(where);
                    yelpProcessor.setDesc(what);
                    restaurantArrayList = yelpProcessor.getRestaurantsForCityState(0);
                    //next page
                    restaurantArrayList2 = yelpProcessor.getRestaurantsForCityState(10);
                    if(restaurantArrayList!=null)
                        restaurantArrayList.addAll(restaurantArrayList2);
                    else {
                        Log.e(ID,"Bad Bad BAD FIX ME FIX ME");
                        restaurantArrayList = restaurantArrayList2;
                    }
                    //  MainActivity.restaurantArrayList = restaurantArrayList;

                    try {
                        geocoder = new Geocoder(activity);
                        for (Restaurant r : restaurantArrayList) {

                            List<Address> addressList;


                            addressList = geocoder.getFromLocationName(r.getAddress(), 1);
                            if (addressList != null) {
                                if (addressList.size() > 0) {
                                    Log.d(ID, "Geocoder Address: " + addressList.get(0).toString());
                                    r.setGoogleAddressFields(addressList.get(0));
                                    publishProgress(r);
                                }

                            } else {
                                Log.e(ID, "GeoCoder Unable to access geolocation");
                                r.setGoogleAddressFields(null);
                            }

                        }
                    } catch (Exception e) {
                        Log.e(ID, "Exception geocoding: " + e.toString());

                    }

                } catch (IOException e) {
                    Log.e(ID, "Exception retrieving Yelp.Com : " + e.toString());

                }
            }
            else
            {
                for (Restaurant r : restaurantArrayList) {
                    publishProgress(r);

                }
            }

            return restaurantArrayList;

        }

        @Override
        protected void onProgressUpdate(Restaurant... values) {
            super.onProgressUpdate(values);

            // MapFragment m = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            // GoogleMap gm = m.getMap();
            if (gm != null &&
                    values.length > 0 &&
                    values[0] != null &&
                    values[0].isHasGoogleAddressFields())
                gm.addMarker(new MarkerOptions()
                                .position(new LatLng(values[0].getLatitude(), values[0].getLongitude()))
                                .title(values[0].getBiz())
                                .snippet(values[0].getAddress() + "\n\r" + values[0].getPhone())
                );
      //        gm.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(values[0].getGoogleAddress().getLatitude(), values[0].getGoogleAddress().getLongitude()), 10));
        }

        @Override
        protected void onPostExecute(ArrayList<Restaurant> restaurantArrayList) {
            super.onPostExecute(restaurantArrayList);

            Restaurant restaurant=null;
            if(restaurantArrayList != null )
            for(int i=0;i<restaurantArrayList.size();i++)
            {
                restaurant = restaurantArrayList.get(i);
                if(restaurant.isHasGoogleAddressFields()) break;

            }
            else
            Log.e(ID,"onPostExecute: RestaurantList is null");

            if (gm != null && restaurantArrayList != null &&
                    restaurantArrayList.size() > 0 &&
                    restaurantArrayList.get(0) != null
                    && restaurant !=null
                    ) {
                float zoom = 0.0f;
                zoom = gm.getCameraPosition().zoom;
                Log.d(ID, "Zoom:=" + zoom);
                if (zoom < 11)
                    zoom = 11;

                gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()), zoom));
            }
            if (searchResultsListener != null && getActivity() != null) {
                Log.d(ID,"Refreshing list results");
                searchResultsListener.onSearchResultsAvailable(restaurantArrayList);
            }
            setRefreshActionButtonState(false);
            Activity activity = getActivity();
            if(activity != null) {
                CardView cardView = (CardView) activity.findViewById(R.id.searchCard);
                if (cardView != null)
                    if (cardView.getVisibility() == View.VISIBLE) {
                        //do nothing
                        cardView.setVisibility(View.GONE);
                        //           fragment.invalidate();
                    }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

        }
    }


}

