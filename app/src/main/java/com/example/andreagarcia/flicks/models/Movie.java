package com.example.andreagarcia.flicks.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by andreagarcia on 6/21/17.
 */

@Parcel
public class Movie {
    // values from API
    String title;
    String overview;
    String posterPath; // only path, not full url
    String backdropPath;
    Double voteAverage;
    Integer id;
    String imageurl;

    //empty constructor required for Parceler
    public Movie() {}

    //initialize from JSON data
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        voteAverage = object.getDouble("vote_average");
        id = object.getInt("id");
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Double getVoteAverage() { return voteAverage; }

    public Integer getId() {
        return id;
    }
}
