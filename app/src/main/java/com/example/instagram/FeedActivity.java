package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private static final String TAG = "FeedActivity";

    List<Post> posts;
    RecyclerView rvPosts;
    PostsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //Find components
        rvPosts = findViewById(R.id.rvPosts);

        //Initialize posts array
        posts = new ArrayList<>();

        //Initialize adapter
        adapter = new PostsAdapter(this, posts);

        //Set layout manager on the rv
        rvPosts.setLayoutManager(new LinearLayoutManager(this));

        //Set adapter on the rv
        rvPosts.setAdapter(adapter);

        //Query posts
        queryPosts();

    }

    private void queryPosts() {
        //Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        //Include user info
        query.include(Post.KEY_USER);

        //Limit query to latest 20 items
        query.setLimit(20);

        //Order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        //Get post objects
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                //Error in query
                if(e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                //Successful query
                for (Post post : posts) {
                    Log.i(TAG, "Post Description: " + post.getDescription() + ", user: " + post.getUser().getUsername());
                }

                //Save received posts to list
                FeedActivity.this.posts.addAll(posts);

                //Notify adapter of change
                adapter.notifyDataSetChanged();
            }
        });
    }
}