package com.example.android.booklisting.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.booklisting.Book;
import com.example.android.booklisting.BookAdapter;
import com.example.android.booklisting.BookLoader;
import com.example.android.booklisting.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BookActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static String url = "https://www.googleapis.com/books/v1/volumes?q=naruto&maxResults=20";
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static final int BOOK_LOADER_ID = 1;
    public static final String BOOK_ICON_KEY = "BOOK_ICON";
    public static final String BOOK_TITLE_KEY = "BOOK_TITLE";
    public static final String BOOK_DESCRIPTION_KEY = "BOOK_DESCRIPTION";
    public static final String BOOK_PRINT_TYPE_KEY = "BOOK_PRINT_TYPE";
    public static final String BOOK_AUTHOR_KEY = "BOOK_AUTHOR";
    public static final String BOOK_VIEWABILITY_KEY = "BOOK_VIEWABILITY";
    public static final String BOOK_MATURE_RATING_KEY = "BOOK_MATURE_RATING";
    public static final String BOOK_EPUB_LINK_KEY = "BOOK_EPUB_LINK";
    public static final String BOOK_PDF_LINK_KEY = "BOOK_PDF_LINK";
    public static final String BOOK_PREVIEW_LINK_KEY = "BOOK_PREVIEW_LINK";
    public static final String BOOK_READ_LINK_KEY = "BOOK_READ_LINK";
    public static final String BOOK_BUY_LINK_KEY = "BOOK_BUY_LINK";
    public static final String BOOK_PDF_DOWNLOAD_KEY = "BOOK_PDF_DOWNLOAD_LINK";
    public static final String BOOK_EPUB_DOWNLOAD_KEY = "BOOK_EPUB_DOWNLOAD_LINK";
    public static final String SAVED_URL_KEY = "url_pref";

    private BookAdapter mBookAdapter = null;
    LoaderManager mLoaderManager;
    MenuItem mRefreshCancelItem;
    boolean isLoaderRunning = false;
    boolean mResultFound = false;
    ProgressBar mProgressBar;
    FrameLayout mEmptyStateFrame;
    TextView mEmptyStateTextView;
    ArrayList<Book> mBooks;
    SharedPreferences mGetCurrentUrlPref;
    String mSearch;
    String mCurrentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        ListView bookListView = (ListView) findViewById(R.id.book_list);

        mBookAdapter = new BookAdapter(this, new ArrayList<Book>());

        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);

        mEmptyStateFrame = (FrameLayout) findViewById(R.id.empty_state_layout);
        mEmptyStateTextView = (TextView) mEmptyStateFrame.findViewById(R.id.empty_state_view);

        bookListView.setEmptyView(mEmptyStateFrame);
        bookListView.setOnItemClickListener(new BookItemClick());

        mGetCurrentUrlPref = getSharedPreferences(SAVED_URL_KEY, MODE_PRIVATE);
        if(isNetworkAvailable(this)){
            mLoaderManager = getSupportLoaderManager();
            mLoaderManager.initLoader(BOOK_LOADER_ID, null, this);
        }else {
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateFrame.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_internet);
        }

        bookListView.setAdapter(mBookAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        mRefreshCancelItem = menu.findItem(R.id.refresh);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearch = query;
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                boolean restrictByDownloadAvail = sharedPref.getBoolean(
                        getString(R.string.download_available_key),
                        Boolean.parseBoolean(getString(R.string.download_available_val)));

                String maxResults = sharedPref.getString(
                        getString(R.string.max_result_key), getString(R.string.max_result_default));
                String orderBy = sharedPref.getString(
                        getString(R.string.order_by_array_key), getString(R.string.order_by_none_value));
                String filter = sharedPref.getString(
                        getString(R.string.filter_list_key), getString(R.string.filter_none_val));
                String printType = sharedPref.getString(
                        getString(R.string.print_type_list_key), getString(R.string.print_type_all_val));
                String matureRating = sharedPref.getString(
                        getString(R.string.mature_rating_list_key), getString(R.string.mature_rating_none_val));

                ArrayList<String> keys = new ArrayList<String>();
                ArrayList<String> values = new ArrayList<String>();
                keys.add("q");
                values.add(query);

                if(restrictByDownloadAvail){
                    keys.add("download");
                    values.add("epub");
                }

                if(!filter.equals(getString(R.string.filter_none_val))){
                    keys.add("filter");
                    values.add(filter);
                }

                keys.add("maxResults");
                values.add(maxResults);

                if(!orderBy.equals(getString(R.string.order_by_none_value))){
                    keys.add("orderBy");
                    values.add(orderBy);
                }

                keys.add("printType");
                values.add(printType);

                if(!matureRating.equals(getString(R.string.mature_rating_none_val))){
                    keys.add("maxAllowedMaturityRating");
                    values.add(matureRating);
                }

                url = createUrlFromBaseUrl(BASE_URL, keys, values);
                SharedPreferences.Editor urlPref = getSharedPreferences(SAVED_URL_KEY, MODE_PRIVATE).edit();
                urlPref.clear();
                urlPref.putString("url", url);
                urlPref.apply();
                if(isNetworkAvailable(BookActivity.this)){
                    //mLoaderManager.initLoader(BOOK_LOADER_ID, null, BookActivity.this);
                    mEmptyStateFrame.setVisibility(View.GONE);
                    mEmptyStateTextView.setVisibility(View.GONE);
                    mLoaderManager.restartLoader(BOOK_LOADER_ID, null, BookActivity.this);
                    isLoaderRunning = true;
                    mRefreshCancelItem.setIcon(R.drawable.ic_cancel);
                    mProgressBar.setVisibility(View.VISIBLE);
                    //searchView.);
                    //Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                }else {
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText(R.string.no_internet);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getApplicationContext(), newText, Toast.LENGTH_LONG).show();
                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.settings){
            Intent settingsIntent = new Intent();
            settingsIntent.setClass(getBaseContext(), SettingsActivity.class);
            startActivity(settingsIntent);
        } else if(id == R.id.refresh){
            if(isLoaderRunning)
                cancelAction();
            else if(!isLoaderRunning)
                refreshAction();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        mEmptyStateFrame.setVisibility(View.GONE);
        mEmptyStateTextView.setVisibility(View.GONE);
        if(mGetCurrentUrlPref != null){
            mCurrentUrl = mGetCurrentUrlPref.getString("url", BASE_URL);
            //Toast.makeText(this, currentUrl, Toast.LENGTH_LONG).show();
        }
        else mCurrentUrl = url;
        return new BookLoader(BookActivity.this, mCurrentUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        mBookAdapter.clear();
        isLoaderRunning = false;
        mRefreshCancelItem.setIcon(R.drawable.ic_refresh);
        mProgressBar.setVisibility(View.GONE);
        mBooks = (ArrayList<Book>) data;
        if(mBooks != null && !mBooks.isEmpty()){
            mBookAdapter.addAll(mBooks);
            mResultFound = true;
        }else{
            mResultFound = false;
            mEmptyStateFrame.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            if(mCurrentUrl.equals(BASE_URL))
                mEmptyStateTextView.setText(R.string.no_recent_search);
            else
                mEmptyStateTextView.setText(R.string.no_book_found);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mBookAdapter.clear();
        isLoaderRunning = true;
        mRefreshCancelItem.setIcon(R.drawable.ic_cancel);
        //mEmptyStateFrame.setVisibility(View.GONE);
    }



    private String createUrlFromBaseUrl(String url, String key, String val){
        Uri baseUrl = Uri.parse(url);
        Uri.Builder urlbuilder = baseUrl.buildUpon();
        urlbuilder.appendQueryParameter(key, val);
        return urlbuilder.toString();
    }

    private String createUrlFromBaseUrl(String url, ArrayList<String> key, ArrayList<String> val){
        Uri baseUrl = Uri.parse(url);
        Uri.Builder urlbuilder = baseUrl.buildUpon();
        for(int i = 0; i < key.size(); ++i){
            urlbuilder.appendQueryParameter(key.get(i), val.get(i));
        }
        return urlbuilder.toString();
    }

    /**
     * This method checks if network is available
     * @param context
     * @return
     */
    private boolean isNetworkAvailable(Context context){
        ConnectivityManager conectManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conectManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    /**this method calls restartLoader() to restart loader*/
    private void refreshAction(){
        if(isNetworkAvailable(BookActivity.this)){
            isLoaderRunning = true;
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshCancelItem.setIcon(R.drawable.ic_cancel);
            mLoaderManager.restartLoader(BOOK_LOADER_ID, null, BookActivity.this);
            //mLoaderManager.getLoader(BOOK_LOADER_ID).reset();
        }
    }

    /**this method when called cancels the specified loader*/
    private void cancelAction(){
        mProgressBar.setVisibility(View.GONE);
        mRefreshCancelItem.setIcon(R.drawable.ic_refresh);
        //bookLoader.cancelLoadInBackground();
        isLoaderRunning = false;
        mLoaderManager.getLoader(BOOK_LOADER_ID).cancelLoad();
        //mLoaderManager.destroyLoader(BOOK_LOADER_ID);
    }

    private class BookItemClick implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Book book = mBooks.get(position);

            Bitmap bookIconBitmap = book.getBitmapImage();
            String bookTitleText = book.getTitle();
            String descriptionText = book.getDescription();
            String printTypeText = book.getPrintType();
            String authorsText = book.getAuthors();
            String viewabilityText = book.getViewability();
            String matureRatingText = book.getMaturityRating();
            String epubLinkText = book.getIsEpubAvailable() + "";
            String pdfLinkText = book.getIsPdfAvailable() + "";
            String previewLinkText = book.getPreviewLink();
            String readLinkText = book.getWebReaderLink();
            String buyLink = book.getBuyLink();
            String pdfDownloadLink = book.getPdfDownloadLink();
            String epubDownloadLink = book.getEpubDownloadLink();

            Intent bookInfoIntent = new Intent();
            bookInfoIntent.setClass(getBaseContext(), BookInfoActivity.class);
            bookInfoIntent.putExtra(BOOK_ICON_KEY, bookIconBitmap);
            bookInfoIntent.putExtra(BOOK_TITLE_KEY, bookTitleText);
            bookInfoIntent.putExtra(BOOK_DESCRIPTION_KEY, descriptionText);
            bookInfoIntent.putExtra(BOOK_PRINT_TYPE_KEY, printTypeText);
            bookInfoIntent.putExtra(BOOK_AUTHOR_KEY, authorsText);
            bookInfoIntent.putExtra(BOOK_VIEWABILITY_KEY, viewabilityText);
            bookInfoIntent.putExtra(BOOK_MATURE_RATING_KEY, matureRatingText);
            bookInfoIntent.putExtra(BOOK_EPUB_LINK_KEY, epubLinkText);
            bookInfoIntent.putExtra(BOOK_PDF_LINK_KEY, pdfLinkText);
            bookInfoIntent.putExtra(BOOK_PREVIEW_LINK_KEY, previewLinkText);
            bookInfoIntent.putExtra(BOOK_READ_LINK_KEY, readLinkText);
            bookInfoIntent.putExtra(BOOK_BUY_LINK_KEY, buyLink);
            bookInfoIntent.putExtra(BOOK_PDF_DOWNLOAD_KEY, pdfDownloadLink);
            bookInfoIntent.putExtra(BOOK_EPUB_DOWNLOAD_KEY, epubDownloadLink);
            startActivityForResult(bookInfoIntent, 1);
            //startActivity(bookInfoIntent);
        }
    }
}
