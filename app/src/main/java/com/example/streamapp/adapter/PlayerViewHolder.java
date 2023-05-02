package com.example.streamapp.adapter;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.streamapp.R;
import com.example.streamapp.model.MediaObject;
import com.example.streamapp.utils.RetrofitClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by Morris on 03,June,2019
 */
public class PlayerViewHolder extends RecyclerView.ViewHolder {

  /**
   * below view have public modifier because
   * we have access PlayerViewHolder inside the ExoPlayerRecyclerView
   */
  public FrameLayout mediaContainer;
  public ImageView mediaCoverImage, volumeControl;
  public ProgressBar progressBar;
  public RequestManager requestManager;
  private TextView title, tvDescription,txtVideoCacheTime;
  private View parent;

  public PlayerViewHolder(@NonNull View itemView) {
    super(itemView);
    parent = itemView;
    mediaContainer = itemView.findViewById(R.id.mediaContainer);
    mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage);
    title = itemView.findViewById(R.id.tvTitle);
    tvDescription = itemView.findViewById(R.id.tvDescription);
    progressBar = itemView.findViewById(R.id.progressBar);
    volumeControl = itemView.findViewById(R.id.ivVolumeControl);
    txtVideoCacheTime = itemView.findViewById(R.id.txtVideoCacheTime);
  }

  void onBind(MediaObject mediaObject, RequestManager requestManager) {
    this.requestManager = requestManager;
    parent.setTag(this);
    title.setText(mediaObject.getTitle());
    tvDescription.setText(mediaObject.getDescription());
    if(mediaObject.getHistory()!=null){
      txtVideoCacheTime.setVisibility(View.VISIBLE);
      txtVideoCacheTime.setText(getFormatTime(mediaObject.getHistory().getTime()));
    }else{
      txtVideoCacheTime.setVisibility(View.GONE);
    }

    this.requestManager
        .load(RetrofitClient.getVideoUrl(mediaObject.getId(),mediaObject.getCoverFileName()))
        .into(mediaCoverImage);
  }


  String getFormatTime(long durationInSeconds){
    long hrs = (durationInSeconds / 3600);
    long mins = ((durationInSeconds % 3600) / 60);
    long secs = durationInSeconds % 60;

// Output like "1:01" or "4:03:59" or "123:03:59"
    String ret = "";

    if (hrs > 0) {
      ret += "" + hrs + ":" + (mins < 10 ? "0" : "");
    }

    ret += "" + mins + ":" + (secs < 10 ? "0" : "");
    ret += "" + secs;

    return ret;
  }
}

