package com.codepath.apps.onefourzero.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 3/8/15.
 */
public class TwitterError {
    private String message;
    private String code;

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    private static TwitterError fromJson(JSONObject json) {
        TwitterError error = new TwitterError();
        try {
            error.message = json.getString("message");
            error.code = json.getString("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return error;
    }

    public static ArrayList<TwitterError> fromJSONObject(JSONObject object) {
        ArrayList<TwitterError> errors = new ArrayList<>();
        JSONArray array = null;
        try {
            array = object.getJSONArray("errors");
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject errorJson = array.getJSONObject(i);
                    TwitterError error = fromJson(errorJson);
                    if (error != null) {
                        errors.add(error);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            return errors;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
