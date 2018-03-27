package com.example.android.booklisting.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.booklisting.Book;
import com.example.android.booklisting.R;

public class BookInfoActivity extends AppCompatActivity {

    private final String CLICKABLE_LINK = "Click here";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        Bitmap bookIconBitmap = getIntent().getParcelableExtra(BookActivity.BOOK_ICON_KEY);
        ImageView bookIcon = (ImageView) findViewById(R.id.info_book_icon);
        //Toast.makeText(BookActivity.this, bookIcon.toString(), Toast.LENGTH_SHORT).show();
        //Log.e("BookItemClick", bookIcon.toString());
        if(bookIconBitmap != null)
            bookIcon.setImageBitmap(bookIconBitmap);

        String bookTitleText = getIntent().getStringExtra(BookActivity.BOOK_TITLE_KEY);
        TextView bookTitleView = (TextView) findViewById(R.id.info_book_title);
        if(!bookTitleText.isEmpty())
            bookTitleView.setText(bookTitleText);

        String descriptionText = getIntent().getStringExtra(BookActivity.BOOK_DESCRIPTION_KEY);
        TextView descriptionTextView = (TextView) findViewById(R.id.info_description_text);
        if(!descriptionText.isEmpty())
            descriptionTextView.setText(descriptionText);

        String printTypeText = getIntent().getStringExtra(BookActivity.BOOK_PRINT_TYPE_KEY);
        TextView printTypeView = (TextView) findViewById(R.id.info_book_type);
        if(!printTypeText.isEmpty())
            printTypeView.setText(printTypeText);

        String authorsText = getIntent().getStringExtra(BookActivity.BOOK_AUTHOR_KEY);
        TextView authorsView = (TextView) findViewById(R.id.info_author);
        if(!authorsText.isEmpty())
            authorsView.setText(authorsText);

        String viewabilityText = getIntent().getStringExtra(BookActivity.BOOK_VIEWABILITY_KEY);
        TextView viewabilityView = (TextView) findViewById(R.id.info_viewability);
        if(!viewabilityText.isEmpty())
            viewabilityView.setText(viewabilityText);

        String matureRatingText = getIntent().getStringExtra(BookActivity.BOOK_MATURE_RATING_KEY);
        TextView matureRatingView = (TextView) findViewById(R.id.info_mature_rating);
        if(!matureRatingText.isEmpty())
            matureRatingView.setText(matureRatingText);

        String epubLinkText = getIntent().getStringExtra(BookActivity.BOOK_EPUB_DOWNLOAD_KEY);
        String epubDowloadLink = getIntent().getStringExtra(BookActivity.BOOK_EPUB_LINK_KEY);
        TextView epubLinkView = (TextView) findViewById(R.id.info_epub_link);
        if(!epubLinkText.isEmpty()){
            epubLinkView.setText(CLICKABLE_LINK);
            if(!epubDowloadLink.isEmpty())
                epubLinkView.setOnClickListener(new OnCLickTextLink(epubDowloadLink));
        }

        String pdfDowloadLink = getIntent().getStringExtra(BookActivity.BOOK_PDF_DOWNLOAD_KEY);
        String pdfLinkText = getIntent().getStringExtra(BookActivity.BOOK_PDF_LINK_KEY);
        TextView pdfLinkView = (TextView) findViewById(R.id.info_pdf_link);
        if(!pdfLinkText.isEmpty()){
            pdfLinkView.setText(CLICKABLE_LINK);
            if(!pdfDowloadLink.isEmpty())
                pdfLinkView.setOnClickListener(new OnCLickTextLink(pdfDowloadLink));

        }

        String previewLinkText = getIntent().getStringExtra(BookActivity.BOOK_PREVIEW_LINK_KEY);
        TextView previewLinkView = (TextView) findViewById(R.id.info_preview_link);
        if(!previewLinkText.isEmpty()){
            previewLinkView.setText(CLICKABLE_LINK);
            previewLinkView.setOnClickListener(new OnCLickTextLink(previewLinkText));
        }

        String readLinkText = getIntent().getStringExtra(BookActivity.BOOK_READ_LINK_KEY);
        TextView readLinkView = (TextView) findViewById(R.id.info_read_link);
        if(!readLinkText.isEmpty()){
            readLinkView.setText(CLICKABLE_LINK);
            readLinkView.setOnClickListener(new OnCLickTextLink(readLinkText));
        }

        String buyLink = getIntent().getStringExtra(BookActivity.BOOK_BUY_LINK_KEY);
        TextView buyLinkView = (TextView) findViewById(R.id.buy_link_view);
        if(!buyLink.isEmpty()){
            buyLinkView.setText("Click to purchase book");
            buyLinkView.setOnClickListener(new OnCLickTextLink(buyLink));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.settings){
            Intent settingsIntent = new Intent();
            settingsIntent.setClass(getBaseContext(), SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    class OnCLickTextLink implements View.OnClickListener{

        private String mUrl;
        public OnCLickTextLink(String url){
            mUrl = url;
        }
        @Override
        public void onClick(View v) {
            //Intent openLintIntent = new Intent();
            Intent openLinkIntent = new Intent(Intent.ACTION_VIEW);
            //openLinkIntent.setClass(getBaseContext(), ReadActivity.class);
            //openLinkIntent.putExtra("URL", mUrl);
            openLinkIntent.setData(Uri.parse(mUrl));
            startActivity(openLinkIntent);
        }
    }
}
