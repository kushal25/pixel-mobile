package com.example.infinity.pixie;

import android.app.Application;

/**
 * Created by infinity on 4/21/17.
 */

public class PixieApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Pixie.appInit(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
