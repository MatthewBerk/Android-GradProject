package com.example.aspectideaone;

import android.app.PendingIntent;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.*;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.core.content.FileProvider;
import com.matthew.gradProject.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Main2Activity extends AppCompatActivity {

    private int listIndex = 0;

    private TextView listContacts;
    private Button parcelTest;
    private Button pendingIntent1Test;
    private Button pendingIntent2Test;
    private Button intentSenderTest;
    private Button broadcastReceiver1Test;
    private Button broadcastReceiver2Test;
    private Button context1Test;
    private Button context2Test;
    private Button contentResolverTest;
    private Button writer1Test;
    private Button writer2Test;
    private Button writer3Test;
    private Button outputStream1Test;
    private Button outputStream2Test;
    private Button imageUriTest;
    private Button imageShareTest1;
    private Button imageShareTest2;
    private Button imageShareTest3;
    private Button serviceTest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        listContacts = (TextView) findViewById(R.id.listContacts);
        parcelTest = (Button) findViewById(R.id.doParcelTest);
        pendingIntent1Test = (Button) findViewById(R.id.doPendingIntentTest1);
        pendingIntent2Test = (Button) findViewById(R.id.doPendingIntentTest2);
        intentSenderTest = (Button) findViewById(R.id.doIntentSenderTest);
        broadcastReceiver1Test = (Button) findViewById(R.id.doBroadcastReceiverTest1);
        broadcastReceiver2Test = (Button) findViewById(R.id.doBroadcastReceiverTest2);
        context1Test = (Button) findViewById(R.id.doContextTest1);
        context2Test = (Button) findViewById(R.id.doContextTest2);
        contentResolverTest = (Button) findViewById(R.id.doContentResolverTest);
        writer1Test = (Button) findViewById(R.id.doWriterTest1);
        writer2Test = (Button) findViewById(R.id.doWriterTest2);
        writer3Test = (Button) findViewById(R.id.doWriterTest3LocalServer);
        outputStream1Test = (Button) findViewById(R.id.doOutputStreamTest1);
        outputStream2Test = (Button) findViewById(R.id.doOutputStreamTest2LocalServer);
        imageUriTest = (Button) findViewById(R.id.doImageUriShare);
        imageShareTest1 = (Button) findViewById(R.id.doImageShare1);
        imageShareTest2 = (Button) findViewById(R.id.doImageShare2);
        imageShareTest3 = (Button) findViewById(R.id.doImageShare3);
        serviceTest = (Button) findViewById(R.id.doServiceTest);

        //So we have sensitive data in file
        if (!DataGathering.calledConsiderContactsSensitiveData()) {
            DataGathering.considerContactsSensitiveData(this);
        }

        //ListenerRegistry.register(new AlertDialogSensitiveData(this,5)); //Uncomment if want user alert. Reminder it can sometimes break program due to ruining program flow.

        /*
        //Test to for seeing if methods in Data storage work. While app developer doesn't need to use them to edit file, feel it be good to still have them.
        InputStream is = this.getResources().openRawResource(R.raw.atestdatalist);
        try {
            DataStorage.overwriteSensitiveDataFile(is);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        */


        parcelTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                parcelableTest(contactData);
            }
        });

        pendingIntent1Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                pendingIntentTest1(contactData);
            }
        });

        pendingIntent2Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                pendingIntentTest2(contactData);
            }
        });

        intentSenderTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                intentSenderTest(contactData);
            }
        });

        broadcastReceiver1Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                broadcastReceiverTest1(contactData);
            }
        });

        broadcastReceiver2Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                broadcastReceiverTest2(contactData);
            }
        });

        context1Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                contextTest1(contactData);
            }
        });

        context2Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                contextTest2(contactData);
            }
        });


        contentResolverTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                contentResolverTest(contactData);
            }
        });


        writer1Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                writerTest1(contactData);
            }
        });

        writer2Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                writerTest2(contactData);
            }
        });

        writer3Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                writerTest3LocalServer(contactData);
            }
        });

        outputStream1Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                outputStreamTest1(contactData);
            }
        });


        outputStream2Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                outputStreamTest2LocalServer(contactData);
            }
        });

        imageUriTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri temp = loadImage();
                imageUriShareTest(temp);

            }
        });

        imageShareTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri temp = loadImage();
                imageShareTest1(temp);
            }
        });

        imageShareTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri temp = loadImage();
                imageShareTest2(temp);
            }
        });

        imageShareTest3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri temp = loadImage();
                imageShareTest3(temp);
            }
        });

        serviceTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> contactData = loadupContacts();
                Intent in = new Intent(Main2Activity.this, MessageActivity.class);
                in.putExtra("Data1", contactData.get(listIndex));
                startActivity(in);
            }
        });
    }


    //Grabs Full name and all the phone numbers associated for each contact
    private List<String> loadupContacts() {
        listContacts.setText("Thank you for pressing button!");
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

    //Would only grab image of first contact, should edit it to so grabs multiple or I pass in a contact id.
    private Uri loadImage() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        cursor.moveToNext();

        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        String selection = ContactsContract.Data.MIMETYPE + " = ?" + " AND " + ContactsContract.Data.CONTACT_ID + " = " + id;

        String[] cdkPhoto = new String[]{
                //ContactsContract.CommonDataKinds.Phone.PHOTO_URI, //Causes some issue when debugging (debugger freezes due to size) though when just run project, it works fine.
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
        };


        String[] photoSelectionArgs = new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};

        Cursor cursor2 = contentResolver.query(ContactsContract.Data.CONTENT_URI, cdkPhoto, selection, photoSelectionArgs, null);

        cursor2.moveToFirst();

        String value1 = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

        Uri imageUri = Uri.parse(value1);

        cursor2.close();
        cursor.close();

        return imageUri;
    }

    //PROOF can share image without fileProvider' doc
    private void imageUriShareTest(Uri theImageUri) {
        Intent intent = new Intent();
        intent.setClassName("com.example.justasecondapp", "com.example.justasecondapp.ReceivingImageActivity");//Second app that serves to demonstrate tests.

        intent.setData(theImageUri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.i("Main2Activity", "No activity for imageUriShareTest");
        }
    }

    //Test where share an image by base64 encoding it.
    private void imageShareTest1(Uri theImageUri) {
        Intent intent = new Intent();
        intent.setClassName("com.example.justasecondapp", "com.example.justasecondapp.ReceivingImageActivity");//Second app that serves to demonstrate tests.

        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//Android 9.0 (API 28 when below approach was introduced)
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), theImageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), theImageUri);//Method was deprecated in API 29 and their substitute was only introduced API 28
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);//Need to extract byte array from bitmap
            byte[] bytes = stream.toByteArray();
            bitmap.recycle();//So garbage collector knows to get rid of bitmap.
            stream.close();

            String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);

            intent.putExtra("EncodedImage", encodedString);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Log.i("Main2Activity", "No activity for imageShareTest1");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Test where share image by byte array
    private void imageShareTest2(Uri theImageUri) {
        Intent intent = new Intent();
        intent.setClassName("com.example.justasecondapp", "com.example.justasecondapp.ReceivingImageActivity");//Second app that serves to demonstrate tests.

        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//Android 9.0 (API 28 when below approach was introduced)
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), theImageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), theImageUri);//Method was deprecated in API 29 and their substitute was only introduced API 28
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);//Need to extract byte array from bitmap
            byte[] bytes = stream.toByteArray();
            bitmap.recycle();//So garbage collector knows to get rid of bitmap.
            stream.close();


            intent.putExtra("ImageBytes", bytes);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Log.i("Main2Activity", "No activity for imageShareTest2");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Test where share image by bitmap. (note bitmap has to be small, so no large pictures).
    private void imageShareTest3(Uri theImageUri) {
        Intent intent = new Intent();
        intent.setClassName("com.example.justasecondapp", "com.example.justasecondapp.ReceivingImageActivity");//Second app that serves to demonstrate tests.

        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//Android 9.0 (API 28 when below approach was introduced)
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), theImageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), theImageUri);//Method was deprecated in API 29 and their substitute was only introduced API 28
            }

            intent.putExtra("ImageBitmap", bitmap);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Log.i("Main2Activity", "No activity for imageShareTest3");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Look at ExampleParcelable class for explanation of test.
    //It should find sensitive data in intent for startActivity join point
    // And in ExampleParcelable when its calling Parcel.write methods in writeToParcel AFTER startActivity begins executing
    private void parcelableTest(List<String> listOfData) {
        Intent intent = new Intent();
        intent.setClassName("com.example.justasecondapp", "com.example.justasecondapp.ReceivingParcelableActivity");//Second app that serves to demonstrate tests.

        // Intent intent = new Intent(getApplicationContext(),MainActivity.class);

        ExampleParcelable exampleParcelable = new ExampleParcelable(listOfData.get(listIndex));
        if (listIndex + 1 < listOfData.size()) {
            JustStaticValues.value1 = listOfData.get(listIndex + 1);
        } else {
            JustStaticValues.value1 = listOfData.get(listIndex);
        }
        if (listIndex + 2 < listOfData.size()) {
            JustStaticValues.value2 = listOfData.get(listIndex + 2).getBytes();
        } else {
            JustStaticValues.value2 = listOfData.get(listIndex).getBytes();
        }

        intent.putExtra("ParcelValue", exampleParcelable);

        //ExampleParcelable writeToParcel method will be called during startActivity execution. Based on tests conducted
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.i("Main2Activity", "No activity for parcelableTest");
        }
    }


    //Passing a pendingIntent to another application "allows this other application to use the permissions of your application to execute a predefined piece of code"
    // Example https://stackoverflow.com/questions/14025797/how-to-send-pendingintent-to-my-service-inside-intent
    private void pendingIntentTest1(List<String> listOfData) {
        Intent intent = new Intent();
        intent.setClassName("com.example.justasecondapp", "com.example.justasecondapp.ReceivingTextActivity");//Second app that serves to demonstrate tests.
        intent.putExtra("Data1", listOfData.get(listIndex));
        if (listIndex + 1 < listOfData.size()) {
            intent.putExtra("Data2", listOfData.get(listIndex + 1));
        }

        if (intent.resolveActivity(getPackageManager()) != null) {

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Issue here is do to alert, pendingIntent will be null when pendingIntent.send() gets executed..
            try {
                pendingIntent.send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Main2Activity", "No activity for pendingIntentTest1");
        }
    }

    //Test where put intent in pendingIntent.send  method call
    private void pendingIntentTest2(List<String> listOfData) {
        Intent intent = new Intent();
        intent.putExtra("Data1", listOfData.get(listIndex));
        if (listIndex + 1 < listOfData.size()) {
            intent.putExtra("Data2", listOfData.get(listIndex + 1));
        }
        Intent blankIntent = new Intent();
        blankIntent.setClassName("com.example.justasecondapp", "com.example.justasecondapp.ReceivingTextActivity");//Second app that serves to demonstrate tests.
        if (blankIntent.resolveActivity(getPackageManager()) != null) {

            //need an intent object to create a PendingIntent object.
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, blankIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            try {
                pendingIntent.send(this, 1, intent);//note destination is still based on intent in pendingIntent
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Main2Activity", "No activity for pendingIntentTest2");
        }
    }

    private void intentSenderTest(List<String> listOfData) {
        Intent intent = new Intent();
        intent.putExtra("Data1", listOfData.get(listIndex));
        if (listIndex + 1 < listOfData.size()) {
            intent.putExtra("Data2", listOfData.get(listIndex + 1));
        }

        Intent blankIntent = new Intent();
        blankIntent.setClassName("com.example.justasecondapp", "com.example.justasecondapp.ReceivingTextActivity");//Second app that serves to demonstrate tests.
        if (blankIntent.resolveActivity(getPackageManager()) != null) {

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, blankIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            IntentSender intentSender = pendingIntent.getIntentSender();

            try {
                intentSender.sendIntent(this, 1, intent, null, null, null);//note destination is still based on intent in pendingIntent
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Main2Activity", "No activity for intentSenderTest");
        }
    }

    //Test where ExampleBroadcast1 attempts to store sensitive data in broadcast before its possibly sent to another broadcast since receivers get broadcast one at a time instead of all at once.
    //Second app will also register a broadcast receiver with action.
    private void broadcastReceiverTest1(List<String> listOfData) {
        JustStaticValues.value1 = listOfData.get(listIndex);//Data ExampleBroadcast1 will try sending off.
        IntentFilter filter = new IntentFilter();
        filter.addAction("matthew.broadCastTest.Action");

        ExampleBroadcast1 exampleBroadcast1 = new ExampleBroadcast1();
        registerReceiver(exampleBroadcast1, filter);

        //todo FEEL Need to add a button to unregister these receivers!

        Intent intent = new Intent("matthew.broadCastTest.Action");

        sendOrderedBroadcast(intent, null);

    }

    //Test where ExampleBroadcast2 attempt to store sensitive data in broadcast while in async mode.
    private void broadcastReceiverTest2(List<String> listOfData) {
        JustStaticValues.value1 = listOfData.get(listIndex);//Data ExampleBroadcast2 will try sending off.
        IntentFilter filter = new IntentFilter();
        filter.addAction("matthew.broadCastTest.Action");

        ExampleBroadcast2 exampleBroadcast2 = new ExampleBroadcast2();
        registerReceiver(exampleBroadcast2, filter);

        Intent intent = new Intent("matthew.broadCastTest.Action");

        sendOrderedBroadcast(intent, null);
    }

    //Testing startActivity that has intent with sensitive data
    private void contextTest1(List<String> listOfData) {
        Intent intent = new Intent();
        intent.setClassName("com.example.justasecondapp", "com.example.justasecondapp.ReceivingTextActivity");//Second app that serves to demonstrate tests.

        intent.putExtra("Data1", listOfData.get(listIndex));
        if (listIndex + 1 < listOfData.size()) {
            intent.putExtra("Data2", listOfData.get(listIndex + 1));
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.i("Main2Activity", "No activity for contextTest1");
        }
    }

    //Testing sendBroadcast that has intent with sensitive data
    private void contextTest2(List<String> listOfData) {
        Intent intent = new Intent("matthew.broadCastTest.Action");
        intent.putExtra("Data1", listOfData.get(listIndex));
        if (listIndex + 1 < listOfData.size()) {
            intent.putExtra("Data2", listOfData.get(listIndex + 1));
        }

        sendBroadcast(intent);
    }

    //Test involving attempt to write sensitive data to a content provider (went with contacts content provider for simplicity).
    //NOTE to see successful execution of insert, need to look at new contact created and click edit. Unable to add display name or number until AFTER this insert was done.
    private void contentResolverTest(List<String> listOfData) {
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();//Stores data in a HashMap
        contentValues.put(ContactsContract.RawContacts.ACCOUNT_TYPE, "Default");
        contentValues.put(ContactsContract.RawContacts.ACCOUNT_NAME, listOfData.get(listIndex) + "Joey");

        contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
    }

    //Test involving writing to a newly created file in apps internal storage file directory. File will be shared with another app.
    private void writerTest1(List<String> listOfData) {
        File aFile = new File(getFilesDir(), "WriterTest1File");
        try {
            boolean fileCreated = aFile.createNewFile();
            Log.i("WriterTest1", "File created : " + fileCreated);
            if (!fileCreated) {
                new PrintWriter(aFile).close();//wipes contents
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(aFile, true));
            bufferedWriter.write(listOfData.get(listIndex), 0, listOfData.get(listIndex).length());
            if (listIndex + 1 < listOfData.size()) {
                char[] chars = listOfData.get(listIndex + 1).toCharArray();//To show can handle char[] that have sensitive data.
                bufferedWriter.write(chars, 0, chars.length);
            }
            bufferedWriter.write("The weather is lovely!");

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri = FileProvider.getUriForFile(this, "com.example.aspectideaone", aFile);

        Intent intent = new Intent();
        intent.setClassName("com.example.justasecondapp", "com.example.justasecondapp.ReceivingFileActivity");//Second app that serves to demonstrate tests.

        intent.setDataAndType(uri, "text/plain");
        //https://developer.android.com/guide/topics/providers/content-provider-basics#getting-access-with-temporary-permissions
        //https://developer.android.com/training/secure-file-sharing/share-file#GrantPermissions
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.i("Main2Activity", "No activity for writerTest1");
        }

    }

    //Test involving writing to a newly created cache file in apps internal storage cache directory. File will be shared with another app.
    // Caches can be seen as temporary files that could be deleted anytime, so shouldn't store data you need persisted in a cache file.
    private void writerTest2(List<String> listOfData) {
        File aFile = new File(getCacheDir(), "WriterTest2Cache");

        try {
            boolean fileCreated = aFile.createNewFile();//false if file already exist.
            Log.i("WriterTest1", "File created : " + fileCreated);
            if (!fileCreated) {
                new PrintWriter(aFile).close();//wipes contents
            }

            Writer bufferedWriter = new BufferedWriter(new FileWriter(aFile, true));//Using BufferedWriter again since besides PrintWriter, other non-abstract writer subclass's don't take writer or file.
            bufferedWriter.write(listOfData.get(listIndex), 0, listOfData.get(listIndex).length());
            if (listIndex + 1 < listOfData.size()) {
                char[] chars = listOfData.get(listIndex + 1).toCharArray();//To show can handle char[] that have sensitive data.
                bufferedWriter.write(chars, 0, chars.length);
            }
            bufferedWriter.write("The grass is green!");

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri = FileProvider.getUriForFile(this, "com.example.aspectideaone", aFile);

        Intent intent = new Intent();
        intent.setClassName("com.example.justasecondapp", "com.example.justasecondapp.ReceivingFileActivity");//Second app that serves to demonstrate tests.

        intent.setDataAndType(uri, "text/plain");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.i("Main2Activity", "No activity for writerTest2");
        }
    }


    //Test involving writing to a socket. Socket is for locally hosted server.
    // Since Android 3, not allowed to read/write to a network on Main thread https://developer.android.com/reference/android/os/NetworkOnMainThreadException

    //You need to create your own local java server and setup the socket.
    private void writerTest3LocalServer(List<String> listOfData) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket;
                PrintWriter printWriter;

                try {
                    socket = new Socket("", 7800);//first argument is computers IPv4 Address.
                    //byte test will be in OutputStream test since Writer and its subclass's don't provide methods for passing in byte[]

                    printWriter = new PrintWriter(socket.getOutputStream());

                    printWriter.println(listOfData.get(listIndex));
                    if (listIndex + 1 < listOfData.size()) {
                        printWriter.print(listOfData.get(listIndex + 1));
                    }
                    printWriter.write("\nHello World!");

                    printWriter.flush();
                    printWriter.close();

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //Test involving writing to a newly created file in apps internal storage file directory. File will be shared with another app.
    private void outputStreamTest1(List<String> listOfData) {
        File aFile = new File(getFilesDir(), "OutputStreamTest1File");
        try {
            boolean fileCreated = aFile.createNewFile();
            Log.i("WriterTest1", "File created : " + fileCreated);
            if (!fileCreated) {
                new PrintWriter(aFile).close();//wipes contents
            }
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(aFile, true));

            dataOutputStream.writeChars(listOfData.get(listIndex));

            if (listIndex + 1 < listOfData.size()) {
                byte[] bytes = listOfData.get(listIndex + 1).getBytes();//To show can handle byte[] that have sensitive data
                dataOutputStream.write(bytes);
            }
            dataOutputStream.writeDouble(9.6);

            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri = FileProvider.getUriForFile(this, "com.example.aspectideaone", aFile);

        Intent intent = new Intent();
        intent.setClassName("com.example.justasecondapp", "com.example.justasecondapp.ReceivingFileActivity");//Second app that serves to demonstrate tests.

        intent.setDataAndType(uri, "text/plain");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.i("Main2Activity", "No activity for outputStreamTest1");
        }
    }

    //You need to create your own local java server and setup the socket.
    private void outputStreamTest2LocalServer(List<String> listOfData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket;
                try {
                    socket = new Socket("", 7800);//first argument is computers IPv4 Address.
                    byte[] bytes1 = listOfData.get(listIndex).getBytes();
                    socket.getOutputStream().write(bytes1);
                    if (listIndex + 1 < listOfData.size()) {
                        byte[] bytes2 = listOfData.get(listIndex).getBytes();
                        socket.getOutputStream().write(bytes2);
                    }
                    socket.getOutputStream().flush();
                    socket.getOutputStream().close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
