package io.hacksters.buyornot.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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

import static android.app.Activity.RESULT_OK;

public class UploadImageFragment extends Fragment {
    private String filePathString;
    private final static int REQUEST_IMAGE_CAPTURE = 1982;
    private final static int PICK_IMAGE_REQUEST = 1983;
    private ImageView imagePreview;
    private Bitmap bitmap;
    private TextView cancelText, uploadText;
    private ImageView addImage;

    public UploadImageFragment() {
        // Required empty public constructor
    }


    public static UploadImageFragment newInstance() {
        UploadImageFragment fragment = new UploadImageFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_image, container, false);

        addImage = (ImageView) view.findViewById(R.id.imageview_upload_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        imagePreview = (ImageView) view.findViewById(R.id.activity_post_image_preview);
        cancelText = (TextView) view.findViewById(R.id.textview_post_cancel);
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePreview.setImageBitmap(null);
                addImage.setVisibility(View.VISIBLE);
            }
        });

        uploadText = (TextView) view.findViewById(R.id.textview_post_upload);
        return view;
    }


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
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {

                Uri photoURI;
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                    photoURI = Uri.fromFile(photoFile);
                else
                    photoURI = FileProvider.getUriForFile(getActivity(), "io.hacksters.buyornot.provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                loadImageIntoView(imagePreview, filePathString);
                uploadText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (filePathString != null) {
                            UploadImageTask task = new UploadImageTask(REQUEST_IMAGE_CAPTURE, null);
                            task.execute(filePathString);
                        }
                    }
                });

            } else if (requestCode == PICK_IMAGE_REQUEST) {
                Uri selectedImage = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                    final Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    loadImageIntoView(imagePreview, yourSelectedImage);
                    addImage.setVisibility(View.INVISIBLE);
                    uploadText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (filePathString != null) {
                                UploadImageTask task = new UploadImageTask(PICK_IMAGE_REQUEST, yourSelectedImage);
                                task.execute(filePathString);
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void loadImageIntoView(ImageView imageView, String path) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(path));
            bitmap = BitmapUtils.getResizedBitmap(bitmap, 320);
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImageIntoView(ImageView imageView, Bitmap bitmap) {
        bitmap = BitmapUtils.getResizedBitmap(bitmap, 640);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "BUY_OR_NOT_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        {
            Log.d("UploadImageFragment", "result of mkdris" + image.mkdirs());
        }
        filePathString = "file:" + image.getAbsolutePath();
        Log.d("UploadImageFragment", filePathString);
        return image;
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
        private int requestID;
        private Bitmap bitmap;

        UploadImageTask(int requestID, Bitmap bitmap) {
            this.requestID = requestID;
            this.bitmap = bitmap;
        }

        @Override
        protected Map doInBackground(String... paths) {
            Map config = new HashMap();
            config.put("cloud_name", "dvsq2ni6v");
            config.put("api_key", "554332252326459");
            config.put("api_secret", "4YtJFT-2YD7RldBXqWzOoV1Jd5k");
            try {
                if (requestID == REQUEST_IMAGE_CAPTURE)
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(paths[0]));
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
                String userID = AccessToken.getCurrentAccessToken().getUserId();
                insertImageToDatabase(imageURL, userID);
            }
        }
    }
}
