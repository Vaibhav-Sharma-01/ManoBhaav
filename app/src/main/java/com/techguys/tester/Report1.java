package com.techguys.tester;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.callback.OnPieSelectListener;
import com.razerdp.widget.animatedpieview.data.IPieInfo;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

public class Report1 extends AppCompatActivity {
    public Context context=Report1.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        setContentView(R.layout.activity_report1);
        TextView intro=findViewById(R.id.intro_txt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intro.setText("click pie chart to see the scores");
        AnimatedPieView animatedPieView=findViewById(R.id.pie);
        AnimatedPieViewConfig animatedPieViewConfig=new AnimatedPieViewConfig();
        animatedPieViewConfig.addData(new SimplePieInfo(sentimentanalyser.positive, Color.GREEN,"POSITIVE"));
        animatedPieViewConfig.addData(new SimplePieInfo(sentimentanalyser.neutral, Color.LTGRAY,"NEUTRAL"));
        animatedPieViewConfig.addData(new SimplePieInfo(sentimentanalyser.negative, Color.RED,"NEGATIVE"));
        animatedPieViewConfig.duration(5000);
        animatedPieViewConfig.drawText(true);
        animatedPieViewConfig.strokeMode(false);
        animatedPieViewConfig.textSize(30);
        animatedPieViewConfig.selectListener(new OnPieSelectListener<IPieInfo>() {
            @Override
            public void onSelectPie(@NonNull IPieInfo pieInfo, boolean isFloatUp) {
                Toast.makeText(Report1.this, pieInfo.getDesc()+"-"+pieInfo.getValue(), Toast.LENGTH_SHORT).show();
            }
        });
        animatedPieViewConfig.startAngle(-180);
        animatedPieView.applyConfig(animatedPieViewConfig);
        animatedPieView.start();
    }
}
