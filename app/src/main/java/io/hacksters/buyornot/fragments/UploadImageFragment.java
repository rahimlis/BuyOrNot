package io.hacksters.buyornot.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import io.hacksters.buyornot.R;
import io.hacksters.buyornot.utils.BitmapUtils;
import io.hacksters.buyornot.utils.UrlBuilder;

public class UploadImageFragment extends Fragment {
    private View view;
    private Context context;
    private String filePathString;

    public UploadImageFragment() {
        // Required empty public constructor
    }


    public static UploadImageFragment newInstance() {
        UploadImageFragment fragment = new UploadImageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_post_image, container, false);
        imagePreview = (ImageView) view.findViewById(R.id.activity_post_image_preview);
        ImageView fromCamera = (ImageView) view.findViewById(R.id.activity_post_image_choose);
        deleteImage = (ImageView) view.findViewById(R.id.activity_post_image_delet);
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
        ImageView uploadImage = (ImageView) view.findViewById(R.id.activity_post_image_send);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImageTask task = new UploadImageTask();
                System.out.println("finniso12 "+filePathString);
                task.execute(filePathString);
            }
        });
        return view;
    }

    private final static int REQUEST_IMAGE_CAPTURE = 1982;
    private final static int PICK_IMAGE_REQUEST = 1983;
    private ImageView imagePreview;
    private Uri filePath;
    private String imageString;
    private Bitmap bitmap;
    private ImageView deleteImage;


    private void selectImage() {
        final CharSequence[] items = {"Take a Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "io.hacksters.buyornot.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    private void galleryIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "io.hacksters.buyornot.provider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent,PICK_IMAGE_REQUEST);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode ==getActivity().RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            final Uri selectedImageUri=data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
            }catch (IOException e){

            }
            imagePreview.setImageBitmap(bitmap);

        }if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(filePathString));
                imagePreview.setImageBitmap(bitmap);
            }catch (IOException e){}
/*
            Uri tempUri = getImageUri(getActivity(), imageBitmap);

            filePathString=getRealPathFromURI(tempUri);
*/
        }
    }


    private File createImageFile() throws IOException {
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
        filePathString = "file:" + image.getAbsolutePath();
        return image;
    }
 /*   public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri)
    {
        try
        {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
        catch (Exception e)
        {
            return uri.getPath();
        }
    }
*/
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
}
