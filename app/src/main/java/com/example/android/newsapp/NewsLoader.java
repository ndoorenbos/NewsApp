package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.ConnectivityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndoor on 11/30/2016.
 */

public class NewsLoader extends AsyncTaskLoader<List<Article>> {
    String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    // Check to see if the network is connected.
    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Article> loadInBackground() {
        if (isNetworkAvailable(getContext())) {
            // Get the information from the server.
            ArrayList<Article> articleResults = QueryUtils.fetchArticleData(mUrl);
            return articleResults;
        } else {
            return null;
        }
    }
}