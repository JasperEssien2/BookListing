package com.example.android.booklisting;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JAHSWILL on 3/18/2018.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private String mUrl;
    public BookLoader(Context context, String stringUrl) {
        super(context);
        mUrl = stringUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if(mUrl == null)
            return null;

        ArrayList<Book> books = QueryUtils.getBookData(mUrl);
        return books;
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();
    }
}
