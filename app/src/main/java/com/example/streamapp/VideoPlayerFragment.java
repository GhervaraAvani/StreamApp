package com.example.streamapp;

import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.streamapp.databinding.FragmentPlayerBinding;
import com.example.streamapp.databinding.FragmentVideoPlayerBinding;
import com.example.streamapp.model.MediaObject;
import com.example.streamapp.model.Time;
import com.example.streamapp.utils.RetrofitClient;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MetadataRetriever;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.video.VideoFrameMetadataListener;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoPlayerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentVideoPlayerBinding binding;
    StyledPlayerView playView;
    MediaObject mediaObject;
    int time=9139;

    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1
     *         Parameter 1.
     * @param param2
     *         Parameter 2.
     *
     * @return A new instance of fragment VideoPlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoPlayerFragment newInstance(String param1, String param2) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mediaObject = VideoPlayerActivity.mediaObject;
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        playView = view.findViewById(R.id.playView);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Uri videoUri = Uri.parse(RetrofitClient.getVideoUrl(mediaObject.getId(),mediaObject.getOutputFileName()));

        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(getContext(), new AdaptiveTrackSelection.Factory());
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
//        trackSelector.setParameters(
//                trackSelector
//                        .buildUponParameters()
//                        .setAllowVideoMixedMimeTypeAdaptiveness(true));
        trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd());
        MediaSource mediaSource =
                new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(mediaItem);
//        MediaSource mediaSource = new ProgressiveMediaSource.Factory(
//                new DefaultDataSource.Factory(getContext()) // <- context
//        )
//                .createMediaSource(mediaItem);

//.setTrackSelector(trackSelector)
        ExoPlayer player = new ExoPlayer.Builder(getContext()).setTrackSelector(trackSelector).build();
        playView.setPlayer(player);
        player.setMediaSource(mediaSource,true);
        player.prepare();
        if(mediaObject.getHistory()!=null){
            long time = mediaObject.getHistory().getTime() * 1000;
            player.seekTo(time);
            player.setPlayWhenReady(true);
        }else{
            player.play();
        }



        player.setVideoFrameMetadataListener(new VideoFrameMetadataListener() {
            @Override
            public void onVideoFrameAboutToBeRendered(long presentationTimeUs, long releaseTimeNs, Format format, @Nullable MediaFormat mediaFormat) {
                //getVideoFrame(getContext(),,releaseTimeNs);
            }
        });
        playView.setFullscreenButtonClickListener(new StyledPlayerView.FullscreenButtonClickListener() {
            @Override
            public void onFullscreenButtonClick(boolean isFullScreen) {
                if(isFullScreen)
                    ((VideoPlayerActivity) getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else{
                    ((VideoPlayerActivity) getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        playView.setControllerVisibilityListener(new StyledPlayerView.ControllerVisibilityListener() {
            @Override
            public void onVisibilityChanged(int visibility) {

            }
        });

        player.addListener(
                new Player.Listener() {
                    @Override
                    public void onTimelineChanged(
                            Timeline timeline, @Player.TimelineChangeReason int reason) {
                        Log.e("tag","onTimelineChanged");
                        Object manifest = player.getCurrentManifest();
                        if (manifest != null) {
                            DashManifest dashManifest = (DashManifest) manifest;
                            // Do something with the manifest.
                        }
                    }

                    @Override
                    public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                        Player.Listener.super.onPlayWhenReadyChanged(playWhenReady, reason);
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition());
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(player.getCurrentPosition());
                    }

                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        Player.Listener.super.onPlaybackStateChanged(playbackState);
                        switch (playbackState) {
                            case Player.STATE_BUFFERING:
                                break;
                            case Player.STATE_ENDED:
                                break;
                            case Player.STATE_IDLE:

                                break;
                            case Player.STATE_READY:

                                break;
                            default:
                                break;
                        }


                    }
                }
                );


    }

//    @Override
//    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
//        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
//        if (isInPictureInPictureMode) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                        getActivity().setPictureInPictureParams(new PictureInPictureParams.Builder()
//                                .setSeamlessResizeEnabled(false)
//                                .build());
//                    }
//                }
//        }
//    }


    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
    }

    @Override
    public void onResume() {
        playView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        playView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public static Bitmap getVideoFrame(Context context, Uri uri, long time) {

        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {

            retriever.setDataSource(context, uri);
            bitmap = retriever.getFrameAtTime(time);

        } catch (RuntimeException ex) {
            ex.printStackTrace();

        } finally {

            try {

                retriever.release();

            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }

        return bitmap;
    }


    void cacheHistoryApiCall(Time objTime){
        Call<Response> call = RetrofitClient.getInstance().getMyApi().cacheHistory(mediaObject.getId(),objTime);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });
    }
}