package com.example.justasecondapp;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import androidx.annotation.Nullable;


//Test for MessageActivity in app 1
//Main app trying to communicate with this apps service.
public class AnExampleService extends Service {

    Messenger messenger;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //display sensitive data that was stored in intent from startService
        String someData = intent.getStringExtra("Data1");
        Log.i("AnExampleService", "Data gotten in onStartCommand " + someData);

        return super.onStartCommand(intent, flags, startId);
    }

    class anReceivingHandlerOne extends Handler {

        @Override
        public void handleMessage(Message msg){
            //display data in msg, that came from startMessageExperiment
            Bundle someData = (Bundle) msg.obj;
            String data1 = someData.getString("Data1");
            Log.i("AnExampleService", "Data gotten in handleMessage " + data1);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // display data in intent, that came from bindService!!
        String someData = intent.getStringExtra("Data1");
        Log.i("AnExampleService", "Data gotten in onBind " + someData);

        messenger = new Messenger(new anReceivingHandlerOne());
        return messenger.getBinder();
    }
}
