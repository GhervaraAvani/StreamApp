package com.example.streamapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Morris on 03,June,2019
 */
public class MediaObject implements Parcelable {
  private String id;
  private String title;

  public MediaObject(){}

  public static final Creator<MediaObject> CREATOR = new Creator<MediaObject>() {
    @Override
    public MediaObject createFromParcel(Parcel in) {
      MediaObject mediaObject = new MediaObject();
      mediaObject.id = in.readString();
      mediaObject.title = in.readString();
      mediaObject.description = in.readString();
      mediaObject.fileName = in.readString();
      mediaObject.coverFileName = in.readString();
      mediaObject.outputFileName = in.readString();
      mediaObject.fileContentType = in.readString();
      mediaObject.fileSize = in.readString();
      mediaObject.videoHistory = in.readParcelable(VideoHistory.class.getClassLoader());
      return mediaObject;
    }

    @Override
    public MediaObject[] newArray(int size) {
      return new MediaObject[size];
    }
  };

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  private String description;
  private String fileName;
  private String coverFileName;
  private String outputFileName;
  private String fileContentType;
  private String fileSize;
  private VideoHistory videoHistory;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  private String status;

  public VideoHistory getVideoHistory() {
    return videoHistory;
  }

  public void setVideoHistory(VideoHistory videoHistory) {
    this.videoHistory = videoHistory;
  }

  public VideoHistory getHistory() {
    return videoHistory;
  }

  public void setHistory(VideoHistory videoHistory) {
    this.videoHistory = videoHistory;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getCoverFileName() {
    return coverFileName;
  }

  public void setCoverFileName(String coverFileName) {
    this.coverFileName = coverFileName;
  }

  public String getOutputFileName() {
    return outputFileName;
  }

  public void setOutputFileName(String outputFileName) {
    this.outputFileName = outputFileName;
  }

  public String getFileContentType() {
    return fileContentType;
  }

  public void setFileContentType(String fileContentType) {
    this.fileContentType = fileContentType;
  }

  public String getFileSize() {
    return fileSize;
  }

  public void setFileSize(String fileSize) {
    this.fileSize = fileSize;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(id);
    parcel.writeString(title);
    parcel.writeString(description);
    parcel.writeString(fileName);
    parcel.writeString(coverFileName);
    parcel.writeString(outputFileName);
    parcel.writeString(fileContentType);
    parcel.writeString(fileSize);
    parcel.writeParcelable(parcel.readParcelable(VideoHistory.class.getClassLoader()),i);
  }
}
