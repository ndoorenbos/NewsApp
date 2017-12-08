package com.example.android.newsapp;

import android.content.Intent;
import android.content.Loader;
import android.app.LoaderManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Article>> {
    public String newsUrlString;
    private ViewHolder viewHolder = new ViewHolder();

    // My Guardian API Key
    private static String API_KEY = "&api-key=820695a2-194d-4363-b7e2-b3e11eb8823b";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create and set a ViewHolder for the {@link MainActivity} Views
        RelativeLayout mainRelativeLayout = (RelativeLayout) findViewById(R.id.activity_main);
        viewHolder.problemTextView = (TextView) findViewById(R.id.problem_text_view);
        viewHolder.articleListView = (ListView) findViewById(R.id.list_view);
        viewHolder.progressBar = (ProgressBar) findViewById(R.id.loading_indicator);
        mainRelativeLayout.setTag(viewHolder);

        newsUrlString = QueryUtils.getSearchUrlString("holiday", API_KEY);

        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Update the UI with the given article information
     * @param articleResults is the ArrayList of {@link Article} objects retrieved from the URL
     */
    private void updateUi(ArrayList<Article> articleResults) {
        // Create an ArrayList of {@link Article} objects and set the input ArrayList to it
        final ArrayList<Article> articles = new ArrayList<Article>(articleResults);

        // Set the ListView with the ID list_view as visible and all other views as gone
        viewHolder.articleListView.setVisibility(View.VISIBLE);
        viewHolder.problemTextView.setVisibility(View.GONE);
        viewHolder.progressBar.setVisibility(View.GONE);

        // Create a new {@link ArticleAdapter} with {@link Article} objects
        ArticleAdapter articleAdapter = new ArticleAdapter(MainActivity.this, articles);

        // Set the adapter on the ListView so the list can be populated in the user interface
        viewHolder.articleListView.setAdapter(articleAdapter);

        // Create an OnItemClickListener for the article, links to the article's Guardian URL
        viewHolder.articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the URL from the current {@link Article} object
                Article currentArticle = articles.get(position);
                String articleUrlString = currentArticle.getUrl();

                // Create a new Intent to open a web browser with the given URL String
                Intent articleWebIntent = new Intent(Intent.ACTION_VIEW);
                articleWebIntent.setData(Uri.parse(articleUrlString));
                startActivity(articleWebIntent);
            }
        });
    }

    // This method updates the UI in the case of no network connection
    private void noConnectionUI() {
        // Set the TextView with the ID problem_text_view as visible and all other views as gone
        viewHolder.problemTextView.setVisibility(View.VISIBLE);
        viewHolder.articleListView.setVisibility(View.GONE);
        viewHolder.progressBar.setVisibility(View.GONE);

        // Update the TextView with the no_connection message
        viewHolder.problemTextView.setText(String.valueOf(R.string.no_connection));
    }

    // This method updates the UI in the case of no results
    private void noResultsUI(){
        // Set the TextView with the ID problem_text_view as visible and all other views as gone
        viewHolder.problemTextView.setVisibility(View.VISIBLE);
        viewHolder.articleListView.setVisibility(View.GONE);
        viewHolder.progressBar.setVisibility(View.GONE);

        // Update the TextView with the no_connection message
        viewHolder.problemTextView.setText(String.valueOf(R.string.no_results));
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {

        // Set the ProgressBar with the ID progress_bar as visible and all other views as gone
        viewHolder.problemTextView.setVisibility(View.GONE);
        viewHolder.articleListView.setVisibility(View.GONE);
        viewHolder.progressBar.setVisibility(View.VISIBLE);

        NewsLoader newNewsLoader =
                new NewsLoader(MainActivity.this, newsUrlString);
        return newNewsLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> articleLoader, List<Article> articleData) {
        ArrayList<Article> retrievedList = (ArrayList) articleData;

        // Set the ProgressBar with the ID progress_bar as gone
        viewHolder.progressBar.setVisibility(View.GONE);

        if (retrievedList == null) {
            noConnectionUI();
        } else if (retrievedList.isEmpty()) {
            noResultsUI();
        } else {
            updateUi(retrievedList);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> articleLoader) {
        ArrayList<Article> blankList = new ArrayList<Article>();
        updateUi(blankList);
    }

    // {@link ViewHolder} for the the Views being changed by the {@link NewsLoader}
    static class ViewHolder {
        TextView problemTextView;
        ListView articleListView;
        ProgressBar progressBar;
    }
}
