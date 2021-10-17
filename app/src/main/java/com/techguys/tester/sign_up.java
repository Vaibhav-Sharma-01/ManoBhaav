package com.techguys.tester;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sign_up extends AppCompatActivity {
    public Context context=sign_up.this;
    String gender="";
    String fname,uname,email,pwd,cpwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        EditText Fullname=findViewById(R.id.fullname);
        EditText Username=findViewById(R.id.username);
        EditText emailId=findViewById(R.id.email);
        EditText password=findViewById(R.id.pass);
        EditText confirmpassword=findViewById(R.id.npass);
        Button Signup=findViewById(R.id.signup);
        TextView tvSignIn=findViewById(R.id.login);
        RadioButton gmale=findViewById(R.id.male);
        RadioButton gfemale=findViewById(R.id.female);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("user");
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname=Fullname.getText().toString();
                uname=Username.getText().toString();
                email=emailId.getText().toString();
                pwd=password.getText().toString();
                cpwd=confirmpassword.getText().toString();
                if(gmale.isChecked()){
                    gender="male";
                }
                if(gfemale.isChecked()){
                    gender="female";
                }

                if(email.isEmpty()){
                    emailId.setError(getString(R.string.PleaseEnteremailid));
                    emailId.requestFocus();
                }
                if(pwd.isEmpty()){
                    password.setError(getString(R.string.PleaseEnterpassword));
                    password.requestFocus();
                }
                if(cpwd.isEmpty()){
                    confirmpassword.setError(getString(R.string.Pleaseconfirmyourpassword));
                    confirmpassword.requestFocus();
                }
                if(fname.isEmpty()){
                    Fullname.setError(getString(R.string.PleaseEnteryourfullname));
                    Fullname.requestFocus();
                }
                if(uname.isEmpty()){
                    Username.setError(getString(R.string.PleaseEnterusername));
                    Username.requestFocus();
                }
                if(!(pwd.equals(cpwd))){
                    confirmpassword.setError(getString(R.string.confirmpassworddidntmatch));
                    confirmpassword.requestFocus();
                }
                firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(sign_up.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    user information=new user(
                                            fname,uname,email,gender
                                    );
                                    FirebaseDatabase.getInstance().getReference("user")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(sign_up.this, R.string.RegistrationSuccessfull,Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), com.techguys.tester.login.class));
                                        }
                                    });

                                } else {
                                    Toast.makeText(sign_up.this, R.string.Registrationunsuccessfull,Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
        });
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), com.techguys.tester.login.class));
            }
        });
    }
}
