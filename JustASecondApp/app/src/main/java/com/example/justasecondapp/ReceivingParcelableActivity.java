package com.example.justasecondapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.example.aspectideaone.ExampleParcelable;
import androidx.appcompat.app.AppCompatActivity;

public class ReceivingParcelableActivity extends AppCompatActivity {

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_parcelable);

        textView1 = (TextView) findViewById(R.id.startTextParcel);
        textView2 = (TextView) findViewById(R.id.parcelValue1);
        textView3 = (TextView) findViewById(R.id.parcelValue2);
        textView4 = (TextView) findViewById(R.id.parcelValue3);
        textView5 = (TextView) findViewById(R.id.parcelValue4);


        textView1.setText("This is second app: " + getApplicationContext().getPackageName() + "\n In ReceivingParcelableActivity \n Below is the information received.");

        Intent intent = getIntent();

        ExampleParcelable exampleParcelable = (ExampleParcelable)intent.getParcelableExtra("ParcelValue");


        if(exampleParcelable.forBytes != null){//In case security project prevented data from being sent.
            textView2.setText("Parcel string value that was stored as a byte array: " + new String(exampleParcelable.forBytes)); //would be the static value one.
        }else{
            textView2.setText("Parcel string value that was stored as a byte array: [none]");
        }

        if(exampleParcelable.someValue1 != null){
            textView3.setText("Parcel first string value: " + exampleParcelable.someValue1);
        }else{
            textView3.setText("Parcel first string value: [none]");
        }

        textView4.setText("Parcel int value: " + exampleParcelable.someValue2);

        if(exampleParcelable.forString != null){
            textView5.setText("Parcel second string value: " + exampleParcelable.forString);//Would be the static value one.
        }else{
            textView5.setText("Parcel second string value: [none]");
        }


    }

}
