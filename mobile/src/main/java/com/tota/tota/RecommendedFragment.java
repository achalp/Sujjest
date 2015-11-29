package com.tota.tota;
/**
 * Created by aprabhakar on 11/27/15.
 */

import android.app.Fragment;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

import com.tota.tota.Entity.Restaurant;
import com.tota.tota.R;

public class RecommendedFragment extends Fragment {

    Bundle arguments;
    Restaurant restaurant;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommended, null);
        return view;
    }



    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Restaurant r = (Restaurant)args.get("recommendedRestaurant");

    }
}