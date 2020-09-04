package com.matthew.gradProject;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.*;

public class AlertDialogSensitiveData implements SensitiveDataUserDialogListener {

    protected final Activity activityFocus; //The activity where alerts will be displayed on for this listener. If activity isn't visible, then alerts are not sent.
    // Activity reference shouldn't be changed due to possible thread issues. A new listener will need to be created for every activity.
    public final int alertId;

    public AlertDialogSensitiveData(Activity activity, int id) {
        activityFocus = activity;
        alertId = id;
    }


    //Because using HashSet in ListenerRegistry and want to prevent duplicate listeners.
    @Override
    public boolean equals(Object val) {
        if (val == this) {
            return true;
        }
        if (!(val instanceof AlertDialogSensitiveData)) {
            return false;
        }else{
            AlertDialogSensitiveData temp = (AlertDialogSensitiveData) val;
            return alertId == (temp.alertId);
        }
    }

    //Because using HashSet in ListenerRegistry and want to prevent duplicate listeners.
    @Override
    public int hashCode()
    {
        return alertId;
    }

    @Override
    public boolean onFoundData(SensitiveDataMatch sensitiveDataMatch, ProceedingJoinPoint proceedingJoinPoint) {
        if(!activityFocus.getWindow().getDecorView().isShown()){
            Log.i("AlertDialogSensitiveData","Listener " + this + " is attached to an activity that isn't visible. To prevent project freeze and displaying hidden alerts, will not execute" +
                    " this listeners onFoundData method.");
            return true;
        }

        Map<String,List<String>> dataFound = extractSensitiveDataFound(sensitiveDataMatch);

        if (Looper.getMainLooper().isCurrentThread()) {
            Thread thread = new Thread(new MyRunnable(proceedingJoinPoint,dataFound));
            thread.start();

            return false;//Have to do the alert dialog for users choice on a non-ui thread since users response determines if the join point proceeds. Would have to have thread wait for the
            // users response, which can't be done on UI thead. So in this situation user would have final say if the join point proceeds regardless of other listeners.
            // todo See about setting up a second app to hold users sensitive data preferences and show alert, need to make sure communication is secure and Androids thread wait policy
            //  won't cause issues.
            //https://developer.android.com/topic/performance/threads#internals
            //https://developer.android.com/guide/components/processes-and-threads#Threads

        } else {//We are not on UI thread, so can do waiting. Users choice would have same impact as any other listener in determining if join point proceeds based on Veto System (one no prevents it from proceeding).
            return displayAlert(dataFound);
        }
    }


    //This provided alert only requires the details of actual sensitive data being leaked and the top key name of JSON file format (such as Contacts which
    // means sensitive data is from users contacts). Doesn't use other data such as which argument it was in.
    protected Map<String,List<String>> extractSensitiveDataFound(SensitiveDataMatch sensitiveDataMatch) {
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


    protected boolean displayAlert(Map<String,List<String>> dataFound) {
        //Should only run this method on non-ui thread
        if(Looper.getMainLooper().isCurrentThread()){
            try{
                Log.e("AlertDialogSensitiveData","Error in displayAlert, method should not be executed on the UI thread.");
                throw new Exception("Trying to create an Alert Dialog on the UI thread. displayAlert should not be executed on the UI thread.");
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }

        final boolean[] canProceed = {true};//Needs to be final so can access it in anonymous OnClickListener class.
        final Object mPauseLock = new Object();//Needed so can pause thread while waiting for response from user on UI thread

        Set<String> dataSet = dataFound.keySet();

        for(String sensitiveData: dataSet){
            if(!canProceed[0]){
                break;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activityFocus);
            String[] choices = {"Deny action", "Allow action this time."};
            builder.setTitle("App trying to leak sensitive data: " + sensitiveData + ", Category: " + dataFound.get(sensitiveData).get(0));//There is a character limit.
            //One major issue is this will be stored in Activity referenced by activityFocus temporarily.


            builder.setItems(choices, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        Log.i("AlertDialogSensitiveData", "Clicked deny action");
                        synchronized (mPauseLock) {
                            canProceed[0] = false;
                            mPauseLock.notify();//unpause aspect thread
                        }
                    } else if (which == 1) {
                        Log.i("AlertDialogSensitiveData", "Clicked allow action");
                        synchronized (mPauseLock) {
                            mPauseLock.notify();//unpause aspect thread
                        }
                    }
                }
            });
            builder.setCancelable(false);

            activityFocus.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    builder.show();
                }
            });

            synchronized (mPauseLock) {//is required to use Object.wait
                try {
                    mPauseLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return canProceed[0];
    }


    private class MyRunnable implements Runnable {

        private ProceedingJoinPoint joinPoint;
        private Map<String,List<String>> sensitiveData;

        public MyRunnable(ProceedingJoinPoint aJoinPoint, Map<String,List<String>> senstiveData) {
            joinPoint = aJoinPoint;
            this.sensitiveData = senstiveData;
        }

        @Override
        public void run() {
            if(displayAlert(sensitiveData)){
                try {
                   joinPoint.proceed(); //Since join point was running on UI thread, had to setup alert on separate thread. So users choice is absolute
                    // unlike if was already off UI thread and another listener could deny leak from occurring despite user saying yes to it.
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }
}
