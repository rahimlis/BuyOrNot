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
    private int buy;
    private int notBuy;
    private String username;
    private int id;
    private String userID;

    public Post(int id, String imageUrl, int buy, int notBuy, String username, String userID) {
        this.imageUrl = imageUrl;
        this.buy = buy;
        this.notBuy = notBuy;
        this.username = username;
        this.id = id;
        this.userID = userID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getBuy() {
        return buy;
    }

    public void setBuy(int buy) {
        this.buy = buy;
    }

    public int getNotBuy() {
        return notBuy;
    }

    public void setNotBuy(int notBuy) {
        this.notBuy = notBuy;
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }


    private static Post getFromJSON(JSONObject postJSON, String username) {
        try {
            int id = postJSON.getInt("id");
            String userID = postJSON.getString("fb_user_id");
            String imgURL = postJSON.getString("img_url");
            int buy = postJSON.getInt("buy");
            int notBuy = postJSON.getInt("not_buy");
            return new Post(id, imgURL, buy, notBuy, username, userID);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Post> parseJSON(JSONArray users) {
        List<Post> posts = new ArrayList<>();
        try {
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                JSONArray images = user.getJSONArray("images");
                String userName = user.getString("name");
                for (int j = 0; j < images.length(); j++)
                    posts.add(getFromJSON(images.getJSONObject(j), userName));
            }
            return posts;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
