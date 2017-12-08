package com.example.android.newsapp;

/**
 * Created by ndoor on 11/26/2016.
 * Object to be used to store the article information needed to update the UI
 */

public class Article {
    private String mSection;
    private String mTitle;
    private String mType;
    private String mDate;
    private String mUrl;

    /**
     * {@link Article} object, takes in 5 String items
     * @param section is the section name which the article is listed
     * @param title is the title of the article
     * @param type is the type of news piece, like article
     * @param date is the date the article was published
     * @param url is the url String of the the article from the Guardian API
     */
    public Article(String section, String title, String type, String date, String url) {
        mSection = section;
        mTitle = title;
        mType = type;
        mDate = date;
        mUrl = url;
    }

    // The following methods are for retrieving the individual string items from the object
    public String getSection() {
        return mSection;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getType() {
        return mType;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }
}
