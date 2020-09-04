package com.example.justasecondapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class ReceivingBroadcast extends BroadcastReceiver {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");

    @Override
    public void onReceive(Context context, Intent intent) {

        //Need to write results to a file so can check it since an app sending a broadcast to this apps broadcast receiver would not change view,
        // so either we have debug mode here or write values to a file so can check on them later!
        File aFile = new File(context.getFilesDir(), "AbroadcastTest");
        try {
            aFile.createNewFile();

            PrintWriter printWriter = new PrintWriter(new FileWriter(aFile, true));
            printWriter.println(simpleDateFormat.format(new Date()));

            String resultData = getResultData();
            String data1 = intent.getStringExtra("Data1");
            String data2 = intent.getStringExtra("Data2");

            if(resultData != null){
                printWriter.println("ResultData:  " + resultData);
            }else{
                printWriter.println("ResultData:  [none]");
            }
            if(data1 != null){
                printWriter.println("Intent data1: " + data1);
            }else{
                printWriter.println("Intent data1: [none]");
            }
            if(data1 != null){
                printWriter.println("Intent data2: " + data2);
            }else{
                printWriter.println("Intent data2: [none]");
            }

            printWriter.println();
            printWriter.println();
            printWriter.flush();
            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
