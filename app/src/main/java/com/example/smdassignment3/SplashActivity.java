package com.example.smdassignment3;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int MAIN_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        TextView heading = findViewById(R.id.welcomeText);

        // Load animations
        Animation translateAnim = AnimationUtils.loadAnimation(this, R.anim.translate);
        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);

        // Start the translation animation first
        logo.startAnimation(translateAnim);
        heading.startAnimation(translateAnim);
        translateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start scale animation after translation
                logo.startAnimation(scaleAnim);
                heading.startAnimation(scaleAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                // When the scale animation ends, start MainActivity using StartActivityForResult
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivityForResult(intent, MAIN_ACTIVITY_REQUEST_CODE);
                finish();  // Finish the SplashActivity
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle any results returned from MainActivity here if needed
    }
}
