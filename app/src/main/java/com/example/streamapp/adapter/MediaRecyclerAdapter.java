package com.example.streamapp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.streamapp.MainActivity;
import com.example.streamapp.R;
import com.example.streamapp.VideoPlayerFragment;
import com.example.streamapp.model.MediaObject;
import com.example.streamapp.utils.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morris on 03,June,2019
 */
public class MediaRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private ArrayList<MediaObject> mediaObjects;
  private RequestManager requestManager;
  Context mContext;

  public MediaRecyclerAdapter(Context mContext, ArrayList<MediaObject> mediaObjects,
                              RequestManager requestManager) {
    this.mContext = mContext;
    this.mediaObjects = mediaObjects;
    this.requestManager = requestManager;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    return new PlayerViewHolder(
        LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.layout_media_list_item, viewGroup, false));
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
    ((PlayerViewHolder) viewHolder).onBind(mediaObjects.get(i), requestManager);
    MediaObject mediaObject = mediaObjects.get(i);
    ((PlayerViewHolder) viewHolder).mediaContainer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        switchContent(mediaObject);
      }
    });
  }

  @Override
  public int getItemCount() {
    return mediaObjects.size();
  }

  public void refreshView(ArrayList<MediaObject> lstVideo){
     mediaObjects.addAll(lstVideo);
     notifyDataSetChanged();
  }

  public void switchContent(MediaObject mediaObject) {
    if (mContext == null)
      return;
    if (mContext instanceof MainActivity) {
      MainActivity mainActivity = (MainActivity) mContext;
//      Fragment frag = fragment;
      mainActivity.switchContent(mediaObject);
    }

  }
}
