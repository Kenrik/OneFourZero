package com.codepath.apps.onefourzero;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.apps.onefourzero.models.Tweet;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "o6on09sQPPn1qz4UALSeQYnkx";
	public static final String REST_CONSUMER_SECRET = "iSYl15ue339STHQyungSoqw5Q6nRI8qgHfWcxPBNamQXiwQQz8";
	public static final String REST_CALLBACK_URL = "oauth://kenrikmarch.com";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    public void getHomeTimeline(int page, AsyncHttpResponseHandler handler) {
        String apiURL = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (page > 0) { params.put("page", page); }
        getClient().get(apiURL, params, handler);
    }

    public void getMentionsTimeline(int page, AsyncHttpResponseHandler handler) {
        String apiURL = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (page > 0) { params.put("page", page); }
        getClient().get(apiURL, params, handler);
    }

    public void postTweet(Tweet tweet, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweet.getBody());
        getClient().post(apiUrl, params, handler);
    }

    public void postReplyTweet(Tweet tweet, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweet.getBody());
        params.put("in_reply_to_status_id", tweet.getUid());
        getClient().post(apiUrl, params, handler);
    }

    public void getUserTimeline(int page, String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (page > 0) { params.put("page", page); }
        params.put("screen_name", screenName);
        getClient().get(apiUrl, params, handler);
    }

    public void getUserInfo(String screenName, AsyncHttpResponseHandler handler) {
        if (screenName == null) {
            String apiUrl = getApiUrl("account/verify_credentials.json");
            getClient().get(apiUrl, null, handler);
        } else {
            String apiUrl = getApiUrl("users/show.json");
            RequestParams params = new RequestParams();
            params.put("screen_name", screenName);
            getClient().get(apiUrl, params, handler);
        }
    }
}