package com.codepath.apps.onefourzero.activities;

import android.content.Intent;
import android.net.NetworkInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.view.MenuItem;
import android.widget.Toast;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.codepath.apps.onefourzero.R;
import com.codepath.apps.onefourzero.TwitterClient;
import com.codepath.apps.onefourzero.TwitterApplication;
import com.codepath.apps.onefourzero.adapters.TweetsArrayAdapter;
import com.codepath.apps.onefourzero.support.EndlessScrollListener;
import com.codepath.apps.onefourzero.fragments.TweetFragment;
import com.codepath.apps.onefourzero.models.TwitterError;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.joanzapata.android.iconify.IconDrawable;
import com.codepath.apps.onefourzero.models.Tweet;
import com.joanzapata.android.iconify.Iconify;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity implements TweetFragment.TweetDialogListener {

    private ListView lvTweets;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private static final int zero = 0;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("My Stream");

        setContentView(R.layout.activity_timeline);
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int sinceCurrentCount) {
                loadAllTheThings(page);
            }
        });
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this,tweets);
        lvTweets.setAdapter(aTweets);
        client = TwitterApplication.getRestClient();
        loadAllTheThings(zero);

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet tweet = (Tweet) tweets.get(position);
                Intent i = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                i.putExtra("tweet", tweet);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    private void loadAllTheThings(int page) {
        if (isNetworkAvailable()) {
            client.getHomeTimeline(page, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    ArrayList<Tweet> tweets = Tweet.fromJSONArray(response);
                    aTweets.addAll(tweets);
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

    private void showTweetDialog() {
        FragmentManager fm = getSupportFragmentManager();
        TweetFragment tweetFragment = TweetFragment.newInstance("");
        Bundle bundle = new Bundle();
        Tweet tweet = new Tweet();
        bundle.putParcelable("tweet",tweet);
        bundle.putString("title"," • Compose Tweet");
        bundle.putString("hint","Tell the internet that it's wrong");
        tweetFragment.setArguments(bundle);
        tweetFragment.show(fm, "fragment_tweet");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        MenuItem composeItem = menu.findItem(R.id.compose_tweet);
        composeItem.setIcon(new IconDrawable(this, Iconify.IconValue.fa_edit)
                   .colorRes(R.color.button_material_light)
                   .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.compose_tweet) {
            showTweetDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void reload() {
        aTweets.clear();
        loadAllTheThings(zero);
    }

    public void sendTweet(Tweet tweet) {
        if (isNetworkAvailable()) {
            client.postTweet(tweet, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    reload();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getApplicationContext(), "Unable to post tweet",Toast.LENGTH_LONG).show();
                }
            });
        } else {
            networkError();
        }
    }

    public void networkError() {
        Toast.makeText(getApplicationContext(), "Network Error",Toast.LENGTH_LONG).show();
    }

    public void showError(JSONObject errorResponse) {
        ArrayList<TwitterError> errors = TwitterError.fromJSONObject(errorResponse);
        if (errors != null) {
            TwitterError error = errors.get(0);
            Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null) && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onFinishTweetDialog(Tweet tweet) {
        sendTweet(tweet);
    }
}
