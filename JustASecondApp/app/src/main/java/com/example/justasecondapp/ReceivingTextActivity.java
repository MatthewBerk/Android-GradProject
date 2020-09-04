package com.example.justasecondapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class ReceivingTextActivity extends AppCompatActivity {

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_text);

        textView1 = (TextView) findViewById(R.id.startText);
        textView2 = (TextView) findViewById(R.id.dataGot1);
        textView3 = (TextView) findViewById(R.id.dataGot2);

        textView1.setText("This is second app: " + getApplicationContext().getPackageName() +"\n In ReceivingTextActivity \n Below is the information received.");

        Intent intent = getIntent();

        String value1 = intent.getStringExtra("Data1");
        String value2 = intent.getStringExtra("Data2");

        if(value1 != null){
            textView2.setText("Intent Data1: " + value1);
        }else{
            textView2.setText("Intent Data1: [Empty]");
        }

        if(value2 != null){
            textView3.setText("Intent Data2: " + value2);
        }else{
            textView3.setText("Intent Data2: [Empty]");
        }
    }
}
