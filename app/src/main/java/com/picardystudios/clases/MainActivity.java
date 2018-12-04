package com.picardystudios.clases;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.picardystudios.clases.Util.CameraUtils;
import com.picardystudios.clases.Util.ImagePath_MarshMallow;
import com.picardystudios.clases.Util.PermissionUtils;

import androidx.appcompat.app.AppCompatActivity;

import static com.picardystudios.clases.Util.AppUtils.showToast;
import static com.picardystudios.clases.Util.PermissionUtils.isPermissionGranted;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int PERMISSION_REQUEST_CODE = 114;//request code for Camera and External Storage permission
    private static final int CAMERA_REQUEST_CODE = 133;//request code for capture image

    private Uri fileUri = null;//Uri to capture image
    private String getImageUrl = "";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        imageView = (ImageView) findViewById(R.id.captured_photo);
        Button clickButton = (Button) findViewById(R.id.bpermisos);
        clickButton.setOnClickListener((View v) -> {
             if (PermissionUtils.isPermissionGranted(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},101)) {
                showToast(this,"Permisos aceptados");
            } else { showToast(this,"Debe aceptar los permisos para continuar..."); }
        });

        Button clickCapture = (Button) findViewById(R.id.bfotos);
        clickCapture.setOnClickListener((View v) -> {
            if (isDeviceSupportCamera())
                captureImage();

        });
    }








    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                try {
                    //When image is captured successfully
                    if (resultCode == RESULT_OK) {

                        //Check if device SDK is greater than 22 then we get the actual image path via below method
                        if (Build.VERSION.SDK_INT > 22)
                            getImageUrl = ImagePath_MarshMallow.getPath(MainActivity.this, fileUri);
                        else
                            //else we will get path directly
                            getImageUrl = fileUri.getPath();


                        //After image capture show captured image over image view
                        showCapturedImage();
                    } else
                        Toast.makeText(this, "Cancelado..", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }






    // Checking camera supportability
    private boolean isDeviceSupportCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA))
            return true;
        else {
            Toast.makeText(MainActivity.this, "No hay camara disponible", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    /*  Capture Image Method  */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//Start intent with Action_Image_Capture
        fileUri = CameraUtils.getOutputMediaFileUri(this);//get fileUri from CameraUtils
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);//Send fileUri with intent
        startActivityForResult(intent, CAMERA_REQUEST_CODE);//start activity for result with CAMERA_REQUEST_CODE
    }

    /*  Show Captured over ImageView  */
    private void showCapturedImage() {
        if (!getImageUrl.equals("") && getImageUrl != null)
            imageView.setImageBitmap(CameraUtils.convertImagePathToBitmap(getImageUrl, false));
        else
            Toast.makeText(this, "Error en la captura", Toast.LENGTH_SHORT).show();
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    /*
     * Here we restore the fileUri again
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }
}