package com.example.justasecondapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;


public class ReceivingFileActivity extends AppCompatActivity {

    private TextView textView1;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_file);

        textView1 = (TextView) findViewById(R.id.startTextFile);
        textView2 = (TextView) findViewById(R.id.dataFromFile);

        textView1.setText("This is second app: " + getApplicationContext().getPackageName() + "\n In ReceivingFileActivity \n Below is the information received.");

        Intent intent = getIntent();
        Uri gottenUri = intent.getData();

        try {
            InputStream inputStream = getContentResolver().openInputStream(gottenUri);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            for (String line = ""; line != null; line = bufferedReader.readLine()) {
                stringBuilder.append(line).append('\n');
            }

            textView2.setText(stringBuilder.toString());

            bufferedReader.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
