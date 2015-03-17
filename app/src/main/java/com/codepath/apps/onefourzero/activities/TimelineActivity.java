package com.codepath.apps.onefourzero.activities;

import android.content.Intent;
import android.net.NetworkInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.os.Bundle;
import android.view.Menu;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.onefourzero.R;
import com.codepath.apps.onefourzero.TwitterClient;
import com.codepath.apps.onefourzero.TwitterApplication;
import com.codepath.apps.onefourzero.fragments.HomeTimelineFragment;
import com.codepath.apps.onefourzero.fragments.MentionsTimelineFragment;
import com.codepath.apps.onefourzero.fragments.TweetListFragment;
import com.codepath.apps.onefourzero.fragments.TweetFragment;
import com.codepath.apps.onefourzero.models.User;
import com.codepath.apps.onefourzero.support.SmartFragmentStatePagerAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.joanzapata.android.iconify.IconDrawable;
import com.codepath.apps.onefourzero.models.Tweet;
import com.joanzapata.android.iconify.Iconify;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class TimelineActivity extends ActionBarActivity implements TweetFragment.TweetDialogListener,TweetListFragment.TweetListListener {

    private TwitterClient client;
    private ViewPager vpPager;
    private TweetsPagerAdapter tpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("My Stream");
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        tpAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(tpAdapter);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);
        client = TwitterApplication.getRestClient();
        client.getUserInfo(null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User user = User.fromJson(response);
                getSupportActionBar().setTitle("@" + user.getScreenName());
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        HomeTimelineFragment fragment = (HomeTimelineFragment) tpAdapter.getRegisteredFragment(vpPager.getCurrentItem());
        if (fragment != null) {fragment.reload();}
    }

    private void showTweetDialog() {
        FragmentManager fm = getSupportFragmentManager();
        TweetFragment tweetFragment = TweetFragment.newInstance("");
        Bundle bundle = new Bundle();
        Tweet tweet = new Tweet();
        bundle.putParcelable("tweet",tweet);
        bundle.putString("title"," • Compose Tweet");
        bundle.putString("hint","Tweet");
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
        MenuItem profileItem = menu.findItem(R.id.profile_item);
        profileItem.setIcon(new IconDrawable(this, Iconify.IconValue.fa_user)
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
        } else if (id == R.id.profile_item) {
            showProfileActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showProfileActivity() {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    @Override
    public void onTweetTapped(Tweet tweet) {
        Intent i = new Intent(TimelineActivity.this, TweetDetailActivity.class);
        i.putExtra("tweet", tweet);
        startActivity(i);
    }

    public void sendTweet(Tweet tweet) {
        if (isNetworkAvailable()) {
            client.postTweet(tweet, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    HomeTimelineFragment fragment = (HomeTimelineFragment) tpAdapter.getRegisteredFragment(vpPager.getCurrentItem());
                    if (fragment != null) {fragment.reload();}
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

    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        private String tabTitles[] = {"Home","Mentions"};
        int MentionsFragment = 1;
        int HomeFragment = 0;

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            if (position == HomeFragment) {
                return new HomeTimelineFragment();
            } else if (position == MentionsFragment) {
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
