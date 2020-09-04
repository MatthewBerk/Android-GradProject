package com.matthew.gradProject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.*;


class AnalyzerProcess {

    private String applicationPackageName;

    public AnalyzerProcess() {
    }

    public void setApplicationPackageName(String name) {
        applicationPackageName = name;
    }

    //todo Setup check for whether to analyze the object method was called on (Target object) based on the value passed from the advices.
    // Should provide speed improvements.

    Object analyzationDecider(ProceedingJoinPoint joinPoint) throws Throwable {
        if (ProjectSettings.isThreadCode()) {//Was an idea I kept from old project design since may be useful.
            //begin threading
            MyRunnable myRunnable = new MyRunnable(joinPoint);
            new Thread(myRunnable).start();
        } else {
            return beginAnalyzation(joinPoint);
        }
        return null;//If thread, have to return null so don't have situation of holding up UI thread.
        // Also return null if was decided not to let join point proceed.
    }

    Object beginAnalyzation(ProceedingJoinPoint joinPoint) throws Throwable {

        //Get arguments of join point
        Object[] variousArgs = joinPoint.getArgs();
        Object[] allJoinPointArgs = new Object[variousArgs.length + 1];
        System.arraycopy(variousArgs, 0, allJoinPointArgs, 0, variousArgs.length);
        if (joinPoint.getTarget() != null) {//Would be false for static method calls.
            // todo Research if need to grab class for static methods. Android does not include class variables when serializing/parcing and object when about to send it over IPC.
            //  Need to see if there is any exit point methods that are static and use class variables that the app developer could give values to!
            //  To get class reference of class static method is from: joinPoint.getStaticPart().getSignature().getDeclaringType()
            //   Test to see if can pass it to DeconstructorMap to get all the class variables.
            allJoinPointArgs[allJoinPointArgs.length - 1] = joinPoint.getTarget();//get object method was called on.
        }


        //This inner explicit intent feature is still a work in progress. Since Intent is parcelable, could use it in a variety of exit points, not just
        // for launching a component. So plan to rework either advice and/or this code, so I only perform this inner explicit intent check on methods
        // involved just for launching a component. This explicit intent feature was planned to be implemented in future but testing project on real world
        // apps revealed need for this in order for proper testing. So implemented a early version of it.
        // The issue with it currently is it may allow certain methods to skip analysis when it shouldn't have (such as for Activity.setResult if didn't have
        // check in place!)
        // setResult will need a different kind of checker, where examine the intent the activity component received (since will indicate where data in setResult will get sent to)
        if (!(joinPoint.getSignature().getName().equals("setResult")) && joinPoint.getTarget() instanceof Context) {
            if (applicationPackageName != null) {//False if entry point used is one I am not handling. //todo (Currently just Content Providers as far as I am aware).
                int canPass = 0;//Only let the join point skip check if it has an Intent(s) and they are all explicit intents for components within application
                // 0: no intents so far.  1: Intents found and they are internal explicit intents. 2: Intents found and at least one is implicit or an external explicit intent
                for (Object value : variousArgs) {

                    if(value == null){
                        continue;
                    }

                    if (value instanceof Intent) {
                        Intent intent = (Intent) value;
                        ComponentName componentName = intent.getComponent();//get destination component
                        if (componentName != null) {//true for implicit intents
                            String packageName = componentName.getPackageName();//get the application package name component is in (the application package, what would be name of apps file system)
                            if (packageName.equals(applicationPackageName)) {
                                if (canPass == 0) {
                                    canPass = 1;
                                }
                            } else {//Is an explicit intent whose destination is an external component, or non existing one.
                                canPass = 2;
                            }
                        } else {//Is an implicit intent
                            canPass = 2;
                        }
                    } else if (value.getClass().isArray()) {//Possibly an array of intents such as from Context.startActivities(Intent[] intents)
                        Class arrayType = value.getClass().getComponentType();
                        if (arrayType.equals(Intent.class)) {//Found no exit point method so far that used 2D+ array for Intent, so no reason to handle multidimensional arrays.
                            Intent[] intents = (Intent[]) value;
                            for (Intent intentValue : intents) {
                                ComponentName componentName = intentValue.getComponent();//get destination component
                                if (componentName != null) {//true for implicit intents
                                    String packageName = componentName.getPackageName();//get the application package name component is in (the application package, what would be name of apps file system)
                                    if (packageName.equals(applicationPackageName)) {
                                        if (canPass == 0) {
                                            canPass = 1;
                                        }
                                    } else {//Is an explicit intent whose destination is an external component, or non existing one.
                                        canPass = 2;
                                    }
                                } else {// is an implicit intent
                                    canPass = 2;
                                }
                            }
                        }
                    }
                }
                if (canPass == 1) { //If intent's destination is within the app, no reason to analyze arguments since app is just passing data to an inner component, not externally.
                    return joinPoint.proceed();//Explicit intents destination is within app. No reason to check for sensitive data.
                }
            }
        }

        Map<String, List<String>> sensitiveData = DataStorage.getJsonAsMap();//<Sensitive data, JSON key name path (i.e. what would need to traverse JSON file to get to data) >.

        //True if sensitive data file is empty.
        if (sensitiveData == null) {
            Log.i("AnalyzerProcess", "Sensitive data file is empty, so no reason to do analyzation");
            return joinPoint.proceed();
        }

        //Get details of the join point for the possible data leak event.
        SensitiveDataMatch sensitiveDataMatch = new SensitiveDataMatch(joinPoint.getSignature().getName(), joinPoint.getSourceLocation().getFileName(), joinPoint.getSourceLocation().getLine());

        for (Object aValue : allJoinPointArgs) {
            DeconstructorMap temp = new DeconstructorMap();
            Map<String, Object> variousValues = temp.beginDeconstruction(aValue);//Extract primitives and strings from aValue.
            OffendingArgument offendingArgument = DataComparator.checkForSensitiveData(variousValues, sensitiveData, aValue);//todo clean sensitiveData before this loop. Save time by not repeatedly cleaning!

            if (offendingArgument != null) {
                sensitiveDataMatch.addOffendingArgument(offendingArgument);
            }
        }

        boolean shouldProceed = true;//in case there are no listeners, we let the join point execute.
        // todo maybe force notification listener if 0 listeners registered! Keep user informed. Sort of do that at component entry points, but maybe move it here?

        //Only send event if sensitive data was found.
        if (!sensitiveDataMatch.isOffendingArgumentListEmpty()) {
            shouldProceed = ListenerRegistry.handle(sensitiveDataMatch, joinPoint);
        }

        if (shouldProceed) {
            return joinPoint.proceed();
        } else {
            Log.i("AnalyzerProcess", "We did not proceed");
            return null;
        }
    }

    private class MyRunnable implements Runnable {

        private ProceedingJoinPoint joinPoint;

        public MyRunnable(ProceedingJoinPoint aJoinPoint) {
            joinPoint = aJoinPoint;
        }

        @Override
        public void run() {
            try {
                beginAnalyzation(joinPoint);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }
    }
}
