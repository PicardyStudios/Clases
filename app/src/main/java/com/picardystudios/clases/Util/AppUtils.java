package com.picardystudios.clases.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.picardystudios.clases.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by Ashiq on 4/12/2017.
 */

public abstract class AppUtils extends  Context {

    private static long backPressed = 0;

    public static void mostrarRespuesta (Activity activity, String response) {
        showToast(activity,response); Log.e("Resultado",response);
        TextView ResultadoText = (TextView) activity.findViewById(R.id.textView3);
        ResultadoText.setTextColor(Color.RED); ResultadoText.setText(response);

    }
    public static void enviarPost(Activity activity, String nombres, String apellidos, String documento, String nacimiento, String direccion, String provincia,
                                  String localidad, String telfijo, String telmovil, String email, String sFotoDorso, String sFotoFrente, String sFotoSelfie, String sFotoTexto)
 {
        Log.e("enviarPost","Iniciado..");
        RequestQueue MyRequestQueue = Volley.newRequestQueue(activity);
        String url = "http://clienteamigo.com.ar/test.php";
                StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,
                response -> mostrarRespuesta(activity,response),
                error -> Log.e("Respuesta Error", "2"+String.valueOf(error)))
                {        protected Map<String, String> getParams() {
                         Map<String, String> MyData = new HashMap<String, String>();
                         MyData.put("nombres",nombres);
                         MyData.put("apellidos", apellidos);
                         MyData.put("documento", documento);
                    MyData.put("nacimiento", nacimiento);
                    MyData.put("direccion", direccion);
                    MyData.put("provincia", provincia);
                    MyData.put("localidad", localidad);
                    MyData.put("telfijo", telfijo);
                    MyData.put("telmovil", telmovil);
                    MyData.put("email", email);
                    MyData.put("sFotoDorso", sFotoDorso);
                    MyData.put("sFotoFrente", sFotoFrente);
                    MyData.put("sFotoSelfie", sFotoSelfie);
                    MyData.put("sFotoTexto", sFotoTexto);

                return MyData;
            }
        };


        MyRequestQueue.add(MyStringRequest);
        Log.e("enviarPost","End?..");
    }
    public static void tapToExit(Activity activity) {
        if (backPressed + 2500 > System.currentTimeMillis()) {
            activity.finish();
        } else {
            showToast(activity.getApplicationContext(), activity.getResources().getString(R.string.tapAgain));
        }
        backPressed = System.currentTimeMillis();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private static String formatPhoneNumber(String previousPhoneNumber) {
        if (previousPhoneNumber != null) {
            previousPhoneNumber = previousPhoneNumber.replaceAll(" ", "");
            if (!previousPhoneNumber.startsWith("0") && !previousPhoneNumber.startsWith("+")) {
                return "0" + previousPhoneNumber;
            }
        }
        return previousPhoneNumber;
    }

    public static void noInternetWarning(View view, final Context context) {
        if (!isNetworkAvailable(context)) {
            Snackbar snackbar = Snackbar.make(view, context.getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(context.getString(R.string.connect), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            snackbar.show();
        }
    }

    public static void youtubeLink(Activity activity) {
        updateLink(activity, activity.getString(R.string.youtube_url));
    }

    public static void faceBookLink(Activity activity) {
        try {
            ApplicationInfo applicationInfo = activity.getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                updateLink(activity, "fb://facewebmodal/f?href=" + activity.getString(R.string.facebook_url));
                return;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        updateLink(activity, activity.getString(R.string.facebook_url));
    }

    public static void twitterLink(Activity activity) {
        try {
            ApplicationInfo applicationInfo = activity.getPackageManager().getApplicationInfo("com.twitter.android", 0);
            if (applicationInfo.enabled) {
                updateLink(activity, activity.getString(R.string.twitter_user_id));
                return;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        updateLink(activity, activity.getString(R.string.twitter_url));
    }

    public static void googlePlusLink(Activity activity) {
        updateLink(activity, activity.getString(R.string.instagram_url));
    }

    private static void updateLink(Activity activity, String text) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager packageManager = activity.getPackageManager();
        if (packageManager.resolveActivity(i,
                PackageManager.MATCH_DEFAULT_ONLY) != null) {
            activity.startActivity(i);
        }
    }

    public static void shareApp(Activity activity) {
        try {
            final String appPackageName = activity.getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, activity.getResources().getString(R.string.share_text) + " https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            activity.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rateThisApp(Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void appClosePrompt(final Activity activity) {
        DialogUtils.showDialogPrompt(activity, null, "Esta seguro que desea cerrar la app?", "Si", "No", true, new DialogUtils.DialogActionListener() {
            @Override
            public void onPositiveClick() {
                //activity.finish();
                activity.finishAffinity();
            }
        });
    }



    public static void sendSMS(Activity activity, String phoneNumber, String text) {
        if (phoneNumber != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
            intent.putExtra("sms_body", text);
            try {
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendEmail(Activity activity, String email, String subject, String body) {
        if (email != null) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            i.putExtra(Intent.EXTRA_SUBJECT, subject);
            i.putExtra(Intent.EXTRA_TEXT, body);
            try {
                activity.startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void invokeMessenger(Activity activity) {
        try {
            if (AppUtils.isPackageInstalled(activity.getApplicationContext(), "com.facebook.orca")) {

                /**
                 * get id of your facebook page from here:
                 * https://findmyfbid.com/
                 *
                 * Suppose your facebook page url is: http://www.facebook.com/hiponcho
                 *
                 * Visit https://findmyfbid.com/ and put your url and click on "Find Numeric Id"
                 * You will get and ID like this: 788720331154519
                 *
                 * Append an extra 'l' with the number and please
                 * bellow. This is not 1 (one), this is l (l for long)
                 * So, final ID: 788720331154519l
                 */

                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://messaging/" + 788720331154519l))); // replace id
            } else {
                AppUtils.showToast(activity.getApplicationContext(),
                        activity.getApplicationContext().getResources().getString(R.string.install_messenger));
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.facebook.orca")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPackageInstalled(Context context, String packagename) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(packagename, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getFormattedDate(String oldDate) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(oldDate);
            SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            return dt1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
