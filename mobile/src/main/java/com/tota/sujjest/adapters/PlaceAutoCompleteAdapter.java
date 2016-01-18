package com.tota.sujjest.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tota.sujjest.processors.PlacesLookupProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprabhakar on 1/4/16.
 */
public class PlaceAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<String> resultList = new ArrayList<String>();

    private static final String ID = "PlacesAutoCmpltAdapter";

    public PlaceAutoCompleteAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<String> cities = getPlaces(mContext, constraint.toString());
                    if (cities != null) {
                        // Assign the data to the FilterResults
                        filterResults.values = cities;
                        filterResults.count = cities.size();
                    }
                }
                    else {
                    Log.e(ID, "getPlaces did not return a result of places. probably a teleport issue");
                    filterResults.values = null;
                    filterResults.count = 0;
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<String>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }


    @Override
    public int getCount() {
        return resultList.size();

    }

    @Override
    public Object getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(getItem(position).toString());
        return convertView;
    }

    /**
     * Returns a search result for the given book title.
     */
    private List<String> getPlaces(Context context, String title) {
        // GoogleBooksProtocol is a wrapper for the Google Books API
        PlacesLookupProcessor processor = new PlacesLookupProcessor();
        return processor.getPlaces(title);
    }
}
