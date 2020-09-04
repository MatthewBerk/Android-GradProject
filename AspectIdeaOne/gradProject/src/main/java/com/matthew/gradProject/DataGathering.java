package com.matthew.gradProject;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


//This is just a class to provide example sensitive data gathering method.
//Currently not meant to be used officially since only gets contacts data and doesn't grab every possible value from various contacts tables.
//Also serves to help test this project.
public class DataGathering {

    private static boolean contactsHelperMethodRan = false;

    // A sample method for app developer to use to see one way they could format the sensitive data json file.
    // Not meant to be actually utilized since doesn't grab every possible value you can from users contacts.
    public static void considerContactsSensitiveData(Context activityContext) {
        if (!canReadContacts(activityContext)) {
            Log.w("DataGathering", "Tried executing considerContactsSensitiveData but app doesn't have permission to access contacts.");
            return;
        }

        JSONArray contactsArray =  getContacts(activityContext);

        try {
            DataStorage.updateDataGroup(contactsArray, "Contacts");
            contactsHelperMethodRan = true;
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    //Can use if invoke helper method in a method that runs repeatedly and don't want to keep grabbing contacts. (example is in an onCreate)
    public static boolean calledConsiderContactsSensitiveData() {
        return contactsHelperMethodRan;
    }


    static boolean canReadContacts(Context activityContext) {
        Boolean canReadContacts = false;
        if (activityContext.getApplicationContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            canReadContacts = true;
        }
        return canReadContacts;
    }

    //For conducting a non image related query. Need projectionNames so can specify name of value which is based on variable name used to create projection array, because that variables value
    // is just a form of data#.
    private static JSONObject newPerformQuery(ContentResolver contentResolver, Uri queryUri, String[] projection, String selection, String[] selectionArgs, String[] projectionNames) {

        JSONObject tableJsonObject = new JSONObject();//This makes it so data category (such as phone numbers) still appear in the JSON file even if no data was found for it.
        Cursor cursor;
        try {
            cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        } catch (Exception e) {
            e.printStackTrace();
            return tableJsonObject;//So don't need to do null check since JSONObject/Array do not allow null values.
        }

        if (cursor != null && cursor.getCount() > 0) {

            int[] columnIndexs = new int[projection.length];
            //So don't need to re-grab column index's every time we move cursor to next row.
            for (int index = 0; index < projection.length; index++) {
                columnIndexs[index] = cursor.getColumnIndex(projection[index]);
            }

            //INSTEAD of iterating through each row and grabbing all column values,
            // I am iterating through each column and looping through rows. Makes it easier for creating JSONObject/Array.
            for (int index = 0; index < columnIndexs.length; index++) {
                List<String> tempList = new ArrayList<>();

                cursor.moveToFirst();
                //Grab every value for a column for all existing rows.
                do {
                    String value = cursor.getString(columnIndexs[index]);
                    if (value != null) {
                        tempList.add(value);
                    }
                } while (cursor.moveToNext());

                //add value or JSONArray of several values, to tableJsonObject before moving on to next column
                try {
                    if (tempList.size() == 0) {//No values for this key
                        //Values cannot be null https://developer.android.com/reference/org/json/JSONObject.html
                    } else if (tempList.size() < 2) {//Only one value, so no need for JSONArray
                        String holder = tempList.get(0);
                        tableJsonObject.put(projectionNames[index], holder);
                    } else {//Need a JSONArray
                        JSONArray temp = new JSONArray(tempList);
                        tableJsonObject.put(projectionNames[index], temp);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        return tableJsonObject;
    }


    // For conducting image related queries. Need projectionNames so can specify name of value which is based on variable name used to create projection array, because that variable value
    // is just a form of data#. Need both URI and Base64 encoded string of image byte array!
    private static JSONObject performImageQuery(ContentResolver contentResolver, Uri queryUri, String[] projection, String selection, String[] selectionArgs, String[] projectionNames) {

        JSONObject tableJsonObject = new JSONObject();//This makes it so data category (such as phone numbers) still appear in the JSON file even if no data was found for it.
        Cursor cursor;

        try {
            cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        } catch (Exception e) {
            e.printStackTrace();
            return tableJsonObject;//So don't need to do null check since JSONObject/Array do not allow null values.
        }

        if (cursor != null && cursor.getCount() > 0) {
            int[] columnIndexs = new int[projection.length];
            //So don't need to re-grab column index's every time we move cursor to next row.
            for (int index = 0; index < projection.length; index++) {
                columnIndexs[index] = cursor.getColumnIndex(projection[index]);
            }

            //Even though image uri columns should only have 1 value for each contact (unlike phone numbers which could have 2+)
            // Still going to iterate just in case Android allows multiple images for one contact in future.
            // I am iterating through each column and looping through rows. Makes it easier for making JSONObject/Array.
            for (int index = 0; index < columnIndexs.length; index++) {
                List<String> tempList = new ArrayList<>();
                cursor.moveToFirst();
                do {
                    String value = cursor.getString(columnIndexs[index]);
                    if (value != null) {
                        tempList.add(value);
                    }

                } while (cursor.moveToNext());

                //add value or JSONArray of several values, to tableJsonObject before moving on to next column
                try {
                    if (tempList.size() == 0) {//No values for this key
                        // Values cannot be null https://developer.android.com/reference/org/json/JSONObject.html
                    } else if (tempList.size() < 2) {//Only one value, so no need for JSONArray
                        String holder = tempList.get(0);
                        String nameHolder = projectionNames[index];
                        tableJsonObject.put(nameHolder, holder);//storing URI of image

                        Uri imageUri = Uri.parse(holder);
                        Bitmap bitmap;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//Android 9.0 (API 28 when below approach was introduced)
                            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, imageUri);
                            bitmap = ImageDecoder.decodeBitmap(source);
                        } else {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);//Method was deprecated in API 29 and their substitute was only introduced API 28
                        }

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);//Need to extract byte array from bitmap
                        byte[] bytes = stream.toByteArray();

                        bitmap.recycle();//So garbage collector knows to get rid of bitmap.
                        stream.close();

                        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);//NOTE this string has some \n  SO IT ISN'T JSON adding those.

                        nameHolder = nameHolder.replace("URI", "EncodedImage");

                        tableJsonObject.put(nameHolder, encodedString);//storing base64 encoded byte array of image

                    } else {//Need array. Unlikely to run unless Android provides option for multiple images for one contact.
                        JSONArray temp = new JSONArray(tempList);
                        String nameHolder = projectionNames[index];
                        tableJsonObject.put(nameHolder, temp);//storing URI of image

                        JSONArray images = new JSONArray();

                        for (int position = 0; position < tempList.size(); position++) {
                            Uri imageUri = Uri.parse(tempList.get(position));
                            Bitmap bitmap;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//Android 9.0 (API 28 when below approach was introduced)
                                ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, imageUri);
                                bitmap = ImageDecoder.decodeBitmap(source);
                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);//Method was deprecated in API 29 and their substitute was only introduced API 28
                            }

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);//Need to extract byte array from bitmap
                            byte[] bytes = stream.toByteArray();
                            bitmap.recycle();//So garbage collector knows to get rid of bitmap.
                            stream.close();

                            String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                            images.put(encodedString);//storing base64 encoded byte array of image
                        }
                        nameHolder = nameHolder.replace("URI", "EncodedImage");
                        tableJsonObject.put(nameHolder, images);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        return tableJsonObject;
    }


    //For grabbing certain user contacts data and storing it in JSONObjects/Arrays
    private static JSONArray getContacts(Context activityContext) {
        ContentResolver contentResolver = activityContext.getContentResolver();
        JSONArray contactsArray = new JSONArray();//This makes it so data groups such as contacts are present in JSON file even if there is no data for it.

        Cursor cursor;
        try {
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            //querying for all contact entries (i.e. what see when open contacts app)
        } catch (Exception e) {
            e.printStackTrace();
            return contactsArray;
        }


        //Iterating through all contacts.
        while (cursor.moveToNext()) {
            try {
                JSONObject contactEntry = new JSONObject();//This will hold all gathered data for a single contact

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                //String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String selection = ContactsContract.Data.MIMETYPE + " = ?" + " AND " + ContactsContract.Data.CONTACT_ID + " = " + id;
                String[] projectionNames;

                //cdk abbreviation stands for CommonDataKinds, its the name of the content provider table I am querying for contacts data

                // Just two types of images for a contact, full size and thumbnail.
                // Photo from here https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Photo
                // Just gives byte array of thumbnail, which we can get through Uri of ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
                String[] cdkPhoto = new String[]{
                        //ContactsContract.CommonDataKinds.Phone.PHOTO_URI, //todo Causes some issue when debugging (debugger freezes due to size) though when just run project, it works fine.
                        ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
                };
                projectionNames = new String[]{
                        //"PHOTO_URI",
                        "PHOTO_THUMBNAIL_URI",
                };
                String[] photoSelectionArgs = new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
                JSONObject photoObject = performImageQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkPhoto, selection, photoSelectionArgs, projectionNames);
                contactEntry.put("Contact_Photo", photoObject);//Will have URI and the Base64 encoded string representation of the bytes of the image.

                //StructuredName
                String[] cdkStructuredNameProjection = new String[]{
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME,
                };
                projectionNames = new String[]{
                        "Given_Name",
                        "Middle_Name",
                        "Family_Name",
                        "Phonetic_Given_Name",
                        "Phonetic_Middle_Name",
                        "Phonetic_Family_Name"
                };
                String[] nameSelectionArgs = new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
                JSONObject structuredNameObject = newPerformQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkStructuredNameProjection, selection, nameSelectionArgs, projectionNames);//FOR grabbing parts of name for all contacts
                contactEntry.put("StructuredName", structuredNameObject);

                //Nickname
                String[] cdkNicknameProjection = new String[]{
                        ContactsContract.CommonDataKinds.Nickname.NAME
                };//A lot of the columns are just ints stating what type the nickname is (e.g. initials)
                projectionNames = new String[]{
                        "Name"
                };
                String[] nickNameSelectionArgs = new String[]{ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE};
                JSONObject nickNameObject = newPerformQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkNicknameProjection, selection, nickNameSelectionArgs, projectionNames);
                contactEntry.put("Nickname", nickNameObject);

                //Organization
                String[] cdkOrganizationProjection = new String[]{
                        ContactsContract.CommonDataKinds.Organization.COMPANY,
                        ContactsContract.CommonDataKinds.Organization.DEPARTMENT,
                        ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION,
                        ContactsContract.CommonDataKinds.Organization.OFFICE_LOCATION,
                        ContactsContract.CommonDataKinds.Organization.PHONETIC_NAME,
                        ContactsContract.CommonDataKinds.Organization.SYMBOL,
                        ContactsContract.CommonDataKinds.Organization.TITLE
                };// Different android devices have different number of these fields.
                projectionNames = new String[]{
                        "Company",
                        "Department",
                        "Job_Description",
                        "Office_Location",
                        "Phonetic_Name",
                        "Symbol",
                        "Title"
                };
                String[] organizationSelectionArgs = new String[]{ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                JSONObject organizationObject = newPerformQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkOrganizationProjection, selection, organizationSelectionArgs, projectionNames);
                contactEntry.put("Organization", organizationObject);

                //Phone
                String[] cdkPhoneProjection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER//It's just +1 and rest of number clumped together without ()
                };
                projectionNames = new String[]{
                        "Number",
                        "Normalized_Number"
                };
                String[] phoneSelectionArgs = new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
                JSONObject phoneObject = newPerformQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkPhoneProjection, selection, phoneSelectionArgs, projectionNames);
                contactEntry.put("Phone", phoneObject);

                //SipAddress
                String[] cdkSipAddressProjection = new String[]{
                        ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS
                };
                projectionNames = new String[]{
                        "Sip_Address"
                };
                String[] sipSelectionArgs = new String[]{ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE};
                JSONObject sipAddrObject = newPerformQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkSipAddressProjection, selection, sipSelectionArgs, projectionNames);
                contactEntry.put("SipAddress", sipAddrObject);

                //Email
                String[] cdkEmailProjection = new String[]{
                        ContactsContract.CommonDataKinds.Email.ADDRESS
                };
                projectionNames = new String[]{
                        "Address"
                };
                String[] emailSelectionArgs = new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
                JSONObject emailObject = newPerformQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkEmailProjection, selection, emailSelectionArgs, projectionNames);
                contactEntry.put("Email", emailObject);

                //StructuredPostal
                String[] cdkStructuredPostalProjection = new String[]{
                        ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                        ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
                        ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD,
                        ContactsContract.CommonDataKinds.StructuredPostal.POBOX,
                        ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                        ContactsContract.CommonDataKinds.StructuredPostal.REGION,
                        ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                };//Different android devices have different number of these fields.
                projectionNames = new String[]{
                        "City",
                        "Country",
                        "Neighborhood",
                        "PoBox",
                        "Postcode",
                        "Region",
                        "Street"
                };
                String[] addressSelectionArgs = new String[]{ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                JSONObject structuredPostalObject = newPerformQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkStructuredPostalProjection, selection, addressSelectionArgs, projectionNames);
                contactEntry.put("StructuredPostal", structuredPostalObject);

                //IM
                String[] cdkIM = new String[]{
                        ContactsContract.CommonDataKinds.Im.DATA
                };
                projectionNames = new String[]{
                        "Data"
                };
                String[] imSelectionArgs = new String[]{ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                JSONObject imObject = newPerformQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkIM, selection, imSelectionArgs, projectionNames);
                contactEntry.put("Im", imObject);

                //Website
                String[] cdkWebsite = new String[]{
                        ContactsContract.CommonDataKinds.Website.URL
                };
                projectionNames = new String[]{
                        "URL"
                };
                String[] websiteSelectionArgs = new String[]{ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE};
                JSONObject websiteObject = newPerformQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkWebsite, selection, websiteSelectionArgs, projectionNames);
                contactEntry.put("Website", websiteObject);

                //Note
                String[] cdkNote = new String[]{
                        ContactsContract.CommonDataKinds.Note.NOTE,
                };
                projectionNames = new String[]{
                        "Note"
                };
                String[] noteSelectionArgs = new String[]{ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                JSONObject noteObject = newPerformQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkNote, selection, noteSelectionArgs, projectionNames);
                contactEntry.put("Note", noteObject);

                //Event
                String[] cdkEvent = new String[]{
                        ContactsContract.CommonDataKinds.Event.START_DATE
                };
                projectionNames = new String[]{
                        "Start_Date"
                };
                String[] eventSelectionArgs = new String[]{ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};
                JSONObject eventObject = newPerformQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkEvent, selection, eventSelectionArgs, projectionNames);
                contactEntry.put("Event", eventObject);

                //Relation
                String[] cdkRelation = new String[]{
                        ContactsContract.CommonDataKinds.Relation.NAME
                };
                projectionNames = new String[]{
                        "Name"
                };
                String[] relationSelectionArgs = new String[]{ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE};
                JSONObject relationObject = newPerformQuery(contentResolver, ContactsContract.Data.CONTENT_URI, cdkRelation, selection, relationSelectionArgs, projectionNames);
                contactEntry.put("Relation", relationObject);

                contactsArray.put(contactEntry);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return contactsArray;
    }
}
