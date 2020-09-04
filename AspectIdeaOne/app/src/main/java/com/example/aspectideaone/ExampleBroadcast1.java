package com.example.aspectideaone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//Going with Context-registered receivers since Android 8.0 added rule for manifest broadcastReceivers
// that limits what broadcast a receiver can listen to. Has to target app specifically, rule doesn't apply to
// context-registered broadCastReceivers. App just needs to be alive (can be paused) for them to work.
//https://developer.android.com/guide/components/broadcasts#android_80
public class ExampleBroadcast1 extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(isOrderedBroadcast()){
            setResultData(JustStaticValues.value1);
        }

    }
}
