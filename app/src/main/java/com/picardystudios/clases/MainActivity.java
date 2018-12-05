package com.picardystudios.clases;


import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.camerakit.CameraKitView;
import com.picardystudios.clases.Util.PermissionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import androidx.appcompat.app.AppCompatActivity;

import static com.picardystudios.clases.Util.AppUtils.enviarPost;
import static com.picardystudios.clases.Util.AppUtils.showToast;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private CameraKitView cameraKitView; Activity myactivity;
    CameraKitView cameraSelfie;     LinearLayout linear2;   LinearLayout linear3;   LinearLayout linear4; ImageView myImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        cameraSelfie = (CameraKitView) findViewById(R.id.camera);
        myImage = (ImageView) findViewById(R.id.captured_photo);
        linear2= (LinearLayout) findViewById(R.id.linear2);
        linear3= (LinearLayout) findViewById(R.id.linear2);
        linear4= (LinearLayout) findViewById(R.id.linear2);

        cameraKitView = findViewById(R.id.camera);
        myactivity = this;

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
                            outputStream.close();    Log.e("Captura","OK! "+String.valueOf(savedPhoto));
                           if(savedPhoto.exists()){
                                Bitmap myBitmap = BitmapFactory.decodeFile(savedPhoto.getAbsolutePath());
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
                                byte[] b = baos.toByteArray();
                                String img_str = android.util.Base64.encodeToString(b, 0); Log.e("Base64",img_str);
                                enviarPost(myactivity,img_str,img_str,img_str);
                                myImage.setImageBitmap(myBitmap);  myImage.getLayoutParams().height = 400; myImage.setVisibility(View.VISIBLE);
                               cameraSelfie.getLayoutParams().height = 1;  cameraSelfie.setVisibility(View.INVISIBLE);
                               linear2.setVisibility(View.VISIBLE);

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



             if (PermissionUtils.isPermissionGranted(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},101)) {
                showToast(this,"Permisos aceptados");
            } else { showToast(this,"Debe aceptar los permisos para continuar..."); }

        myImage.setOnClickListener((View v) -> {

            myImage.getLayoutParams().height = 1; myImage.setVisibility(View.INVISIBLE);
            cameraSelfie.getLayoutParams().height = 400;  cameraSelfie.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.INVISIBLE);
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