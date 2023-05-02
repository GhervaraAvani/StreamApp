package com.example.streamapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Avani
 */
public class VideoHistory implements Parcelable {

    String id;
    Long time;
    Long duration;

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    protected VideoHistory() {

    }

    public static final Creator<VideoHistory> CREATOR = new Creator<VideoHistory>() {
        @Override
        public VideoHistory createFromParcel(Parcel in) {
            VideoHistory objVideoHistory = new VideoHistory();
            objVideoHistory.id = in.readString();
            objVideoHistory.time = in.readLong();
            objVideoHistory.duration = in.readLong();
            return objVideoHistory;
        }

        @Override
        public VideoHistory[] newArray(int size) {
            return new VideoHistory[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeLong(time);
        parcel.writeLong(duration);
    }
}
