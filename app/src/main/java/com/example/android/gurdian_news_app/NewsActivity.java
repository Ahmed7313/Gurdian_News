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

    private static final String GUARDIANAPIS_REQUEST_URL = "http://content.guardianapis.com/search";
    private NewsAdapter mAdapter;
    String url = "section=politics&show-tags=contributor&show-fields=thumbnail&api-key=c72fedd3-c805-4cf1-ab05-f02f8d8b9a6a";
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
        ListView newsListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        //get a reference to ConnectivityManager to check the internet connectivity state
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = conMan.getActiveNetworkInfo();


        newsListView.setEmptyView(mEmptyStateTextView);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
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
            mNoInternetConnection = (TextView) findViewById(R.id.no_internet_connection);
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
    // onCreateLoader instantiates and returns a new Loader for the given ID
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String numArticles = sharedPrefs.getString(
                getString(R.string.settings_num_shown_key),
                getString(R.string.settings_num_shown_default));
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        final String FIXED_QUERY = "android AND ";
        String queryArticles = sharedPrefs.getString(
                getString(R.string.settings_query_key),
                getString(R.string.settings_query_default));
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIANAPIS_REQUEST_URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Append query parameter and its value. For example, the `show-tags=contributor`
        uriBuilder.appendQueryParameter("q", FIXED_QUERY.concat(queryArticles));
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", numArticles);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", "test");
        //display Log.v with the uri created
        Log.v("MainActivity", "uri= " + uriBuilder.toString());
        // Return the completed uri
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
