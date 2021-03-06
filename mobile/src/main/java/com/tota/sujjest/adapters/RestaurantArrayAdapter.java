package com.tota.sujjest.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.tota.sujjest.Entity.Restaurant;
import com.tota.sujjest.Entity.Sentiment;
import com.tota.sujjest.R;

import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Created by aprabhakar on 12/22/15.
 */
public class RestaurantArrayAdapter extends ArrayAdapter<Restaurant> {

    private static final String ID="RestaurantArrayAdapter";
    Context mContext;
    int mLayout;
    // Restaurant[] arrayList;
    private ArrayList<Restaurant> restaurantArrayList;

    public RestaurantArrayAdapter(Context context, int resource, ArrayList<Restaurant> data) {
        super(context, resource, data);
        this.mContext = context;
        this.mLayout = resource;
        this.restaurantArrayList = new ArrayList<Restaurant>();
        //    this.clear();
        //    this.addAll(data);
        //this.restaurantArrayList=data;
        //= new ArrayList<Restaurant>();
        //   this.arrayList = data;
        //   this.addAll(data);
        setNotifyOnChange(true);
    }

    @Override
    public void clear() {
        super.clear();
        //     this.arrayList=null;
        this.restaurantArrayList.clear();
    }

    @Override
    public void add(Restaurant object) {

        super.add(object);
        this.restaurantArrayList.add(object);


    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void addAll(Restaurant... items) {
        super.addAll(items);

//        this.clear();
        for (Restaurant r : items)
            this.add(r);



    }

    public void replaceAll(Restaurant[] items) {
//        super.addAll(items);
        //    this.arrayList = items;
        // this.addAll(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txtTitle;
        String title;
        ImageView imageView;
        TextView address;
        View row = convertView;
        TextView name;
        TextView numReviews;
        TextView cost;
        TextView userPerceptionIndex;
        TextView userSentiment;


        if(row == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(mLayout, parent, false);


            imageView = (ImageView) row.findViewById(R.id.imageViewRecommended);

            address = (TextView) row.findViewById(R.id.textRecAddress);

            name = (TextView) row.findViewById(R.id.textRecBizName);

            numReviews = (TextView) row.findViewById(R.id.textRecNumReviews);

            cost = (TextView) row.findViewById(R.id.textRecCost);


            userPerceptionIndex = (TextView) row.findViewById(R.id.textRecUserIndex);

            userSentiment = (TextView) row.findViewById(R.id.textRecUserFeel);

            ///   row.setTag(txtTitle);
        }
        else
        {
            imageView = (ImageView) row.findViewById(R.id.imageViewRecommended);

            address = (TextView) row.findViewById(R.id.textRecAddress);

            name = (TextView) row.findViewById(R.id.textRecBizName);

            numReviews = (TextView) row.findViewById(R.id.textRecNumReviews);

            cost = (TextView) row.findViewById(R.id.textRecCost);
            userPerceptionIndex = (TextView) row.findViewById(R.id.textRecUserIndex);

            userSentiment = (TextView) row.findViewById(R.id.textRecUserFeel);

        }

        Restaurant restaurant;

        restaurant = this.getItem(position);
        //title  = arrayList[position];

        if(restaurant!=null) {
            String image = restaurant.getImage();
            image = image.substring(2, image.length());

            Uri u = Uri.parse("http:" + "//" + URLDecoder.decode(image));

            Picasso.with(mContext).load(u).memoryPolicy(MemoryPolicy.NO_STORE).into(imageView);

            name.setText(restaurant.getBiz());
            address.setText((restaurant.getAddress()));
            numReviews.setText(restaurant.getNumReviews());
            cost.setText(restaurant.getCost());
            Sentiment sentiment;
            sentiment = restaurant.getSentiment();
            if (sentiment != null)
            {
                Float indexFloat = Float.parseFloat(sentiment.getScore());
                indexFloat = indexFloat*100;
                int index = Math.round(indexFloat);
                if (index > 100) index = 100;
                if(userPerceptionIndex != null )
                userPerceptionIndex.setText(sentiment.getScore());
                if(userSentiment != null )
                userSentiment.setText( sentiment.getSentiment());

            }
            else
            {
                if(userPerceptionIndex != null )
                    userPerceptionIndex.setText("Not Available");
                if(userSentiment != null )
                    userSentiment.setText( "NA");

            }
            Log.d(ID, "Done setting Restaurant data for Row at position("+position+").");
            return row;
        }
        else
        {
            Log.e(ID, "Restaurant at position("+position+") is null. No data to set in the row.");
            return row;

        }
    }
}
