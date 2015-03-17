package com.codepath.apps.onefourzero.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.onefourzero.R;
import com.codepath.apps.onefourzero.TwitterApplication;
import com.codepath.apps.onefourzero.TwitterClient;
import com.codepath.apps.onefourzero.fragments.TweetFragment;
import com.codepath.apps.onefourzero.models.Tweet;
import com.codepath.apps.onefourzero.support.CircleTransform;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;

public class TweetDetailActivity extends ActionBarActivity implements TweetFragment.TweetDialogListener {

    private static Tweet tweet;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        client = TwitterApplication.getRestClient();
        Bundle bundle = getIntent().getExtras();
        tweet = bundle.getParcelable("tweet");
        getSupportActionBar().setTitle(tweet.getUser().getScreenName());
        setupViewWithTweet(tweet);
    }

    public void setupViewWithTweet(Tweet tweet) {

        ImageView ivProfileImage = (ImageView) this.findViewById(R.id.ivProfileImage_detail);
        TextView tvUserName = (TextView) this.findViewById(R.id.tvUserName_detail);
        TextView tvBody = (TextView) this.findViewById(R.id.tvBody_detail);
        TextView itvRetweet = (TextView) this.findViewById(R.id.itvRetweet_detail);
        TextView itvReply = (TextView) this.findViewById(R.id.itvReply_detail);
        TextView itvStar = (TextView) this.findViewById(R.id.itvStar_detail);
        TextView tvTime = (TextView) this.findViewById(R.id.tvTime_detail);
        TextView tvRetweet = (TextView) this.findViewById(R.id.tvRetweet_detail);
        TextView tvStar = (TextView) this.findViewById(R.id.tvStar_detail);

        tvBody.setText(tweet.getBody());
        tvTime.setText(tweet.getCreatedAt());
        tvUserName.setText(tweet.getUser().getScreenName());
        tvRetweet.setText(tweet.getRetweets());
        tvStar.setText(tweet.getStars());
        ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl()).transform(new CircleTransform()).into(ivProfileImage);
        Iconify.addIcons(itvStar);
        Iconify.addIcons(itvReply);
        Iconify.addIcons(itvRetweet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweet_detail, menu);
        MenuItem composeItem = menu.findItem(R.id.reply_tweet);
        composeItem.setIcon(new IconDrawable(this, Iconify.IconValue.fa_reply)
                .colorRes(R.color.button_material_light)
                .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.reply_tweet) {
            FragmentManager fm = getSupportFragmentManager();
            TweetFragment tweetFragment = TweetFragment.newInstance("Reply");
            Bundle bundle = new Bundle();
            bundle.putParcelable("tweet",tweet);
            bundle.putString("title"," • Reply to Tweet");
            bundle.putString("hint","Reply");
            tweetFragment.setArguments(bundle);
            tweetFragment.show(fm,"fragment_tweet");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendTweet(Tweet tweet) {
        if (isNetworkAvailable()) {
            client.postReplyTweet(tweet, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getApplicationContext(), "Reply Sent", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getApplicationContext(), "Unable to post tweet", Toast.LENGTH_LONG).show();
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

    public void onFinishTweetDialog(Tweet tweet) {
        tweet.setBody("@" + tweet.getUser().getScreenName().toString() + " " + tweet.getBody());
        sendTweet(tweet);
    }
}
