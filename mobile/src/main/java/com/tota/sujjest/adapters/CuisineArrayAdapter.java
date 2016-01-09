package com.tota.sujjest.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tota.sujjest.R;

import java.util.ArrayList;

/**
 * Created by aprabhakar on 12/22/15.
 */
public class CuisineArrayAdapter extends ArrayAdapter<String> {

    Context mContext;
    int mLayout;
    String[] arrayList;

    public CuisineArrayAdapter(Context context, int resource, String[] data) {
        super(context, resource,data);
        this.mContext = context;
        this.mLayout = resource;
        this.arrayList = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txtTitle;
        String title;

        View row = convertView;

        if(row == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(mLayout, parent, false);


            txtTitle = (TextView)row.findViewById(R.id.txtTitle);

         ///   row.setTag(txtTitle);
        }
        else
        {
            //txtTitle = (TextView)row.getTag();
            txtTitle = (TextView)row.findViewById(R.id.txtTitle);
        }

        title = this.getItem(position);
        //title  = arrayList[position];
        txtTitle.setText(title);
        Log.d("CuisineArrayAdapter", "Title: " + title);
        return row;
    }
}
