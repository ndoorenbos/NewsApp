package com.example.android.newsapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ndoor on 11/26/2016.
 * An {@link ArrayAdapter} used to populate a ListView with {@link Article} object information
 */

public class ArticleAdapter extends ArrayAdapter<Article> {

    /**
     * Constructs a new {@link ArticleAdapter}
     * @param context of the app
     * @param articles is the list of articles, which is the data source of the adapter
     */
    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
    }

    // Returns a list item view that displays information about the articles at the given position
    // in the list of articles
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View articleListItemView = convertView;
        ViewHolder viewHolder;

        // Check for an existing list view item to reuse, otherwise inflate a new layout
        if (articleListItemView == null) {
            articleListItemView = LayoutInflater.from(getContext()).inflate
                    (R.layout.article_list_item, parent, false);

            // Create a ViewHolder and populate it with the views being updated by this adapter
            viewHolder = new ViewHolder();
            viewHolder.articleSectionTextView =
                    (TextView) articleListItemView.findViewById(R.id.section_text_view);
            viewHolder.articleTitleTextView =
                    (TextView) articleListItemView.findViewById(R.id.title_text_view);
            viewHolder.articleTypeTextView =
                    (TextView) articleListItemView.findViewById(R.id.type_text_view);
            viewHolder.articleDateTextView =
                    (TextView) articleListItemView.findViewById(R.id.date_text_view);

            // Store the holder with the view
            articleListItemView.setTag(viewHolder);
        } else {
            // Use the ViewHolder which is already there
            viewHolder = (ViewHolder) articleListItemView.getTag();
        }

        // Get the {@link Article} object located at this position on the list
        final Article currentArticle = getItem(position);

        // If the current article object is not null, get the section, title, type String from the
        // {@link Article} object and update the corresponding Views
        if (currentArticle != null) {
            viewHolder.articleSectionTextView.setText(currentArticle.getSection());
            viewHolder.articleTitleTextView.setText(currentArticle.getTitle());
            viewHolder.articleTypeTextView.setText(currentArticle.getType());

            try {
                // Get the publication date String from the {@link Article} object, parse it,
                // reformat it, and update the date TextView
                String dateString = currentArticle.getDate();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date newDate = format.parse(dateString);
                String newDateString = formatDate(newDate);
                viewHolder.articleDateTextView.setText(newDateString);

            } catch (ParseException pe) {
                // Catch the error if ParseException error is thrown, print exception to logs
                Log.e("QueryUtils", "Problem parsing the date String", pe);
            }
        }

        // Return the whole article list item layout so that it can be shown in the ListView
        return articleListItemView;
    }

    // {@link ViewHolder} for the the Views being changed by the {@link ArticleAdapter}
    static class ViewHolder {
        TextView articleSectionTextView;
        TextView articleTitleTextView;
        TextView articleTypeTextView;
        TextView articleDateTextView;
    }

    // This method formats a date String (i.e. "Aug 6, 1981") from a Date object
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormat.format(dateObject);
    }
}
