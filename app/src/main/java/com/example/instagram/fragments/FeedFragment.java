package com.example.instagram.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.FeedActivity;
import com.example.instagram.LoginActivity;
import com.example.instagram.MainActivity;
import com.example.instagram.Post;
import com.example.instagram.PostsAdapter;
import com.example.instagram.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    private static final String TAG = "FeedFragment";

    // Store a member variable for the listener
    EndlessRecyclerViewScrollListener scrollListener;

    SwipeRefreshLayout swipeRefreshLayout;
    List<Post> allPosts;
    RecyclerView rvPosts;
    PostsAdapter adapter;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Find components
        rvPosts = view.findViewById(R.id.rvPosts);
        toolbar = view.findViewById(R.id.tbFeedToolbar);
        swipeRefreshLayout = view.findViewById(R.id.scPosts);

        //Remove title from toolbar
        toolbar.setTitle("");

        // Sets the Toolbar to act as the ActionBar for this Activity window
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

        //Initialize posts array
        allPosts = new ArrayList<>();

        //Initialize adapter
        adapter = new PostsAdapter(getContext(), allPosts);

        //Set layout manager on the rv
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);

        //Set adapter on the rv
        rvPosts.setAdapter(adapter);

        // Retain an instance of EndlessRecyclerViewScrollListener so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };

        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);

        //Query posts
        queryPosts();
    }

    //Loading additional posts for infinite scrolling
    private void loadNextDataFromApi(int page) {
        Log.d(TAG, "loadNextDataFromApi: Called");
        //Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery("Post");

        //Include user info
        query.include(Post.KEY_USER);

        //Limit query to latest 20 items
        query.setLimit(20);

        //Order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        //Higher createdAt values == older posts??
        //Log.d(TAG, "loadNextDataFromApi: " + posts.get(posts.size() - 1).getCreatedAt());
        query.whereGreaterThan("createdAt", allPosts.get(allPosts.size() - 1).getCreatedAt());            //Not doing what I thought it would do

        //Get post objects
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                //Error in query
                if(e != null) {
                    Log.e(TAG, "Issue with getting additional posts", e);
                    return;
                }
                //Successful query
                Log.i(TAG, "done: Retrieved Additional Posts");
//                for (Post post : posts) {
//                    Log.i(TAG, "Post Description: " + post.getDescription() + ", user: " + post.getUser().getUsername());
//                }

                //Save received posts to list
                allPosts.addAll(posts);

                //Notify adapter of change
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchTimelineAsync(int i) {
        allPosts.clear();
        adapter.clear();
        queryPosts();
        adapter.addAll(allPosts);
        swipeRefreshLayout.setRefreshing(false);
    }



    //Menu icons are inflated
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_feed, menu);
        Log.d(TAG, "onCreateOptionsMenu: Inflated");
    }

    //Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemLogout) {
            ParseUser.logOut();
            Log.i(TAG, "onClick: User logged out");
            Intent intentLogout = new Intent(getContext(), LoginActivity.class);
            startActivity(intentLogout);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                Log.i(TAG, "done: Retrieved Posts");
//                for (Post post : posts) {
//                    Log.i(TAG, "Post Description: " + post.getDescription() + ", user: " + post.getUser().getUsername());
//                }

                //Save received posts to list
                allPosts.addAll(posts);

                //Notify adapter of change
                adapter.notifyDataSetChanged();
            }
        });
    }
}