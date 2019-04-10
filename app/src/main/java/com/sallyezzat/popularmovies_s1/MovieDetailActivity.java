package com.sallyezzat.popularmovies_s1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class MovieDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
        Serializable movie = intent.getSerializableExtra("movie");//if it's a string you stored.
        Movie m = (Movie) movie;
       // String s = m.getPosterPath();
        TextView title = findViewById(R.id.tv_title);
        TextView date = findViewById(R.id.tv_date);
        TextView vote = findViewById(R.id.tv_vote_average);
        TextView plot_synopsis = findViewById(R.id.tv_plot_synopsis);
        ImageView image = findViewById(R.id.im_imageView);

        title.setText(m.getTitle());
        date.setText(m.getReleaseDate());
        vote.setText(String.valueOf(m.getVoteAverage()));
        plot_synopsis.setText(m.getOverview());
        Picasso.with(this).load(m.getPosterPath()).into(image);
    }
}
