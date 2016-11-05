package io.hacksters.buyornot.utils;

/**
 * Created by Rahimli Rahim on 05/11/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public class UrlBuilder {
    private static final String SERVER_ADRESS = "http://rahimlis.com/";
    public static String insertImageURL(String img_url,String fb_user_id){
        return SERVER_ADRESS+"insert_image?"
                +"img_url="+img_url
                +"&fb_user_id="+fb_user_id;
    }
}
