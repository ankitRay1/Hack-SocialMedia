package com.videodownloader.whatsappstatussaver.history_feature;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistorySQLite extends SQLiteOpenHelper {
    private final String VISITED_PAGES = "visited_pages";
    private SQLiteDatabase dB;

    public HistorySQLite(Context context) {
        super(context, "history.db", null, 1);
        dB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE visited_pages (title TEXT, link TEXT, time TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addPageToHistory(VisitedPage page) {
        ContentValues v = new ContentValues();
        v.put("title", page.title);
        v.put("link", page.link);
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy MM dd HH mm ss SSS",
                Locale.getDefault());
        v.put("time", simpleDateFormat.format(time));
        if (dB.update(VISITED_PAGES, v, "link = '" + page.link + "'", null) <= 0) {
            dB.insert(VISITED_PAGES, null, v);
        }
    }

    public void deleteFromHistory(String link) {
        dB.delete(VISITED_PAGES, "link = '" + link + "'", null);
    }

    public void clearHistory() {
        dB.execSQL("DELETE FROM visited_pages");
    }

    public List<VisitedPage> getAllVisitedPages() {
        Cursor c = dB.query(VISITED_PAGES, new String[]{"title", "link"}, null, null, null,
                null, "time DESC");
        List<VisitedPage> pages = new ArrayList<>();
        while (c.moveToNext()) {
            VisitedPage page = new VisitedPage();
            page.title = c.getString(c.getColumnIndex("title"));
            page.link = c.getString(c.getColumnIndex("link"));
            pages.add(page);
        }
        c.close();
        return pages;
    }

    public List<VisitedPage> getVisitedPagesByKeyword(String keyword) {
        Cursor c = dB.query(VISITED_PAGES, new String[]{"title", "link"}, "title LIKE '%" +
                keyword + "%'", null, null, null, "time DESC");
        List<VisitedPage> pages = new ArrayList<>();
        while (c.moveToNext()) {
            VisitedPage page = new VisitedPage();
            page.title = c.getString(c.getColumnIndex("title"));
            page.link = c.getString(c.getColumnIndex("link"));
            pages.add(page);
        }
        c.close();
        return pages;
    }
}
