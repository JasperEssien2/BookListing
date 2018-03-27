package com.example.android.booklisting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.booklisting.Activities.BookActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by JAHSWILL on 3/18/2018.
 */

public class QueryUtils {

    public static final String LOG_TAG = BookActivity.class.getName();

    public static ArrayList<Book> getBookData(String stringUrl){
        if(TextUtils.isEmpty(stringUrl)){
            return null;}

        URL url = createUrl(stringUrl);
        String jsonResponse = "";
        try{
            jsonResponse = makeHTTPRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG, "trouble making a http connection", e);
        }
        ArrayList<Book> books = extractJsonResult(jsonResponse);
        return books;
    }

    /**
     * This method takes in a jsonResponse as String and then extract the JsonResponse
     * @param jsonResponse
     * @return
     */
    private static ArrayList<Book> extractJsonResult(String jsonResponse){

        ArrayList<Book> books = new ArrayList<Book>();
        JSONArray jsonBookArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            /**This gets the array of items from jsonResponse*/
            jsonBookArray = jsonObject.getJSONArray("items");
        }catch (JSONException e){
            jsonBookArray = new JSONArray();
            e.printStackTrace();
        }
            int jsonArrayLen = jsonBookArray.length();
            for(int i = 0; i < jsonArrayLen; ++i){
                JSONObject jsonItemObject;
                try{
                    jsonItemObject = jsonBookArray.getJSONObject(i);
                }catch (JSONException e){
                    jsonItemObject = new JSONObject();
                }
                    JSONObject bookInfo;
                    try{
                        /**This section handles getting the book info*/
                        bookInfo = jsonItemObject.getJSONObject("volumeInfo");
                    }catch (JSONException e){
                        bookInfo = new JSONObject();
                    }
                        String bookTitle;
                        try{
                            bookTitle = bookInfo.getString("title");
                        }catch (JSONException e){
                            bookTitle = "";
                        }
                        String publishedDate;
                        try{
                            publishedDate = bookInfo.getString("publishedDate");
                        }catch (JSONException e){
                            publishedDate = "";
                        }
                        String description;
                        try{
                            description = bookInfo.getString("description");
                        }catch (JSONException e){
                            description = "";
                        }

                        String maturityRating;
                        try{
                            maturityRating = bookInfo.getString("maturityRating");
                        }catch (JSONException e){
                            maturityRating = "";
                        }
                        JSONObject imageLinks;
                        String thumbnailLink;
                        Bitmap bitmapImage = null;
                        try{
                            /**This sections gets the image link and converts it to a Bitmap*/
                            imageLinks = bookInfo.getJSONObject("imageLinks");
                            thumbnailLink = imageLinks.getString("thumbnail");
                            bitmapImage = getBitmap(thumbnailLink);
                        } catch (JSONException e){
                            imageLinks = new JSONObject();
                            thumbnailLink = "";
                        }

                        String previewLink;
                        try{
                            previewLink = bookInfo.getString("previewLink");
                        }catch (JSONException e){
                            previewLink = "";
                        }
                        JSONArray authorList;
                        try {
                            authorList = bookInfo.getJSONArray("authors");
                        }catch (JSONException e){
                            authorList = new JSONArray();
                        }
                            String authors;
                            StringBuilder stringBuilder = new StringBuilder();
                            int authorListLen = authorList.length();
                            final int ONE = 1;
                            for(int a = 0; a < authorListLen; ++a){
                                try {
                                    stringBuilder.append(authorList.get(a));
                                    if(authorListLen > ONE && a != authorListLen - ONE)
                                        stringBuilder.append(", ");
                                    if(a == authorList.length() - ONE)
                                        stringBuilder.append(".");
                                }catch (JSONException e){
                                    stringBuilder = new StringBuilder();
                                }
                            }
                            authors = stringBuilder.toString();

                        String printType;
                        try{
                            printType = bookInfo.getString("printType");
                        }catch (JSONException e){
                            printType = "";
                        }

                    JSONObject saleInfo = null;
                    try{
                        saleInfo = jsonItemObject.getJSONObject("saleInfo");
                    } catch (JSONException e){

                    }
                        String buyLink;
                        try{
                            buyLink = saleInfo.getString("buyLink");
                        } catch (JSONException e){
                            buyLink = "";
                        }

                    JSONObject accessInfo = null;
                    boolean isEpubAvailable = false;
                    boolean isPdfAvailable = false;
                    try{
                        /**This gets the access info object*/
                        accessInfo = jsonItemObject.getJSONObject("accessInfo");
                    }catch (JSONException e){e.printStackTrace();}

                            JSONObject epubJson = null;
                            JSONObject pdfJson = null;
                            String epubDownloadLink = "";
                            String pdfDownloadLink = "";
                            try {
                                epubJson = accessInfo.getJSONObject("epub");
                            }catch (JSONException e) {}
                                try {
                                    isEpubAvailable = epubJson.getBoolean("isAvailable");
                                }catch (JSONException e) {}
                                try{
                                    epubDownloadLink = epubJson.getString("downloadLink");
                                } catch (JSONException e){epubDownloadLink = "";}

                            try {
                                pdfJson = accessInfo.getJSONObject("pdf");
                            }catch (JSONException e) {}
                                try{
                                    isPdfAvailable = pdfJson.getBoolean("isAvailable");
                                } catch (JSONException e){e.printStackTrace();}
                                try{
                                    pdfDownloadLink = pdfJson.getString("downloadLink");
                                } catch (JSONException e){pdfDownloadLink = "";}

                            String viewability = "";
                            try {
                                viewability = accessInfo.getString("viewability");
                            } catch (JSONException e){
                                viewability = "";
                                e.printStackTrace();
                            }

                            String webReaderLink = "";
                            try {
                                webReaderLink = accessInfo.getString("webReaderLink");
                            } catch (JSONException e){
                                webReaderLink = "";
                                e.printStackTrace();
                            }

                books.add(new Book(bookTitle, description, publishedDate, authors, printType,
                        maturityRating, viewability, previewLink, webReaderLink, buyLink,
                        pdfDownloadLink, epubDownloadLink, isEpubAvailable, isPdfAvailable, bitmapImage));
            }
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
        return books;
    }

    private static String makeHTTPRequest(URL url) throws IOException{
        String jsonResponse = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            }else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }
        finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static Bitmap getBitmap(String bitmapUrl){
        try{
            URL url = createUrl(bitmapUrl);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static URL createUrl(String stringUrl){
        URL url;
        try{
            url = new URL(stringUrl);
            return url;
        }catch (MalformedURLException e){
            Log.v(LOG_TAG, e.toString());
        }
        return null;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();

        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }
}
