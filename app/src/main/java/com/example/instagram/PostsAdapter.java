package com.example.instagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

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
        holder.bind(post);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName;
        TextView tvDescription;
        TextView tvTimeAgo;
        TextView tvName;
        ImageView ivPostImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Get views
            tvUserName = itemView.findViewById(R.id.tvItemUserName);
            tvDescription = itemView.findViewById(R.id.tvItemDescription);
            ivPostImage = itemView.findViewById(R.id.ivItemPostImage);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(Post post) {
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
        }
    }
}
