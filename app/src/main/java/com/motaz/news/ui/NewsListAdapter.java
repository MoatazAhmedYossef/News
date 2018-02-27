package com.motaz.news.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.motaz.news.R;
import com.motaz.news.model.News;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by moatz on 23/02/18.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsItemViewHolder> {


    private List<News> news;
    private final Context context;
    final private NewsClickListener mOnNewsClickListener;

    public NewsListAdapter(Context context, List<News> news){
        this.news = news;
        this.context = context;
        mOnNewsClickListener =(NewsClickListener) context;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.news_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem,parent,false);
        return new NewsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsItemViewHolder holder, int position) {
        final News newsToShow = news.get(position);
        Picasso.with(context).load(newsToShow.getUrlToImage())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.thumbnail);
        holder.newsTitle.setText(newsToShow.getTitle());
        holder.newsSubtitle.setText(newsToShow.getAuthor() + ", "+newsToShow.getPublishedAt());
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnNewsClickListener.onNewsClick(newsToShow);
            }
        };
        holder.newsTitle.setOnClickListener(mOnClickListener);
        holder.newsSubtitle.setOnClickListener(mOnClickListener);
        holder.thumbnail.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        if(news == null)
            return 0;
        return news.size();
    }

    //View holder for news
    public class NewsItemViewHolder  extends RecyclerView.ViewHolder {

        @BindView(R.id.thumbnail)
        ImageView thumbnail = null;
        @BindView(R.id.news_title)
        TextView newsTitle  = null;
        @BindView(R.id.news_subtitle)
        TextView newsSubtitle  = null;
        //connect view to variables
        public NewsItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface NewsClickListener {
        void onNewsClick(News clickedNews);
    }

}
