package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcel;
import org.parceler.Parcels;

public class PostDetailActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailActivity";
    Post post;
    TextView tvUserName;
    TextView tvDescription;
    TextView tvTimeAgo;
    TextView tvName;
    ImageView ivPostImage;
    ImageButton ibLike;
    ImageButton ibComment;
    ImageButton ibShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //Unwrap the post passed in via intent, using its simple name as a key
        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        Log.d(TAG, "Unwrapped Post");

        //Get views
        tvUserName = findViewById(R.id.tvItemUserName);
        tvDescription = findViewById(R.id.tvItemDescription);
        ivPostImage = findViewById(R.id.ivItemPostImage);
        tvTimeAgo = findViewById(R.id.tvTimeAgo);
        tvName = findViewById(R.id.tvName);
        ibLike = findViewById(R.id.ibLike);
        ibComment = findViewById(R.id.ibComment);
        ibShare = findViewById(R.id.ibShare);

        //Set data
        tvUserName.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvTimeAgo.setText(post.getDate());
        tvName.setText(post.getUser().getUsername());

        //Setting parse image
        ParseFile image = post.getImage();
        if(image != null) {
            Glide.with(this).load(image.getUrl()).into(ivPostImage);
        }
    }
}