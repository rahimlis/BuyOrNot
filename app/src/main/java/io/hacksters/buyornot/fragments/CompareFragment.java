package io.hacksters.buyornot.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import com.facebook.AccessToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import io.hacksters.buyornot.R;
import io.hacksters.buyornot.activities.CompareActivity;
import io.hacksters.buyornot.utils.BitmapUtils;
import io.hacksters.buyornot.utils.UrlBuilder;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class CompareFragment extends Fragment implements View.OnClickListener {

    private View view;

    private static final int IMAGE_FIRST = 1;
    private static final int IMAGE_SECOND = 2;
    private static final String SHARED_PREFERENCES = "prefs";
    private String TAG = "CompareActivity";
    private ImageView background1;
    private ImageView background2;
    private String filePath;

    public CompareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_compare, container, false);
        setupUI();
        return view;
    }

    private void setupUI() {
        ImageView addPhoto1 = (ImageView) view.findViewById(R.id.imageview_add_photo_first);
        ImageView addPhoto2 = (ImageView) view.findViewById(R.id.imageview_add_photo_second);
        ImageView selectFirst = (ImageView) view.findViewById(R.id.imageview_select_first);
        ImageView selectSecond = (ImageView) view.findViewById(R.id.imageview_select_second);
        ImageView deleteFirst = (ImageView) view.findViewById(R.id.imageview_delete_first);
        ImageView deleteSecond= (ImageView) view.findViewById(R.id.imageview_delete_second);
        background1 = (ImageView) view.findViewById(R.id.imageview_background_first);
        background2 = (ImageView) view.findViewById(R.id.imageview_background_second);
        addPhoto1.setOnClickListener(this);
        addPhoto2.setOnClickListener(this);
        selectFirst.setOnClickListener(this);
        selectSecond.setOnClickListener(this);
        deleteFirst.setOnClickListener(this);
        deleteSecond.setOnClickListener(this);
        String path1 = getFromSharedPrefs(IMAGE_FIRST);
        String path2 = getFromSharedPrefs(IMAGE_SECOND);
        if (path1 != null) {
            loadImageIntoView(background1, path1);
            addPhoto1.setVisibility(View.INVISIBLE);
        }
        if (path2 != null) {
            loadImageIntoView(background2, path2);
            addPhoto2.setVisibility(View.INVISIBLE);
        }

    }

    private void loadImageIntoView(ImageView imageView, String path) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(path));
            bitmap = BitmapUtils.getResizedBitmap(bitmap, 640);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void takePicture(int requestID) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(requestID);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "io.hacksters.buyornot.provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, requestID);
            }
        }
    }

    private File createImageFile(int whichImage) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "BUY_OR_NOT_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        filePath = "file:" + image.getAbsolutePath();
        putInSharedPrefs(whichImage, filePath);
        return image;
    }



    private void putInSharedPrefs(int whichImage, String path) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String key = "Image" + whichImage;
        sharedPreferences.edit().putString(key, path).apply();
    }

    private String getFromSharedPrefs(int whichImage) {
        String key = "Image" + whichImage;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    private void clearSharedPrefs() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String path = getFromSharedPrefs(requestCode);
/*
            UploadImageTask task = new UploadImageTask();
            task.execute(path);*/
            if (requestCode == IMAGE_FIRST) {
                loadImageIntoView(background1, path);
                view.findViewById(R.id.imageview_add_photo_first).setVisibility(View.INVISIBLE);
            } else if (requestCode == IMAGE_SECOND) {
                loadImageIntoView(background2, path);
                view.findViewById(R.id.imageview_add_photo_second).setVisibility(View.INVISIBLE);
            }
        }
    }


    private void onImageSelected() {
        clearSharedPrefs();
        background1.setImageResource(android.R.color.transparent);
        background2.setImageResource(android.R.color.transparent);
        view.findViewById(R.id.imageview_add_photo_first).setVisibility(View.VISIBLE);
        view.findViewById(R.id.imageview_add_photo_second).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageview_add_photo_first:
                takePicture(IMAGE_FIRST);
                break;
            case R.id.imageview_add_photo_second:
                takePicture(IMAGE_SECOND);
                break;
            case R.id.imageview_select_first:
                onImageSelected();
                break;
            case R.id.imageview_select_second:
                onImageSelected();
                break;
            case R.id.imageview_delete_first:
                deleFirstImage();
                break;
            case R.id.imageview_delete_second:
                deleteSecondImage();
        }
    }

    public void deleFirstImage(){
        background1.setImageBitmap(null);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        sharedPreferences.edit().remove("Image"+IMAGE_FIRST).apply();
    }
    public void deleteSecondImage(){
        background2.setImageBitmap(null);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        sharedPreferences.edit().remove("Image"+IMAGE_SECOND).apply();
    }
/*
    private void insertImageToDatabase(String imageURL, String userID) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(UrlBuilder.insertImageURL(imageURL, userID), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int status = response.getInt("status");
                    if (status == 200) {
                        Toast.makeText(getActivity(), "File inserted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Error inserting file", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "JSON error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
*/
    public static Fragment newInstance() {
        CompareFragment fragment = new CompareFragment();
        return fragment;
    }
/*
    class UploadImageTask extends AsyncTask<String, Void, Map> {
        @Override
        protected Map doInBackground(String... paths) {
            Map config = new HashMap();
            config.put("cloud_name", "dvsq2ni6v");
            config.put("api_key", "554332252326459");
            config.put("api_secret", "4YtJFT-2YD7RldBXqWzOoV1Jd5k");
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(paths[0]));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);
                InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
//                InputStream inputStream = new FileInputStream(Uri.parse(paths[0]).getPath());
                Cloudinary cloudinary = new Cloudinary(config);
                Uploader uploader = cloudinary.uploader();
                return uploader.upload(inputStream, ObjectUtils.emptyMap());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Map map) {
            super.onPostExecute(map);
            if (map != null) {
                String imageURL = (String) map.get("url");
                System.out.println("finnico "+imageURL);
                String userID = AccessToken.getCurrentAccessToken().getUserId();
                insertImageToDatabase(imageURL, userID);
            }
        }
    }
    */
}
