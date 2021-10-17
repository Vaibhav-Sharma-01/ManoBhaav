package com.techguys.tester;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import jxl.read.biff.BiffException;
import twitter4j.Paging;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class tweets extends AppCompatActivity {
    /* variable declarations*/
    public Context context = tweets.this;
    LinearLayout ll;
    private StringBuilder contents;
    ProgressDialog mProgressBar;
    static StringBuilder tweetsfeedbuilder2;
    static String text;
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    /*ui*/
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        setContentView(R.layout.activity_tweets);
        ll = findViewById(R.id.ll);
        TextView intro = findViewById(R.id.intro_txt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intro.setText("showing " + MainActivity.th + "'s tweets" + "\n" + getString(R.string.find) + "\n" + getString(R.string.phone));
        mProgressBar = new ProgressDialog(this);
        startLoadData();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 12) {
                startActivity(new Intent(getApplicationContext(), sentimentanalyser.class));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent in = new Intent(tweets.this, Settings.class);
                startActivity(in);
                return true;
            case R.id.help:
                return true;
            case R.id.logout:
                SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                String check = sharedPreferences.getString("remember", "");
                if (check.equals("true")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                }
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(tweets.this, login.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*progress bar*/
    public void startLoadData() {

        mProgressBar.setCancelable(false);
        mProgressBar.setMessage(getString(R.string.fetching));
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.show();
        new GetTweets().execute(MainActivity.th);
    }

    /* Async task to save the tweets in the excel file in different sheets */
    @SuppressLint("StaticFieldLeak")
    private class GetTweets extends AsyncTask<String, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @SuppressLint("SetTextI18n")
        @Override
        protected String doInBackground(String... twitterURL) {
            StringBuilder tweetFeedBuilder = new StringBuilder();
            for (String searchtearm : twitterURL) {
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true).setOAuthConsumerKey("XOIA1vCGSYZXrUI0YSjCtrsNu")
                        .setOAuthConsumerSecret("O30MJ4rJFoKrjnxLgzuR4uSS65lEii8Dqw3SIVxX7k07m92pGf")
                        .setOAuthAccessToken("1269516346418696193-P4IpgTBeQCvKO4Gu7a5ufpP2HavAox")
                        .setOAuthAccessTokenSecret("QPTEfBEn25Hkq360uJQDFRjH3pSCDeZGfGUoAIsseqLxA");
                TwitterFactory tf = new TwitterFactory(cb.build());
                twitter4j.Twitter twitter = tf.getInstance();
                List<twitter4j.Status> statuses;
                if (Environment.getExternalStorageState() != null) {
                    Paging page;
                    if (MainActivity.max >= 0) {
                        if (MainActivity.max > 0) {
                            page = new Paging(1, MainActivity.max);
                        }//page number, number per page
                        else {
                            page = new Paging(1, 1);
                        }
                        User users = null;
                        try {
                            users = twitter.showUser(searchtearm);
                        } catch (TwitterException e) {
                            e.printStackTrace();
                        }
                        try {
                            assert users != null;
                            statuses = twitter.getUserTimeline(searchtearm, page);
                            String Fnamexls = MainActivity.th + ".xls";
                            File sdCard = Environment.getExternalStorageDirectory();
                            File directory = new File(sdCard.getAbsolutePath() + "/tweetsreader");
                            directory.mkdirs();
                            File file = new File(directory, Fnamexls);
                            if (!file.exists()) {
                                int i = 1;
                                Workbook wb = new HSSFWorkbook();
                                //Now we are creating sheet
                                Sheet sheet = null;
                                sheet = wb.createSheet(String.valueOf(wb.getNumberOfSheets() + 1));
                                //Now column and row
                                Row header = sheet.createRow(0);
                                header.createCell(0).setCellValue("S.NO.");
                                header.createCell(1).setCellValue("Created At");
                                header.createCell(2).setCellValue("text/message");
                                header.createCell(3).setCellValue("Sentiment");
                                header.createCell(4).setCellValue("Score");
                                for (twitter4j.Status status : statuses) {
                                    text = status.getText().trim()
                                            .replaceAll("http.*?[\\S]+", "")
                                            .replaceAll("@[\\S]+", "")
                                            .replaceAll("#", "")
                                            .replaceAll("[\\s]+", " ");
                                    Row dataRow = sheet.createRow(i);
                                    dataRow.createCell(0).setCellValue(i);
                                    dataRow.createCell(1).setCellValue(String.valueOf(status.getCreatedAt()));
                                    dataRow.createCell(2).setCellValue(text);
                                    dataRow.createCell(3).setCellValue("");
                                    dataRow.createCell(4).setCellValue("");
                                    i++;
                                }
                                sheet.setColumnWidth(0, (10 * 200));
                                sheet.setColumnWidth(1, (10 * 900));
                                sheet.setColumnWidth(2, (10 * 5500));
                                FileOutputStream outputStream = null;
                                try {
                                    outputStream = new FileOutputStream(file);
                                    wb.write(outputStream);
                                    wb.close();
                                    Toast.makeText(getApplicationContext(), R.string.tweetsreader, Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "something went wrong in saving the file check if you have granated the save permission", Toast.LENGTH_LONG).show();
                                    try {
                                        outputStream.close();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } else {
                                int i = 1;
                                Workbook wb = WorkbookFactory.create(file);
                                //Now we are creating sheet
                                Sheet sheet = null;
                                sheet = wb.createSheet(String.valueOf(wb.getNumberOfSheets() + 1));
                                //Now column and row
                                Row header = sheet.createRow(0);
                                header.createCell(0).setCellValue("S.NO.");
                                header.createCell(1).setCellValue("Created At");
                                header.createCell(2).setCellValue("text/message");
                                header.createCell(3).setCellValue("Sentiment");
                                header.createCell(4).setCellValue("Score");
                                for (twitter4j.Status status : statuses) {
                                    text = status.getText().trim()
                                            .replaceAll("http.*?[\\S]+", "")
                                            .replaceAll("@[\\S]+", "")
                                            .replaceAll("#", "")
                                            .replaceAll("[\\s]+", " ");
                                    Row dataRow = sheet.createRow(i);
                                    dataRow.createCell(0).setCellValue(i);
                                    dataRow.createCell(1).setCellValue(String.valueOf(status.getCreatedAt()));
                                    dataRow.createCell(2).setCellValue(text);
                                    dataRow.createCell(3).setCellValue("");
                                    dataRow.createCell(4).setCellValue("");
                                    i++;
                                }
                                sheet.setColumnWidth(0, (10 * 200));
                                sheet.setColumnWidth(1, (10 * 900));
                                sheet.setColumnWidth(2, (10 * 5500));
                                FileOutputStream outputStream = null;
                                try {
                                    outputStream = new FileOutputStream(file);
                                    wb.write(outputStream);
                                    wb.close();
                                    Toast.makeText(getApplicationContext(), R.string.tweetsreader, Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "something went wrong in saving the file check if you have granated the save permission", Toast.LENGTH_LONG).show();
                                    try {
                                        outputStream.close();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (Environment.getExternalStorageState() == null) {
                    Paging page;
                    if (MainActivity.max >= 0) {
                        if (MainActivity.max > 0) {
                            page = new Paging(1, MainActivity.max);
                        }//page number, number per page
                        else {
                            page = new Paging(1, 1);
                        }
                        User users = null;
                        try {
                            users = twitter.showUser(searchtearm);
                        } catch (TwitterException e) {
                            e.printStackTrace();
                        }
                        try {
                            assert users != null;
                            statuses = twitter.getUserTimeline(searchtearm, page);
                            String Fnamexls = MainActivity.th + ".xls";
                            File sdCard = Environment.getDataDirectory();
                            File directory = new File(sdCard.getAbsolutePath() + "/tweetsreader");
                            directory.mkdirs();
                            File file = new File(directory, Fnamexls);
                            if (!file.exists()) {
                                int i = 1;
                                Workbook wb = new HSSFWorkbook();
                                //Now we are creating sheet
                                Sheet sheet = null;
                                sheet = wb.createSheet(String.valueOf(wb.getNumberOfSheets() + 1));
                                //Now column and row
                                Row header = sheet.createRow(0);
                                header.createCell(0).setCellValue("S.NO.");
                                header.createCell(1).setCellValue("Created At");
                                header.createCell(2).setCellValue("text/message");
                                header.createCell(3).setCellValue("Sentiment");
                                header.createCell(4).setCellValue("Score");
                                for (twitter4j.Status status : statuses) {
                                    text = status.getText().trim()
                                            .replaceAll("http.*?[\\S]+", "")
                                            .replaceAll("@[\\S]+", "")
                                            .replaceAll("#", "")
                                            .replaceAll("[\\s]+", " ");
                                    Row dataRow = sheet.createRow(i);
                                    dataRow.createCell(0).setCellValue(i);
                                    dataRow.createCell(1).setCellValue(String.valueOf(status.getCreatedAt()));
                                    dataRow.createCell(2).setCellValue(text);
                                    dataRow.createCell(3).setCellValue("");
                                    dataRow.createCell(4).setCellValue("");
                                    i++;
                                }
                                sheet.setColumnWidth(0, (10 * 200));
                                sheet.setColumnWidth(1, (10 * 900));
                                sheet.setColumnWidth(2, (10 * 5500));
                                FileOutputStream outputStream = null;
                                try {
                                    outputStream = new FileOutputStream(file);
                                    wb.write(outputStream);
                                    wb.close();
                                    Toast.makeText(getApplicationContext(), R.string.tweetsreader, Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "something went wrong in saving the file check if you have granated the save permission", Toast.LENGTH_LONG).show();
                                    try {
                                        outputStream.close();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } else {
                                int i = 1;
                                Workbook wb = WorkbookFactory.create(file);
                                //Now we are creating sheet
                                Sheet sheet = null;
                                sheet = wb.createSheet(String.valueOf(wb.getNumberOfSheets() + 1));
                                //Now column and row
                                Row header = sheet.createRow(0);
                                header.createCell(0).setCellValue("S.NO.");
                                header.createCell(1).setCellValue("Created At");
                                header.createCell(2).setCellValue("text/message");
                                header.createCell(3).setCellValue("Sentiment");
                                header.createCell(4).setCellValue("Score");
                                for (twitter4j.Status status : statuses) {
                                    text = status.getText().trim()
                                            .replaceAll("http.*?[\\S]+", "")
                                            .replaceAll("@[\\S]+", "")
                                            .replaceAll("#", "")
                                            .replaceAll("[\\s]+", " ");
                                    Row dataRow = sheet.createRow(i);
                                    dataRow.createCell(0).setCellValue(i);
                                    dataRow.createCell(1).setCellValue(String.valueOf(status.getCreatedAt()));
                                    dataRow.createCell(2).setCellValue(text);
                                    dataRow.createCell(3).setCellValue("");
                                    dataRow.createCell(4).setCellValue("");
                                    i++;
                                }
                                sheet.setColumnWidth(0, (10 * 200));
                                sheet.setColumnWidth(1, (10 * 900));
                                sheet.setColumnWidth(2, (10 * 5500));
                                FileOutputStream outputStream = null;
                                try {
                                    outputStream = new FileOutputStream(file);
                                    wb.write(outputStream);
                                    wb.close();
                                    Toast.makeText(getApplicationContext(), R.string.tweetsreader, Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "something went wrong in saving the file check if you have granated the save permission", Toast.LENGTH_LONG).show();
                                    try {
                                        outputStream.close();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            tweetFeedBuilder = get_data_from_excelsheet();
            return tweetFeedBuilder.toString();
        }

        protected void onPostExecute(String result) {
            mProgressBar.hide();
            String[] res = result.split("iamthebest");
            for (int i = 0; i < res.length; i++) {
                CardView cardview = new CardView(tweets.this);
                ViewGroup.LayoutParams layoutparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cardview.setLayoutParams(layoutparams);
                cardview.setRadius(95);
                cardview.setPadding(25, 25, 25, 25);
                cardview.setMaxCardElevation(30);
                cardview.setMaxCardElevation(6);
                TextView textview = new TextView(tweets.this);
                textview.setLayoutParams(layoutparams);
                textview.setText(res[i]);
                textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                if (!(new DarkModePrefManager(tweets.this).isNightMode())) {
                    textview.setTextColor(Color.BLACK);
                } else {
                    textview.setTextColor(Color.WHITE);
                }
                textview.setPadding(25, 25, 25, 25);
                textview.setGravity(Gravity.CENTER);
                cardview.addView(textview);
                ll.addView(cardview);
                TextView space = new TextView(tweets.this);
                space.setText("\n\n\n");
                ll.addView(space);
            }
            Button button = new Button(tweets.this);
            button.setBackgroundResource(R.drawable.button_rounded);
            button.setText(R.string.FindSentiment);
            button.setPadding(10, 10, 10, 10);
            button.setWidth(500);
            button.setTextColor(Color.WHITE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), sentimentanalyser.class));
                }
            });
            ll.addView(button);
            TextView space = new TextView(tweets.this);
            space.setText("\n\n");
            ll.addView(space);
        }
    }

    private StringBuilder get_data_from_excelsheet() {
        String Fnamexls = MainActivity.th + ".xls";
        String s;
        StringBuilder contents = new StringBuilder();
        if (Environment.getExternalStorageState() != null) {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() + "/tweetsreader");
            try {
                jxl.Workbook workbook = null;
                try {

                    workbook = jxl.Workbook.getWorkbook(new File(directory, Fnamexls));
                    jxl.Sheet sheet = workbook.getSheet(workbook.getNumberOfSheets() - 1);
                    int row = sheet.getRows();
                    tweetsfeedbuilder2 = new StringBuilder();
                    for (int r = 1; r < row; r++) {
                        for (int c = 0; c < 3; c++) {
                            jxl.Cell cell1 = sheet.getCell(c, r);
                            if (c == 1) {
                                s = cell1.getContents().trim().replaceAll("GMT\\+05:30", "").replaceAll(":", "");
                                String[] t = s.split(" ");
                                contents.append(t[0]).append(" ").append(t[1]).append(" ").append(t[2]).append(" ").append(t[4]).append(t[5]).append("\n");
                            } else {
                                if (!cell1.getContents().isEmpty())
                                    contents.append(cell1.getContents()).append("\n");
                                else
                                    contents.append("----").append("\n");
                            }
                        }
                        contents.append("iamthebest");
                        jxl.Cell cell2 = sheet.getCell(2, r);
                        if (!cell2.getContents().isEmpty())
                            tweetsfeedbuilder2.append(cell2.getContents()).append("\n");
                        else
                            tweetsfeedbuilder2.append("----");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BiffException e) {
                    e.printStackTrace();
                } finally {
                    if (workbook != null) {
                        workbook.close();
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (Environment.getExternalStorageState() == null) {
            File sdCard = Environment.getDataDirectory();
            File directory = new File(sdCard.getAbsolutePath() + "/tweetsreader");
            try {
                jxl.Workbook workbook = null;
                try {

                    workbook = jxl.Workbook.getWorkbook(new File(directory, Fnamexls));
                    jxl.Sheet sheet = workbook.getSheet(workbook.getNumberOfSheets() - 1);
                    int row = sheet.getRows();
                    tweetsfeedbuilder2 = new StringBuilder();
                    for (int r = 1; r < row; r++) {
                        for (int c = 0; c < 3; c++) {
                            jxl.Cell cell1 = sheet.getCell(c, r);
                            if (c == 1) {
                                s = cell1.getContents().trim().replaceAll("GMT\\+05:30", "").replaceAll(":", "");
                                String[] t = s.split(" ");
                                contents.append(t[0]).append(" ").append(t[1]).append(" ").append(t[2]).append(" ").append(t[4]).append(t[5]).append("\n");
                            } else {
                                contents.append(cell1.getContents()).append("\n");
                            }
                        }
                        contents.append("iamthebest");
                        jxl.Cell cell2 = sheet.getCell(2, r);
                        if (!cell2.getContents().isEmpty())
                            tweetsfeedbuilder2.append(cell2.getContents()).append("\n");
                        else
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BiffException e) {
                    e.printStackTrace();
                } finally {
                    if (workbook != null) {
                        workbook.close();
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return contents;
    }
}