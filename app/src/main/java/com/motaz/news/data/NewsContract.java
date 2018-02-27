package com.motaz.news.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.motaz.news.model.News;
import com.motaz.news.model.Source;

/**
 * Created by moatz on 14/02/18.
 */

public class NewsContract {
    public static final String AUTHORITY = "com.motaz.news";

    // The base content URI = "content://" + <authority>
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // This is the path for the "tasks" directory
    public static final String PATH_READ_LATER_NEWS = "read_later_news";

    public static final class ReadLaterNewsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_READ_LATER_NEWS).build();
        public static final String TABLE_NAME = "read_later_news";
        public static final String COLUMN_SOURCE_NAME = "source_name";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_URL_TO_IMAGE = "url_to_image";
        public static final String COLUMN_PUBLISHED_AT = "published_at";

        public static News parseNewsFromCursor(Cursor c){
            return new News(new Source(null , c.getString(c.getColumnIndex(COLUMN_SOURCE_NAME))),
                    c.getString(c.getColumnIndex(COLUMN_AUTHOR)),
                    c.getString(c.getColumnIndex(COLUMN_TITLE)),
                    c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)),
                    c.getString(c.getColumnIndex(COLUMN_URL)),
                    c.getString(c.getColumnIndex(COLUMN_URL_TO_IMAGE)),
                    c.getString(c.getColumnIndex(COLUMN_PUBLISHED_AT)));
        }

    }

}
