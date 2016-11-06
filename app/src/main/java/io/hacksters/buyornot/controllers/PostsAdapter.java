package io.hacksters.buyornot.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.hacksters.buyornot.OnPostItemClicksListener;
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
    private OnPostItemClicksListener listener;
    public PostsAdapter(List<Post> items,Context context,OnPostItemClicksListener listener) {
        this.friendsPostItems = items;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Post post = friendsPostItems.get(position);
        holder.showBuy.setText(" "+ post.getBuy());
        holder.showNot.setText(" "+ post.getNotBuy());
        holder.username.setText( post.getName());
        Picasso.with(context).load(post.getImageUrl()).fit().into(holder.postImage);

        holder.actionBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBuyClick(post);
            }
        });

        holder.actionNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onNotClick(post);
            }
        });

        /*
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, LikersActivity.class);
                //    intent.putExtra("post_id", friendsPostItems.get(position).getPostId());
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
                  //  intent.putExtra("post_id", friendsPostItems.get(position).getPostId());
                    intent.putExtra("likes", false);
                    context.startActivity(intent);
                } else {
                    Snackbar snackbar = Snackbar.make(v, context.getResources().getString(R.string.no_connection), Snackbar.LENGTH_LONG);
                    TextView snackText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    snackbar.show();
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return friendsPostItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView postImage;
        final TextView actionBuy;
        final TextView username;
        final TextView actionNot;
        final TextView showBuy;
        final TextView showNot;

        ViewHolder(View view) {
            super(view);
            mView = view;
            postImage = (ImageView) view.findViewById(R.id.imageview_friends_post);
            username = (TextView) view.findViewById(R.id.textview_username);
            actionBuy = (TextView) view.findViewById(R.id.textview_action_buy);
            actionNot = (TextView) view.findViewById(R.id.textview_action_not);
            showBuy = (TextView) view.findViewById(R.id.textview_show_buy);
            showNot = (TextView) view.findViewById(R.id.textview_show_not);
        }
    }
}