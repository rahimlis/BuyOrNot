package io.hacksters.buyornot;

/**
 * Created by Farid Mammadov on 05-Nov-16.
 */

public class FriendsPostItem {
    private String imageUrl;
    private String likes;
    private String unlikes;
    private String name;
    private String postId;

    public FriendsPostItem(String imageUrl, String likes, String unlikes, String name, String postId) {
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
}
