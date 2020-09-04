package com.matthew.gradProject;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;


 final class DeconstructorMap {
    private Map<Object, Object> objectsLookedAt;
    private List<Object> objectsToAnalyze;
    private Map<String, Object> primAndStringValues;
    private  Object blank = new Object();


    // Sets up the Maps and List needed for when deconstructing a value. Don't retain records between values, even if the values are from same join point.
    public Map<String, Object> beginDeconstruction(Object value) {
        objectsLookedAt = new IdentityHashMap<Object, Object>();//Don't want to use class's own equals method, only want to compare by the Hash value.
        //Encountered some classes that override equals and just threw an exception which caused program to crash.
        objectsToAnalyze = new ArrayList<Object>();
        primAndStringValues = new HashMap<String, Object>();//Holds all unique primitives and String values found in the object (except booleans), or interpreted from object (i.e. Base64 encoding, Bitmap)

        //The reason for this method setup is may have a different analyzation method for Cursor objects when setup handling for content providers.
        analyzeObject(value);

        return primAndStringValues;
    }

     //Navigates through the object and breaks it down to its primitives and strings via reflection or grab the important values when doing a specific class handler approach (such as Bitmaps).
     // Grabs every primitive and string value contained in the object, which includes values from parents of object.
     // If object has a class/instance variable that is an object, that object will be broken down to its primitives and strings also.
     void analyzeObject(Object value) {
        objectsToAnalyze.add(value);
        Object aHolder;
        while (!objectsToAnalyze.isEmpty()) {

            aHolder = objectsToAnalyze.remove(objectsToAnalyze.size() - 1);

            if (aHolder == null) {
                Log.i("DeconstructorMap", "Got a null aHolder");
                continue;
            }

            //Don't want to re-examine an object we already looked at it since otherwise we enter an infinite loop if object has a reference of itself
            if (objectsLookedAt.containsKey(aHolder)) {//is needed because for reflection, I could end up adding several of the same value to objectsToAnalyze
                continue;//already investigated object
            } else {
                objectsLookedAt.put(aHolder, new Object());
            }


           if (isWrapperType(aHolder.getClass())) {//is true for primitives, false for primitive arrays (i.e. int[] )
                if (!(aHolder instanceof Boolean)) { //want to scrap boolean since wouldn't hold sensitive data
                    primAndStringValues.put(String.valueOf(aHolder), blank);
                }
            } else if (aHolder instanceof String) {
                String aString = String.valueOf(aHolder);
                byte[] bytes = aString.getBytes();
                //two byte arrays created from same string would produce two different identity hash codes. So no reason to check objectsLookedAt
                objectsToAnalyze.add(bytes);// May be a Base64 encoded string, handling encodings in the analyzeArrays section. Do convert byte arrays to strings
                // and store them in primAndStringValues map, so this string value will still be grabbed.
            } else if (aHolder.getClass().isArray()) {
                analyzeArrays(aHolder);
            } else if(aHolder instanceof Uri) {
                Uri temp = (Uri) aHolder;
                primAndStringValues.put(temp.toString(), blank);//Only care about the path that the Uri object represents.
            }else if(aHolder instanceof Bitmap){//Need to extract byte array of the image represented by Bitmap object.
                Bitmap temp = (Bitmap) aHolder;
                if(temp.isRecycled()){
                    continue;//if try compressing a recycle bitmap, program crashes.
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                temp.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] bytes = stream.toByteArray();
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String encodedString = Base64.encodeToString(bytes,Base64.DEFAULT);
                primAndStringValues.put(encodedString, "IsBitmap");

            } else {//Is an object without a special handler.
                List<Field> aList = analyzeAllParents(aHolder.getClass());

                //Perform reflection to get class fields value.
                for (Field aField : aList) {
                    try {
                        aField.setAccessible(true);
                        Object fieldsValue = aField.get(aHolder);

                        if (fieldsValue != null) {
                            if (objectsLookedAt.containsKey(fieldsValue)) {

                            } else {
                                objectsToAnalyze.add(fieldsValue);
                            }
                        }

                    } catch (Exception e) {//On rare occasion, end up trying to access a variable that is being garbage collected, would crash app, only seen it a small handful
                        // of times, each one from analyze activity object.
                        Log.w("DeconstructorMap", "We encountered an illegalAccessException");
                        e.printStackTrace();

                    }
                }

            }
        }
    }

    //Gets a list of all fields (class and instance variables) of the object and its parents, up to the Object class.
    public List<Field> analyzeAllParents(Class<?> aValue) {
        List<Field> objectsFields = new ArrayList<Field>(Arrays.asList(aValue.getDeclaredFields()));
        Class<?> parentClass = aValue.getSuperclass();

        while (parentClass != null) {
            objectsFields.addAll(new ArrayList<Field>(Arrays.asList(parentClass.getDeclaredFields())));
            parentClass = parentClass.getSuperclass();
        }

        return objectsFields;
    }


    //Since arrays are special, need a specific approach for analyzing an array (no matter size or dimensions)
    //Extracts (or interprets if byte array) values in the array, regardless of size/dimensions of the array.
    public void analyzeArrays(Object value) {
        if (value == null) {
            Log.w("DeconstructorMap", "got a null in array method");
            return;
        }

        if (value.getClass().isArray()) {//doesn't matter size of the array, will grab value at each index and explore it (means also handles multi dimensional arrays)
            int sizeOfArray = Array.getLength(value);
            Class arrayType = value.getClass().getComponentType();//note: for a 2D byte array, value would be "class [B" instead of just byte like for 1D byte array
            if (arrayType.equals(byte.class)) {//Specify in document that can't consider a byte array sensitive data, need to Base64 encode it to a string.
                byte[] bytes = (byte[]) value;

                //Handling the strings that were transformed into bytes in the string category. Cases where sensitive data is
                // sent out in its byte form, such as for OutputStream.write which only accepts bytes.
                String plainString = new String(bytes);
                primAndStringValues.put(plainString, blank);
                objectsLookedAt.put(plainString, new Object());


                //Base64 encode the byte array to a string. And store it.
                 //is for handling the passing of sensitive byte arrays, such as images. The project requires sensitive byte arrays/images
                // to be Base64 encoded to strings before being stored in the sensitive data file. So, the byte array
                // is Base64 encoded and stored in primAndStringValues with the map-value ‘IsBase64Encoded’ to indicate the value
                // should be handled a special way during sensitive data checking.
                String encodedString = Base64.encodeToString(bytes,Base64.DEFAULT);//todo Implement other popular encoders in the future.
                primAndStringValues.put(encodedString, "IsBase64Encoded");
                objectsLookedAt.put(encodedString, new Object());

                //Base64 decode the byte array then map them to String via default character set [ignoring other character sets]
                // Using 'try catch' to determine whether byte array had the possibility of being a Base64 representation.
                // is for handling cases where the sensitive data was Base64 encoded before being sent out. (K9-Mail app did do this)
                try {
                    byte[] decodedBytes = Base64.decode(bytes, Base64.DEFAULT);
                    String decodedString = new String(decodedBytes);
                    primAndStringValues.put(decodedString, blank);
                    objectsLookedAt.put(decodedString, new Object());
                }catch(IllegalArgumentException e){
                    Log.i("DeconstructorMap","Attempted to decode a non Base64 byte array");
                }

            } else if (arrayType.equals(char.class)) {//Since can convert a string into an array of char, need to handle this.
                objectsToAnalyze.add(new String((char[]) value));
            }
            else {//either primitive/string (excluding byte and char) array or a multidimensional array.
                for (int index = 0; index < sizeOfArray; index++) {
                    Object arrayValue = Array.get(value, index);
                    if (arrayValue != null) {
                        if (objectsLookedAt.containsKey(arrayValue)) {//Improved speed of the program

                        } else {
                            objectsToAnalyze.add(arrayValue);
                        }
                    }
                }
            }
        } else {
            Log.e("DeconstructorMap", "Error, we have an object");
        }
    }


    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    public static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }


}
