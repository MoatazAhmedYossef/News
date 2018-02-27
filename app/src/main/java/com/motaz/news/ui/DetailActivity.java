package com.motaz.news.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.motaz.news.R;
import com.motaz.news.model.News;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.motaz.news.data.NewsContract.ReadLaterNewsEntry;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.detail_thumbnail)
    ImageView thumbnail;
    @BindView(R.id.detail_news_title)
    TextView newsTitle;
    @BindView(R.id.detail_news_subtitle)
    TextView newsSubtitle;
    @BindView(R.id.detail_news_body)
    TextView newsBody;
    @BindView(R.id.adView)
    AdView mAdView;
    private News news;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            Gson gson = new Gson();
            news = gson.fromJson(getIntent().getStringExtra(Intent.EXTRA_TEXT), News.class);
            Picasso.with(this).load(news.getUrlToImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(thumbnail);
            newsTitle.setText(news.getTitle());
            newsSubtitle.setText(news.getAuthor() + ", "+news.getPublishedAt());
            newsBody.setText(news.getDescription()  +"\n"+news.getUrl());
        }
        MobileAds.initialize(this, getString(R.string.banner_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    private void addToReadLater() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReadLaterNewsEntry.COLUMN_TITLE, String.valueOf(news.getTitle()));
        contentValues.put(ReadLaterNewsEntry.COLUMN_AUTHOR, news.getAuthor());
        contentValues.put(ReadLaterNewsEntry.COLUMN_DESCRIPTION, news.getDescription());
        contentValues.put(ReadLaterNewsEntry.COLUMN_PUBLISHED_AT, news.getPublishedAt());
        contentValues.put(ReadLaterNewsEntry.COLUMN_URL, news.getUrl());
        contentValues.put(ReadLaterNewsEntry.COLUMN_SOURCE_NAME, news.getSource().getName());
        contentValues.put(ReadLaterNewsEntry.COLUMN_URL_TO_IMAGE, news.getUrlToImage());
        getContentResolver().insert(ReadLaterNewsEntry.CONTENT_URI, contentValues);
        news.setReadLater(true);

    }

    private void removeFromReadLater() {
        getContentResolver().delete(ReadLaterNewsEntry.CONTENT_URI, ReadLaterNewsEntry.COLUMN_URL + "=?",
                new String[]{news.getUrl()});

    }

    @OnClick(R.id.fab)
    void onPlusButtonClick(){
        if(!news.isReadLater()){
            addToReadLater();
            Toast.makeText(this,R.string.added_to_read_later,Toast.LENGTH_LONG).show();
        }else{
            removeFromReadLater();
            Toast.makeText(this,R.string.removed_from_read_later,Toast.LENGTH_LONG).show();
        }
    }
}
