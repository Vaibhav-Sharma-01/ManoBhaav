package com.techguys.tester;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import io.kommunicate.KmConversationBuilder;
import io.kommunicate.Kommunicate;
import io.kommunicate.callbacks.KmCallback;
import io.kommunicate.users.KMUser;

public class sentimentanalyser extends AppCompatActivity {
    public Context context=sentimentanalyser.this;
    ProgressDialog mProgressBar;
    private TableLayout tl;
    StringBuilder collection;
    private ActionBarDrawerToggle t;
    static int positive, negative, neutral;
    Properties pipelineProps, tokenizerProps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        setContentView(R.layout.activity_sentimentanalyser);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Kommunicate.init(sentimentanalyser.this, "1148703756691a30a027f34a905a93c74");
        KMUser user=new KMUser();
        user.setUserId(MainActivity.email);
        user.setDisplayName(MainActivity.name);
        mProgressBar = new ProgressDialog(this);
        tl = findViewById(R.id.mylayout);
        TableRow tr = new TableRow(sentimentanalyser.this);
        TextView tv = new TextView(sentimentanalyser.this);
        tv.setWidth(480);
        tv.setText("Tweets/Message");
        tv.setTypeface(Typeface.create("cursive", Typeface.NORMAL));
        TextView tv1 = new TextView(sentimentanalyser.this);
        tv1.setText("Sentiments");
        tv1.setWidth(120);
        tv1.setTypeface(Typeface.create("cursive", Typeface.NORMAL));
        TextView tv2 = new TextView(sentimentanalyser.this);
        tv2.setWidth(120);
        tv2.setText("Score");
        tv2.setTypeface(Typeface.create("cursive", Typeface.NORMAL));
        tr.addView(tv);
        tr.addView(tv1);
        tr.addView(tv2);
        tl.addView(tr);
        startLoadData();
        DrawerLayout dl = (DrawerLayout) findViewById(R.id.activity_sentimentanalyser);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        t.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView nv = (NavigationView) findViewById(R.id.nv);
        View view=nv.inflateHeaderView(R.layout.nav_header);
        ImageView imageView=view.findViewById(R.id.propic);
        if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null){
            Glide.with(sentimentanalyser.this)
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .placeholder(R.drawable.ic_android_black_24dp)
                    .into(imageView);
        }
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.report1) {
                    Intent i = new Intent(sentimentanalyser.this, Report1.class);
                    startActivity(i);
                } else if (id == R.id.scheduletweets) {
                    Intent i = new Intent(sentimentanalyser.this, wrapperscheduler.class);
                    startActivity(i);
                } else if (id == R.id.settings) {
                    Intent i = new Intent(sentimentanalyser.this, Settings.class);
                    startActivity(i);
                } else if (id == R.id.help) {
                    new KmConversationBuilder(sentimentanalyser.this)
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
                    if (check.equals("true")) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("remember", "false");
                        editor.apply();
                    }
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(sentimentanalyser.this, login.class);
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

    public void startLoadData() {

        mProgressBar.setCancelable(false);
        mProgressBar.setMessage(getString(R.string.Analysing));
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.show();
        new getsentiments().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class getsentiments extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            String output, out = null;
            collection = new StringBuilder();
            String[] lines = tweets.tweetsfeedbuilder2.toString().split("\\r?\\n");
            for (String line : lines) {
                pipelineProps = new Properties();
                tokenizerProps = new Properties();
                pipelineProps.setProperty("annotators", "parse, sentiment");
                pipelineProps.setProperty("parse.binaryTrees", "true");
                pipelineProps.setProperty("enforceRequirements", "false");
                tokenizerProps.setProperty("annotators", "tokenize ssplit");
                StanfordCoreNLP tokenizer = new StanfordCoreNLP(tokenizerProps);
                StanfordCoreNLP pipeline = new StanfordCoreNLP(pipelineProps);
                Annotation annotation = tokenizer.process(line);
                pipeline.annotate(annotation);
                for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                    output = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
                    out = line + "iamthebestengg" + output + "\n";
                }
                collection.append(out);
            }
            return collection.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            mProgressBar.hide();
            String[] line = s.split("\\r?\\n");
            for (String value : line) {
                TableRow tr = new TableRow(getApplicationContext());
                TextView textView = new TextView(getApplicationContext());
                TextView textView1 = new TextView(getApplicationContext());
                TextView textView2 = new TextView(getApplicationContext());
                String[] realline = value.split("iamthebestengg");
                textView.setWidth(480);
                textView.setPadding(5, 5, 5, 5);
                textView.setText(realline[0]);
                textView.setTypeface(Typeface.create("cursive", Typeface.NORMAL));
                textView1.setText(realline[1]);
                textView.setTextSize(28);
                textView1.setTextSize(20);
                textView2.setTextSize(25);
                textView2.setPadding(10, 5, 5, 5);
                textView1.setWidth(160);
                textView1.setTypeface(Typeface.create("cursive", Typeface.NORMAL));
                textView2.setWidth(80);
                if (textView1.getText().toString().equals("Positive")) {
                    positive++;
                    textView1.append(" " + getEmoji(0x1F600));
                    textView2.setText("1");
                    tr.setBackgroundColor(Color.GREEN);
                } else if (textView1.getText().toString().equals("Negative")) {
                    negative++;
                    textView2.setText("-1");
                    textView1.append(" " + getEmoji(0x1F621));
                    tr.setBackgroundColor(Color.RED);
                } else if (textView1.getText().toString().equals("Neutral")) {
                    neutral++;
                    textView2.setText("0");
                    textView1.append(" " + getEmoji(0x1F610));
                    tr.setBackgroundColor(Color.LTGRAY);
                } else if (textView1.getText().toString().equalsIgnoreCase("VERY NEGATIVE")) {
                    negative = negative + 2;
                    textView2.setText("-2");
                    textView1.append(" " + getEmoji(0x1F620));
                    tr.setBackgroundColor(Color.rgb(77, 32, 32));
                } else if (textView1.getText().toString().equalsIgnoreCase("VERY POSITIVE")) {
                    positive = positive + 2;
                    textView2.setText("2");
                    textView1.append(" " + getEmoji(0x1F606));
                    tr.setBackgroundColor(Color.rgb(8, 255, 102));
                }
                tr.addView(textView);
                tr.addView(textView1);
                tr.addView(textView2);
                tl.addView(tr);
            }
            try {
                update_excel();
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private String getEmoji(int uni) {
        return new String(Character.toChars(uni));
    }

    private void update_excel() throws IOException, InvalidFormatException {
        String Fnamexls = MainActivity.th + ".xls";
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/tweetsreader");
        File file = new File(directory, Fnamexls);
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);
        String[] line = collection.toString().split("\\r?\\n");
        for (int i = 0; i < line.length; i++) {
            String[] realline = line[i].split("iamthebestengg");
            sheet.getRow(i + 1).getCell(3).setCellValue(realline[1]);
            if (realline[1].equalsIgnoreCase("positive")) {
                sheet.getRow(i + 1).getCell(4).setCellValue("1");
            } else if (realline[1].equalsIgnoreCase("negative")) {
                sheet.getRow(i + 1).getCell(4).setCellValue("-1");
            } else if (realline[1].equalsIgnoreCase("very positive")) {
                sheet.getRow(i + 1).getCell(4).setCellValue("2");
            } else if (realline[1].equalsIgnoreCase("very negative")) {
                sheet.getRow(i + 1).getCell(4).setCellValue("-2");
            } else {
                sheet.getRow(i + 1).getCell(4).setCellValue("0");
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        workbook.close();
        fileOutputStream.close();
    }
}