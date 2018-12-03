package com.picardystudios.clases.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.picardystudios.clases.R;



public class Utilidades {

    public static void appClosePrompt(final Activity activity) {
        DialogUtils.showDialogPrompt(activity, null, "Esta seguro que desea cerrar la app?", "Si","No", true, new DialogUtils.DialogActionListener() {
            @Override
            public void onPositiveClick() {
                //activity.finish();
                activity.finishAffinity();
            }
        });
    }

    public static void noInternetWarning(View view, final Context context) {
        if (!isNetworkAvailable(context)) {
            Snackbar snackbar = Snackbar.make(view,"No hay conexión.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Conectar", new View.OnClickListener() {
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

    private static long backPressed = 0;

    public static void tapToExit(Activity activity) {
        if (backPressed + 2500 > System.currentTimeMillis()) {
            activity.finish();
        } else {
            showToast(activity.getApplicationContext(), "Presione otra vez para salir.");
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
}

