package com.example.infinity.pixie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.infinity.pixie.util.AnimationView;

public class HomeActivity extends AppCompatActivity {

    private AnimationView gifImageView;
    private TextView existingUser;
    private Button getStarted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(Pixie.P.AUTH_CODE!=null)
        {
            goToMain();
        }
        initView();

        gifImageView.setGifImageResource(R.drawable.gif_image);

        existingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent existingUserIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(existingUserIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent = new Intent(HomeActivity.this, SignupActivity.class);
                startActivity(signupIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    public void initView()
    {
        gifImageView = (AnimationView) findViewById(R.id.gifImageView);
        existingUser = (TextView) findViewById(R.id.existingUser);
        getStarted = (Button) findViewById(R.id.getStarted);
    }

    public void goToMain() {
        HomeActivity.this.finish();
        Intent callIntent = new Intent(HomeActivity.this, MainActivity.class);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(callIntent);
        //onDestroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
