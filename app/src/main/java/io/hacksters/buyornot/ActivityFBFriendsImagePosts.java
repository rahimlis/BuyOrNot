package io.hacksters.buyornot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ActivityFBFriendsImagePosts extends AppCompatActivity {

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbfriends_image_posts);
        context = this;
    }

    public class CustomRecyclerView extends RecyclerView.Adapter<CustomRecyclerView.ViewHolder>{
        private List<FriendsPostItem> friendsPostItems;

        public CustomRecyclerView(List<FriendsPostItem> items) {
            friendsPostItems = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friends_post_content, parent, false);
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

                    ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();


                    if (networkInfo!=null&&networkInfo.isConnected()) {
                                                    Context context = v.getContext();
                            Intent intent = new Intent(context, ActivityLikersUnlikers.class);
                            intent.putExtra("post_id", friendsPostItems.get(position).getPostId());
                            intent.putExtra("likes",true);
                            context.startActivity(intent);
                    }else {
                        Snackbar snackbar=Snackbar.make(v,getResources().getString(R.string.no_connection),Snackbar.LENGTH_LONG);
                        TextView snackText=(TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        snackText.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                        snackbar.show();
                    }
                }
            });
            holder.unlikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();


                    if (networkInfo!=null&&networkInfo.isConnected()) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ActivityLikersUnlikers.class);
                        intent.putExtra("post_id", friendsPostItems.get(position).getPostId());
                        intent.putExtra("likes",false);
                        context.startActivity(intent);
                    }else {
                        Snackbar snackbar=Snackbar.make(v,getResources().getString(R.string.no_connection),Snackbar.LENGTH_LONG);
                        TextView snackText=(TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        snackText.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                        snackbar.show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendsPostItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView postImage;
            public final TextView name;
            public final TextView likes;
            public final TextView unlikes;
            public final ImageButton like;
            public final ImageButton unlike;
            /*public DummyContent.DummyItem mItem;*/

            public ViewHolder(View view) {
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

    private List<FriendsPostItem> JSONParse(String result){
        List<FriendsPostItem> friendsPostItemList = new ArrayList<>();
        FriendsPostItem friendsPostItem = null;
        try{
            JSONArray jsonArray = new JSONArray(result);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = new JSONArray().getJSONObject(i);
                String name = jsonObject.getString("name");
                String imageUrl = jsonObject.getString("image_url");
                String likes = jsonObject.getString("likes");
                String unlikes  =jsonObject.getString("unlikes");
                String postId = jsonObject.getString("post_id");

                friendsPostItem.setPostId(postId);
                friendsPostItem.setImageUrl(imageUrl);
                friendsPostItem.setLikes(likes);
                friendsPostItem.setUnlikes(unlikes);
                friendsPostItem.setName(name);

                friendsPostItemList.add(friendsPostItem);
            }
        }catch (JSONException i){

        }

        return friendsPostItemList;
    }

}
