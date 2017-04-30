package com.example.infinity.pixie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.infinity.pixie.util.AnimationView;

import java.io.IOException;
import java.io.InputStream;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        AnimationView gifImageView = (AnimationView) findViewById(R.id.gifImageView);
        gifImageView.setGifImageResource(R.drawable.gif_image);
    }
}
