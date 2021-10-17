package com.techguys.tester;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class Settings extends AppCompatActivity {
    TextView username;
    private Switch darkModeSwitch;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    String PROFILE_IMAGE_URL=null;
    int TAKE_IMAGE_CODE=10001;
    ImageView profileimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        loadlocale();
        setContentView(R.layout.settings_activity);
        profileimage=findViewById(R.id.profileCircleImageView);
        username=findViewById(R.id.utv);
        auth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("user").child(auth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name=snapshot.child("Username").getValue().toString();
                username.setText(name);
                if(auth.getCurrentUser().getPhotoUrl()!=null){
                    Glide.with(Settings.this)
                            .load(auth.getCurrentUser().getPhotoUrl())
                            .into(profileimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //function for enabling dark mode
        setDarkModeSwitch();
    }
    private void setDarkModeSwitch(){
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        darkModeSwitch.setChecked(new DarkModePrefManager(this).isNightMode());
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DarkModePrefManager darkModePrefManager = new DarkModePrefManager(Settings.this);
                darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
            }
        });
    }
    public void logout(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String check = sharedPreferences.getString("remember", "");
        assert check != null;
        if (check.equals("true")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("remember", "false");
            editor.apply();
        }
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(Settings.this, login.class);
        startActivity(i);
    }

    public void handleimageclick(View view) {

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TAKE_IMAGE_CODE){
            switch (resultCode){
                case RESULT_OK:
                    Bitmap bitmap= (Bitmap) data.getExtras().get("data");
                    profileimage.setImageBitmap(bitmap);
                    handleupload(bitmap);
            }
        }
    }
    private void handleupload(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        StorageReference reference= FirebaseStorage.getInstance().getReference()
                .child("profileimage")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpeg");
        reference.putBytes(byteArrayOutputStream.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         getDownloadurl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Settings.this, "Oops something went wrong...", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getDownloadurl(StorageReference reference){
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        setuserprofileurl(uri);
                    }
                });
    }
    private void setuserprofileurl(Uri uri){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request=new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Settings.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Settings.this, "Profile image failed", Toast.LENGTH_SHORT).show();     
                    }
                });
    }

    public void language(View view) {

        showchangelanguage();

    }

    private void showchangelanguage() {

        final String [] lang={"English","हिंदी"};
        AlertDialog.Builder builder=new AlertDialog.Builder(Settings.this);
        builder.setTitle("Choose Language....");
        builder.setSingleChoiceItems(lang, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    setLocale("en");
                }
                else if(i==1){
                    setLocale("hi");
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
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

    public void notification(View view) {
    }

    public void editprofile(View view) {

    }
}