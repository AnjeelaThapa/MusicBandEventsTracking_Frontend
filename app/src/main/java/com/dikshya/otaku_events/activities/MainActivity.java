package com.dikshya.otaku_events.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dikshya.otaku_events.R;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_DURATION = 4000;

    Animation topAnim, bottomAnim;
    ImageView logoImg;
    TextView sloganText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Use this for high end devices (Full screen with punch holes on screen)
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Hooks
        logoImg = findViewById(R.id.logo);
        sloganText = findViewById(R.id.txtSlogan);



        this.logoAnimation();

        SharedPreferences sharedPreferences = getSharedPreferences("AuthSP", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String user_id = sharedPreferences.getString("user_id", "");
        String user_type = sharedPreferences.getString("user_type", "");

        if (!token.equals("")) {
            // Splash screen action to LOGGED IN --
            if (!user_type.equals("admin")) {
                // IF CLIENT --
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(MainActivity.this, BottonNavActivity.class);
                        startActivity(i);
                    }
                }, SPLASH_SCREEN_DURATION);
            } else {
                // IF ADMIN --
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(MainActivity.this, AddEventsActivity.class);
                        startActivity(i);
                    }
                }, SPLASH_SCREEN_DURATION);
            }

        } else {
            // Splash screen action TO NOT LOGGED IN --
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(logoImg, "logo_image");
                    pairs[1] = new Pair<View, String>(sloganText, "logo_name");

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                        startActivity(i, options.toBundle());
                    }

                }
            }, SPLASH_SCREEN_DURATION);
        }
    }

    private void logoAnimation() {
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        logoImg.setAnimation(topAnim);
        sloganText.setAnimation(bottomAnim);
    }
}
