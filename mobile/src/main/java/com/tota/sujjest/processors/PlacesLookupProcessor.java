package com.tota.sujjest.processors;

import android.util.Log;

import com.tota.sujjest.Entity.Review;
import com.tota.sujjest.Entity.Sentiment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by aprabhakar on 11/23/15.
 */
public class PlacesLookupProcessor {

    //https://api.prestaging.teleport.ee/api/cities/?search=san%20francisco
    public final static String TELEPORT_ENDPOINT = "https://api.prestaging.teleport.ee/api/cities/?search=";


    public ArrayList<String> getPlaces(String bits)  {


        try {
            HttpURLConnection urlConnection;

            URL url = new URL(TELEPORT_ENDPOINT+bits);
            Log.d("Teleport URL", url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(1000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
      //      urlConnection.setRequestProperty("Accept",
      //              "application/vnd.teleport.v1+json");
//            Accept: application/vnd.teleport.v1+json

            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(false);
         //   urlConnection.connect();

            //urlConnection.set
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                StringBuilder responseStrBuilder = new StringBuilder();
                String teleportInputStr=null;
                while ((teleportInputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(teleportInputStr);
                teleportInputStr = responseStrBuilder.toString();
                teleportInputStr=teleportInputStr.replace("city:search-results","searchResults");

                JSONObject jObj = null, jObj2 = null;
                JSONArray jObj3=null;
                try {
                    jObj = new JSONObject(teleportInputStr);
                    String sCount = jObj.getString("count");
                    int count = Integer.parseInt(sCount);

                    jObj2 = jObj.getJSONObject("_embedded");
                    jObj3 = jObj2.getJSONArray("searchResults");
                    //jObj3 = jObj2.getJSONArray("");
                    ArrayList<String> citiesArray = new ArrayList<String>();

                    for(int i=0;i<jObj3.length();i++)
                    {
                        citiesArray.add(jObj3.getJSONObject(i).getString("matching_full_name"));
                    }

                    return citiesArray;

                } catch (JSONException e) {
                    Log.e("Teleport JSON Parser", "Error parsing data " + e.toString());
                }


            } else {
                Log.e("Teleport_HTTP_NOT_OK", "Http response code " + urlConnection.getResponseCode());
            }


        } catch (IOException e) {
            Log.d("Teleport Error", e.toString());
        }


//TODO
        return null;


    }
}

