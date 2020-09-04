package com.matthew.gradProject;

import android.app.Activity;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import java.io.*;
import java.util.*;

// Sample Logging sensitive data leak listener.
//Should only make one of these listeners since will log all sensitive data match occurrences, even ones in different directories.
public class LogSensitiveDataMatchOccurrences implements SensitiveDataLoggingListener {
    private File logFile;
    public final String fileName;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");

    public LogSensitiveDataMatchOccurrences(Activity activityFocus, String fileName, boolean wipeFileIfAlreadyPresent) throws IOException {
        this.fileName = fileName;
        logFile = new File(activityFocus.getFilesDir(), fileName);
        boolean fileCreated = logFile.createNewFile();

        if(!fileCreated){//file with that name already exist.
             Log.i("LogSensitiveDataMatchOccurrences","The listeners logging file already exist. " + fileName);
             if(wipeFileIfAlreadyPresent){
                 Log.i("LogSensitiveDataMatchOccurrences","Wiping file contents in constructor.");
                 new PrintWriter(logFile).close();
             }
        }
    }

    //Because using HashSet in ListenerRegistry and want to prevent duplicate listeners.
    @Override
    public boolean equals(Object val) {
        if (val == this) {
            return true;
        }
        if (!(val instanceof LogSensitiveDataMatchOccurrences)) {
            return false;
        }else{
            LogSensitiveDataMatchOccurrences temp = (LogSensitiveDataMatchOccurrences) val;
            return this.fileName.equals(temp.fileName);
        }
    }

    //Because using HashSet in ListenerRegistry and want to prevent duplicate listeners.
    @Override
    public int hashCode()
    {
        return fileName.length();
    }

    @Override
    public boolean onFoundData(SensitiveDataMatch sensitiveDataMatch) {
        List<String> dataLog = designLog(sensitiveDataMatch);

        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(logFile,true));
            //Used FileWriter so could do append (write to end of file) and still use PrintWriter's other methods besides append.

            for(String data : dataLog){
                printWriter.println(data);
            }

            printWriter.println();
            printWriter.println();
            printWriter.flush();
            printWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    //Formats the sensitive data leak details so can be written to file.
    List<String> designLog(SensitiveDataMatch sensitiveDataMatch){
        List<String> logInfo = new ArrayList<>();

        logInfo.add(simpleDateFormat.format(new Date()));

        logInfo.add("Sensitive data was found in method call " + sensitiveDataMatch.getMethodName() + " at");
        logInfo.add("File Name:    " + sensitiveDataMatch.getFileName());
        logInfo.add("Line Number:  " + sensitiveDataMatch.getLineNumber());

        List<OffendingArgument> offendingArgList = sensitiveDataMatch.getOffendingArgumentList();
        for(OffendingArgument offendingArgument : offendingArgList){
            logInfo.add("Sensitive data was found in this argument value: " + offendingArgument.getArgumentValue());
            List<OffendingData> offendingDataList = offendingArgument.getDataMatches();

            for(OffendingData offendingData : offendingDataList){
                logInfo.add("  Value in argument that contains sensitive data is: " + offendingData.getValue());
                logInfo.add("     The sensitive data found in value is:");
                Map<String,List<String>> sensitiveDataAndCategory = offendingData.getSensitiveDataThatWasFound();

                Set<String> sensitiveData = sensitiveDataAndCategory.keySet();

                for(String data : sensitiveData){
                    logInfo.add("      " + data + " | JSON data path : " + sensitiveDataAndCategory.get(data));
                }
            }
        }
        return logInfo;
    }
}
