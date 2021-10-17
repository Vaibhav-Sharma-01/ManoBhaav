package com.techguys.tester;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class wrapperscheduler extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    static EditText editText,editText2,editText3;
    static String th;
    static Calendar startTime,st;
    AlarmManager alarmManager;
    PendingIntent alarmIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        setContentView(R.layout.activity_wrapperscheduler);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences("checkboxes", MODE_PRIVATE);
        String h1 = sharedPreferences.getString("s1", "");
        String h2 = sharedPreferences.getString("s2", "");
        String h3 = sharedPreferences.getString("s3", "");
        editText = findViewById(R.id.t1);
        editText2 = findViewById(R.id.t2);
        editText3 = findViewById(R.id.t3);
        assert h1 != null;
        if (!h1.isEmpty()) {
            editText.setText(h1);
        }
        assert h2 != null;
        if (!h2.isEmpty()) {
            editText2.setText(h2);
        }
        assert h3 != null;
        if (!h3.isEmpty()) {
            editText3.setText(h3);
        }
    }

    public void schedule(View view) {
        if(!editText.getText().toString().replaceAll("@","").isEmpty()) {
            th=editText.getText().toString();
            SharedPreferences sharedPreferences = getSharedPreferences("checkboxes", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("s1", "@" + editText);
            editor.apply();
            new tweet().execute(th);
            DialogFragment timefragment = new Timepickerframent();
            timefragment.show(getSupportFragmentManager(), "time picker");
        }
        else {
            editText.setError(getString(R.string.handlefirst));
        }
    }

    public void schedule2(View view) {
        if(!editText2.getText().toString().replaceAll("@","").isEmpty()) {
            th=editText2.getText().toString();
            SharedPreferences sharedPreferences = getSharedPreferences("checkboxes", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("s2", "@" + editText2);
            editor.apply();
            new tweet().execute(th);
            DialogFragment timefragment = new Timepickerframent();
            timefragment.show(getSupportFragmentManager(), "time picker");
        }
        else {
            editText2.setError(getString(R.string.handlefirst));
        }
    }

    public void schedule3(View view) {
        if(!editText3.getText().toString().replaceAll("@","").isEmpty()) {
            th=editText3.getText().toString();
            SharedPreferences sharedPreferences = getSharedPreferences("checkboxes", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("s3", "@" + editText3);
            editor.apply();
            new tweet().execute(th);
            DialogFragment timefragment = new Timepickerframent();
            timefragment.show(getSupportFragmentManager(), "time picker");
        }
        else {
            editText3.setError(getString(R.string.handlefirst));
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        startTime = Calendar.getInstance();
        st=Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND, 0);
        long alarmStartTime = startTime.getTimeInMillis();
        Intent intent = new Intent(wrapperscheduler.this,AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(wrapperscheduler.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,alarmStartTime,AlarmManager.INTERVAL_DAY,alarmIntent);
        Toast.makeText(this, R.string.schedulesuccess, Toast.LENGTH_SHORT).show();
    }
}