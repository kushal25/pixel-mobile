package com.example.infinity.pixie;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.infinity.pixie.service.HttpClientService;
import com.example.infinity.pixie.util.CameraSource;
import com.example.infinity.pixie.util.CameraSourcePreview;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;


public class MainActivity extends AppCompatActivity {

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
//    private ImageView capturedPicturePreview;
    private FrameLayout cameraLayout;
    private RelativeLayout buttonLayout;
    private RelativeLayout imagePreviewLayout;
    private Button uploadButton;
    private Button retryButton;
    private Button galleryButton;
    private Button rotateButton;
    private Button profileButton;
    private Button cropButton;
    private LinearLayout mainWaitLayout;

    private static final String TAG = "Main Activity";
    //public static String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    public static long timeStamp = Calendar.getInstance().getTimeInMillis();

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private int PICK_IMAGE_REQUEST = 1;
    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private boolean useFlash = false;
    private boolean autoFocus = true;

    private byte[] xDpi = null;
    private byte[] yDpi = null;
    public Dialog mDialog = null;

    private byte[] imageData = null;
    File pictureFile = null;

    private CropImageView mCropImageView;

    private Uri mCropImageUri;

    HttpClientService httpClient = new HttpClientService();

    JsonHttpResponseHandler uploadListener = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            mainWaitLayout.setVisibility(View.GONE);
            Intent intent = new Intent(MainActivity.this, ExtractedDataActivity.class);
            Log.d("Extracted Data", response.toString());
            Bundle bundle = new Bundle();
            bundle.putString("ExtractedData", response.toString());

            intent.putExtras(bundle);

            startActivity(intent);

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Pixie.showToast(MainActivity.this, "Something went wrong. Please try again!!");
            throwable.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        Pixie.verifyStoragePermissions(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Pixie.verifyStoragePermissions(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);
        dispatchTakePictureIntent();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Pixie.isNetworkOK(MainActivity.this)) {
                    pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                    if (pictureFile == null) {
                        Log.d(TAG, "Error creating media file, check storage permissions: " +
                                "e.getMessage()");
                        return;
                    }

                    try {
                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        fos.write(imageData);
                        fos.close();
                    } catch (FileNotFoundException e) {
                        Log.d(TAG, "File not found: " + e.getMessage());
                    } catch (IOException e) {
                        Log.d(TAG, "Error accessing file: " + e.getMessage());
                    }

                    //setContainersVisibility(View.INVISIBLE);
                    imagePreviewLayout.setVisibility(View.GONE);
                    buttonLayout.setVisibility(View.GONE);
                    profileButton.setVisibility(View.GONE);
                    //new UploadPicture().execute(pictureFile);
                    httpClient.extractData(uploadListener, pictureFile);
                    mainWaitLayout.setVisibility(View.VISIBLE);
                }
                else {
                    openConnectionDialog();
                }
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageData = null;
                imagePreviewLayout.setVisibility(View.GONE);
                mPreview.setVisibility(View.VISIBLE);
                buttonLayout.setVisibility(View.VISIBLE);
                profileButton.setVisibility(View.VISIBLE);
                if (mPreview != null) {
                    mPreview.stop();
                }
                startCameraSource();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(MainActivity.this,GalleryActivity.class);
//                startActivity(intent);
                Intent intent = new Intent();
// Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                //startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });

        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Rotation", "rotating by 90");
                mCropImageView.setRotation(mCropImageView.getRotation() + 90);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCropImageClick();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                mPreview.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.GONE);
                profileButton.setVisibility(View.GONE);
                imagePreviewLayout.setVisibility(View.VISIBLE);
                mCropImageView.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imageData= stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void initViews()
    {
        mPreview = (CameraSourcePreview) findViewById(R.id.camera_preview);
//        capturedPicturePreview = (ImageView) findViewById(R.id.capturedPicturePreview);
        cameraLayout = (FrameLayout) findViewById(R.id.cameraLayout);
        buttonLayout = (RelativeLayout) findViewById(R.id.buttonLayout);
        imagePreviewLayout = (RelativeLayout) findViewById(R.id.imagePreviewLayout);
        mainWaitLayout = (LinearLayout) findViewById(R.id.main_wait_layout);
        mCropImageView = (CropImageView) findViewById(R.id.cropImageView);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setTypeface(Pixie.fontawesome);
        retryButton = (Button) findViewById(R.id.retryButton);
        retryButton.setTypeface(Pixie.fontawesome);
        galleryButton = (Button) findViewById(R.id.galleryButton);
        galleryButton.setTypeface(Pixie.fontawesome);
        rotateButton = (Button) findViewById(R.id.rotateButton);
        rotateButton.setTypeface(Pixie.fontawesome);
        profileButton = (Button) findViewById(R.id.profile);
        profileButton.setTypeface(Pixie.fontawesome);
        cropButton = (Button) findViewById(R.id.cropButton);
        cropButton.setTypeface(Pixie.fontawesome);
    }

    public void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initCamPreview();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    public void openConnectionDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout dialogView = (RelativeLayout) inflater.inflate(R.layout.dialog_no_internet, null);
        TextView textTitle = (TextView) dialogView.findViewById(R.id.title);
        TextView retryBtn = (TextView) dialogView.findViewById(R.id.retry);
        builder.setView(dialogView);

        mDialog = builder.create();
        mDialog.setCancelable(false);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Pixie.isNetworkOK(MainActivity.this)) {
                    mDialog.dismiss();
                    onResume();
                } else {
                    openConnectionDialog();
                }
            }
        });
        mDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });
        mDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCamPreview();
                } else {
                    Pixie.showToast(this,"Permission Denied");
                }
                return;
            }
        }
    }

    public void onCropImageClick() {
        Bitmap cropped = mCropImageView.getCroppedImage();
        if (cropped != null)
            mCropImageView.setImageBitmap(cropped);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            cropped.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageData = stream.toByteArray();
    }

    private void initCamPreview() {
        // permission hai! enjoy!!
        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource =
                new CameraSource.Builder(getApplicationContext())
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(2.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .build();

        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCameraSource.takePicture(new CameraSource.ShutterCallback() {
                            @Override
                            public void onShutter() {
                                // TODO: play shutter sound
                            }
                        }, mPicture);
                    }
                }
        );
    }

    private CameraSource.PictureCallback mPicture = new CameraSource.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data) {
            imageData = data;

            Bitmap photo = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            mPreview.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.GONE);
            profileButton.setVisibility(View.GONE);
            imagePreviewLayout.setVisibility(View.VISIBLE);
            mCropImageView.setImageBitmap(photo);
            //capturedPicturePreview.setImageBitmap(Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true));

        }
    };

    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }



    private class UploadPicture extends AsyncTask<File, Void, String> {

        @Override
        protected String doInBackground(File... params) {

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(params[0].getAbsolutePath());
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
//                URL url = new URL("http://fairy-m3-xlarge.us-west-2.elasticbeanstalk.com/ReceivePicture");
                URL url = new URL("http://52.53.93.85/api/images/writeData");

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", pictureFile.getName());

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"img\";filename=\""
                        + pictureFile.getName() + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.flush();
                dos.close();

//                conn.disconnect();

                // Responses from the server (code and message)
                int serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                Log.d(TAG, "File Uploaded");
                Log.d(TAG, serverResponseMessage.toString());

                if (serverResponseCode == 200) {

                }

                InputStream inputStream;
                // get stream
                if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String temp = "";
                StringBuilder response = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    response.append(temp);
                }
                bufferedReader.close();
                inputStream.close();
                Log.d("Response", response.toString());

                //close the streams //
                fileInputStream.close();

                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String response) {
            final View view = (ViewGroup) ((ViewGroup) MainActivity.this
                    .findViewById(android.R.id.content)).getChildAt(0);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Pixie.showToast(MainActivity.this, "Post Execution");
                            mainWaitLayout.setVisibility(View.GONE);
                            Intent intent = new Intent(MainActivity.this, ExtractedDataActivity.class);
                            Log.d("Extracted Data", response);
                            Bundle bundle = new Bundle();
                            bundle.putString("ExtractedData", response);

                            intent.putExtras(bundle);

                            startActivity(intent);

                            finish();
                        }
                    });
                }
            }, 2);
        }
    }

    /**
     * Create a file Uri for saving an image or video
     */
//    private static Uri getOutputMediaFileUri(int type) {
//        return Uri.fromFile(getOutputMediaFile(type));
//    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Pixie");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name

        File mediaFile;

        String imgName = "img-" + timeStamp + ".jpg";
        Pixie.setImage(imgName);

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + Pixie.getImgName()
            );
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPreview != null) {
            mPreview.stop();
        }
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPreview != null) {
            mPreview.release();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




}
