package com.example.justasecondapp;


import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


//This class is for running test of another app (this app) trying to start/bind with a service in my main app
// that has the security project.
//Test just consist of having main app use the messenger-handler we sent to it to send a message to handler here.
// since Handler/messageQueue isn't specifically a services thing, so wanted to test an external app using our main app service to have it
// send data to it. (Wanted to make sure security checks worked in child of Handler class in service class in main app).
public class MainActivity extends AppCompatActivity {

    private Button startTest;
    private Button startTest2;
    private Button startTest3;
    private Messenger messengerRequest;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTest = (Button) findViewById(R.id.startTest);
        startTest2 = (Button) findViewById(R.id.startTest2);
        startTest3 = (Button) findViewById(R.id.startTest3);

        intent=new Intent();
        intent.setComponent(new ComponentName("com.example.aspectideaone","com.example.aspectideaone.MyExampleService"));

        //For broadcast test
        IntentFilter filter = new IntentFilter();
        filter.addAction("matthew.broadCastTest.Action");
        ReceivingBroadcast receivingBroadcast = new ReceivingBroadcast();
        registerReceiver(receivingBroadcast, filter);


        startTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                bindToRemoteService();
            }
        });

        startTest2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startMessageExperiment();
            }
        });


        startTest3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startServiceGettingContextExperiment();
            }
        });

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messengerRequest = new Messenger(service);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            messengerRequest = null;
        }
    };

    private void bindToRemoteService(){
        bindService(intent,mConnection,BIND_AUTO_CREATE);//to test onCreate and onBind, in regards to getting context
    }

    //For starting handleMessage test in first app!
    private void startMessageExperiment(){
        Message requestMessage = Message.obtain(null,7);
        requestMessage.replyTo = new Messenger(new basicHandler());//Soo app1 will then try passing data to this activities handler as a reply!!!
        try {
            messengerRequest.send(requestMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    class basicHandler extends Handler {

        @Override
        public void handleMessage(Message msg){
            Bundle someData = (Bundle) msg.obj;
            String data1 = someData.getString("Data1");
            Log.i("MainActivity", "Data gotten in handleMessage " + data1);
        }
    }

    // todo Not needed anymore, was just to check on entry points of a service (onCreate) to make sure context grabbing code worked !!!
    private void startServiceGettingContextExperiment(){
        startService(intent);//Just want to see about what methods I need to weave for getting context!
    }
}
