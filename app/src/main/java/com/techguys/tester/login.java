package com.techguys.tester;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class login extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private int STORAGE_PERMISSION_CODE=1;
    public Context context=login.this;
    Settings settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        SharedPreferences sharedPreferences=getSharedPreferences("checkbox",MODE_PRIVATE);
        String check=sharedPreferences.getString("remember","");
        if(check.equals("true")){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        loadlocale();

        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        setContentView(R.layout.activity_login);
        EditText emailId=findViewById(R.id.emaili);
        TextView fp=findViewById(R.id.fp);
        EditText password=findViewById(R.id.password);
        CheckBox rem=findViewById(R.id.remember);
        Button btnsignin=findViewById(R.id.signin);
        rem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    SharedPreferences sharedPreferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("remember","true");
                    editor.apply();
                }else if(!compoundButton.isChecked()){
                    SharedPreferences sharedPreferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("remember","false");
                    editor.apply();
                }
            }
        });
        if(ContextCompat.checkSelfPermission(login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            firebaseAuth = FirebaseAuth.getInstance();
            btnsignin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailId.getText().toString().trim();
                    String pwd = password.getText().toString().trim();
                    if (email.isEmpty()) {
                        emailId.setError("Please Enter email id");
                        emailId.requestFocus();
                    } else if (pwd.isEmpty()) {
                        password.setError("please enter password");
                        password.requestFocus();
                    }

                    firebaseAuth.signInWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    } else {
                                        Toast.makeText(login.this, "incorrect email or password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
            fp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText resetmail=new EditText(view.getContext());
                    final AlertDialog.Builder prd=new AlertDialog.Builder(view.getContext());
                    prd.setTitle(R.string.resetpassword);
                    prd.setMessage(R.string.forgottext);
                    prd.setView(resetmail);

                    prd.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String mail=resetmail.getText().toString();
                            firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(login.this, "check the mail to reset password", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(login.this, "Whoops...Unale to send the link"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    prd.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            /*nothing*/
                        }
                    });
                    prd.show();
                }
            });
        }
        else{
            requestStoragePermission();
        }
    }
public void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(login.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(login.this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to save the file in your device")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(login.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE );
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }
        else{
            ActivityCompat.requestPermissions(login.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE );
        }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==STORAGE_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissioin Granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sign_up_now(View view) {
        startActivity(new Intent(getApplicationContext(),sign_up.class));
    }
    private String getEmoji(int uni){
        return new String(Character.toChars(uni));//U+1F1EE//U+1F1F3
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