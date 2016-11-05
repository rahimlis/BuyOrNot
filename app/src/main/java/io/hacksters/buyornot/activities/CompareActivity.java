package io.hacksters.buyornot.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import io.hacksters.buyornot.utils.UrlBuilder;

public class CompareActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int IMAGE_FIRST = 1;
    private static final int IMAGE_SECOND = 2;
    private static final String SHARED_PREFERENCES = "prefs";
    private String TAG = "CompareActivity";
    private ImageView background1;
    private ImageView background2;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        setupUI();
    }

    private void setupUI() {
        ImageView addPhoto1 = (ImageView) findViewById(R.id.imageview_add_photo_first);
        ImageView addPhoto2 = (ImageView) findViewById(R.id.imageview_add_photo_second);
        ImageView selectFirst = (ImageView) findViewById(R.id.imageview_select_first);
        ImageView selectSecond = (ImageView) findViewById(R.id.imageview_select_second);
        background1 = (ImageView) findViewById(R.id.imageview_background_first);
        background2 = (ImageView) findViewById(R.id.imageview_background_second);
        addPhoto1.setOnClickListener(this);
        addPhoto2.setOnClickListener(this);
        selectFirst.setOnClickListener(this);
        selectSecond.setOnClickListener(this);
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
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(path));
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void takePicture(int requestID) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(requestID);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "io.hacksters.buyornot.provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, requestID);
            }
        }

    }

    private File createImageFile(int whichImage) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "BUY_OR_NOT_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String key = "Image" + whichImage;
        sharedPreferences.edit().putString(key, path).apply();
    }

    private String getFromSharedPrefs(int whichImage) {
        String key = "Image" + whichImage;
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    private void clearSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String path = getFromSharedPrefs(requestCode);

            UploadImageTask task = new UploadImageTask();
            task.execute(path);

            if (requestCode == IMAGE_FIRST) {
                loadImageIntoView(background1, path);
                findViewById(R.id.imageview_add_photo_first).setVisibility(View.INVISIBLE);
            } else if (requestCode == IMAGE_SECOND) {
                loadImageIntoView(background2, path);
                findViewById(R.id.imageview_add_photo_second).setVisibility(View.INVISIBLE);
            }
        }
    }


    private void onImageSelected() {
        clearSharedPrefs();
        background1.setImageResource(android.R.color.transparent);
        background2.setImageResource(android.R.color.transparent);
        findViewById(R.id.imageview_add_photo_first).setVisibility(View.VISIBLE);
        findViewById(R.id.imageview_add_photo_second).setVisibility(View.VISIBLE);
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
        }
    }

    private void insertImageToDatabase(String imageURL, String userID) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(UrlBuilder.insertImageURL(imageURL, userID), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int status = response.getInt("status");
                    if (status == 200) {
                        Toast.makeText(CompareActivity.this, "File inserted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CompareActivity.this, "Error inserting file", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CompareActivity.this, "JSON error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class UploadImageTask extends AsyncTask<String, Void, Map> {
        @Override
        protected Map doInBackground(String... paths) {
            Map config = new HashMap();
            config.put("cloud_name", "dvsq2ni6v");
            config.put("api_key", "554332252326459");
            config.put("api_secret", "4YtJFT-2YD7RldBXqWzOoV1Jd5k");
            try {
                InputStream inputStream = new FileInputStream(Uri.parse(paths[0]).getPath());
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
                String userID = AccessToken.getCurrentAccessToken().getUserId();
                insertImageToDatabase(imageURL, userID);
            }
        }
    }
}
