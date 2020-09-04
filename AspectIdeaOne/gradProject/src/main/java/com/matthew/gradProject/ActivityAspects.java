package com.matthew.gradProject;

import android.content.Context;
import android.util.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;


@Aspect
public class ActivityAspects {

    private boolean gotContextReference = false;
    private AnalyzerProcess analyzerProcess = new AnalyzerProcess();

    // Its fine if the pointcuts are void, still able to return since advice returns an Object!
    // Ignore error about PointCut call, IntelliJ utilizes spring aop by default which cannot handle call pointcuts, that is why I am using AspectJ, it works if have aspectjrt.jar as a global library.

    //+ Weaves calls to these methods, including subclass versions.

    @Pointcut("call(* android.content.Context+.bindIsolatedService(..))")
    public void contextBindIsolatedService() {
    }

    @Pointcut("call(* android.content.Context+.bindService(..))")
    public void contextBindService() {
    }

    @Pointcut("call(* android.content.Context+.bindServiceAsUser(..))")//Added api 30
    public void contextBindServiceAsUser() {
    }

    @Pointcut("call(* android.content.Context+.sendBroadcast(..))")
    public void contextSendBroadcast() {
    }

    @Pointcut("call(* android.content.Context+.sendBroadcastAsUser(..))")
    public void contextSendBroadcastAsUser() {
    }

    @Pointcut("call(* android.content.Context+.sendOrderedBroadcast(..))")
    public void contextSendOrderedBroadcast() {
    }

    @Pointcut("call(* android.content.Context+.sendOrderedBroadcastAsUser(..))")
    public void contextSendOrderedBroadcastAsUser() {
    }

    @Pointcut("call(* android.content.Context+.startActivities(..))")
    public void contextStartActivities() {
    }

    @Pointcut("call(* android.content.Context+.startActivity(..))")
    public void contextStartActivity() {
    }

    @Pointcut("call(* android.content.Context+.startForegroundService(..))")
    public void contextStartForegroundService() {
    }

    @Pointcut("call(* android.content.Context+.startInstrumentation(..))")
    public void contextStartInstrumentation() {
    }

    @Pointcut("call(* android.content.Context+.startIntentSender(..))")
    public void contextStartIntentSender() {
    }

    @Pointcut("call(* android.content.Context+.startService(..))")
    public void contextStartService() {
    }

    @Pointcut("call(* android.content.Context+.stopService(..))")
    public void contextStopService() {
    }

    @Pointcut("call(* android.content.Context+.startActivityForResult(..))")
    public void contextStartActivityForResult() {
    }

    @Pointcut("call(* android.app.Activity+.createPendingResult(..))")
    public void activityCreatePendingResult() {
    }

    @Pointcut("call(* android.app.Activity+.dump(..))")//since could have it write sensitive Data to PrintWriter.
    public void activityDump() {
    }

    @Pointcut("call(* android.app.Activity+.navigateUpTo(..))")
    public void activityNavigateUpTo() {
    }

    @Pointcut("call(* android.app.Activity+.navigateUpToFromChild(..))")
    public void activityNavigateUpToFromChild() {
    }

    @Pointcut("call(* android.app.Activity+.setResult(..))")
    public void activitySetResult() {
    }

    @Pointcut("call(* android.app.Activity+.startActivityFromChild(..))")
    public void activityStartActivityFromChild() {
    }

    //though deprecated in API 28. Though handling methods that were present in API26+ (Android 8.0 + )
    @Pointcut("call(* android.app.Activity+.startActivityFromFragment(..))")
    public void activityStartActivityFromFragment() {
    }

    @Pointcut("call(* android.app.Activity+.startActivityIfNeeded(..))")
    public void activityStartActivityIfNeeded() {
    }

    @Pointcut("call(* android.app.Activity+.startIntentSenderForResult(..))")
    public void activityStartIntentSenderForResult() {
    }

    @Pointcut("call(* android.app.Activity+.startIntentSenderFromChild(..))")
    public void activityStartIntentSenderFromChild() {
    }

    @Pointcut("call(* android.app.Activity+.startNextMatchingActivity(..))")
    public void activityStartNextMatchingActivity() {
    }

    @Pointcut("call(* android.app.Activity+.startLocalVoiceInteraction(..))")
    public void activityStartLocalVoiceInteraction() {
    }

    @Pointcut("call(* androidx.fragment.app.FragmentActivity+.startIntentSenderFromFragment(..))")
    public void fragmentActivityStartIntentSenderFromFragment() {
    }


    //These are pointcuts related to Writer (and subclass's)
    //Handles all write methods of Writer class and its children.
    @Pointcut("call(* java.io.Writer+.write(..))")
    public void writerWrite() {
    }

    @Pointcut("call(* java.io.Writer+.append(..))")
    public void writerAppend() {
    }

    @Pointcut("call(* java.io.PrintWriter+.format(..))")
    public void printWriterFormat() {
    }

    @Pointcut("call(* java.io.PrintWriter+.print(..))")
    public void printWriterPrint() {
    }

    @Pointcut("call(* java.io.PrintWriter+.printf(..))")
    public void printWriterPrintf() {
    }

    @Pointcut("call(* java.io.PrintWriter+.println(..))")
    public void printWriterPrintln() {
    }

    @Pointcut("call(* java.io.CharArrayWriter+.writeTo(..))")
    public void charArrayWriterWriteTo() {
    }


    //Handles all write methods of OutputStream class and its children.
    @Pointcut("call(* java.io.OutputStream+.write(..))")
    public void outputStreamWrite() {
    }

    @Pointcut("call(* java.io.ByteArrayOutputStream+.writeTo(..))")
    public void byteArrayOutputStreamWriteTo() {
    }

    //Handles all the write.. methods (such as writeBoolean(..) of ObjectOutputStream.)
    @Pointcut("call(* java.io.ObjectOutputStream+.write*(..))")
    public void objectOutputStreamWriteAll() {
    }

    @Pointcut("call(* java.io.ObjectOutputStream+.defaultWriteObject(..))")
    public void objectOutputStreamDefaultWriteObject() {
    }

    @Pointcut("call(* java.io.ObjectOutputStream+.replaceObject(..))")
    public void objectOutputStreamReplaceObject() {
    }

    //Handles all the write.. methods (such as writeBoolean(..) of DataOutputStream.)
    @Pointcut("call(* java.io.DataOutputStream+.write*(..))")
    public void dataOutputStreamWriteAll() {
    }

    @Pointcut("call(* java.io.PrintStream+.append(..))")
    public void printStreamAppend() {
    }

    @Pointcut("call(* java.io.PrintStream+.format(..))")
    public void printStreamFormat() {
    }

    @Pointcut("call(* java.io.PrintStream+.print(..))")
    public void printStreamPrint() {
    }

    @Pointcut("call(* java.io.PrintStream+.printf(..))")
    public void printStreamPrintf() {
    }

    @Pointcut("call(* java.io.PrintStream+.println(..))")
    public void printStreamPrintln() {
    }

    @Pointcut("call(* java.util.zip.ZipOutputStream+.setComment(..))")
    public void zipOutputStreamSetComment() {
    }


    //.. Would weave calls to the method, including ones designed in inner classes

    //Reminder, written values are not stored in Parcel object, get sent to native.
    //https://developer.android.com/reference/android/os/Parcel
    //Handles all write methods of Parcel class except writeNoException since see no need.
    //Also can handle Parcels inner class 'ReadWriteHelper' writeString method even though android prevents app developer from using it directly.
    //Parcel is for Parcelable, one of the two class's that allow sending "objects across" intents. Other is synchronized.
    @Pointcut("call(* android.os.Parcel..write*(..)) && !call(* android.os.Parcel..writeNoException(..))")
    public void parcelWriteAll() {
    }

    @Pointcut("call(* android.os.Parcel.unmarshall(..))")//todo do research because as of 2020 July, program is now crashing when use method. So maybe it can't be used to pass data?
    public void parcelUnmarshall() {
    }

    /*
    // Chose not to weave since seems can't store Parcel object in an intent or have it as an argument in exit method (excluding Parcel.write but its a targetObject there).
    // Android system can pass Parcel object to another app in some way, but seems app developer can't pass Parcel object themselves.
    // In other words, it seems app developer calling writeToParcel wouldn't lead to data sneaking by security checks since they can't pass Parcel object,
    // would be an unnecessary weaving.
    // If find example where app developer calling writeToParcel (not Parcel.write() ) could lead to data leak, then would weave method call.
    @Pointcut("call(* android.os.Parcelable.writeToParcel(..))")
    public void parcelableWrite(){}
     */

    //PendingIntent seems to not store the data from intent passed into these methods directly into the object, couldn't find them when analyzing object.
    // Couldn't access the methods used in these methods that handled intent. //todo figure out how to view the classes that you currently cannot access. Searching online didn't turn up anything initially.
    @Pointcut("call(* android.app.PendingIntent.getActivity(..)) || call(* android.app.PendingIntent.getActivities(..)) " +
            "|| call(* android.app.PendingIntent.getBroadcast(..)) || call(* android.app.PendingIntent.getForegroundService(..))" +
            "|| call(* android.app.PendingIntent.getService(..))")
    public void pendingIntentGetComponents() {
    }

    @Pointcut("call(* android.app.PendingIntent.send(..))")
    public void pendingIntentSend() {
    }

    @Pointcut("call(* android.content.IntentSender+.sendIntent(..))")
    public void intentSenderSendIntent() {
    }


    //Methods that relate to inserting/sending values to an Android ContentProvider (that will send them to data repository) by using ContentResolver
    @Pointcut("call(* android.content.ContentResolver+.insert(..))")
    public void contentResolverInsert() {
    }

    @Pointcut("call(* android.content.ContentResolver+.bulkInsert(..))")
    public void contentResolverBulkInsert() {
    }

    @Pointcut("call(* android.content.ContentResolver+.update(..))")
    public void contentResolverUpdate() {
    }

    @Pointcut("call(* android.content.ContentResolver+.applyBatch(..))")
    public void contentResolverApplyBatch() {
    }

    //Methods that relate to inserting/sending values to a data storage that is handled by current ContentProvider
    @Pointcut("call(* android.content.ContentProvider+.insert(..))")
    public void contentProviderInsert() {
    }

    @Pointcut("call(* android.content.ContentProvider+.bulkInsert(..))")
    public void contentProviderBulkInsert() {
    }

    @Pointcut("call(* android.content.ContentProvider+.update(..))")
    public void contentProviderUpdate() {
    }

    @Pointcut("call(* android.content.ContentProvider+.applyBatch(..))")
    public void contentProviderApplyBatch() {
    }

    //A variation of ContentProvider, but not a child of ContentProvider
    @Pointcut("call(* android.content.ContentProviderClient+.insert(..))")
    public void contentProviderClientInsert() {
    }

    @Pointcut("call(* android.content.ContentProviderClient+.bulkInsert(..))")
    public void contentProviderClientBulkInsert() {
    }

    @Pointcut("call(* android.content.ContentProviderClient+.update(..))")
    public void contentProviderClientUpdate() {
    }

    @Pointcut("call(* android.content.ContentProviderClient+.applyBatch(..))")
    public void contentProviderClientApplyBatch() {
    }

    //A Interface added in API 29. In API 29 this Interface is implemented by ContentProvider,ContentProviderClient, ContentResolver.
    @Pointcut("call(* android.content.ContentInterface+.insert(..))")
    public void contentInterfaceInsert() {
    }

    @Pointcut("call(* android.content.ContentInterface+.bulkInsert(..))")
    public void contentInterfaceBulkInsert() {
    }

    @Pointcut("call(* android.content.ContentInterface+.update(..))")
    public void contentInterfaceUpdate() {
    }

    @Pointcut("call(* android.content.ContentInterface+.applyBatch(..))")
    public void contentInterfaceApplyBatch() {
    }

    //A interface that a few class's that are variations of ContentProvider such as ContentProviderNative, implement.
    // Can't access it normally but possibility of creating class's that implement it.
    @Pointcut("call(* android.content.IContentProvider+.insert(..))")
    public void iContentProviderInsert() {
    }

    @Pointcut("call(* android.content.IContentProvider+.bulkInsert(..))")
    public void iContentProviderBulkInsert() {
    }

    @Pointcut("call(* android.content.IContentProvider+.update(..))")
    public void iContentProviderUpdate() {
    }

    @Pointcut("call(* android.content.IContentProvider+.applyBatch(..))")
    public void iContentProviderApplyBatch() {
    }


    //gets setResult,setResultCode,setResultData,setResultExtras
    @Pointcut("call(* android.content.BroadcastReceiver+.setResult*(..))")
    public void broadcastReceiverSetResults() {
    }

    @Pointcut("call(* android.content.BroadcastReceiver+.peekService(..))")
    public void broadcastReceiverPeekService() {
    }

    //gets setResult,setResultCode,setResultData,setResultExtras
    @Pointcut("call(* android.content.BroadcastReceiver.PendingResult+.setResult*(..))")
    public void broadcAstReceiverPendingResultSetResults() {
    }

    /*
    // Since Android 7.0, seems no reason to weave this method since can't share SharedPreferences with other apps directly like could
    // with other files. (file sharing).
    @Pointcut("call(* android.content.SharedPreferences.Editor+.put*(..))")
    public void sharedPreferencesEditorPutAll() {
    }
     */


    //StartChooser calls startActivity and starts a chooser intent.
    // While user does get to select (or even deny by exiting chooser) where the intent gets sent to.
    // They wouldn't know what data is being sent off.
    @Pointcut("call(* androidx.core.app.ShareCompat.IntentBuilder.startChooser(..))")
    public void shareCompatIntentBuilderStartChooser() {
    }


    //This method calls context.startActivity which is a method I am weaving. Though since can't weave Android libraries without creating my own version of the Android system,
    // I need to weave methods like these.
    @Pointcut("call(* androidx.core.app.TaskStackBuilder.startActivities(..)) || call(* android.app.TaskStackBuilder.startActivities(..))")
    public void taskStackBuilderStartActivities() {
    }
    //TaskStackBuilder is one of many classes that were copied to androidx libraries. Original was in Support library, could still be used.


    //Need to weave these methods since they are another way to write to a file and that file could be shared.
    @Pointcut("call(* android.util.JsonWriter.name(..))")
    public void jsonWriterName(){
    }

    @Pointcut("call(* android.util.JsonWriter.setIndent(..))")
    public void jsonWriterSetIndent(){
    }

    @Pointcut("call(* android.util.JsonWriter.value(..))")
    public void jsonWriterValue(){
    }

    @Pointcut("call(* android.os.Messenger.send(..))")
    public void messengerSend() {
    }


    //Don't want this projects own files getting weaved since would lead to infinite loop
    @Pointcut("!within(com.matthew.gradProject..*)")
    public void excludeOwnFilesFromWeaving() {
    }

    //Needed so when app developer creates their own listeners, don't want the code to be weaved since would lead into an infinite loop
    // if sensitive data was found.
    @Pointcut("!within(com.matthew.gradProject.SensitiveDataListener+)")
    public void excludeSDListeners() {
    }


    @Around("(contextBindIsolatedService() || contextBindService() || contextBindServiceAsUser() || contextSendBroadcast() || contextSendBroadcastAsUser() " +
            "|| contextSendOrderedBroadcast() || contextSendOrderedBroadcastAsUser() || contextStartActivities() " +
            "|| contextStartActivity() || contextStartForegroundService() || contextStartInstrumentation() " +
            "|| contextStartIntentSender() || contextStartService() || contextStopService() || contextStartActivityForResult() " +
            "|| activityCreatePendingResult() || activityDump() || activityNavigateUpTo() " +
            "|| activityNavigateUpToFromChild() || activitySetResult() || activityStartActivityFromChild() " +
            "|| activityStartActivityFromFragment() || activityStartActivityIfNeeded() || activityStartIntentSenderForResult() " +
            "|| activityStartIntentSenderFromChild() || activityStartNextMatchingActivity() || activityStartLocalVoiceInteraction() " +
            "|| fragmentActivityStartIntentSenderFromFragment() " +
            "|| pendingIntentGetComponents() || pendingIntentSend() " +
            "|| intentSenderSendIntent() " +
            "|| broadcastReceiverSetResults() || broadcastReceiverPeekService() || broadcAstReceiverPendingResultSetResults() " +
            "|| parcelWriteAll() || parcelUnmarshall() " +
            "|| contentResolverInsert() || contentResolverBulkInsert() || contentResolverUpdate() || contentResolverApplyBatch() " +
            "|| contentProviderInsert() || contentProviderBulkInsert() || contentProviderUpdate() || contentProviderApplyBatch() " +
            "|| contentProviderClientInsert() || contentProviderClientBulkInsert() || contentProviderClientUpdate() || contentProviderClientApplyBatch() " +
            "|| contentInterfaceInsert() || contentInterfaceBulkInsert() || contentInterfaceUpdate() || contentInterfaceApplyBatch() " +
            "|| iContentProviderInsert() || iContentProviderBulkInsert() || iContentProviderUpdate() || iContentProviderApplyBatch() " +
            "|| shareCompatIntentBuilderStartChooser()" +
            "|| writerWrite() || writerAppend() " +
            "|| printWriterFormat() || printWriterPrint() || printWriterPrintf() || printWriterPrintln() " +
            "|| charArrayWriterWriteTo() " +
            "|| outputStreamWrite() " +
            "|| byteArrayOutputStreamWriteTo() " +
            "|| objectOutputStreamWriteAll() || objectOutputStreamDefaultWriteObject() || objectOutputStreamReplaceObject() " +
            "|| dataOutputStreamWriteAll() " +
            "|| printStreamAppend() || printStreamFormat() || printStreamPrint() || printStreamPrintf() || printStreamPrintln() " +
            "|| zipOutputStreamSetComment() " +
            "|| taskStackBuilderStartActivities() " +
            "|| jsonWriterName() || jsonWriterSetIndent() || jsonWriterValue() " +
            "|| messengerSend()) " +
            "&& excludeOwnFilesFromWeaving() && excludeSDListeners()")
    public Object mainAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        return analyzerProcess.analyzationDecider(joinPoint);
    }
    //todo setup a second advice for methods where don't need to analyze target object.


    @Before("execution(* android.app.Activity+.onCreate(..))")
    public void setupContextReferenceActivity(JoinPoint joinPoint) {
        if (!gotContextReference) {
            if (joinPoint.getThis() instanceof Context) {
                Context context = (Context) joinPoint.getThis();
                DataStorage.createFile(context);//No need for app developer to create it.
                gotContextReference = true;
                SensitiveDataNotification.createNotificationChannel(context);

                analyzerProcess.setApplicationPackageName(context.getPackageName());
            }else{
                Log.i("ActivityAspects", "setupContextReferenceActivity: JoinPoint " + joinPoint + " geThis(): " + joinPoint.getThis()  + " is not an instance of Context");
            }
        }
    }

    @Before("execution(* android.content.BroadcastReceiver+.onReceive(..))")
    public void setupContextReferenceReceiver(JoinPoint joinPoint) {
        if (!gotContextReference) {
            //Testing revealed the context in arguments of onReceive was a context belonging to app with BroadcastReceiver being run, not the sender of broadcast event.
            Object[] methodArgs = joinPoint.getArgs();
            for (int index = 0; index < methodArgs.length; index++) {
                if (methodArgs[index] instanceof Context) {
                    Context context = (Context)methodArgs[index];
                    DataStorage.createFile(context);//No need for app developer to create it.
                    gotContextReference = true;
                    SensitiveDataNotification.createNotificationChannel(context);

                    analyzerProcess.setApplicationPackageName(context.getPackageName());
                    break;
                }
            }
        }
    }

    @Before("execution(* android.app.Service+.onCreate(..))")
    public void setupContextReferenceService(JoinPoint joinPoint) {
        if (!gotContextReference) {
            if (joinPoint.getThis() instanceof Context) {
                Context context = (Context) joinPoint.getThis();
                DataStorage.createFile(context);//No need for app developer to create it.
                gotContextReference = true;
                SensitiveDataNotification.createNotificationChannel(context);

                analyzerProcess.setApplicationPackageName(context.getPackageName());
            }else{
                Log.i("ActivityAspects", "setupContextReferenceService: JoinPoint " + joinPoint + " geThis(): " + joinPoint.getThis()  + " is not an instance of Context");
            }
        }
    }

    //Since it implements its own onCreate, and found another app subclassing it.
    @Before("execution(* android.app.Application+.onCreate(..))")
    public void setupContextReferenceApplication(JoinPoint joinPoint) {
        if (!gotContextReference) {
            if (joinPoint.getThis() instanceof Context) {
                Context context = (Context) joinPoint.getThis();
                DataStorage.createFile(context);//No need for app developer to create it.
                gotContextReference = true;
                SensitiveDataNotification.createNotificationChannel(context);

                analyzerProcess.setApplicationPackageName(context.getPackageName());
            }else{
                Log.i("ActivityAspects", "setupContextReferenceApplication: JoinPoint " + joinPoint + " geThis(): " + joinPoint.getThis()  + " is not an instance of Context");
            }
        }
    }
}


