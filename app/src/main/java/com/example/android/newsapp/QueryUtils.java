package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

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

/**
 * Created by ndoor on 11/29/2016.
 * Class meant to hold static variables and methods for retrieving articles
 */

public class QueryUtils {

    // This class is only meant to hold static variables and methods, which can be accessed
    // directly from the class name QueryUtils, an object instance of QueryUtils is not needed
    private QueryUtils() {
    }

    // Tag for the log messages
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Query the Guardian API and return an ArrayList of {@link Article} objects meeting the
     * requested requirements of our query.
     * @param requestUrlString is the query url to the Guardian API
     * @return ArrayList<Article>
     */
    public static ArrayList<Article> fetchArticleData(String requestUrlString) {
        // Create URL object from the given String
        URL requestUrl = createUrl(requestUrlString);

        // Perform HTTP request to the URL and receive a JSON response back. If there is a problem
        // with connecting to the server, an IOException exception object will be thrown. Catch the
        // exception so the app does not crash, and print the error message to the logs.
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(requestUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        if (jsonResponse != null) {
            // Extract relevant fields from the JSON response and create an ArrayList of
            // {@link Article} objects
            ArrayList<Article> articles = (ArrayList) extractArticlesFromJson(jsonResponse);

            // Return the {@link Event}
            return articles;
        }

        return null;
    }

    /**
     * Returns a new URL object from the given String url. If there is a problem with the way the
     * URL is formed, a MalformedURLException exception object will be thrown. Catch the exception
     * so the app does not crash, and print the error message to the logs.
     * @param stringUrl is the URL which we want in String form
     * @return URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response. If there is a
     * problem with retrieving the JSON information from the server, an IOException exception
     * object will be thrown. Catch the exception so the app does not crash, and print the error
     * message to the logs.
     * @param url in the query URL we wish to use when granted an HTTP URL Connection.
     * @return String of the JSON response from the query
     * @throws IOException
     */
    // Try to parse the JSON response string. If there is a problem with the way the JSON
    // is formatted, a JSONException exception object will be thrown. Catch the exception so
    // the app does not crash, and print the error message to the logs.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = null;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        // If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200), then read the input stream and
            // parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from
     * the server. If there is a problem with reading the InputStream, an IOException exception
     * object will be thrown. Catch the exception so the app does not crash, and print the error
     * message to the logs.
     * @param inputStream is the response from the server
     * @return String of the JSON response
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            try {
                InputStreamReader inputStreamReader =
                        new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem with reading the InputStream", e);
            }
        }
        return output.toString();
    }

    /**
     * Return an ArrayList of {@link Article} objects that has been built up from parsing a JSON
     * response
     * @param jsonResponse is the JSON response from the server as a String
     * @return ArrayList<Article>
     */
    public static List<Article> extractArticlesFromJson(String jsonResponse) {
        String sectionString = "";
        String titleString = "";
        String typeString = "";
        String publicationDateString = "";
        String urlString = "";

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<Article> articles = new ArrayList<>();

        // Try to parse the JSON response string. If there is a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown. Catch the exception so
        // the app does not crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            try {
                // Extract the JSONObject associated with the key called "response"
                JSONObject responseJsonObject = baseJsonResponse.getJSONObject("response");

                if (responseJsonObject != null) {
                    // Extract the JSONArray associated with the key called "results"
                    JSONArray resultsJsonArray = responseJsonObject.getJSONArray("results");

                    if (resultsJsonArray !=null) {
                        // Create an {@link Article} object for each article in the resultsJsonArray
                        for (int i = 0; i < resultsJsonArray.length(); i++) {

                            // Get the current article from the resultsJsonArray
                            JSONObject currentArticleJsonObject = resultsJsonArray.getJSONObject(i);

                            if (currentArticleJsonObject != null) {
                                // Get the Section from the current article "sectionName" key
                                sectionString = currentArticleJsonObject.getString("sectionName");

                                // Get the Title from the current article "webTitle" key
                                titleString = currentArticleJsonObject.getString("webTitle");

                                // Get the Type from the current article "type" key
                                typeString = currentArticleJsonObject.getString("type");

                                // Get the Date from the current article "webPublicationDate" key
                                publicationDateString =
                                        currentArticleJsonObject.getString("webPublicationDate");

                                // Get the URL from the current article "webUrl" key
                                urlString = currentArticleJsonObject.getString("webUrl");
                            }

                            // Create a new {@link Article} object with the section, title, author,
                            // publication date, and url from the JSON response
                            Article article = new Article(sectionString, titleString, typeString,
                                    publicationDateString, urlString);

                            // Add the new {@link Article} to the list of articles
                            articles.add(article);
                        }
                    }
                }
            } catch (JSONException e) {
                // Catch the error if JSONException error is thrown, print exception to logs
                Log.e("QueryUtils", "Problem parsing the article JSON results", e);
            }
        } catch (JSONException e) {
            // Catch the error if JSONException error is thrown, print exception to logs
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        }

        // Return the list of articles
        return articles;
    }

    /**
     * Create the request URL String using the search word and API Key
     * @param searchWordString is the String of search words
     * @param mySearchKey is my Guardian API Key
     * @return URL String
     */
    public static String getSearchUrlString(String searchWordString, String mySearchKey) {
        String searchURLString = "http://content.guardianapis.com/search?q=";
        if (searchWordString != null && mySearchKey != null) {
            searchURLString = searchURLString + searchWordString + mySearchKey;
        }

        return searchURLString;
    }
}
