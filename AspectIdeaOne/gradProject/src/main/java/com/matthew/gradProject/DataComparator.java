package com.matthew.gradProject;

import android.util.Log;
import java.util.*;

class DataComparator {

    // Iterates through key values in argDataMap to see if any of them contain a value that appears in sensitiveData map keys.
    // I chose to compare by strings because wanted to handle cases like a contacts full name being sent, while Android has each part of a name as a separate value.
    static OffendingArgument checkForSensitiveData(Map<String, Object> argDataMap, Map<String, List<String>> sensitiveDataMap, Object arg) {

        if ( sensitiveDataMap.isEmpty()) {//If no sensitiveData, then no reason to run code.
            Log.i("DataComparator", "At checkForSensitiveData and sensitiveData is empty");
            return null;
        }

        Set<String> argDataMapKeys = argDataMap.keySet();//In DeconstructorMap, we store value as the key.
        // The map value is just used as indicator for if encoding happened, is from a Bitmap, or neither.

        OffendingArgument offendingArgument = new OffendingArgument(arg);//Setting up for possible data leak event.

        Map<String, String> cleanedSensitiveDataMap = new HashMap<String, String>();//<cleanedData,original version of data>
        // Since need to clean the data to make comparing via strings possible, and want original so can record it in data leak event.

        Set<String> sensitiveDataKeys =  sensitiveDataMap.keySet();//The keys are the sensitive data while the object is
        // JSON key name path (i.e. what would need to traverse JSON file to get to data)


        //Cleaning value so can compare by strings.
        for (String sensitiveDataKey : sensitiveDataKeys) {
            String cleanedData = sensitiveDataKey.replaceAll("\\s", "");//removes all spaces
            cleanedData = cleanedData.toLowerCase();

            cleanedSensitiveDataMap.put(cleanedData, sensitiveDataKey);
        }

        Set<String> cleanSensitiveDataKeys = cleanedSensitiveDataMap.keySet();

        for (String argDatakey :  argDataMapKeys) {
            String cleanedKey = argDatakey.replaceAll("\\s", "");//removes all spaces
            cleanedKey = cleanedKey.toLowerCase();

            OffendingData offendingData = new OffendingData(argDatakey);

            for (String cleanedSensitiveData : cleanSensitiveDataKeys) {

                if(cleanedSensitiveData.isEmpty()){//For if "" was considered sensitive data, encountered this with a contact entry where "" was as an actual value.
                    continue;
                }

                //For encoded values, usually Base64 encoded strings of images or Base64 encoded strings of byte arrays from DeconstructorMap
                // , need to do equals otherwise could lead to false positives such as finding CEO in string.
                if(argDataMap.get(argDatakey) instanceof String){//if value of key,value mapping is a string, means value is special (such as encoded string or bitmap)
                    String temp = argDataMap.get(argDatakey).toString();
                    if(temp.equals("IsBitmap") || temp.equals("IsBase64Encoded")){
                        String theSensitiveData = cleanedSensitiveDataMap.get(cleanedSensitiveData);
                        List<String> thePath =  sensitiveDataMap.get(theSensitiveData);
                        for (String s : thePath) {
                            if (s.contains("Encoded")) {
                                if (argDatakey.equals(theSensitiveData)) {//Don't want to compare by clean keys since its Base64 encoded strings
                                    offendingData.addSensitiveData(theSensitiveData, sensitiveDataMap.get(theSensitiveData));
                                }
                                break;
                            }
                        }
                    }
                }
                else if (cleanedKey.contains(cleanedSensitiveData)) {//Could have false positives but no way to tell at runtime.
                    String uncleanSensitiveData = cleanedSensitiveDataMap.get(cleanedSensitiveData);
                    offendingData.addSensitiveData(uncleanSensitiveData,  sensitiveDataMap.get(uncleanSensitiveData));
                }
            }

            //Have a finished offendingData object.
            if (!offendingData.getSensitiveDataThatWasFound().isEmpty()) {
                offendingArgument.addSensitiveData(offendingData);
            }
        }

        if (offendingArgument.getDataMatches().isEmpty()) {//argument had no sensitive data so no reason to report it.
            return null;
        }
        return offendingArgument;
    }
}
