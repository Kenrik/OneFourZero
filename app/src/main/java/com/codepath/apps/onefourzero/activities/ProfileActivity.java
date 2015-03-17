package com.codepath.apps.onefourzero.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.apps.onefourzero.R;
import com.codepath.apps.onefourzero.TwitterApplication;
import com.codepath.apps.onefourzero.TwitterClient;
import com.codepath.apps.onefourzero.fragments.TweetListFragment;
import com.codepath.apps.onefourzero.fragments.UserTimelineFragment;
import com.codepath.apps.onefourzero.models.Tweet;
import com.codepath.apps.onefourzero.models.User;
import com.codepath.apps.onefourzero.support.CircleTransform;
import com.joanzapata.android.iconify.Iconify;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ProfileActivity extends ActionBarActivity implements TweetListFragment.TweetListListener {

    private TwitterClient client;
    private UserTimelineFragment timelineFragment;
    private String screenName;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView itvFollowers = (TextView) findViewById(R.id.itvFollowers);
        TextView itvFollowing = (TextView) findViewById(R.id.itvFollowing);
        Iconify.addIcons(itvFollowers);
        Iconify.addIcons(itvFollowing);
        client = TwitterApplication.getRestClient();
        if (savedInstanceState == null) {
            screenName = getIntent().getStringExtra("screen_name");
            showUserFragment(screenName);
        }
        client.getUserInfo(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJson(response);
                populateProfileHeader(user);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    private void populateProfileHeader(User user) {
        TextView tvUser = (TextView) findViewById(R.id.tvUserName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        getSupportActionBar().setTitle("@" + user.getScreenName());
        tvFollowers.setText("Followers " + user.getFollowers());
        tvFollowing.setText("Following " + user.getFollowing());
        tvTagline.setText(user.getTagline());
        tvUser.setText(user.getName());
        Picasso.with(this).load(user.getProfileImageUrl()).transform(new CircleTransform()).into(ivProfileImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void showUserFragment(String screenName) {
        FragmentManager fm = getSupportFragmentManager();
        timelineFragment = UserTimelineFragment.newInstance(screenName);
        Bundle bundle = new Bundle();
        bundle.putString("screen_name", screenName);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, timelineFragment);
        ft.commit();
    }

    @Override
    public void onTweetTapped(Tweet tweet) {

    }
}