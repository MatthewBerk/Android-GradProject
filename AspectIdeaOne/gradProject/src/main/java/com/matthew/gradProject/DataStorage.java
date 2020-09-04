package com.matthew.gradProject;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.util.*;

//Should not be subclassed.
public final class DataStorage {

    private static final String fileName = "SensitiveData";
    private static File dataFile;
    static String filePath;//If I have file path to file, don't need to save context reference since my project is allowed to access apps internal storage since it is part of the app.

    //Creates the SensitiveData file if doesn't exist and saves the file path so don't need to persist context reference.
    public static boolean createFile(Context context)  {
        try {
            filePath = context.getFilesDir().toString();
            dataFile = new File(filePath, fileName);
            return dataFile.createNewFile();//Not going to wipe contents
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getFileName(){
        return fileName;
    }

    public static void assignFilePath(String path){
        filePath = path;
    }

    //Method app developer could use if they want to wipe current sensitive data file and write new content in.
    //They can edit the file without needing to utilize class since it's stored in their apps internal storage.
    public static synchronized void overwriteSensitiveDataFile(File jsonFile) throws IOException, JSONException {
        FileReader fileReader = new FileReader(jsonFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder jsonFileContents = new StringBuilder();
        String fileLine;
        while ((fileLine = bufferedReader.readLine()) != null) {
            jsonFileContents.append(fileLine);
        }
        bufferedReader.close();
        fileReader.close();
        overwriteSensitiveDataFile(jsonFileContents.toString());
    }

    //Method app developer could use if they want to wipe current sensitive data file and write new content in.
    //They can edit the file without needing to utilize class since it's stored in their apps internal storage.
    public static synchronized void overwriteSensitiveDataFile(InputStream inputStream) throws IOException, JSONException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder jsonFileContents = new StringBuilder();
        String fileLine;
        while ((fileLine = bufferedReader.readLine()) != null) {
            jsonFileContents.append(fileLine);
        }
        bufferedReader.close();
        overwriteSensitiveDataFile(jsonFileContents.toString());
    }

    //Method app developer could use if they want to wipe current sensitive data file and write new content in.
    //They can edit the file without needing to utilize class since its stored in their apps internal storage.
    public static synchronized void overwriteSensitiveDataFile(String jsonString) throws JSONException, IOException {
        JSONObject jSONObject;
        if (jsonString.equals("")) {//File Is empty
            jSONObject = new JSONObject();
        }else{
            jSONObject = new JSONObject(jsonString);
        }
        overwriteSensitiveDataFile(jSONObject);
    }

    //Method app developer could use if they want to wipe current sensitive data file and write new content in.
    //They can edit the file without needing to utilize class since its stored in their apps internal storage.
    public static synchronized void overwriteSensitiveDataFile(JSONObject jsonObject) throws IOException, JSONException {
        if (dataFile == null) {
            dataFile = new File(filePath, fileName);
            dataFile.createNewFile();//Not going to wipe contents
        }
        FileWriter fileWriter = new FileWriter(dataFile, false);
        fileWriter.write(jsonObject.toString(4));
        fileWriter.flush();
        fileWriter.close();
    }

    //Method returns the string representation of the sensitive data json file.
    public static synchronized String getSensitiveDataFileContentsString() throws IOException {
        if(filePath == null){
            //Filepath wasn't initialized, this is due to either entering a content provider or an entrance to one of
            // the other components wasn't weaved, usually due to dynamically loaded libraries.
            return null;
        }

        if (dataFile == null) {
            dataFile = new File(filePath, fileName);
            dataFile.createNewFile();//Not going to wipe contents
        }

        FileReader fileReader = new FileReader(dataFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        StringBuilder jsonFileContents = new StringBuilder();
        String fileLine;
        while ((fileLine = bufferedReader.readLine()) != null) {
            jsonFileContents.append(fileLine);
        }
        bufferedReader.close();
        fileReader.close();
        return jsonFileContents.toString();
    }

    //Method to update a specific data group in JSON file. Note, requires JSON file to be formatted like shown in documentation
    // for it to properly update.
    // It just overwrites value in dataGroup if exist, does not compare and do updates that way. Avoids retaining outdated sensitive data.
    public static synchronized void updateDataGroup(JSONArray jsonArray,String dataGroup) throws IOException, JSONException {
        if(filePath == null){
            //Filepath wasn't initialized, this is due to either entering a content provider or an entrance to one of
            // the other components wasn't weaved, usually due to being dynamically loaded.
            return;
        }

        String jsonFileContents = getSensitiveDataFileContentsString();

        JSONObject fileJSONObject;
        if (jsonFileContents.equals("")) {//File Is empty
            fileJSONObject = new JSONObject();

        }else{//We update Contacts key value. Just overwriting and not comparing.
            fileJSONObject = new JSONObject(jsonFileContents);
        }

        fileJSONObject.put(dataGroup,jsonArray);
        FileWriter fileWriter = new FileWriter(dataFile, false);
        fileWriter.write(fileJSONObject.toString(4));
        fileWriter.flush();
        fileWriter.close();
    }

    //Returns the Map representation of the sensitive data JSON file, where key is the value and the map value is a list that represents 'key name'-'index path' would need
    // to use to get to the value in JSON file.
    public static Map<String, List<String>> getJsonAsMap() throws JSONException, IOException {
        String jsonFileContents = getSensitiveDataFileContentsString();
        if(jsonFileContents == null){
            //Filepath wasn't initialized, this is due to either entering a content provider or an entrance to one of
            // the other components that wasn't weaved, usually due to being dynamically loaded.
            return null;
        }

        if (jsonFileContents.equals("")) {
            Log.w("DataStorage", "Sensitive data file is empty");
            return null;
        }
        HashMap<String, List<String>> mapOfData = new HashMap<>();
        List<String> aPath = new ArrayList<>();
        JSONObject mainJSONObject = new JSONObject(jsonFileContents);
        determineObjectType(mainJSONObject, mapOfData, aPath);

        return mapOfData;
    }

    private static void determineObjectType(Object value1, HashMap<String, List<String>> mapOfData, List<String> path) {
        if (value1 instanceof JSONObject) {
            aJsonObject((JSONObject) value1, mapOfData, path);
        } else if (value1 instanceof JSONArray) {
            aJsonArray((JSONArray) value1, mapOfData, path);
        } else if (value1 instanceof Number) {
            Number temp = (Number) value1;
            mapOfData.put(temp.toString(), path);
        } else if (value1 instanceof String) {
            mapOfData.put((String) value1, path);
        } else {
            //error
            Log.w("DataStorage", "ERROR");
        }
    }

    //Iterates through JSONObject.
    private static void aJsonObject(JSONObject value1, HashMap<String, List<String>> mapOfData, List<String> path) {
        Iterator<?> keys = value1.keys();
        while (keys.hasNext()) {
            List<String> newPath = new ArrayList<>(path);
            String keyName = (String) keys.next();
            newPath.add(keyName);
            Object object = value1.opt(keyName);
            determineObjectType(object, mapOfData, newPath);
        }
    }

    //Iterates through JSONArray
    private static void aJsonArray(JSONArray value1, HashMap<String, List<String>> mapOfData, List<String> path) {
        for (int index = 0; index < value1.length(); index++) {
            List<String> newPath = new ArrayList<>(path);
            newPath.add(index + "");
            Object object = value1.opt(index);
            determineObjectType(object, mapOfData, newPath);
        }
    }


    // Method that App developer could use if they consider user input as sensitive data.
    // They don't have to use it since they have access to sensitive data file since its in their own internal storage.
    public static synchronized void userInputAsSensitiveData(String input, String dataGroupName) throws IOException, JSONException {
        if(filePath == null){
            //Filepath wasn't initialized, this is due to either entering a content provider or an entrance to one of
            // the other components wasn't weaved, usually due to being dynamically loaded.
            return;
        }
        String jsonFileContents = getSensitiveDataFileContentsString();

        JSONObject mainJSONObject;

        if (jsonFileContents.equals("")) {
            mainJSONObject = new JSONObject();
        } else {
            mainJSONObject = new JSONObject(jsonFileContents);
        }

        JSONArray usersInputs = mainJSONObject.optJSONArray(dataGroupName);
        if (usersInputs == null) {//Means that category doesn't exist. yet
            usersInputs = new JSONArray();
            usersInputs.put(input);
            mainJSONObject.put(dataGroupName, usersInputs);
        } else {
            usersInputs.put(input);
        }

        FileWriter fileWriter = new FileWriter(dataFile, false);
        fileWriter.write(mainJSONObject.toString(4));
        fileWriter.flush();
        fileWriter.close();
    }
}
