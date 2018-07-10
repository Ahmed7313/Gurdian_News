package com.example.android.gurdian_news_app;


import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {

    private static final String URL_REQUEST = "http://content.guardianapis.com/search?";
    private static final String AND_OPERATOR = "%20AND%20";
    private NewsAdapter mAdapter;
    /**
     * Constant value for the News loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private final int LOADER_NUMBER = 1;
    private ProgressBar spinner;
    private TextView mEmptyStateTextView;
    private TextView mNoInternetConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        android.app.LoaderManager loaderManager = getLoaderManager();
        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        //get a reference to ConnectivityManager to check the internet connectivity state
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = conMan.getActiveNetworkInfo();


        newsListView.setEmptyView(mEmptyStateTextView);

        spinner = findViewById(R.id.progressBar);
        //make the spinner visible to the user until the app fetch the data from USGS servers
        spinner.setVisibility(View.VISIBLE);

        if(networkInfo != null && networkInfo.isConnected()){
            // Get a reference to the LoaderManager, in order to interact with loaders.
            loaderManager = getLoaderManager();

            loaderManager.initLoader(LOADER_NUMBER,null,this);
        }else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            ProgressBar loadingIndicator = findViewById(R.id.progressBar);
            loadingIndicator.setVisibility(View.GONE);
            mNoInternetConnection = findViewById(R.id.no_internet_connection);
            // Update empty state with no connection error message
            mNoInternetConnection.setText(R.string.no_internet_connection);
        }
        // Create a new adapter that takes an empty list of News as input
        mAdapter = new NewsAdapter(this,new ArrayList<News>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);
        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected News.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current News that was clicked on
                News currentNews = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri NewsUri = Uri.parse(currentNews.getWebUrl());

                // Create a new intent to view the News URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, NewsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    // This method initialize the contents of the Activity's options menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String numberOfNews = sharedPreferences.getString(getString(R.string.settings_number_of_news_key), getString(R.string.settings_number_of_news_default));
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));
        Boolean android = sharedPreferences.getBoolean(getString(R.string.settings_theme_android_key), true);
        Boolean ios = sharedPreferences.getBoolean(getString(R.string.settings_theme_ios_key), false);
        Boolean phones = sharedPreferences.getBoolean(getString(R.string.settings_theme_phones_key), false);
        Boolean ai = sharedPreferences.getBoolean(getString(R.string.settings_theme_ai_key), false);
        Boolean gadgets = sharedPreferences.getBoolean(getString(R.string.settings_theme_gadgets_key), false);
        Boolean games = sharedPreferences.getBoolean(getString(R.string.settings_theme_games_key), false);

        StringBuilder themes = new StringBuilder();
        if (android) {
            themes.append(getString(R.string.settings_theme_android_key) + AND_OPERATOR);
        }
        if (ios) {
            themes.append(getString(R.string.settings_theme_ios_key) + AND_OPERATOR);
        }
        if (phones) {
            themes.append(getString(R.string.settings_theme_phones_key) + AND_OPERATOR);
        }
        if (ai) {
            themes.append(getString(R.string.settings_theme_ai_key) + AND_OPERATOR);
        }
        if (gadgets) {
            themes.append(getString(R.string.settings_theme_gadgets_key) + AND_OPERATOR);
        }
        if (games) {
            themes.append(getString(R.string.settings_theme_games_key) + AND_OPERATOR);
        }

        if (themes.toString().endsWith(AND_OPERATOR)) {
            themes.delete(themes.toString().length() - AND_OPERATOR.length(), themes.toString().length());
            Log.i("ArticleActivity", themes.toString());
        }

        Uri baseUri = Uri.parse(URL_REQUEST);

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", themes.toString());
        uriBuilder.appendQueryParameter("section", "technology");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", numberOfNews);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", "c72fedd3-c805-4cf1-ab05-f02f8d8b9a6a");
        Log.i("ArticleActivity", uriBuilder.toString());
        return new NewsLoader(this, uriBuilder.toString());
    }
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
// Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.progressBar);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No News found."
        mEmptyStateTextView.setText(R.string.no_news_found);

        // Clear the adapter of previous News data
        mAdapter.clear();

        if(newsList != null && !newsList.isEmpty()){
            mAdapter.addAll(newsList);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Clear the adapter of previous News data
        mAdapter.clear();
    }
}
