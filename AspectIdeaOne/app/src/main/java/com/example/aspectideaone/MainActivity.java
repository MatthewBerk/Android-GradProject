package com.example.aspectideaone;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.matthew.gradProject.*;

import java.io.*;


public class MainActivity extends AppCompatActivity {

    private TextView textView1;
    private Button aButton;
    private Button aButton2;
    private Button aButton3;
    private Button aButton4;

    private static LogSensitiveDataMatchOccurrences logSensitiveDataMatchOccurrences;//Should only need one to exist for entire project.
    //Since it will log all sensitive data leaks that occur in any class in this module or other modules (excluding dynamically loaded)

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /*
        InputStream is = this.getResources().openRawResource(R.raw.atestdatalist);
        InputStream is2 = this.getResources().openRawResource(R.raw.somesettings);
        try {
            ProjectSettings.specifySettings(is2)
            ProjectSettings.enterSensitiveData(is)
        } catch (IOException e) {
            e.printStackTrace();
        }
         */

        //ListenerRegistry.register(new AlertDialogSensitiveData(this));//unneeded here since alert wouldn't display

        try {
            if(logSensitiveDataMatchOccurrences == null) {//Don't want to create another one just because activity was re-created
                logSensitiveDataMatchOccurrences = new LogSensitiveDataMatchOccurrences(this,"LogSDMOccurrences", false);
                ListenerRegistry.register(logSensitiveDataMatchOccurrences);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        textView1 = (TextView) findViewById(R.id.textView1);

        aButton = (Button) findViewById(R.id.aButton);
        aButton2 = (Button) findViewById(R.id.aButton2);
        aButton3 = (Button) findViewById(R.id.aButton3);
        aButton4 = (Button) findViewById(R.id.aButton4);
        textView1.setText("A possible setup apps could have, where it request user to grant permissions it will need throughout app instead of at the time it needs them.");


        aButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                requestPerms();
            }
        });
        //Need to separate requestPerms and sendAnIntent since otherwise my dialog for requesting perms will be hidden during activity transition.
        aButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                sendAnIntent();
            }
        });

        aButton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, UserInputActivity.class);
                startActivity(intent);
            }
        });

        aButton4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!DataGathering.calledConsiderContactsSensitiveData()){
                    DataGathering.considerContactsSensitiveData(MainActivity.this);
                }
            }
        });

    }


    private void requestPerms(){
        Log.i("MainActivity", "we are at requestPerms");

        if(getApplicationContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Read Contacts permission");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setMessage("Please enable access to contacts.");
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(
                            new String[]
                                    {android.Manifest.permission.READ_CONTACTS}
                            , 1);
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        else if(getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Write Contacts permission");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setMessage("Please enable access to contacts.");
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(
                            new String[]
                                    {android.Manifest.permission.WRITE_CONTACTS}
                            , 1);
                }
            });
            builder.setCancelable(false);
            builder.show();
        }

    }

    private void sendAnIntent(){
        Intent in = new Intent(getApplicationContext(),Main2Activity.class);
        startActivity(in);
    }
}
