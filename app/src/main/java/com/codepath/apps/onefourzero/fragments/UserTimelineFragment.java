package com.codepath.apps.onefourzero.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.codepath.apps.onefourzero.TwitterApplication;
import com.codepath.apps.onefourzero.TwitterClient;
import com.codepath.apps.onefourzero.models.Tweet;
import com.codepath.apps.onefourzero.models.TwitterError;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 3/15/15.
 */
public class UserTimelineFragment extends TweetListFragment {

    private TwitterClient client;
    private String screenName;

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment frag = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        loadAllTheThings(0);
    }

    private void loadAllTheThings(int page) {
        if (isNetworkAvailable()) {
            screenName = getArguments().getString("screen_name");
            client.getUserTimeline(page, screenName, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
                    addAll(tweets);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    showError(errorResponse);
                }
            });
        } else {
            networkError();
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null) && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public void loadTweetsForPage(int page) {
        loadAllTheThings(page);
    }

    @Override
    public void reload() {
        clear();
        loadAllTheThings(0);
    }

    public void networkError() {
        Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
    }

    public void showError(JSONObject errorResponse) {
        ArrayList<TwitterError> errors = TwitterError.fromJSONObject(errorResponse);
        if (errors != null) {
            TwitterError error = errors.get(0);
            Toast.makeText(getActivity(), error.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
