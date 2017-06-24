package com.example.andreagarcia.flicks;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MovieTrailerActivity extends YouTubeBaseActivity {
    String videoId;
    //constants
    //base URL for API
    String API_BASE = "https://api.themoviedb.org/3";
    //parameter name for API key
    public final static String API_KEY_PARAM = "api_key";
    // tag for logging from this activity
    public final static String TAG = "MovieListActivity";

    //instance fields
    AsyncHttpClient client;

    private void logError(String message, Throwable error, boolean alertUser) {
        //always log error
        Log.e(TAG, message, error);
        //alert user to avoid silent error
        if (alertUser) {
            //show a long toast with error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void getVideo(Integer movieId) {
        String url = API_BASE + "/movie/" + movieId + "/videos";
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //load results into movie list
                try {

                    JSONArray results = response.getJSONArray("results");

                    if (results.length() == 0) {
                        videoId = "";
                    } else {
                        videoId = results.getJSONObject(0).getString("key");
                    }
                    //Log.i(AsyncHttpResponseHandler.TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse now_playing movies", e, true);
                }
                videoPrompt();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now_playing endpoint", throwable, true);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailer);
        Integer movieId = getIntent().getIntExtra("movieId", 0);

        client = new AsyncHttpClient();

        getVideo(movieId);


        //resolve player view from layout
    }
    public void videoPrompt() {
        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player);
        //initialize with API in secrets.xml
        playerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                //code here cues, plays, etc video
                youTubePlayer.cueVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                //log error
                Log.e("MovieTrailerActivity", "Error initializing YouTube player");
            }
        });
    }
}

