package com.tota.sujjest.processors;

import android.util.Log;

import com.tota.sujjest.Entity.Review;
import com.tota.sujjest.Entity.Sentiment;

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
public class AlchemyProcessor {

    public final static String ALCHEMY_TEXT_SENTIMENT_ENDPOINT = "http://gateway-a.watsonplatform.net/calls/text/TextGetTextSentiment";
    public final static String ALCHEMY_API_KEY = "";

    //            /"186525230e98c5421b4c4ce3f6bcf6d37315a32c";
    private HttpURLConnection urlConnection;


    public Sentiment getSentiment(String key, ArrayList<Review> restaurantReviews) throws JSONException {


        String reviewsConcatenated;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < restaurantReviews.size(); i++) {
            sb.append(restaurantReviews.get(i).getReview());
            sb.append("\n");
        }
        reviewsConcatenated = sb.toString();


        try {

            URL url = new URL(ALCHEMY_TEXT_SENTIMENT_ENDPOINT);
            Log.d("Alchemy URL", url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(1000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            String parameters = null;
                parameters = "outputMode=json"
                    + "&apikey=" + ALCHEMY_API_KEY
                    + "&text="
                    + URLEncoder.encode(reviewsConcatenated, "UTF-8");

         //   Log.d("ALCHEMY_Request_Params", parameters);

            urlConnection.setRequestProperty("Content-Length", "" +
                    Integer.toString(parameters.getBytes().length));

            Log.d("Alchemy_request_Size", Integer.toString(parameters.getBytes().length));


            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    urlConnection.getOutputStream());
            wr.writeBytes(parameters);
            wr.flush();
            wr.close();


            //urlConnection.set
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                String alchemyInputStr = null;
                StringBuilder responseStrBuilder = new StringBuilder();
                while ((alchemyInputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(alchemyInputStr);
                alchemyInputStr = responseStrBuilder.toString();


                JSONObject jObj = null, jObj2 = null;
                try {
                    jObj = new JSONObject(alchemyInputStr);

                    jObj2 = jObj.getJSONObject("docSentiment");
                    String type = jObj2.getString("type");
                    String score = jObj2.getString("score");
                    String mixed = jObj2.getString("mixed");
                    String response = "Type=" + type + "\n" + "Score=" + score + "\n" + "isMixed=" + mixed;

                    Sentiment sentiment = new Sentiment();
                    sentiment.setMixed(mixed);
                    sentiment.setScore(score);
                    sentiment.setSentiment(type);
                    return sentiment;

                } catch (JSONException e) {
                    Log.e("ALCHEMY JSON Parser", "Error parsing data " + e.toString());
                }


            } else {
                Log.e("ALCHEMY_HTTP_NOT_OK", "Http response code " + urlConnection.getResponseCode());
            }


        } catch (IOException e) {
            Log.d("Alchemy Error", e.toString());
        }


//TODO
        return null;


    }
}

