package com.example.andreagarcia.flicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.andreagarcia.flicks.models.Config;
import com.example.andreagarcia.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends AppCompatActivity {

    //constants
    //base URL for API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //parameter name for API key
    public final static String API_KEY_PARAM = "api_key";
    // tag for logging from this activity
    public final static String TAG = "MovieListActivity";

    //instance fields
    AsyncHttpClient client;
    //base url for loading images
    String imageBaseUrl;
    //poster size to use when fetching images, part of url
    String posterSize;
    //list of currently playing movies
    ArrayList<Movie> movies;
    //recycler view
    RecyclerView rvMovies;
    //adapter wired to recycler view
    MovieAdapter adapter;
    //image config
    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        //initialize client
        client = new AsyncHttpClient();
        // initialize list of movies
        movies = new ArrayList<>();
        //initialize adapter. after array is created b/c it is passed into adapter. movies array cannot be reinitialized after this point
        adapter = new MovieAdapter(movies);

        // resolve recycler view and connect a layout manager and adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        //get configuration on app creation
        getConfiguration();

    }

    // get list of currently playing movies from API
    private void getNowPlaying() {
        //create URL to access
        String url = API_BASE_URL + "/movie/now_playing";
        //set request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); //API key always required
        //execute a GET request expecting JSON object response
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //load results into movie list
                try {
                    JSONArray results = response.getJSONArray("results");
                    //iterate through result set and create Movie objects
                    for (int i = 0; i< results.length(); i++) {
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        //notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size() -1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse now_playing movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now_playing endpoint", throwable, true);
            }
        });
    }


    //get configuration from API
    private void getConfiguration() {
        //create URL to access
        String url = API_BASE_URL + "/configuration";
        //set request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); //API key always required
        //execute a GET request expecting JSON object response
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    config = new Config(response);
                    JSONObject images = response.getJSONObject("images");
                    //get image base url
                    imageBaseUrl = images.getString("secure_base_url");
                    //get poster size
                    JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
                    //use option at index 3 or w342 as fallback
                    posterSize = posterSizeOptions.optString(3, "w342");
                    Log.i(TAG,
                            String.format("Loaded configuration with imageBaseUrl %s and posterSize %s",
                                    config.getImageBaseUrl(),
                                    config.getPosterSize()));
                    //pass config to adapter
                    adapter.setConfig(config);
                    //get now playing movie list
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed while parsing configuration", e, true);
                }
             }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });

    }
    
    //handle errors, long and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        //always log error
        Log.e(TAG, message, error);
        //alert user to avoid silent error
        if (alertUser) {
            //show a long toast with error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
