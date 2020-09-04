# Currently is at version 1.0. 

The actual project can be found in AspectIdeaOne. It is the gradProject module.

## What is this project?
This is an Android data leakage security project I developed as part of my master’s at SFSU. This project serves to improve an Android app’s data security by notifying the user when the app is leaking sensitive data to external sources. Sensitive data is determined by the app developer and the device owner. At its current stage, it serves as an app developer tool to help highlight and prevent sensitive data leaks within their app. This project uses AspectJ to weave security checks at compile time on calls to the methods involved with sending data outside of an app’s environment. The security checks involve launching an event if any sensitive data is found within the arguments of the method call at runtime. The provided event listeners log the event to a file in the apps private local storage and notifies the device owner of the event through a notification.
 
[Read paper for more details]

Project uses https://github.com/Archinamon/android-gradle-aspectj to allow AspectJ weaving in Android build.

## How to use project
Download the ‘gradProject-release.aar’ and store it in a folder in the Android app. 
Then add this line of code to the dependencies of top build.gradle file
```
classpath "com.archinamon:android-gradle-aspectj:3.4.0"
```

Add these lines of code to build.gradle file of module you want weaved.
```
apply plugin: 'com.archinamon.aspectj'
implementation fileTree(dir: 'libs',include: ['*.aar'])
```
^ should point to wherever you have gradProject-release.aar stored.
```
aspectj{
    includeAspectsFromJar 'gradProject'
    debugInfo true
    ajcArgs << '-referenceInfo' << '-warn:deprecation' << '-verbose'
}
```

Android projects minSdkVersion must be 26, while targetSdkversion is 29.
Gradle 5.6 is used.

## Notes
Project needs to be updated to use the latest Gradle version and the latest Archinamon plugin.

Additional exit points (method calls that can lead to sensitive data leaving the apps environment) need to be identified and weaved by creating pointcuts.

Was tested on a physical Android 8.0 device. Needs to be tested on other physical devices.

Sensitive Images must have their byte array be Base64 encoded to a string in order to be considered sensitive data. Same applies to any byte arrays, since project does not expect byte arrays in sensitive data JSON file.

[Read paper for other improvements]
