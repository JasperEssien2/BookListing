package com.example.android.booklisting;

import android.graphics.Bitmap;

import java.io.InputStream;

/**
 * Created by JAHSWILL on 3/18/2018.
 */

public class Book {

    private String mTitle;
    private String mAuthors;
    private String mPublishDate;
    private String mDescription;
    private String mMaturityRating;
    private String mPreviewLink;
    private String mViewability;
    private String mWebReaderLink;
    private Bitmap mBitmapImage;
    private String mPrintType;
    private String mBuyLink;
    private String mPdfDownloadLink;
    private String mEpubDownloadLink;
    private boolean mIsEpubAvailable;
    private boolean mIsPdfAvailable;

    private final String TITLE = "Book Title: ";
    private final String AUTHOR = "AUTHOR(S): ";
    private final String TYPE = "TYPE: ";

    public Book(String title, String description, String publishDate, String authors, String printType,
                String maturityRating, String viewability, String previewLink, String webReaderLink,
                String buyLink, String pdfDownloadLink, String epubDownloadLink, boolean isEpubAvail,
                boolean isPdfAvail, Bitmap bitmapImage){
        mTitle = title;
        mDescription = description;
        mIsEpubAvailable = isEpubAvail;
        mIsPdfAvailable = isPdfAvail;
        mPublishDate = publishDate;
        mAuthors = authors;
        mPrintType = printType;
        mMaturityRating = maturityRating;
        mViewability = viewability;
        mPreviewLink = previewLink;
        mWebReaderLink = webReaderLink;
        mBuyLink = buyLink;
        mEpubDownloadLink = epubDownloadLink;
        mPdfDownloadLink = pdfDownloadLink;
        mBitmapImage = bitmapImage;
    }

    public String getTitle(){return (TITLE + mTitle);}

    public String getDescription(){return mDescription;}

    public  String getAuthors(){return (AUTHOR + mAuthors);}

    public boolean getIsEpubAvailable(){return mIsEpubAvailable;}

    public boolean getIsPdfAvailable(){return mIsPdfAvailable;}

    public String getPublishedDate(){return mPublishDate;}

    public String getPrintType(){return TYPE + mPrintType;}

    public String getMaturityRating(){return mMaturityRating;}

    public String getViewability(){return mViewability;}

    public String getPreviewLink(){return mPreviewLink;}

    public String getWebReaderLink(){return mWebReaderLink;}

    public String getBuyLink(){return mBuyLink;}

    public String getEpubDownloadLink(){return mEpubDownloadLink;}

    public String getPdfDownloadLink(){return mPdfDownloadLink;}

    public Bitmap getBitmapImage(){return mBitmapImage;}
}
