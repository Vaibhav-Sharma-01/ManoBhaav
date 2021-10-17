package com.techguys.tester;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import twitter4j.Paging;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
/* Async task to save the tweets in the excel file in different sheets */
@SuppressLint("StaticFieldLeak")
public class tweet extends AsyncTask<String, Void, String> {

        String text;
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @SuppressLint("SetTextI18n")
        @Override
        protected String doInBackground(String... twitterURL) {
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
                            page = new Paging(1, 1);
                        User users = null;
                        try {
                            users = twitter.showUser(searchtearm);
                        } catch (TwitterException e) {
                            e.printStackTrace();
                        }
                        try {
                            assert users != null;
                            statuses = twitter.getUserTimeline(searchtearm, page);
                            String Fnamexls = "schedule" + wrapperscheduler.th + ".xls";
                            File sdCard = Environment.getExternalStorageDirectory();
                            File directory = new File(sdCard.getAbsolutePath() + "/Android/data/com.techguys.ManoBhaavApp");
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
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    try {
                                        outputStream.close();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }else{
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
                                } catch (IOException e) {
                                    e.printStackTrace();
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
                } else if (Environment.getExternalStorageState() == null) {
                    Paging page;
                            page = new Paging(1, 1);
                        User users = null;
                        try {
                            users = twitter.showUser(searchtearm);
                        } catch (TwitterException e) {
                            e.printStackTrace();
                        }
                        try {
                            assert users != null;
                            statuses = twitter.getUserTimeline(searchtearm, page);
                            String Fnamexls = "schedule" + wrapperscheduler.th + ".xls";
                            File sdCard = Environment.getDataDirectory();
                            File directory = new File(sdCard.getAbsolutePath() + "/Android/data/com.techguys.ManoBhaavApp");
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
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    try {
                                        outputStream.close();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }else{
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
                                } catch (IOException e) {
                                    e.printStackTrace();
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
            return "done";
        }
}