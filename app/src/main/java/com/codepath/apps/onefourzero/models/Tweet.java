package com.codepath.apps.onefourzero.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Tweet implements Parcelable {

    private String body;
    private String retweets;
    private String stars;
    private long uid;
    private User user;
    private String createdAt;

    public Tweet() {}
    public long getUid() {
        return uid;
    }
    public User getUser() {
        return user;
    }
    public String getBody() {
        return body;
    }
    public String getRetweets() { return retweets; }
    public String getCreatedAt() {
        return createdAt;
    }
    public String getStars() { return stars; }
    public void setBody(String body) {
        this.body = body;
    }

    private static Tweet fromJson(JSONObject json) {
        Tweet tweet = new Tweet();
        try {
            tweet.uid = json.getLong("id");
            tweet.body = json.getString("text");
            tweet.createdAt = getRelativeTimeAgo(json.getString("created_at"));
            tweet.user = User.fromJson(json.getJSONObject("user"));
            tweet.retweets = json.getString("retweet_count");
            tweet.stars = json.getString("favorite_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray array) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject tweetJson = array.getJSONObject(i);
                Tweet tweet = fromJson(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(body);
        out.writeLong(uid);
        out.writeParcelable(user, flags);
        out.writeString(createdAt);
        out.writeString(retweets);
        out.writeString(stars);
    }

    public static final Parcelable.Creator<Tweet> CREATOR
            = new Parcelable.Creator<Tweet>() {
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    public Tweet(Parcel in) {
        this.body = in.readString();
        this.uid = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.createdAt = in.readString();
        this.retweets = in.readString();
        this.stars = in.readString();
    }
}
