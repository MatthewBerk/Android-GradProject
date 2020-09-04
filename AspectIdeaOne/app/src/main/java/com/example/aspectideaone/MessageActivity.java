package com.example.aspectideaone;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;


//THIS is for test of app trying to start/bind to an external service and send messages to it.
public class MessageActivity extends AppCompatActivity {

    private Button aButton;
    private Button aButton2;
    private Button aButton3;

    private Messenger requestMessenger;
    private Intent serviceIntent;
    private String sensitiveData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        aButton = (Button) findViewById(R.id.aButton25);
        aButton2 = (Button) findViewById(R.id.aButton35);
        aButton3 = (Button) findViewById(R.id.aButton45);

        Intent receivedIntent = getIntent();
        sensitiveData = receivedIntent.getStringExtra("Data1");

        serviceIntent=new Intent();
        serviceIntent.setComponent(new ComponentName("com.example.justasecondapp","com.example.justasecondapp.AnExampleService"));
        serviceIntent.putExtra("Data1", sensitiveData);

        aButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                bindToRemoteService();
            }
        });
        aButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startMessageExperiment();
            }
        });
        aButton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startServiceExperiment();
            }
        });

    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            requestMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            requestMessenger = null;
        }
    };

    private void bindToRemoteService(){
        bindService(serviceIntent,mConnection,BIND_AUTO_CREATE);//trying to send sensitive data to another app.
    }

    private void startMessageExperiment(){
        Message requestMessage = Message.obtain(null,7);
        Bundle temp = new Bundle();
        temp.putCharSequence("Data1",sensitiveData);
        requestMessage.obj = temp;
        try {
            requestMessenger.send(requestMessage);//similar experiment to one in service class
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startServiceExperiment(){
        startService(serviceIntent);//trying to send sensitive data to another app.
    }

}
