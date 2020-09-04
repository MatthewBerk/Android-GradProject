package com.example.aspectideaone;

//This class is just to store data in static variables to be used in various test without needing to create class or instance variables for Activity classes
// or other class's when this would do.
// One example is for 'ExampleParcelable' where app developer could try to write some data to parcel that isn't part of the object.
public class JustStaticValues {

    public static String value1;
    public static byte[] value2;//For parcelable test

}
