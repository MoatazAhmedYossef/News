package com.motaz.news.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by moatz on 15/02/18.
 */

public class ApiUrlBuilder {
    //base
    final private String SCHEME = "https";
    final private String BASE_URL = "newsapi.org";
    final private String VERSION = "v2";
    //pathes
    final private String API_PATH = "api_path";
    final public String TOP_HEADLINES_PATH = "top-headlines";
    final public String EVERYTHING_PATH = "everything";
    private String favoritePath = TOP_HEADLINES_PATH;
    //parameter
    final private String API_KEY_PARAMETER = "apiKey";
    final private String API_KEY_VALUE = "71515df00e044aea9618a91553bfd20f";
    final private String COUNTARY_PARAMETER = "country";
    private String countaryValue = "us";
    final private String CATEGOERY_PARAMETER = "category";
    private String categoryValue = "general";

    //read parameters from shared preference if they are found
    public void refreshParameters(String sharedPrefsName,Context context){
        SharedPreferences prefs = context.getSharedPreferences(sharedPrefsName,
                Context.MODE_PRIVATE);
        countaryValue = prefs.getString(COUNTARY_PARAMETER, countaryValue);
        categoryValue = prefs.getString(CATEGOERY_PARAMETER, categoryValue);
        favoritePath = prefs.getString(API_PATH, favoritePath);
    }

    public URL buildUrl() throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASE_URL)
                .appendPath(VERSION)
                .appendPath(favoritePath)
                .appendQueryParameter(COUNTARY_PARAMETER, countaryValue)
                .appendQueryParameter(CATEGOERY_PARAMETER, categoryValue)
                .appendQueryParameter(API_KEY_PARAMETER, API_KEY_VALUE);
        return new URL(builder.build().toString());

    }


}
