package com.codepath.apps.onefourzero.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements Parcelable {
    private long uid;
    private String name;
    private String screenName;
    private String profileImageUrl;
    private String following;
    private String followers;
    private String tagline;

    public User() {}
    public long getUid() {
        return uid;
    }
    public String getName() {
        return name;
    }
    public String getScreenName() {
        return screenName;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public String getFollowing() { return following; }
    public String getFollowers() { return followers; }
    public String getTagline() { return tagline; }

    public static User fromJson(JSONObject json) {
        User user = new User();
        try {
            user.uid = json.getLong("id");
            user.name = json.getString("name");
            user.tagline = json.getString("description");
            user.followers = json.getString("followers_count");
            user.following = json.getString("friends_count");
            user.screenName = json.getString("screen_name");
            user.profileImageUrl = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeLong(uid);
        out.writeString(screenName);
        out.writeString(profileImageUrl);
        out.writeString(followers);
        out.writeString(following);
        out.writeString(tagline);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
        this.followers = in.readString();
        this.following = in.readString();
        this.tagline = in.readString();
    }
}
