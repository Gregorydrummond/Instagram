package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private static final String TAG = "PostsAdapter";

    Context context;
    List<Post> posts;

    //Constructor
    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    //Inflate view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    //Bind data
    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        try {
            holder.bind(post);
        } catch (JSONException e) {
            Log.e(TAG, "onBindViewHolder: Error retrieving json array", e);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    //Helper methods for swipe refresh
    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Post> posts) {
        this.posts.addAll(posts);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvUserName;
        TextView tvDescription;
        TextView tvTimeAgo;
        TextView tvName;
        ImageView ivPostImage;
        ImageView ivLike;
        ImageView ivComment;
        ImageView ivSave;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Get views
            tvUserName = itemView.findViewById(R.id.tvItemUserName);
            tvDescription = itemView.findViewById(R.id.tvItemDescription);
            ivPostImage = itemView.findViewById(R.id.ivItemPostImage);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvName = itemView.findViewById(R.id.tvName);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivSave = itemView.findViewById(R.id.ivSave);

            //Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) throws JSONException {
            //Set data
            tvUserName.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
            tvTimeAgo.setText(post.getDate());
            tvName.setText(post.getUser().getUsername());

            //Setting parse image
            ParseFile image = post.getImage();
            if(image != null) {
                Glide.with(context).load(image.getUrl()).into(ivPostImage);
            }

            //Like status
            //Get liked by array
            JSONArray jsonArray = post.getLikedByArray();

            //See if current user liked the post
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("name").equals(ParseUser.getCurrentUser().getObjectId())) {
                    ivLike.setImageResource(R.drawable.ufi_heart_active);
                    post.likedByCurrentUser = true;
                    break;
                }
            }

            //Like button click listener
            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //User likes post
                    if(!post.likedByCurrentUser) {
                        post.likedByCurrentUser = true;
                        ivLike.setImageResource(R.drawable.ufi_heart_active);
                        try {
                            post.setLikedByArray(ParseUser.getCurrentUser(), true);
                            post.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    //Error handler
                                    if(e != null) {
                                        Log.e(TAG, "done: Error saving liking post", e);
                                    }
                                    Log.i(TAG, "done: Post liked!");
                                }
                            });
                        } catch (JSONException e) {
                            Log.e(TAG, "onClick: Like unsuccessful!", e);
                        }
                    }
                    //User unlikes post
                    else {
                        post.likedByCurrentUser = false;
                        ivLike.setImageResource(R.drawable.ufi_heart);
                        try {
                            post.setLikedByArray(ParseUser.getCurrentUser(), false);
                            post.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    //Error handler
                                    if(e != null) {
                                        Log.e(TAG, "done: Error unliking post", e);
                                    }
                                    Log.i(TAG, "done: Post unliked!");
                                }
                            });
                        } catch (JSONException e) {
                            Log.e(TAG, "onClick: Like unsuccessful!", e);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            //Get item position
            int position = getAdapterPosition();

            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the post at the position, this won't work if the class is static
                Post post = posts.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, PostDetailActivity.class);
                // serialize the post using parceler, use its short name as a key
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                // show the activity
                context.startActivity(intent);
            }
        }
    }
}
