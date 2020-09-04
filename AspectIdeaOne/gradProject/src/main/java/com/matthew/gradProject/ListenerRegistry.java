package com.matthew.gradProject;

import android.util.Log;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


//Contains registered listeners and calls each listeners handle method when a data leak event occurs.
public class ListenerRegistry {

    private static final SensitiveDataNotification theNotification = new SensitiveDataNotification();
    private static Set<SensitiveDataListener> setOfListeners = new HashSet<SensitiveDataListener>(Arrays.asList(theNotification));//Notifications are enabled by default so user can be notified.


    private static final Object setLock = new Object();

    public static boolean register(SensitiveDataListener listener) {
        synchronized (setLock) {
            return setOfListeners.add(listener);
        }
    }

    public static boolean unRegister(SensitiveDataListener listener) {
        synchronized (setLock) {
            return setOfListeners.remove(listener);
        }
    }

    public static Set<SensitiveDataListener> getSetOfListeners(){
        return setOfListeners;
    }

    public static SensitiveDataNotification getTheNotification(){return theNotification;}


    //Had to remove synchronized SINCE if have two aspect threads. One on UI thread and other not, and the one that isn't on UI thread gets here first
    // Program would freeze since alerts can't be displayed since UI thread is waiting to be able to enter this method.
    protected static boolean handle(SensitiveDataMatch sensitiveDataMatch, ProceedingJoinPoint proceedingJoinPoint) {
        Set<SensitiveDataListener> currentListenersAtMethodBegin;

        //Below prevents issue of modifying Set while trying to read it.
        synchronized (setLock){
            currentListenersAtMethodBegin = new HashSet<SensitiveDataListener>(setOfListeners);
        }


        boolean joinPointProceed = true;

        for (SensitiveDataListener listener : currentListenersAtMethodBegin) {
            if (!joinPointProceed) {//a listener returned false so we will deny join point from proceeding regardless of what other listeners say. Veto system.
                if (listener instanceof SensitiveDataUserDialogListener) {//The listener needs ProceedingJoinPoint reference since wants users response to make decision.
                    ((SensitiveDataUserDialogListener) listener).onFoundData(sensitiveDataMatch,proceedingJoinPoint);
                }else if(listener instanceof SensitiveDataLoggingListener){
                    ((SensitiveDataLoggingListener) listener).onFoundData(sensitiveDataMatch);
                }

            } else {
                if (listener instanceof SensitiveDataUserDialogListener) {//The listener needs ProceedingJoinPoint reference since wants the users response to make decision.
                    joinPointProceed =((SensitiveDataUserDialogListener) listener).onFoundData(sensitiveDataMatch,proceedingJoinPoint);
                }else if(listener instanceof SensitiveDataLoggingListener){
                    joinPointProceed =((SensitiveDataLoggingListener) listener).onFoundData(sensitiveDataMatch);
                }

                if (joinPointProceed == false) {
                    Log.i("ListenerRegistry", "Listener " + listener.toString() + " has returned false. So will not allow method call to execute.");
                }
            }
        }
        return joinPointProceed;
    }
}
