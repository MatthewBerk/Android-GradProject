package com.example.aspectideaone;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.*;
import android.provider.ContactsContract;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


//This is for test where an external source sends a message to this service, and we try to send a reply back.

// This is primarily to show I can get context for sensitive data file, when an external source starts/binds to this service.
//To begin test, need to start from the second app.
public class MyExampleService extends Service {

    Messenger messenger;
    IncomingHandler incomingHandler;

    @Override
    public void onCreate(){
        HandlerThread thread = new HandlerThread("ServiceStartArguments");
        thread.start();//Start a new thread for our service to run on
        incomingHandler = new IncomingHandler(thread.getLooper());
    }

    //Invoked when startService is called.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        messenger = new Messenger(incomingHandler);
        return messenger.getBinder();
    }


    class IncomingHandler extends Handler {

        public IncomingHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg){
            //Replying to message
            Message newMessage = Message.obtain(null, 5);
            List<String> contactsData =  loadupContacts();
            Bundle temp = new Bundle();
            temp.putCharSequence("Data1",contactsData.get(0));
            newMessage.obj = temp;
            try {
                msg.replyTo.send(newMessage);//Test worked, intercepted data even when the app was initially dead before binding happened!
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    private List<String> loadupContacts() {
        List<String> collectionOfSomeUserContactData = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                collectionOfSomeUserContactData.add(name);

                if (hasPhoneNumber > 0) {
                    Cursor cursor2 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    cursor2.moveToFirst();
                    do {
                        String phoneNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        collectionOfSomeUserContactData.add(phoneNumber);

                    } while (cursor2.moveToNext());
                    cursor2.close();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return collectionOfSomeUserContactData;
    }

}
