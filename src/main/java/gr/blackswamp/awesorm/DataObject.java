package gr.blackswamp.awesorm;

import android.os.Parcelable;

public abstract class DataObject implements Parcelable {
    private static final String TAG = "DataObject";

    protected DataObject() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
