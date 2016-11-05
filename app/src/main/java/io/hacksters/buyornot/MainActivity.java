package io.hacksters.buyornot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int IMAGE_FIRST = 1;
    private static final int IMAGE_SECOND = 2;
    private static final String SHARED_PREFERENCES = "prefs";
    private String TAG = "MainActivity";
    private ImageView background1;
    private ImageView background2;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
