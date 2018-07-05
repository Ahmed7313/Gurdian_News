package com.example.android.gurdian_news_app;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    //Tag for the log message
    private static final  String LOG_TAG = QueryUtils.class.getSimpleName();

    public static final List <News>fetchDataFromNewsURl (String requestUrl){

        //create the url object
        URL url = createURL(requestUrl);
        String jsonResponse= "";
        try{
            jsonResponse = makeHTTPrequist(url);
        }catch (IOException e){
            Log.e(LOG_TAG,"Error with make teh HTTP requist to the server");
        }
        List <News> newsList = extractFeatureFromJson(jsonResponse);

        return newsList;
    }



    private static URL createURL(String requestUrl) {
        URL url = null;
        try{
            url = new URL(requestUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error with creating URl");
        }
        return url;
    }

    private static String makeHTTPrequist(URL url) throws IOException {
        
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null){
            return jsonResponse;
        }
        
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000/* Time in milliseconds*/);
            urlConnection.setReadTimeout(15000/* Time in milliseconds*/);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else {
                Log.e(LOG_TAG,"Error response code"+urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the News JSON results.", e);
        }finally {
            if (urlConnection == null){
                urlConnection.disconnect();
            }

            if(inputStream == null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String line = reader.readLine();
        while (line != null){
            output.append(line);
            line = reader.readLine();
        }
        return output.toString();
        }
    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding News to
        List<News> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject response = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or News).
            JSONArray resultsArray = response.getJSONArray("results");

            // For each News in the newsArray, create an {@link News} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single News at position i within the list of News
                JSONObject resultsArticle = resultsArray.getJSONObject(i);


                // Extract the value for the key called "mag"
                String sectionName = resultsArticle.getString("sectionName");

                // Extract the value for the key called "place"
                String webPublicationDate = resultsArticle.getString("webPublicationDate");

                // Extract the value for the key called "time"
                String webTitle = resultsArticle.getString("webTitle");

                // Extract the value for the key called "url"
                String webUrl = resultsArticle.getString("webUrl");

                String section = resultsArticle.getString("sectionName");

                // Geitting the author name from tags array
                JSONArray tags = resultsArticle.getJSONArray("tags");
                String author = "author name not found";
                //if the author found ,parse it to author string
                if (tags != null && tags.length() > 0) {
                   JSONObject tagsObject = tags.getJSONObject(0);
                   author = tagsObject.optString("webTitle");
                }

                    // Create a new {@link News} object with the magnitude, location, time,
                // and url from the JSON response.
                News news = new News(sectionName, webPublicationDate, webTitle, webUrl,section, author);

                // Add the new {@link News} to the list of News.
                newsList.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the News JSON results", e);
        }

        // Return the list of News
        return newsList;
    }
}
