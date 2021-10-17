package com.techguys.tester;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splashscreen extends AppCompatActivity {
    public static int SPLASHSCREEN = 5000;
    Animation topanim;
    ImageView image;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        constraintLayout=findViewById(R.id.temp);
        topanim = AnimationUtils.loadAnimation(this, R.anim.topanim);
        image = findViewById(R.id.imageView3);
        image.setAnimation(topanim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), login.class));
                finish();
            }
        }, SPLASHSCREEN);
    }
}
