package io.hacksters.buyornot.controllers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.hacksters.buyornot.activities.LikersActivity;
import io.hacksters.buyornot.R;
import io.hacksters.buyornot.models.Post;

/**
 * Created by Rahimli Rahim on 05/11/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private List<Post> friendsPostItems;
    private Context context;

    public PostsAdapter(List<Post> items,Context context) {
        this.friendsPostItems = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.unlikes.setText(friendsPostItems.get(position).getLikes());
        holder.likes.setText(friendsPostItems.get(position).getUnlikes());
        holder.name.setText(friendsPostItems.get(position).getName());
        Picasso.with(context).load(friendsPostItems.get(position).getImageUrl()).into(holder.postImage);

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, LikersActivity.class);
                    intent.putExtra("post_id", friendsPostItems.get(position).getPostId());
                    intent.putExtra("likes", true);
                    context.startActivity(intent);
                } else {
                    Snackbar snackbar = Snackbar.make(v, context.getResources().getString(R.string.no_connection), Snackbar.LENGTH_LONG);
                    TextView snackText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    snackbar.show();
                }
            }
        });
        holder.unlikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, LikersActivity.class);
                    intent.putExtra("post_id", friendsPostItems.get(position).getPostId());
                    intent.putExtra("likes", false);
                    context.startActivity(intent);
                } else {
                    Snackbar snackbar = Snackbar.make(v, context.getResources().getString(R.string.no_connection), Snackbar.LENGTH_LONG);
                    TextView snackText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsPostItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView postImage;
        final TextView name;
        final TextView likes;
        final TextView unlikes;
        final ImageButton like;
        final ImageButton unlike;
            /*public DummyContent.DummyItem mItem;*/

        ViewHolder(View view) {
            super(view);
            mView = view;
            postImage = (ImageView) view.findViewById(R.id.friends_post_image);
            name = (TextView) view.findViewById(R.id.friends_post_name);
            likes = (TextView) view.findViewById(R.id.friends_post_likes);
            unlikes = (TextView) view.findViewById(R.id.friends_post_unlikes);
            like = (ImageButton) view.findViewById(R.id.friends_post_like);
            unlike = (ImageButton) view.findViewById(R.id.friends_post_unlike);
        }
    }
}