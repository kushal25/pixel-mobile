package com.example.infinity.pixie;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by infinity on 4/21/17.
 */

public class Pixie {

    public static Typeface fontawesome = null;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void appInit(Context cx) {
        P.read(cx);

    }

    public static void showToast(Context cx, String st)
    {
        Toast.makeText(cx, st,Toast.LENGTH_SHORT).show();
    }

    public static void verifyStoragePermissions(Activity cx, String readExternalStorage) {
        int permission = ActivityCompat.checkSelfPermission(cx, readExternalStorage);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    cx,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void runOnBackground(final Context cx, final Runnable r) {
        new Thread() {
            @Override
            public void run() {
                try {
                    r.run();
                } catch (Exception e) {
                    //L.fe(cx, Event.EXCEPTION, e);
                    Log.d("Error", e.toString());
                }
            }
        }.start();
    }

    public static boolean isNetworkOK(Context cx) {
        try {
            ConnectivityManager cm = (ConnectivityManager) cx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null == cm) {
                return false;
            }
            NetworkInfo ni;
            ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (null != ni && ni.isConnectedOrConnecting()) return true;
            ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (null != ni && ni.isConnectedOrConnecting()) return true;
            ni = cm.getActiveNetworkInfo();
            if (null != ni && ni.isConnectedOrConnecting()) return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static final class P {
        public static void read(Context cx)
        {
            try {
                if (null == fontawesome) {
                    fontawesome = Typeface.createFromAsset(cx.getAssets(), "fontawesome.ttf");
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
