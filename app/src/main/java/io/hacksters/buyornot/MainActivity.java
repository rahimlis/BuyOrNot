package io.hacksters.buyornot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE_FOR_FIRST = 1;
    private static final int REQUEST_IMAGE_CAPTURE_FOR_SECOND = 2;
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
        background1 = (ImageView) findViewById(R.id.imageview_background_first);
        background2 = (ImageView) findViewById(R.id.imageview_background_second);
        addPhoto1.setOnClickListener(this);
        addPhoto2.setOnClickListener(this);
    }

    //
//    private void takePicture(int requestID) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, requestID);
//        }
//    }
//
    private void takePicture(int requestID) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(requestID);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
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
        putInSharedPrefs(whichImage,filePath);
        return image;
    }

    private void putInSharedPrefs(int whichImage,String path){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE);
        String key = "Image"+whichImage;
        sharedPreferences.edit().putString(key,path).apply();
    }

    private String getFromSharedPrefs(int whichImage){
        String key = "Image"+whichImage;
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE_FOR_FIRST && resultCode == RESULT_OK) {
              String path  =getFromSharedPrefs(REQUEST_IMAGE_CAPTURE_FOR_FIRST);


//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            background1.setImageBitmap(imageBitmap);
//            findViewById(R.id.imageview_add_photo_first).setVisibility(View.INVISIBLE);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE_FOR_SECOND && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            background2.setImageBitmap(imageBitmap);
            findViewById(R.id.imageview_add_photo_second).setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageview_add_photo_first:
                takePicture(REQUEST_IMAGE_CAPTURE_FOR_FIRST);
                break;
            case R.id.imageview_add_photo_second:
                takePicture(REQUEST_IMAGE_CAPTURE_FOR_SECOND);
                break;
        }
    }
}
