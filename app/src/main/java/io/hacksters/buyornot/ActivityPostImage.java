package io.hacksters.buyornot;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class ActivityPostImage extends AppCompatActivity {

    private final static int REQUEST_IMAGE_CAPTURE = 1982;
    private final static int PICK_IMAGE_REQUEST = 1983;
    private ImageView imagePreview;
    private Uri filePath;
    private String imageString;
    private Bitmap bitmap;
    private ImageView deleteImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image);
        imagePreview = (ImageView) findViewById(R.id.activity_post_image_preview);
        ImageView fromCamera = (ImageView) findViewById(R.id.activity_post_image_choose);
        deleteImage = (ImageView) findViewById(R.id.activity_post_image_delet);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePreview.setImageBitmap(null);
            }
        });
        fromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take a Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPostImage.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take a Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    private void galleryIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode ==RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            final Uri selectedImageUri=data.getData();
            if (Build.VERSION.SDK_INT<19){
                try {
                    //Getting the Bitmap from Gallery
                    String[] filePathColumn={MediaStore.Images.Media.DATA};
                    Cursor cursor=getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
                    String selectedImagePath=cursor.getString(columnIndex);
                    cursor.close();
                    if (selectedImagePath.contains("https:")){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    bitmap = getPicasaBitmap(selectedImageUri);

                                }catch (Exception e){
                                }
                            }
                        }).start();

                    }else {
                        bitmap= BitmapFactory.decodeFile(imageString);
                    }
                    imagePreview .setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    String wholeID = DocumentsContract.getDocumentId(selectedImageUri);
                    // Split at colon, use second item in the array
                    String id = wholeID.split(":")[1];
                    String[] column = { MediaStore.Images.Media.DATA };
                    // where id is equal to
                    String sel = MediaStore.Images.Media._ID + "=?";
                    Cursor cursor = getContentResolver().
                            query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    column, sel, new String[]{ id }, null);
                    String filePathString = "";
                    int columnIndex = cursor.getColumnIndex(column[0]);
                    if (cursor.moveToFirst()) {
                        filePathString = cursor.getString(columnIndex);
                    }
                    cursor.close();
                    imageString=filePathString;
                    if (filePathString.contains("https:")){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    bitmap = getPicasaBitmap(selectedImageUri);

                                }catch (Exception e){
                                }
                            }
                        }).start();
                    }else {
                        bitmap= BitmapFactory.decodeFile(imageString);
                    }
                    imagePreview .setImageBitmap(bitmap);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

        }if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagePreview.setImageBitmap(imageBitmap);
        }
    }

    public Bitmap getPicasaBitmap(Uri uri) {
        Bitmap orgImage = null;
        try {
            orgImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            // do something if you want
        }
        return orgImage;
    }
}
