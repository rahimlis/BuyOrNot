package io.hacksters.buyornot;

import io.hacksters.buyornot.models.Post;

/**
 * Created by Rahimli Rahim on 06/11/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public interface OnPostItemClicksListener {
    void onBuyClick(Post post);
    void onNotClick(Post post);

}
