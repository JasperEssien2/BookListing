package com.example.android.booklisting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

/**
 * Created by JAHSWILL on 3/18/2018.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(@NonNull Context context, List<Book> books){
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listViewItem = convertView;

        Book book = getItem(position);

        if(listViewItem == null){
            listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.book_list, parent, false);
        }

        TextView bookTitleView = (TextView) listViewItem.findViewById(R.id.book_title);
        TextView authorView = (TextView) listViewItem.findViewById(R.id.author);
        TextView typeView = (TextView) listViewItem.findViewById(R.id.type);
        ImageView bookIconView = (ImageView) listViewItem.findViewById(R.id.book_icon);

        try{
            Bitmap bitmapImage = book.getBitmapImage();
            //if not equal null set the bookIcon to the bitmapImage
            bookIconView.setImageBitmap(bitmapImage);
        }catch (NullPointerException e){
            bookIconView.setImageResource(R.drawable.ic_book);
        }
//        //Check if book.getBitmapImage() returns null
//        if(bitmapImage != null){
//        }else //else set it to default
//            bookIconView.setImageResource(R.drawable.ic_book);

        bookTitleView.setText(book.getTitle());
        authorView.setText(book.getAuthors());
        typeView.setText(book.getPrintType());

        return listViewItem;
    }

    private String convertBoolean(boolean val){
        String newVal = "";
        if(val == true){
            newVal = "YES";
        }else{
            newVal = "NO";
        }
        return  newVal;
    }
}
