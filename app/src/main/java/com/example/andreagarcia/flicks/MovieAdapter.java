package com.example.andreagarcia.flicks;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.andreagarcia.flicks.models.Config;
import com.example.andreagarcia.flicks.models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by andreagarcia on 6/21/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    //list of movies
    ArrayList<Movie> movies;
    //config needed for image urls
    Config config;
    //context for rendering
    Context context;

    //initialize with list

    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    //creates and inflates new view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        //get context from parent and create inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //create view using item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(movieView);
        ButterKnife.bind(viewHolder, movieView);
        return viewHolder;
    }
    //binds an inflated view to new item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //get movie data at specified position (access movies array)
        Movie movie = movies.get(position);
        //populate controls in viewholder object with above data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());

        //determine phone orientation
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        //build url for poster image
        String imageUrl = null;

        //if in portrait mode, load poster image
        if (isPortrait) {
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
            movie.setImageurl(config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath()));


        } else {
            //load backdrop
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
            movie.setImageurl(config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath()));

        }

        //get correct placeholder and imageview for current orientation
        int placeholderId = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortrait ? holder.ivPosterImage : holder.ivBackdropImage;


        //load image using glide
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                 //placeholder image also appears if poster fails to load
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(imageView);

    }
    //returns size of data set. if set to nonzero value/not implemented, nothing is displayed
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //track view objects
        @Nullable
        @BindView(R.id.ivPosterImage) ImageView ivPosterImage;
        @Nullable
        @BindView(R.id.ivBackdropImage) ImageView ivBackdropImage;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvOverview) TextView tvOverview;

        public ViewHolder(View itemView) {
            super(itemView);
            //on click event, listener gets notified and calls onClick method
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //get item position
            int position  = getAdapterPosition();
            //make sure position is in view
            if (position != RecyclerView.NO_POSITION) {
                //get movie
                Movie movie = movies.get(position);
                //create intent for new activity
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                //serialize movie using parceler
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                //show activity
                context.startActivity(intent);
            }
        }
    }
}
