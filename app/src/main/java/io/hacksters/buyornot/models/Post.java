package io.hacksters.buyornot.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Farid Mammadov on 05-Nov-16.
 */

public class Post {
    private String imageUrl;
    private String likes;
    private String unlikes;
    private String name;
    private String postId;

    public Post(String imageUrl, String likes, String unlikes, String name, String postId) {
        this.imageUrl = imageUrl;
        this.likes = likes;
        this.unlikes = unlikes;
        this.name = name;
        this.postId = postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getUnlikes() {
        return unlikes;
    }

    public void setUnlikes(String unlikes) {
        this.unlikes = unlikes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }


    public static List<Post> JSONParse(String result){
        List<Post> friendsPostItemList = new ArrayList<>();
        Post post = null;
        try{
            JSONArray jsonArray = new JSONArray(result);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = new JSONArray().getJSONObject(i);
                String name = jsonObject.getString("name");
                String imageUrl = jsonObject.getString("image_url");
                String likes = jsonObject.getString("likes");
                String unlikes  =jsonObject.getString("unlikes");
                String postId = jsonObject.getString("post_id");

                post.setPostId(postId);
                post.setImageUrl(imageUrl);
                post.setLikes(likes);
                post.setUnlikes(unlikes);
                post.setName(name);
                friendsPostItemList.add(post);
            }
        }catch (JSONException i){

        }
        return friendsPostItemList;
    }
}
