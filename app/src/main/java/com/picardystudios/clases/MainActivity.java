package com.picardystudios.clases;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
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
import com.camerakit.api.camera1.Camera1;
import com.camerakit.api.camera2.Camera2;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.picardystudios.clases.Util.PermissionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

import static com.picardystudios.clases.Util.AppUtils.enviarPost;
import static com.picardystudios.clases.Util.AppUtils.showToast;
import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity {
    Uri UriFrenteDNI; File photoFrenteDNI;     Bitmap bitmap;   ImageView mimageView;
    Uri UriDorsoDNI; File photoDorsoDNI;     Bitmap bitmapD;   ImageView mimageViewD;
    Uri UriTexto; File photoTexto;     Bitmap bitmapU;   ImageView mimageViewU;
    Boolean EnviaroNo = true;
    private static final int CAMERA_TEXTO = 1888;    private static final int CAMERA_DORSODNI = 1889;    private static final int CAMERA_FRENTEDNI = 1890;
    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;
    private static final String TAG = MainActivity.class.getSimpleName();
    private CameraKitView cameraKitView;     Activity myactivity;
    CameraKitView cameraSelfie;     LinearLayout linear2;   LinearLayout linear3;   LinearLayout linear4; ImageView myImage;
    ImageView imgFrente;    ImageView imgDorso;    ImageView imgTexto;
    String sResult;       String DNI = null;    String TarjetaC = null; ImageView laimagen;
    EditText nombres; EditText apellidos; EditText documento; EditText nacimiento; EditText direccion;
    EditText localidad; Spinner provincia; EditText telfijo; EditText telmovil; EditText email; TextView textoapapel; TextView textoapapelD;
    int TAKE_PHOTO_CODE = 0;    public static int count = 0;    String sFotoDorso = ""; String sFotoFrente= ""; String sFotoTexto= ""; String sFotoSelfie = "";

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        File dis = File.createTempFile(part, ext, tempDir);
        Log.e("Dis", String.valueOf(dis));
        return File.createTempFile(part, ext, tempDir);
    }

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

        cameraKitView = (CameraKitView) findViewById(R.id.camera);
        myactivity = this;


        nombres = findViewById(R.id.nombres);           apellidos = findViewById(R.id.apellidos);
        documento = findViewById(R.id.documento);       nacimiento = findViewById(R.id.nacimiento);
        direccion = findViewById(R.id.direccion);       localidad = findViewById(R.id.localidad);
        provincia = findViewById(R.id.provincia);       telfijo = findViewById(R.id.telfijo);
        telmovil = findViewById(R.id.telmovil);         email = findViewById(R.id.email);
        textoapapel = findViewById(R.id.textoapapel);   textoapapelD = findViewById(R.id.textoapapelD);



        EscanearCodigo();


        // BOTON FOTO SELFIE
        Button bSelfie = (Button) findViewById(R.id.bselfie);
        bSelfie.setOnClickListener((View v) -> {
          if(myImage.getVisibility() == View.VISIBLE)  { myImage.setVisibility(View.GONE); cameraKitView.onResume();  Log.e("bSelfie","show camera"); }
          else if(myImage.getVisibility() == View.GONE)  { captureSave(cameraKitView,0,0);  Log.e("bSelfie","Capture"); }
          else { Log.e("bSelfie","ni Visible ni Gone"); }

        });


        // BOTON FOTO FRENTE DNI
        Button frenteDNI = (Button) findViewById(R.id.frenteDNI);
        frenteDNI.setOnClickListener((View v) -> {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {  photoFrenteDNI = createTemporaryFile("MutualCentral_FrenteDNI", ".jpg");  }
        catch(Exception e)  { Toast.makeText(getApplicationContext(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG); }
        UriFrenteDNI = PhotoProvider.getPhotoUri(photoFrenteDNI);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, UriFrenteDNI);
        startActivityForResult(intent, CAMERA_FRENTEDNI);
        });


        // BOTON FOTO DORSO DNI
        Button dorsoDNI = (Button) findViewById(R.id.dorsoDNI);
        dorsoDNI.setOnClickListener((View v) -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {  photoDorsoDNI = createTemporaryFile("MutualCentral_DorsoDNI", ".jpg");  }
            catch(Exception e)  { Toast.makeText(getApplicationContext(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG); }
            UriDorsoDNI = PhotoProvider.getPhotoUri(photoDorsoDNI);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, UriDorsoDNI);
            startActivityForResult(intent, CAMERA_DORSODNI);

        });



        // BOTON FOTO TEXTO MANUSCRITO
        Button TextoManuscrito = (Button) findViewById(R.id.fototexto);
        TextoManuscrito.setOnClickListener((View v) -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {  photoTexto = createTemporaryFile("MutualCentral_Texto", ".jpg");  }
            catch(Exception e)  { Toast.makeText(getApplicationContext(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG); }
            UriTexto = PhotoProvider.getPhotoUri(photoTexto);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, UriTexto);
            // And away we go!
            startActivityForResult(intent, CAMERA_TEXTO);

        });


        // ENVIAR SOLICITUD
        Button EnviarSolicitud = (Button) findViewById(R.id.enviarsolicitud);
        EnviarSolicitud.setOnClickListener((View v) -> {
            //EditText nombres; EditText apellidos; EditText documento; EditText nacimiento; EditText direccion;
            //EditText localidad; Spinner provincia; EditText telfijo; EditText telmovil; EditText email;

            EnviaroNo = true;



            if(sFotoSelfie.replaceAll("\\s+","").equals("")) {
                showToast(this,"Debe sacarse una selfie."); Log.e("Verificacion","Debe sacarse una selfie.");   EnviaroNo = false; }

            if(nombres.getText().toString().replaceAll("\\s+","").equals("")) {
                showToast(this,"Debe completar su Nombre."); Log.e("Verificacion","Debe completar su Nombre.");   EnviaroNo = false; }

            if(apellidos.getText().toString().replaceAll("\\s+","").equals("")) {
                showToast(this,"Debe completar su Apellido."); Log.e("Verificacion","Debe completar su Apellido.");   EnviaroNo = false; }

            if(documento.getText().toString().replaceAll("\\s+","").equals("")) {
                showToast(this,"Debe completar su Documento."); Log.e("Verificacion","Debe completar su Documento.");   EnviaroNo = false; }

            if(nacimiento.getText().toString().replaceAll("\\s+","").equals("")) {
                showToast(this,"Debe completar su Fecha de Nacimiento."); Log.e("Verificacion","Debe completar su Fecha de Nacimiento.");   EnviaroNo = false; }

            if(direccion.getText().toString().replaceAll("\\s+","").equals("")) {
                showToast(this,"Debe completar su Direccion."); Log.e("Verificacion","Debe completar su Direccion.");   EnviaroNo = false; }

            if(localidad.getText().toString().replaceAll("\\s+","").equals("")) {
                showToast(this,"Debe completar su Localidad."); Log.e("Verificacion","Debe completar su Localidad.");   EnviaroNo = false; }

            if((telfijo.getText().toString().replaceAll("\\s+","").equals(""))&&(telmovil.getText().toString().replaceAll("\\s+","").equals(""))) {
                showToast(this,"Debe completar un número de telefono."); Log.e("Verificacion","Debe completar un número de telefono.");   EnviaroNo = false; }

            if(email.getText().toString().replaceAll("\\s+","").equals("")) {
                showToast(this,"Debe completar su Email."); Log.e("Verificacion","Debe completar su Email.");   EnviaroNo = false; }


            if(sFotoFrente.replaceAll("\\s+","").equals("")) {
                showToast(this,"Debe sacarle una foto al frente del DNI."); Log.e("Verificacion","Debe sacarle una foto al frente del DNI.");   EnviaroNo = false; }

            if(sFotoDorso.replaceAll("\\s+","").equals("")) {
                showToast(this,"Debe sacarle una foto al dorso del DNI.."); Log.e("Verificacion","Debe sacarle una foto al dorso del DNI.");   EnviaroNo = false; }

            if(sFotoTexto.replaceAll("\\s+","").equals("")) {
                showToast(this,"Debe sacarle una foto al Texto escrito a mano."); Log.e("Verificacion","Debe sacarle una foto al Texto escrito a mano.");   EnviaroNo = false; }

            if(EnviaroNo==true) { EnviarSolicitud.setEnabled(false); enviarPost(this, nombres.getText().toString() , apellidos.getText().toString() , documento.getText().toString() , nacimiento.getText().toString() , direccion.getText().toString() , provincia.getSelectedItem().toString(), localidad.getText().toString() , telfijo.getText().toString() , telmovil.getText().toString() , email.getText().toString() , sFotoDorso, sFotoFrente, sFotoSelfie, sFotoTexto); } else { Log.e("Boton","No True"); }

        });


        if (PermissionUtils.isPermissionGranted(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},101)) {
           showToast(this,"Permisos aceptados");
        } else { showToast(this,"Debe aceptar los permisos para continuar..."); }




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
        Log.e("captureSave","Starting...");

            cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {

                    File savedPhoto = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
                    try {
                        FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                        outputStream.write(capturedImage);
                        outputStream.close();
                        Log.e("Captura", "OK! " + String.valueOf(savedPhoto));
                        if (savedPhoto.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(savedPhoto.getAbsolutePath());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();
                            String img_str = android.util.Base64.encodeToString(b, 0);
                            //Log.e("Base64", img_str);
                            sFotoSelfie = img_str;
                            //enviarPost(myactivity, img_str, img_str, img_str);
                            myImage.setImageBitmap(myBitmap);
                            myImage.setVisibility(View.VISIBLE);
                            cameraKitView.onPause();
                            linear2.setVisibility(View.VISIBLE);

                        }

                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                        Log.e("Camera Exception", e.toString());
                    }
                }
                    });

            }


    @Override
    protected void onStart() {
        super.onStart();


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











    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("requestCode", String.valueOf(requestCode)); Log.e("resultCode", String.valueOf(resultCode));
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);



        if (requestCode == 49374 ) {

            switch (requestCode) {
                case CUSTOMIZED_REQUEST_CODE: {
                    Toast.makeText(this, "Datos escaneados, aguarde...", Toast.LENGTH_LONG).show();
                    // + requestCode
                    sResult = String.valueOf(requestCode);  ProcesaResultado();
                    break;
                }
                default:
                    break;
            }


            if(result.getContents() == null) {
                Log.d("MainActivity", "Verificación cancelada");
                Toast.makeText(this, "Verificación cancelSda!", Toast.LENGTH_LONG).show();
                sResult = "Verificación cancelada";   finishActivity(requestCode); //ProcesaResultado();
            } else {
                Log.d("MainActivity", "Datos escaneados, aguarde...");
                Toast.makeText(this, "Datos escaneados, aguarde...", Toast.LENGTH_LONG).show();
                //+ result.getContents()
                sResult = result.getContents();  finishActivity(requestCode); ProcesaResultado();
            }

            cameraKitView.onStart();
        }



        if (requestCode == CAMERA_FRENTEDNI && resultCode == RESULT_OK) {
            //Bitmap pmphoto = (Bitmap) data.getExtras().get("data");
            this.getContentResolver().notifyChange(UriFrenteDNI, null);
            ContentResolver cr = this.getContentResolver();
            try
            {
                bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, UriFrenteDNI);
                //bitmap = scaleBitmap(bitmap);
                Log.e("Bitmap", String.valueOf(bitmap));
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] image=stream.toByteArray();

                imgFrente.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
                //mimageView.setImageBitmap(pmphoto);
                sFotoFrente = android.util.Base64.encodeToString(image, 0);
                //mimageView.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                Log.e("UriUri", "Failed to load", e);
            }

        }












        if (requestCode == CAMERA_DORSODNI && resultCode == RESULT_OK) {
            //Bitmap pmphoto = (Bitmap) data.getExtras().get("data");
            this.getContentResolver().notifyChange(UriDorsoDNI, null);
            ContentResolver cr = this.getContentResolver();
            try
            {
                bitmapD = android.provider.MediaStore.Images.Media.getBitmap(cr, UriDorsoDNI);
                //bitmap = scaleBitmap(bitmap);
                Log.e("Bitmap", String.valueOf(bitmapD));
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmapD.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] image=stream.toByteArray();

                imgDorso.setImageBitmap(Bitmap.createScaledBitmap(bitmapD, 120, 120, false));
                //mimageView.setImageBitmap(pmphoto);
                sFotoDorso = android.util.Base64.encodeToString(image, 0);
                //mimageView.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                Log.e("UriUri", "Failed to load", e);
            }

        }












        if (requestCode == CAMERA_TEXTO && resultCode == RESULT_OK) {
            //Bitmap pmphoto = (Bitmap) data.getExtras().get("data");
            this.getContentResolver().notifyChange(UriTexto, null);
            ContentResolver cr = this.getContentResolver();
            try
            {
                bitmapU = android.provider.MediaStore.Images.Media.getBitmap(cr, UriTexto);
                //bitmap = scaleBitmap(bitmap);
                Log.e("Bitmap", String.valueOf(bitmapU));
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmapU.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] image=stream.toByteArray();

                imgTexto.setImageBitmap(Bitmap.createScaledBitmap(bitmapU, 120, 120, false));
                //mimageView.setImageBitmap(pmphoto);
                sFotoTexto = android.util.Base64.encodeToString(image, 0);
                //mimageView.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                Log.e("UriUri", "Failed to load", e);
            }

        }





    }


}