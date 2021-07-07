package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private static final String TAG = "FeedActivity";

    List<Post> posts;
    RecyclerView rvPosts;
    PostsAdapter adapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //Find components
        rvPosts = findViewById(R.id.rvPosts);
        toolbar = findViewById(R.id.tbFeedToolbar);
        toolbar.setTitle("");

        // Sets the Toolbar to act as the ActionBar for this Activity window
        setSupportActionBar(toolbar);

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

    //Menu icons are inflated
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    //Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemCreate:
                Intent intentCreate = new Intent(FeedActivity.this, MainActivity.class);
                startActivity(intentCreate);
                return true;
            case R.id.itemLogout:
                ParseUser.logOut();
                Log.i(TAG, "onClick: User logged out");
                Intent intentLogout = new Intent(FeedActivity.this, LoginActivity.class);
                startActivity(intentLogout);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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