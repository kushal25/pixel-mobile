package com.example.infinity.pixie;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
    //Set Image Name On creation
    public static String imgName;
    public static void setImage(String imgN){
        imgName = imgN;
    }
    public static String getImgName(){
        return imgName;
    }
    public static Typeface fontawesome = null;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final String TAG = "Pixie";

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
        public static String APP_VERSION = "1.0.0";
        public static String AUTH_CODE = null;
        public static String NAME = "";
        public static String EMAIL_ADDRESS = "";
        public static String MOBILE_NUMBER = "";
        public static String LAST_LOGIN_AT = "";
        public static String USER_STATUS = "";
        public static String SHARING_MSG = "Pixie is your Personal Virtual Assistant. Invite your friends";

        public static void read(Context cx)
        {
            try
            {
                if (null == fontawesome) {
                    fontawesome = Typeface.createFromAsset(cx.getAssets(), "fontawesome.ttf");
                }

                SharedPreferences pref = cx.getSharedPreferences(TAG, Activity.MODE_PRIVATE);

                AUTH_CODE = pref.getString("AUTH_CODE", AUTH_CODE);
                NAME = pref.getString("NAME", NAME);
                EMAIL_ADDRESS = pref.getString("EMAIL_ADDRESS", EMAIL_ADDRESS);
                MOBILE_NUMBER = pref.getString("MOBILE_NUMBER", MOBILE_NUMBER);
                LAST_LOGIN_AT = pref.getString("LAST_LOGIN_AT", LAST_LOGIN_AT);
                USER_STATUS = pref.getString("USER_STATUS", USER_STATUS);
                SHARING_MSG = pref.getString("SHARING_MSG", SHARING_MSG);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        public static void write(Context cx)
        {
            try
            {
                SharedPreferences.Editor prefEditor = cx.getSharedPreferences(TAG, Activity.MODE_PRIVATE).edit();
                prefEditor.putString("AUTH_CODE", AUTH_CODE);
                prefEditor.putString("NAME", NAME);
                prefEditor.putString("EMAIL_ADDRESS", EMAIL_ADDRESS);
                prefEditor.putString("MOBILE_NUMBER", MOBILE_NUMBER);
                prefEditor.putString("LAST_LOGIN_AT", LAST_LOGIN_AT);
                prefEditor.putString("USER_STATUS", USER_STATUS);
                prefEditor.putString("SHARING_MSG", SHARING_MSG);

                prefEditor.commit();

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
