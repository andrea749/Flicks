package com.example.andreagarcia.flicks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.andreagarcia.flicks.models.Movie;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieDetailsActivity extends AppCompatActivity {
    //movie to display
    Movie movie;

    //view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView posterPic;

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
        posterPic = (ImageView) findViewById(R.id.posterPic);
        Glide.with(this)
                .load(movie.getImageurl())
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                //placeholder image also appears if poster fails to load
                .placeholder(R.drawable.flicks_movie_placeholder)
                .error(R.drawable.flicks_movie_placeholder)
                .into(posterPic);


        posterPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                //serialize movie using parceler
                intent.putExtra("movieId", movie.getId());
                //show activity
                startActivity(intent);
            }
        });

    }
}
