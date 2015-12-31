package com.tota.sujjest;
/**
 * Created by aprabhakar on 11/27/15.
 */

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.tota.sujjest.Entity.Restaurant;

import java.net.URLDecoder;

public class LeastRecommendedFragment extends Fragment {

    private Bundle arguments;
    private View  view;
    private static final String ID="LeastRecommendedFragment";
    private Restaurant restaurant=null,r2=null,r3=null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(ID, "Starting onCreateView");

        View view = inflater.inflate(R.layout.fragment_recommended_most, null);
        this.view = view;
        ImageView imageView;
        TextView address,name,numReviews,cost;


        if(null != restaurant ) {

            imageView = (ImageView) view.findViewById(R.id.imageViewRecommended);
            address = (TextView) view.findViewById(R.id.textRecAddress);
            name = (TextView) view.findViewById(R.id.textRecBizName);
            numReviews = (TextView) view.findViewById(R.id.textRecNumReviews);
            cost = (TextView) view.findViewById(R.id.textRecCost);


            String image = restaurant.getImage();
            image = image.substring(2, image.length());

            Uri u = Uri.parse("http:" + "//" + URLDecoder.decode(image));

            Picasso.with(getActivity().getApplicationContext()).load(u).memoryPolicy(MemoryPolicy.NO_STORE).into(imageView);

            name.setText(restaurant.getBiz());
            address.setText((restaurant.getAddress()));
            numReviews.setText(restaurant.getNumReviews());
            cost.setText(restaurant.getCost());
        }
        else
        {
            Log.e("Error", "Restaurant is null");
        }

        if(null != r2 ) {

            imageView = (ImageView) view.findViewById(R.id.imageViewRecommended2);
            address = (TextView) view.findViewById(R.id.textRecAddress2);
            name = (TextView) view.findViewById(R.id.textRecBizName2);
            numReviews = (TextView) view.findViewById(R.id.textRecNumReviews2);
            cost = (TextView) view.findViewById(R.id.textRecCost2);


            String image = r2.getImage();
            image = image.substring(2, image.length());

            Uri u = Uri.parse("http:" + "//" + URLDecoder.decode(image));

            Picasso.with(getActivity().getApplicationContext()).load(u).memoryPolicy(MemoryPolicy.NO_STORE).into(imageView);

            name.setText(r2.getBiz());
            address.setText((r2.getAddress()));
            numReviews.setText(r2.getNumReviews());
            cost.setText(r2.getCost());
        }
        else
        {
            Log.e("Error", "Restaurant 2 is null");
        }

        if(null != r3 ) {
            imageView = (ImageView) view.findViewById(R.id.imageViewRecommended3);
            address = (TextView) view.findViewById(R.id.textRecAddress3);
            name = (TextView) view.findViewById(R.id.textRecBizName3);
            numReviews = (TextView) view.findViewById(R.id.textRecNumReviews3);
            cost = (TextView) view.findViewById(R.id.textRecCost3);


            String image = r3.getImage();
            image = image.substring(2, image.length());

            Uri u = Uri.parse("http:" + "//" + URLDecoder.decode(image));

            Picasso.with(getActivity().getApplicationContext()).load(u).memoryPolicy(MemoryPolicy.NO_STORE).into(imageView);

            name.setText(r3.getBiz());
            address.setText((r3.getAddress()));
            numReviews.setText(r3.getNumReviews());
            cost.setText(r3.getCost());
        }
        else
        {
            Log.e("Error", "Restaurant 3 is null");
        }


        return view;
    }



    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        Restaurant r = (Restaurant)args.get("LeastRecommendedRestaurant-1");
        restaurant=r;
         r2 = (Restaurant)args.get("LeastRecommendedRestaurant-2");
        r3 = (Restaurant)args.get("LeastRecommendedRestaurant-3");

    }
}