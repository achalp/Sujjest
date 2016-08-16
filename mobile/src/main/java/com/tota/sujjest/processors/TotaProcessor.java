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
import java.util.ArrayList;

/**
 * Created by aprabhakar on 11/23/15.
 */
public class TotaProcessor {

    // public final static String ALCHEMY_TEXT_SENTIMENT_ENDPOINT = "http://10.0.0.16:8080/reviewanalyzer/sentiment/review";
//    public final static String ALCHEMY_TEXT_SENTIMENT_ENDPOINT = "http://ec2-54-200-239-111.us-west-2.compute.amazonaws.com:8080/reviewanalyzer/sentiment/review";
    public final static String ALCHEMY_TEXT_SENTIMENT_ENDPOINT = "http://sujjest.cloudapp.net/reviewanalyzer/sentiment/review";
    public final static String ALCHEMY_API_KEY = "186525230e98c5421b4c4ce3f6bcf6d37315a32c";
    private HttpURLConnection urlConnection;


    public Sentiment getSentiment(String key, ArrayList<Review> restaurantReviews) throws JSONException {


        String reviewsConcatenated;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < restaurantReviews.size(); i++) {
            if (i > 0 && i < restaurantReviews.size()) sb.append(",{\"review\":");
            else
                sb.append("{\"review\":");
            sb.append(JSONObject.quote(restaurantReviews.get(i).getReview()));
            sb.append("}");

        }
        sb.append("]");
        reviewsConcatenated = (sb.toString());


        try {

            URL url = new URL(ALCHEMY_TEXT_SENTIMENT_ENDPOINT);
            Log.d("Alchemy URL", url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(100000);
            urlConnection.setRequestProperty("Content-Type",
                    "application/json");

            String parameters =
                    //URLEncoder.encode(reviewsConcatenated, "UTF-8")
                    reviewsConcatenated;

            /*String parameters = null;
                parameters = "outputMode=json"
                    + "&apikey=" + ALCHEMY_API_KEY
                    + "&text="
                    + URLEncoder.encode(reviewsConcatenated, "UTF-8");
*/
            Log.d("ALCHEMY_Request_Params", parameters);

            //   urlConnection.setRequestProperty("Content-Length", "" +
            //           Integer.toString(parameters.getBytes().length));

            Log.d("Tota_request_Size", Integer.toString(parameters.getBytes().length));


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

                    //jObj2 = jObj.getJSONObject("docSentiment");
                    String type = jObj.getString("type");
                    String score = jObj.getString("score");
                    String mixed = jObj.getString("mixed");
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

