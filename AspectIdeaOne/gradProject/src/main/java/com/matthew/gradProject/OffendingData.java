package com.matthew.gradProject;

import java.util.*;


// Class to hold details of the data leak, specifically the variable value that was in an argument of join point being analyzed, and all the sensitive data that was
// found in it.
// NOTE false positives can happen, such as if had a small number such as 2903 marked as sensitive data, and variable value in an arg was like 21234320002903
// This can't be fully avoided since trying to handle cases where sensitive data like users contact numbers could be clumped together.
public final class OffendingData {
    private String valueInArg;
    private Map<String,List<String>> sensitiveDataThatWasFound;

    public OffendingData(String valueOfObject){
        valueInArg = valueOfObject;
        sensitiveDataThatWasFound = new HashMap<>();
    }

    //JsonDataPath is the key/array index path to get to 'value' in current JSON file
    public void addSensitiveData(String value,List<String> jsonDataPath){
        sensitiveDataThatWasFound.put(value,jsonDataPath);
    }

    public String getValue() {
        return valueInArg;
    }

    //JsonDataPath is the key/array index path to get to 'value' in current JSON file
    public Map<String, List<String>> getSensitiveDataThatWasFound() {
        return sensitiveDataThatWasFound;
    }
}
