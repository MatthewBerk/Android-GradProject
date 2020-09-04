package com.example.aspectideaone;

import android.os.Parcel;
import android.os.Parcelable;

//One way I found to receive a custom Parcelable object from another app and be able to read the data,
// is by having a near exact copy of that Parcelable object class (including package name).
// Now app can still get the Parcelable object (and store it in variable of type Object) and do reflection, though this approach is cleaner.
public class ExampleParcelable implements Parcelable {

    public String someValue1;
    public int someValue2;

    public byte[] forBytes;
    public String forString;

    public ExampleParcelable(String data1){
    }

    protected ExampleParcelable(Parcel in) {
        forBytes = in.createByteArray();
        someValue1 = in.readString();
        someValue2 = in.readInt();
        forString = in.readString();
    }

    public static final Creator<ExampleParcelable> CREATOR = new Creator<ExampleParcelable>() {
        @Override
        public ExampleParcelable createFromParcel(Parcel in) {
            return new ExampleParcelable(in);
        }

        @Override
        public ExampleParcelable[] newArray(int size) {
            return new ExampleParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
