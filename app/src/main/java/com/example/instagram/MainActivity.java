package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Button btnLogOut;
    ImageView ivPostImage;
    EditText etDescription;
    Button btnGetImage;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find Components
        btnLogOut = findViewById(R.id.btnLogOut);
        ivPostImage = findViewById(R.id.ivPostImage);
        etDescription = findViewById(R.id.etDescription);
        btnGetImage = findViewById(R.id.btnGetImaage);
        btnSubmit = findViewById(R.id.btnSubmit);

        //Set logout click
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Log.i(TAG, "onClick: User logged out");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Query Posts
        queryPosts();
    }

    private void queryPosts() {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        //Include user info
        query.include(Post.KEY_USER);

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
            }
        });
    }
}