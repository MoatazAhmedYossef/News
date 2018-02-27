package com.motaz.news.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.motaz.news.R;
import com.motaz.news.model.News;
import com.motaz.news.ui.DetailActivity;
import com.motaz.news.ui.NewsListActivity;

/**
 * Implementation of App Widget functionality.
 */
public class NewsAppWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.news_app_widget);
        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(NewsListActivity.SHARED_PREFERENCE,
                context.MODE_PRIVATE);
        String gsonNews = prefs.getString(NewsListActivity.WIDGET_NEWS, null);
        News news;
        if(gsonNews != null){
            news = gson.fromJson(gsonNews,News.class);
            views.setTextViewText(R.id.appwidget_text,context.getString(R.string.top_news) + news.getTitle());
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT,gsonNews);
            PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0);
            // viewID - our clickable view ID
            views.setOnClickPendingIntent(R.id.appwidget_text, pIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        // Construct the RemoteViews object


        // Instruct the widget manager to update the widget

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //remove comment
        //WidgetIntentService.startActionSetHotNews(context);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
