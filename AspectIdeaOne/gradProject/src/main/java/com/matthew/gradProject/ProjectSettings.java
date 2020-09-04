package com.matthew.gradProject;


import android.content.Context;
import android.content.res.Resources;
import java.io.*;
import java.util.Properties;

//Originally designed project to have several project settings but as I developed project and did experiments, found there are not
// many worthwhile settings to implement. ThreadCode provides a possibility of being useful. Future iterations of project may see more project settings so keeping class
// just in case.
public final class ProjectSettings {

    //default values
    private static boolean threadCode = false;
    private static boolean notificationReturn = true;

    public static void specifySettings(File settingsFile) throws IOException {
        InputStream inputStream = new FileInputStream(settingsFile);
        Properties projectProperties = new Properties();
        projectProperties.load(inputStream);
        inputStream.close();
        setupSettings(projectProperties);
    }
    public static void specifySettings(InputStream inputStream) throws IOException {
        Properties projectProperties = new Properties();
        projectProperties.load(inputStream);
        inputStream.close();
        setupSettings(projectProperties);
    }
    public static void specifySettings(FileReader fileReader) throws IOException {
        Properties projectProperties = new Properties();
        projectProperties.load(fileReader);
        fileReader.close();
        setupSettings(projectProperties);
    }
    public static void specifySettings(Context context, int resourceId) throws IOException {
        Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(resourceId);
        Properties projectProperties = new Properties();
        projectProperties.load(inputStream);
        inputStream.close();
        setupSettings(projectProperties);
    }
    public static void specifySettings(Properties properties){
        Properties projectProperties = properties;
        setupSettings(projectProperties);
    }


    private static synchronized void setupSettings(Properties projectProperties){
        threadCode = Boolean.parseBoolean(projectProperties.getProperty("ThreadCode","true"));//Any other value besides true will make variable false!
        notificationReturn = Boolean.parseBoolean(projectProperties.getProperty("DefaultNotificationReturns","true"));
    }

    public static synchronized boolean isThreadCode() {
        return threadCode;
    }

    public static synchronized boolean isNotificationReturn(){return notificationReturn;}
}
