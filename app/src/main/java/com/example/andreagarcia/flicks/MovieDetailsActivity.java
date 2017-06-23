package com.example.andreagarcia.flicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.andreagarcia.flicks.models.Movie;

import org.parceler.Parcels;

public class MovieDetailsActivity extends AppCompatActivity {
    //movie to display
    Movie movie;

    //view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        //resolve view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        //unwrap movie passed in via intent, use simple name as key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for %s", movie.getTitle()));
        //set title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        //convert rating from 0-5
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
    }
}
