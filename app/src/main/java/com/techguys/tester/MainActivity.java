package com.techguys.tester;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import io.kommunicate.KmConversationBuilder;
import io.kommunicate.Kommunicate;
import io.kommunicate.callbacks.KmCallback;
import io.kommunicate.users.KMUser;

public class MainActivity extends AppCompatActivity {
    static String th;
    TextView textView;
    public Context context1=MainActivity.this;
    AutoCompleteTextView handle1, handle2, handle3;
    private ActionBarDrawerToggle t;
    static int min, max = 100;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    ImageView profileimage;
    static String name,email;
    private ArrayList<String> text = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        loadlocale();
        String[] text1 = {"@BarackObama", "@justinbieber", "@katyperry", "@rihanna", "@taylorswift13", "@Cristiano", "@ladygaga", "@TheEllenShow", "@YouTube", "@realDonaldTrump"
                , "@michelleobama", "@speakerpelosi", "@narendramodi", "@iamsrk", "@priyankachopra", "@aliaa08", "@deepikapadukone", "@aliaa08"};
        String quotes[] = {getString(R.string.quote1),
                getString(R.string.quote2),
                getString(R.string.quote3),
                getString(R.string.quote4)};
        Kommunicate.init(MainActivity.this, "1148703756691a30a027f34a905a93c74");
        KMUser user=new KMUser();
        text.addAll(Arrays.asList(text1));
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        setContentView(R.layout.activity_main);
        profileimage=findViewById(R.id.propic);
        textView=findViewById(R.id.textView4);
        auth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("user").child(auth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name=snapshot.child("Username").getValue().toString();
                email=snapshot.child("Email").getValue().toString();

                user.setUserId(email);
                user.setDisplayName(name);
                textView.setText(getString(R.string.hello)+ name +getString(R.string.welcome));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        RangeSeekBar rangeSeekBar = findViewById(R.id.rangeseekbar);
        SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String h1 = sharedPreferences.getString("handle1", "");
        String h2 = sharedPreferences.getString("handle2", "");
        String h3 = sharedPreferences.getString("handle3", "");
        handle1 = findViewById(R.id.tweeterhandle1);
        handle2 = findViewById(R.id.tweeterhandle2);
        handle3 = findViewById(R.id.tweeterhandle3);
        assert h1 != null;
        if (!h1.isEmpty()) {
            handle1.setText(h1);
        }
        assert h2 != null;
        if (!h2.isEmpty()) {
            handle2.setText(h2);
        }
        assert h3 != null;
        if (!h3.isEmpty()) {
            handle3.setText(h3);
        }
        rangeSeekBar.setSelectedMaxValue(100);
        rangeSeekBar.setSelectedMinValue(0);
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {
                Number max_value = bar.getSelectedMaxValue();
                Number min_value = bar.getSelectedMinValue();
                min = (int) min_value;
                max = (int) max_value;
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, text);
        handle1.setAdapter(adapter);
        handle2.setAdapter(adapter);
        handle3.setAdapter(adapter);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        TextView tv = findViewById(R.id.textView4);
        LinearLayout ll = findViewById(R.id.llayout);
        /*DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("user");
        databaseReference.child(mAuth.getCurrentUser().getDisplayName());*/
        Random r = new Random();
        TextView tv1 = new TextView(this);
        ScrollView sv = new ScrollView(this);
        int randomNumber = r.nextInt(quotes.length);
        tv1.setPadding(5, 5, 5, 5);
        tv1.setWidth(540);
        tv1.setTypeface(Typeface.create("cursive", Typeface.NORMAL));
        tv1.setTextSize(32);
        tv1.setText(quotes[randomNumber]);
        sv.addView(tv1);
        sv.setScrollBarSize(0);
        ll.addView(sv);
        DrawerLayout dl = findViewById(R.id.main);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        t.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(t);
        t.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        NavigationView nv = findViewById(R.id.nv);
        View view=nv.inflateHeaderView(R.layout.nav_header);
        ImageView imageView=view.findViewById(R.id.propic);
        if(auth.getCurrentUser().getPhotoUrl()!=null){
            Glide.with(MainActivity.this)
                    .load(auth.getCurrentUser().getPhotoUrl())
                    .placeholder(R.drawable.ic_android_black_24dp)
                    .into(imageView);
        }
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.report1) {
                    Intent i = new Intent(MainActivity.this, Report1.class);
                    Toast.makeText(MainActivity.this, R.string.Goback, Toast.LENGTH_SHORT).show();
                } else if (id == R.id.scheduletweets) {
                    Intent i = new Intent(MainActivity.this, wrapperscheduler.class);
                    startActivity(i);
                }else if (id == R.id.settings) {
                    Intent i = new Intent(MainActivity.this, Settings.class);
                    startActivity(i);
                } else if (id == R.id.help) {
                    new KmConversationBuilder(MainActivity.this)
                            .setAppId("1148703756691a30a027f34a905a93c74")
                            .setKmUser(user)
                            .setSingleConversation(false)
                            .launchConversation(new KmCallback() {
                                @Override
                                public void onSuccess(Object message) {
                                    Log.d("Conversation", "Success : " + message);
                                }

                                @Override
                                public void onFailure(Object error) {
                                    Log.d("Conversation", "Failure : " + error);
                                }
                            });
                } else if (id == R.id.logout) {
                    SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    String check = sharedPreferences.getString("remember", "");
                    assert check != null;
                    if (check.equals("true")) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("remember", "false");
                        editor.apply();
                    }
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(MainActivity.this, login.class);
                    startActivity(i);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (t.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    public void search_now(View view) {
        th = handle1.getText().toString().trim().replaceAll("@", "");
        text.add(handle1.getText().toString());
        if (th.isEmpty()) {
            handle1.setError(getString(R.string.handlefirst));
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("handle1", "@" + th);
            editor.apply();
            startActivity(new Intent(getApplicationContext(), tweets.class));
        }
    }

    public void search_now2(View view) {
        th = handle2.getText().toString().trim().replaceAll("@", "");
        text.add(handle2.getText().toString());
        if (th.isEmpty()) {
            handle2.setError(getString(R.string.handlefirst));
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("handle2", "@" + th);
            editor.apply();
            startActivity(new Intent(getApplicationContext(), tweets.class));
        }
    }

    public void search_now3(View view) {
        th = handle3.getText().toString().trim().replaceAll("@", "");
        text.add(handle3.getText().toString());
        if (th.isEmpty()) {
            handle3.setError(getString(R.string.handlefirst));
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("handle3", "@" + th);
            editor.apply();
            startActivity(new Intent(getApplicationContext(), tweets.class));
        }
    }
    private void setLocale(String lang) {

        Locale locale=new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("settings",MODE_PRIVATE).edit();
        editor.putString("My lang",lang);
        editor.apply();
    }

    public void loadlocale(){
        SharedPreferences preferences=getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language=preferences.getString("My lang","");
        setLocale(language);
    }
}
