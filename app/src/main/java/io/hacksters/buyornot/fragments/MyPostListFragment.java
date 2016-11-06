package io.hacksters.buyornot.fragments;

/**
 * Created by Rahimli Rahim on 05/11/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.hacksters.buyornot.OnPostItemClicksListener;
import io.hacksters.buyornot.R;
import io.hacksters.buyornot.controllers.PostsAdapter;
import io.hacksters.buyornot.models.Post;
import io.hacksters.buyornot.utils.UrlBuilder;

public class MyPostListFragment extends Fragment implements OnPostItemClicksListener {
    private static final String TAG = "MyPostListFragment";
    private TextView noFriends;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    public MyPostListFragment() {
    }

    public static MyPostListFragment newInstance() {
        MyPostListFragment fragment = new MyPostListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_posts, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_friends_posts);
        noFriends = (TextView) rootView.findViewById(R.id.textview_no_friends);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_friends_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getFriendsPosts();
    }

    private void getFriendsPosts() {
        AsyncHttpClient client = new AsyncHttpClient();
        String token = AccessToken.getCurrentAccessToken().getToken();
        Log.d(TAG,UrlBuilder.getFriendsURL(token));
        client.get(UrlBuilder.getFriendsURL(token), new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int status = response.getInt("status");
                    if (status == 200) {
                        JSONArray data = response.getJSONArray("data");
                        List<Post> posts =  Post.parseJSON(data);
                        updateUI(posts);
                    } else {
                        Toast.makeText(getActivity(), "Error inserting file " + response.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "JSON error", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "error: ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(List<Post> posts) {
        progressBar.setVisibility(View.INVISIBLE);
        if(!posts.isEmpty()){
            PostsAdapter adapter = new PostsAdapter(posts,getActivity(),this);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        } else{
            noFriends.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBuyClick(Post post) {

    }

    @Override
    public void onNotClick(Post post) {

    }
}