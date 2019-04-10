package com.sallyezzat.popularmovies_s1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahmed on 12/18/2017.
 */

class MovieAdapter extends BaseAdapter {

    private final List<Movie> mMovies = new ArrayList<>();
    private final Context mContext;


    public MovieAdapter(Context c, List<Movie> moviesList) {
        mContext = c;
        mMovies.addAll(moviesList);
    }

    public int getCount() {
        return mMovies.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            //    imageView.setLayoutParams(new GridView.LayoutParams(350, 300)); //width, height
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); //crop if necessary
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

     //   String s = mMovies.get(position).getPosterPath();
        Picasso.with(mContext).load(mMovies.get(position).getPosterPath()).into(imageView);

        return imageView;
    }

    public void add(List<Movie> moviess) {
        mMovies.addAll(moviess);
        notifyDataSetChanged();
    }
}
