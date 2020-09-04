package com.matthew.gradProject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SensitiveDataNotification implements SensitiveDataLoggingListener {

    private static final String channelId = "SenstivieDataNotificationLeak";
    private static final String groupId = "SDNLeakGroup";
    private static Context heldContext;
    private static int notificationCounter = 1;

    public SensitiveDataNotification(){
    }


    //This is needed from Android 8.0 + . It is fine if call this multiple times, if channel is already created, nothing happens.
    public static void createNotificationChannel(Context context){
        heldContext = context;
        CharSequence name = "SensitiveDataLeakAlertChannel";
        String description = "This channel is for giving user a notification when app with security library attached, attempts to leak what is deemed sensitive data!";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(channelId,name,importance);
        channel.setDescription(description);

        //Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager= heldContext.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }


    @Override
    public boolean onFoundData(SensitiveDataMatch sensitiveDataMatch) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(heldContext);
        Map<String,List<String>> dataFound = extractSensitiveDataFound(sensitiveDataMatch);

        Set<String> dataSet = dataFound.keySet();

        ApplicationInfo applicationInfo = heldContext.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
         String value = stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : heldContext.getString(stringId);

         int counter = 1;
         StringBuilder notificationMessage = new StringBuilder(value + " Is trying to leak the sensitive data: \n");
         //Don't want to have a new notification for each individual leak, INSTEAD want a new notification for each new complete data leak event (so one notification per join point).
        for(String sensitiveData: dataSet){
            notificationMessage.append(counter + ": " + sensitiveData + "\n It's data category  is " + dataFound.get(sensitiveData).get(0) + "\n");
            counter++;
        }
        counter--;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(heldContext,channelId)
                .setContentTitle("Sensitive Data Leak in " + value)
                .setSmallIcon(com.matthew.gradProject.R.drawable.exclamation)
                .setContentText(counter +" sensitive data leaks were just found occurring in this app, details below.....")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage.toString()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup(groupId );

        notificationManager.notify(notificationCounter,builder.build());
        notificationCounter++;//So don't overwrite notifications
        //if this is the same number as previous notification, it just updates notification!!!!!!!!!

        return ProjectSettings.isNotificationReturn();
    }

    protected Map<String, List<String>> extractSensitiveDataFound(SensitiveDataMatch sensitiveDataMatch) {
        Map<String,List<String>> data = new HashMap<>();

        //Would be for one join point. I don't combine details of multiple join points.
        List<OffendingArgument> offendingArgList = sensitiveDataMatch.getOffendingArgumentList();
        for (OffendingArgument offendingArgument : offendingArgList) {
            List<OffendingData> offendingDataList = offendingArgument.getDataMatches();

            for (OffendingData offendingData : offendingDataList) {
                data.putAll(offendingData.getSensitiveDataThatWasFound());//Would get sensitive data and JSON path to data from Sensitive data file.
            }
        }
        return data;
    }
}
