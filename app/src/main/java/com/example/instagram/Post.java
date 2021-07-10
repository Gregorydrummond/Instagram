package com.example.instagram;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze=Post.class)
@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String TAG = "Post";

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_LIKED_BY = "likedBy";
    public boolean likedByCurrentUser = false;

    public Post() {}

    //Create likedBy array
    public Post(ParseUser parseUser) {
        put(KEY_LIKED_BY, new JSONArray());
    }

    //Get description
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    //Set description
    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    //Get image
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    //Set image
    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    //Get user
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    //Set user
    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    //Get liked by array
    public JSONArray getLikedByArray() {
        return getJSONArray(KEY_LIKED_BY);
    }

    //Update liked by array
    public void setLikedByArray(ParseUser parseUser, boolean liked) throws JSONException {
        JSONArray jsonArray = getJSONArray(KEY_LIKED_BY);
        assert jsonArray != null;

        //Adds or remove user depending on whether user liked or removed the like
        if(liked) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user", parseUser.getUsername());
            jsonArray.put(jsonObject);
            put(KEY_LIKED_BY, jsonArray);
        }
        else {
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("user").equals(parseUser.getUsername())) {
                    jsonArray.remove(i);
                    put(KEY_LIKED_BY, jsonArray);
                    return;
                }
            }
        }
    }

    //Get createdAt
    public String getDate() {
        return calculateTimeAgo(this.getCreatedAt());
    }

    //Get createdAt helper
    public static String calculateTimeAgo(Date createdAt) {
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i(TAG, "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
