package com.motaz.news.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.motaz.news.data.NewsContract.ReadLaterNewsEntry;
/**
 * Created by moatz on 14/02/18.
 */

public class NewsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "newsDb.db";
    private static final int VERSION = 1;

    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME,null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE "  + ReadLaterNewsEntry.TABLE_NAME + " (" +
                ReadLaterNewsEntry._ID                               + " INTEGER PRIMARY KEY, " +
                ReadLaterNewsEntry.COLUMN_AUTHOR                     + " TEXT,"          +
                ReadLaterNewsEntry.COLUMN_DESCRIPTION                + " TEXT, "         +
                ReadLaterNewsEntry.COLUMN_PUBLISHED_AT               + " TEXT,"          +
                ReadLaterNewsEntry.COLUMN_SOURCE_NAME                + " TEXT,"          +
                ReadLaterNewsEntry.COLUMN_TITLE                      + " TEXT,"          +
                ReadLaterNewsEntry.COLUMN_URL                        + " TEXT,"           +
                ReadLaterNewsEntry.COLUMN_URL_TO_IMAGE               + " TEXT,"           +
                "UNIQUE (" + ReadLaterNewsEntry.COLUMN_URL+ ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReadLaterNewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
