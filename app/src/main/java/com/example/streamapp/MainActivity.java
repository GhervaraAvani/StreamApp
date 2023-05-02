package com.example.streamapp;

import static android.widget.LinearLayout.VERTICAL;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.streamapp.adapter.MediaRecyclerAdapter;
import com.example.streamapp.model.MediaObject;
import com.example.streamapp.ui.ExoPlayerRecyclerView;
import com.example.streamapp.utils.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ExoPlayerRecyclerView mRecyclerView;

    private ArrayList<MediaObject> mediaObjectList = new ArrayList<>();
    private MediaRecyclerAdapter mAdapter;
    private boolean firstTime = true;
    ProgressBar progressBar;
    //public static MediaObject mediaObject=new MediaObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if(savedInstanceState == null)
//        {
//            getSupportFragmentManager().beginTransaction().add(R.id.container,new PlayerFragment()).commit();
//        }
        progressBar = findViewById(R.id.progressBar);
        initView();

        //set data object
        mRecyclerView.setMediaObjects(mediaObjectList);
        mAdapter = new MediaRecyclerAdapter(this, mediaObjectList, initGlide());

        //Set Adapter
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.getLayoutManager().scrollToPosition(0);
        if (firstTime) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.playVideo(false);
                }
            });
            firstTime = false;
        }
        // Prepare demo content
        getVideoList();

    }
    private void initView() {
        mRecyclerView = findViewById(R.id.exoPlayerRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_drawable);
//        if (dividerDrawable != null) {
//            mRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));
//        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions();

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    @Override
    protected void onDestroy() {
        if (mRecyclerView != null) {
            mRecyclerView.releasePlayer();
        }
        super.onDestroy();
    }


    private void getVideoList() {
        Call<ArrayList<MediaObject>> call = RetrofitClient.getInstance().getMyApi().getAllVideoList();
        call.enqueue(new Callback<ArrayList<MediaObject>>() {
            @Override
            public void onResponse(Call<ArrayList<MediaObject>> call, Response<ArrayList<MediaObject>> response) {
                mediaObjectList = response.body();
                mAdapter.refreshView(mediaObjectList);
                progressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ArrayList<MediaObject>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
            }

        });
    }

//    private void prepareVideoList() {
//        MediaObject mediaObject = new MediaObject();
//        mediaObject.setId(1);
//        mediaObject.setUserHandle("@h.pandya");
//        mediaObject.setTitle(
//                "Do you think the concept of marriage will no longer exist in the future?");
//        mediaObject.setCoverUrl(
//                "https://picsum.photos/seed/picsum/200/300");
//        mediaObject.setUrl("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4");
//
//        MediaObject mediaObject2 = new MediaObject();
//        mediaObject2.setId(2);
//        mediaObject2.setUserHandle("@hardik.patel");
//        mediaObject2.setTitle(
//                "If my future husband doesn't cook food as good as my mother should I scold him?");
//        mediaObject2.setCoverUrl(
//                "https://picsum.photos/seed/picsum/200/300");
//        mediaObject2.setUrl("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4");
//
//        MediaObject mediaObject3 = new MediaObject();
//        mediaObject3.setId(3);
//        mediaObject3.setUserHandle("@arun.gandhi");
//        mediaObject3.setTitle("Give your opinion about the Ayodhya temple controversy.");
//        mediaObject3.setCoverUrl(
//                "https://picsum.photos/seed/picsum/200/300");
//        mediaObject3.setUrl("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4");
//
//        MediaObject mediaObject4 = new MediaObject();
//        mediaObject4.setId(4);
//        mediaObject4.setUserHandle("@sachin.patel");
//        mediaObject4.setTitle("When did kama founders find sex offensive to Indian traditions");
//        mediaObject4.setCoverUrl(
//                "https://fastly.picsum.photos/id/10/2500/1667.jpg?hmac=J04WWC_ebchx3WwzbM-Z4_KC_LeLBWr5LZMaAkWkF68");
//        mediaObject4.setUrl("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4");
//
//        MediaObject mediaObject5 = new MediaObject();
//        mediaObject5.setId(5);
//        mediaObject5.setUserHandle("@monika.sharma");
//        mediaObject5.setTitle("When did you last cry in front of someone?");
//        mediaObject5.setCoverUrl(
//                "https://picsum.photos/seed/picsum/200/300");
//        mediaObject5.setUrl("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4");
//
//        mediaObjectList.add(mediaObject);
//        mediaObjectList.add(mediaObject2);
//        mediaObjectList.add(mediaObject3);
//        mediaObjectList.add(mediaObject4);
//        mediaObjectList.add(mediaObject5);
//        mediaObjectList.add(mediaObject);
//        mediaObjectList.add(mediaObject2);
//        mediaObjectList.add(mediaObject3);
//        mediaObjectList.add(mediaObject4);
//        mediaObjectList.add(mediaObject5);
//    }

    public void switchContent(MediaObject mediaObject) {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(id, fragment, fragment.toString());
//        ft.addToBackStack(null);
//        ft.commit();

        Intent homepage = new Intent(this, VideoPlayerActivity.class);
        VideoPlayerActivity.mediaObject = mediaObject;
        homepage.putExtra("data",mediaObject);
        startActivity(homepage);
    }
}