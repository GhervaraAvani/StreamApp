package com.example.streamapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PictureInPictureParams;
import android.os.Build;
import android.os.Bundle;

import com.example.streamapp.model.MediaObject;

public class VideoPlayerActivity extends AppCompatActivity {

    boolean isPipMode = true;
    public static MediaObject mediaObject = new MediaObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        if(savedInstanceState == null)
        {
            VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("data",mediaObject);
            //bundle.putString("param1",value);
            videoPlayerFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.container,videoPlayerFragment).commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBackPressed() {
        if(!isPipMode){
            enterPictureInPictureMode();
            isPipMode = true;
        }else {
            super.onBackPressed();
        }
    }
}