package com.picardystudios.clases;


import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.picardystudios.clases.Util.PermissionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

import static com.picardystudios.clases.Util.AppUtils.enviarPost;
import static com.picardystudios.clases.Util.AppUtils.showToast;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private CameraKitView cameraKitView;
    private CameraKitView cameraFrente;
    private CameraKitView cameraDorso;
    private CameraKitView cameraTexto;
    Activity myactivity;
    CameraKitView cameraSelfie;     LinearLayout linear2;   LinearLayout linear3;   LinearLayout linear4; ImageView myImage;
    ImageView imgFrente;    ImageView imgDorso;    ImageView imgTexto;
    String sResult;       String DNI = null;    String TarjetaC = null; ImageView laimagen;
    EditText nombres; EditText apellidos; EditText documento; EditText nacimiento; EditText direccion;
    EditText localidad; Spinner provincia; EditText telfijo; EditText telmovil; EditText email; TextView textoapapel; TextView textoapapelD;
    int TAKE_PHOTO_CODE = 0;    public static int count = 0;    String sFotoDorso = ""; String sFotoFrente= ""; String sFotoTexto= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        cameraSelfie = (CameraKitView) findViewById(R.id.camera);
        myImage = (ImageView) findViewById(R.id.captured_photo);
        imgFrente = (ImageView) findViewById(R.id.imgFrente);
        imgDorso = (ImageView) findViewById(R.id.imgDorso);
        imgTexto = (ImageView) findViewById(R.id.imgTexto);
        linear2= (LinearLayout) findViewById(R.id.linear2);
        linear3= (LinearLayout) findViewById(R.id.linear2);
        linear4= (LinearLayout) findViewById(R.id.linear2);

        cameraKitView = findViewById(R.id.camera);
        cameraFrente =  (CameraKitView) findViewById(R.id.camFrente);
        cameraDorso =  (CameraKitView) findViewById(R.id.camDorso);
        cameraTexto =  (CameraKitView) findViewById(R.id.camTexto);
        myactivity = this;


        nombres = findViewById(R.id.nombres);           apellidos = findViewById(R.id.apellidos);
        documento = findViewById(R.id.documento);       nacimiento = findViewById(R.id.nacimiento);
        direccion = findViewById(R.id.direccion);       localidad = findViewById(R.id.localidad);
        provincia = findViewById(R.id.provincia);       telfijo = findViewById(R.id.telfijo);
        telmovil = findViewById(R.id.telmovil);         email = findViewById(R.id.email);
        textoapapel = findViewById(R.id.textoapapel);   textoapapelD = findViewById(R.id.textoapapelD);
        EscanearCodigo();


        imgFrente.setOnClickListener((View v) -> {
            imgFrente.setVisibility(View.GONE);
            cameraFrente.onStart();
        });

        imgDorso.setOnClickListener((View v) -> {
            imgDorso.setVisibility(View.GONE);
            cameraDorso.onStart();
        });

        imgTexto.setOnClickListener((View v) -> {
            imgTexto.setVisibility(View.GONE);
            cameraTexto.onStart();
        });



             if (PermissionUtils.isPermissionGranted(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},101)) {
                showToast(this,"Permisos aceptados");
            } else { showToast(this,"Debe aceptar los permisos para continuar..."); }

        myImage.setOnClickListener((View v) -> {

            myImage.setVisibility(View.GONE);
            //cameraSelfie.onResume(); cameraKitView.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.GONE);

        });


        cameraKitView.setGestureListener(new CameraKitView.GestureListener() {
            @Override public void onTap(CameraKitView cameraKitView, float v, float v1) {  captureSave(cameraKitView,v,v1);  }
            @Override public void onLongTap(CameraKitView cameraKitView, float v, float v1) {   }
            @Override public void onDoubleTap(CameraKitView cameraKitView, float v, float v1) {  }
            @Override public void onPinch(CameraKitView cameraKitView, float v, float v1, float v2) {  }
        });

    }






    public void EscanearCodigo() {

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN);
        integrator.setPrompt("Escanee su DNI para completar el formulario.");
        integrator.initiateScan();

    }

    public void ProcesaResultado() {
        Log.e("Resultado",sResult); String sResult1 = sResult; //"9876500491025";
        //String sResult1 = "00217089338@GONZALEZ@MARIELA@0F@10316864@B@03/04/1993@09/09/2013";
        Toast.makeText(this, sResult1, Toast.LENGTH_LONG).show();
        //localidad.setText((sResult1));

        if(sResult1.matches("(.*)@(.*)")) {

            //8 - dos nombres
            String[] split = sResult1.split("@");
            for (count = 0; count < split.length; count++) {        }
            Log.e("miSplit", String.valueOf(count));



            if (count == 9) {  //DNI NUEVO
                nombres.setText(split[2]);
                apellidos.setText(split[1]);
                documento.setText(split[4]);
                nacimiento.setText(split[6]);
                DNI = split[4]; Log.e("Resultado","Verificando su documento #" + DNI + "...");

                String[] arra={"avion","san carlos","mutual","televisor","juegos","ventilador","enchufe","cielo","cortina","turismo","beneficios","tarjeta","gratis","exclusivo","sucursal","club","deportes"};
                String[] arre={"zanahoria","aceite","vaso","impresora","alfombra","auto","tabla","dibujo","enchufe","microfono","cama","cemento"};
                String[] arri={"energia","naranja","tinta","papel","barba","viajes","sistema","amigo","felicidad","insomio","libro","mouse","cartas","flauta","guitarra"};
                Random ra=new Random();   int randoma=ra.nextInt(arra.length);       String stringa = arra[randoma];
                Random re=new Random();   int randome=re.nextInt(arre.length);       String stringe = arre[randome];
                Random ri=new Random();   int randomi=ri.nextInt(arri.length);      String stringi = arri[randomi];

                String TextoAEscribir = "Yo, "+apellidos.getText()+ " "+nombres.getText()+", con Documento N°: "+documento.getText()+", solicito ingresar como socio a la Asociación Mutualista del Club Central San Carlos. "+stringa+" | "+stringe+" | "+stringi+" ";

                textoapapel.setText(TextoAEscribir);
            }

            else if (count == 17) {  //DNI VIEJO
                nombres.setText(split[5]);
                apellidos.setText(split[4]);
                documento.setText(split[1]);
                nacimiento.setText(split[7]);
                DNI = split[4]; Log.e("Resultado","Verificando su documento #" + DNI + "...");

                String[] arra={"avion","san carlos","mutual","televisor","juegos","ventilador","enchufe","cielo","cortina","turismo","beneficios","tarjeta","gratis","exclusivo","sucursal","club","deportes"};
                String[] arre={"zanahoria","aceite","vaso","impresora","alfombra","auto","tabla","dibujo","enchufe","microfono","cama","cemento"};
                String[] arri={"energia","naranja","tinta","papel","barba","viajes","sistema","amigo","felicidad","insomio","libro","mouse","cartas","flauta","guitarra"};
                Random ra=new Random();   int randoma=ra.nextInt(arra.length);       String stringa = arra[randoma];
                Random re=new Random();   int randome=re.nextInt(arre.length);       String stringe = arre[randome];
                Random ri=new Random();   int randomi=ri.nextInt(arri.length);      String stringi = arri[randomi];

                String TextoAEscribir = "Yo, "+apellidos.getText()+ " "+nombres.getText()+", con Documento N°: "+documento.getText()+", solicito ingresar como socio a la Asociación Mutualista del Club Central San Carlos. "+stringa+" | "+stringe+" | "+stringi+" ";

                textoapapel.setText(TextoAEscribir);

            } else {   //DNI NUEVO
                nombres.setText(split[2]);
                apellidos.setText(split[1]);
                documento.setText(split[4]);
                nacimiento.setText(split[6]);
                DNI = split[4]; Log.e("Resultado","Verificando su documento #" + DNI + "...");

                String[] arra={"avion","san carlos","mutual","televisor","juegos","ventilador","enchufe","cielo","cortina","turismo","beneficios","tarjeta","gratis","exclusivo","sucursal","club","deportes"};
                String[] arre={"zanahoria","aceite","vaso","impresora","alfombra","auto","tabla","dibujo","enchufe","microfono","cama","cemento"};
                String[] arri={"energia","naranja","tinta","papel","barba","viajes","sistema","amigo","felicidad","insomio","libro","mouse","cartas","flauta","guitarra"};
                Random ra=new Random();   int randoma=ra.nextInt(arra.length);       String stringa = arra[randoma];
                Random re=new Random();   int randome=re.nextInt(arre.length);       String stringe = arre[randome];
                Random ri=new Random();   int randomi=ri.nextInt(arri.length);      String stringi = arri[randomi];

                String TextoAEscribir = "Yo, "+apellidos.getText()+ " "+nombres.getText()+", con Documento N°: "+documento.getText()+", solicito ingresar como socio a la Asociación Mutualista del Club Central San Carlos. "+stringa+" | "+stringe+" | "+stringi+" ";

                textoapapel.setText(TextoAEscribir);
            }


            Toast.makeText(this, "Verificando su documento #" + DNI + "...", Toast.LENGTH_LONG).show();

            if (DNI != null) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                        if (Build.VERSION.SDK_INT > 9) {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                        }
                        //db.upUser(userDetails.get("name"),userDetails.get("celular"),userDetails.get("email"),userDetails.get("tarjeta"),userDetails.get("documento"),"true");
                        //UserFunctions userFunction = new UserFunctions();
                        //userFunction.updateUser(userDetails.get("name"), userDetails.get("email"), userDetails.get("password"),userDetails.get("documento"),userDetails.get("celular"),userDetails.get("sucursal"),ubicacion, refreshedToken,"true");


                        Toast.makeText(getBaseContext(), "Verifique los datos y complete los faltantes.", Toast.LENGTH_LONG).show();


                    }
                }, 1000);

            }else{   Toast.makeText(getBaseContext(), "No hemos podido verificar su DNI, intente nuevamente.", Toast.LENGTH_LONG).show(); }
        } else { Toast.makeText(getBaseContext(), "Codigo no válido, intente nuevamente.", Toast.LENGTH_LONG).show(); }

    }






        public void captureSave(CameraKitView cameraKitView, float v, float v1){

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
                            myImage.setImageBitmap(myBitmap);
                            myImage.setVisibility(View.VISIBLE);
                            //cameraKitView.setVisibility(View.GONE);
                            linear2.setVisibility(View.VISIBLE);

                        }

                    } catch (java.io.IOException e) {
                        e.printStackTrace(); Log.e("Camera Exception",e.toString());
                    }
                }
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