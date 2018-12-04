package com.picardystudios.clases;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.camerakit.CameraKitView;
import com.picardystudios.clases.Util.CameraUtils;
import com.picardystudios.clases.Util.ImagePath_MarshMallow;
import com.picardystudios.clases.Util.PermissionUtils;

import java.io.File;
import java.io.FileOutputStream;

import androidx.appcompat.app.AppCompatActivity;

import static com.picardystudios.clases.Util.AppUtils.showToast;
import static com.picardystudios.clases.Util.PermissionUtils.isPermissionGranted;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private CameraKitView cameraKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        cameraKitView = findViewById(R.id.camera);

        cameraKitView.setGestureListener(new CameraKitView.GestureListener() {
            @Override
            public void onTap(CameraKitView cameraKitView, float v, float v1) {

            }

            @Override
            public void onLongTap(CameraKitView cameraKitView, float v, float v1) {

            }

            @Override
            public void onDoubleTap(CameraKitView cameraKitView, float v, float v1) {


                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                        File savedPhoto = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
                        try {
                            FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                            outputStream.write(capturedImage);
                            outputStream.close();
                            Log.e("Captura","OK! "+String.valueOf(savedPhoto));


                           if(savedPhoto.exists()){

                                Bitmap myBitmap = BitmapFactory.decodeFile(savedPhoto.getAbsolutePath());

                                ImageView myImage = (ImageView) findViewById(R.id.captured_photo);

                                myImage.setImageBitmap(myBitmap);

                            }

                        } catch (java.io.IOException e) {
                            e.printStackTrace(); Log.e("Camera Exception",e.toString());
                        }
                    }
                });

            }

            @Override
            public void onPinch(CameraKitView cameraKitView, float v, float v1, float v2) {

            }
        });


        Button clickButton = (Button) findViewById(R.id.bpermisos);
        clickButton.setOnClickListener((View v) -> {
             if (PermissionUtils.isPermissionGranted(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},101)) {
                showToast(this,"Permisos aceptados");
            } else { showToast(this,"Debe aceptar los permisos para continuar..."); }
        });

        Button clickCapture = (Button) findViewById(R.id.bfotos);
        clickCapture.setOnClickListener((View v) -> {


        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}