package com.example.aspectideaone;

import android.os.Parcel;
import android.os.Parcelable;

//Reminder, second app would need almost an exact copy of this class (including package name) in order for it to properly handle object
// being passed to it! So while situation is unlikely, it is still a possibility.

//In this example, we are writing values in this object to Parcel in 'writeToParcel', but also writing values not in object.
// Now why a non malicious app developer would do something likely that, I don't know. HOWEVER it is something that could happen, so I
// am handling it. Especially since can't find values written to Parcel in parcel object since they are written to native.

public class ExampleParcelable implements Parcelable {

    public String someValue1;
    public int someValue2;

    //public byte[] forBytes;
   // public String forString;

    public ExampleParcelable(String data1){
        someValue1 = data1;
        someValue2 = 96;
    }


    protected ExampleParcelable(Parcel in) {
        someValue1 = in.readString();
        someValue2 = in.readInt();
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
        dest.writeByteArray(JustStaticValues.value2);//To demonstrate that could write values to Parcel that are not part of object your passing over IPC.
        dest.writeString(someValue1);
        dest.writeInt(someValue2);
        dest.writeString(JustStaticValues.value1);//To demonstrate that could write values to Parcel that are not part of object your passing over IPC.

    }
}
