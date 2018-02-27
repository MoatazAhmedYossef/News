package com.motaz.news.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.motaz.news.R;
import com.motaz.news.data.NewsContract;
import com.motaz.news.model.News;
import com.motaz.news.utilities.ApiResponse;
import com.motaz.news.utilities.ApiUrlBuilder;
import com.motaz.news.utilities.NewsApi;
import com.motaz.news.widget.NewsAppWidget;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NewsListActivity extends AppCompatActivity implements NewsListAdapter.NewsClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private final String CURRENT_STATE = "current_state";
    private static final String TAG = NewsListActivity.class.toString();
    //Activity XML Components
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.list_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressbar;
    //Global Attributes
    public final static String SHARED_PREFERENCE = "shared_preference";
    public final static String WIDGET_NEWS= "widget_news";
    //Activity Attributes
    private ApiUrlBuilder mApiUrlBuilder;
    private ApiResponse mApiResponse;
    private Context context;
    //holds state activity
    private States currentState = States.TOP_NEWS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        ButterKnife.bind(this);
        mApiUrlBuilder = new ApiUrlBuilder();
        mApiUrlBuilder.refreshParameters(SHARED_PREFERENCE, this);
        context = this;
        setSupportActionBar(mToolbar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this
                ,LinearLayoutManager.VERTICAL,false));
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        currentState = States.valueOf(prefs.getString(CURRENT_STATE,
                String.valueOf(States.TOP_NEWS)));
        if(currentState == States.READ_LATER)
            getSupportLoaderManager().initLoader(1, null, this);
        else
            new NewsQueryTask().execute();
    }

    //Menu Part
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Inflate Menu");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE).
                edit();
        editor.putString(CURRENT_STATE, String.valueOf(currentState));
        editor.apply();


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.top_news_menu:
                if(currentState != States.TOP_NEWS){
                    new NewsQueryTask().execute();
                    currentState = States.TOP_NEWS;
                }
                return true;
            case R.id.read_later_menu:
                if(currentState != States.READ_LATER){
                    getSupportLoaderManager().initLoader(1, null, this);
                    currentState = States.READ_LATER;
                }
                return true;
            case R.id.sign_menu:
                startActivity(new Intent(this,GoogleSignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onNewsClick(News clickedNews) {
        Intent intent = new Intent(this, DetailActivity.class);
        Gson gson = new Gson();
        intent.putExtra(Intent.EXTRA_TEXT,gson.toJson(clickedNews));
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        getSupportActionBar().setTitle(R.string.read_later);
        return new CursorLoader(this, NewsContract.ReadLaterNewsEntry.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<News> news = new ArrayList<News>();
        while (cursor.moveToNext()) {
            news.add(NewsContract.ReadLaterNewsEntry.parseNewsFromCursor(cursor));
        }
        if(mRecyclerView.getAdapter() != null)
            ((NewsListAdapter)mRecyclerView.getAdapter()).setNews(news);
        else
            mRecyclerView.setAdapter(new NewsListAdapter(context, news));
        mRecyclerView.getAdapter().notifyDataSetChanged();
        currentState = States.READ_LATER;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private class NewsQueryTask extends AsyncTask<URL, Void, ApiResponse> {


        @Override
        protected void onPreExecute() {
            getSupportActionBar().setTitle(R.string.app_name);
            mProgressbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected ApiResponse doInBackground(URL... urls) {
            NewsApi newsApi = new NewsApi();
            try {
                ApiResponse newsResponse = newsApi.getResponse(mApiUrlBuilder.buildUrl());
                Set<String> urlsSet = new HashSet<>();
                Cursor c = context.getContentResolver().query(NewsContract.ReadLaterNewsEntry.CONTENT_URI,
                        new String[]{NewsContract.ReadLaterNewsEntry.COLUMN_URL}, null, null, null);
                assert c != null;
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    urlsSet.add(c.getString(c.getColumnIndex(NewsContract.ReadLaterNewsEntry.COLUMN_URL)));
                }
                c.close();
                for(News n:newsResponse.getArticles()){
                    if(urlsSet.contains(n.getUrl())){
                        n.setReadLater(true);
                    }
                }
                return newsResponse;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ApiResponse newsResponse) {
            mProgressbar.setVisibility(View.GONE);
            if (newsResponse != null) {
                mApiResponse = newsResponse;
                //TODO error
                Log.d(TAG, "msg : "+ mApiResponse.getStatus());

                if(newsResponse.getStatus().trim().equals("ok")){
                    Log.d(TAG, "Size : " + String.valueOf(newsResponse.getArticles().size()));
                    putWidgetNews(newsResponse.getArticles().
                            get(new Random().nextInt(newsResponse.getArticles().size())));
                    if(mRecyclerView.getAdapter() == null){
                        NewsListAdapter mNewsAdapter = new NewsListAdapter(context, newsResponse.getArticles());
                        mRecyclerView.setAdapter(mNewsAdapter);
                    }else{
                        ((NewsListAdapter)mRecyclerView.getAdapter()).setNews(newsResponse.getArticles());
                    }
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    currentState = States.TOP_NEWS;
                }else{
                    Toast.makeText(context, R.string.api_error_status + newsResponse.getStatus(),Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, R.string.failed_to_connect,
                        Toast.LENGTH_SHORT).show();
            }
        }


    }



    private void putWidgetNews(News news){
        Gson gson = new Gson();
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE).
                edit();
        editor.putString(WIDGET_NEWS, gson.toJson(news));
        editor.apply();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.news_app_widget);
        ComponentName thisWidget = new ComponentName(this, NewsAppWidget.class);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    enum States {
        TOP_NEWS,
        READ_LATER
    }

}

