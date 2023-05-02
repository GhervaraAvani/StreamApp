package com.example.streamapp.ui;

import static com.google.android.exoplayer2.Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.streamapp.R;
import com.example.streamapp.adapter.PlayerViewHolder;
import com.example.streamapp.model.MediaObject;

import com.example.streamapp.utils.RetrofitClient;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;



public class ExoPlayerRecyclerView extends RecyclerView {

  private static final String TAG = "ExoPlayerRecyclerView";
  private static final String AppName = "Android ExoPlayer";
  /**
   * PlayerViewHolder UI component
   * Watch PlayerViewHolder class
   */
  private ImageView mediaCoverImage, volumeControl;
  private ProgressBar progressBar;
  private View viewHolderParent;
  private FrameLayout mediaContainer;
  private PlayerView videoSurfaceView;
  private ExoPlayer videoPlayer;
  /**
   * variable declaration
   */
  // Media List
  private ArrayList<MediaObject> mediaObjects = new ArrayList<>();
  private int videoSurfaceDefaultHeight = 0;
  private int screenDefaultHeight = 0;
  private Context context;
  private int playPosition = -1;
  private boolean isVideoViewAdded;
  private RequestManager requestManager;
  // controlling volume state
  private VolumeState volumeState;
  private int minSecond = 5;
  private Date startTime;
  Calendar calendar = Calendar.getInstance();
  Handler handler;
  Runnable runnable;
  //Timer timer=new Timer();

  private View.OnClickListener videoViewClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      toggleVolume();
    }
  };

  public ExoPlayerRecyclerView(@NonNull Context context) {
    super(context);
    init(context);
  }

  public ExoPlayerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  private void init(Context context) {
    this.context = context.getApplicationContext();
    Display display = ((WindowManager) Objects.requireNonNull(
        getContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
    Point point = new Point();
    display.getSize(point);
    handler = new Handler();
    videoSurfaceDefaultHeight = point.x;
    screenDefaultHeight = point.y;

    videoSurfaceView = new PlayerView(this.context);
    videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//      TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

//    TrackSelection.Factory videoTrackSelectionFactory =
//        new AdaptiveTrackSelection.Factory(bandwidthMeter);
//    TrackSelector trackSelector =
//        new DefaultTrackSelector(videoTrackSelectionFactory);
    DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);
    trackSelector.setParameters(
            trackSelector.buildUponParameters().setAllowVideoMixedMimeTypeAdaptiveness(true));

    videoPlayer = new ExoPlayer.Builder(context).setTrackSelector(trackSelector).build();
    //Create the player using ExoPlayerFactory
//    videoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
    // Disable Player Control
    videoSurfaceView.setUseController(false);
    // Bind the player to the view.
    videoSurfaceView.setPlayer(videoPlayer);
    // Turn on Volume
    setVolumeControl(VolumeState.ON);


    addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          if (mediaCoverImage != null) {
            // show the old thumbnail
            mediaCoverImage.setVisibility(VISIBLE);
          }

          // There's a special case when the end of the list has been reached.
          // Need to handle that with this bit of logic
          if (!recyclerView.canScrollVertically(1)) {
            playVideo(true);
          } else {
            startTime = null;
            playVideo(false);
          }
        }
      }

      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
      }
    });

    addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
      @Override
      public void onChildViewAttachedToWindow(@NonNull View view) {

      }

      @Override
      public void onChildViewDetachedFromWindow(@NonNull View view) {
        if (viewHolderParent != null && viewHolderParent.equals(view)) {
          resetVideoView();
        }
      }
    });

    videoPlayer.addListener(new Player.Listener() {
      @Override
      public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {

          case Player.STATE_BUFFERING:
            Log.e(TAG, "onPlayerStateChanged: Buffering video.");
            if (progressBar != null) {
              progressBar.setVisibility(VISIBLE);
            }

            break;
          case Player.STATE_ENDED:
            Log.d(TAG, "onPlayerStateChanged: Video ended.");
            videoPlayer.seekTo(0);
            break;
          case Player.STATE_IDLE:

            break;
          case Player.STATE_READY:
            Log.e(TAG, "onPlayerStateChanged: Ready to play.");
            if (progressBar != null) {
              progressBar.setVisibility(GONE);
            }
            if (!isVideoViewAdded) {
              addVideoView();
              handler.postDelayed(runnable, 4000);
            }
            break;
          default:
            break;
        }
      }
    });

    videoPlayer.addAnalyticsListener(new AnalyticsListener() {
      @Override
      public void onIsPlayingChanged(EventTime eventTime, boolean isPlaying) {
        Log.e("tag","timeLine"+"onIsPlayingChanged");
        AnalyticsListener.super.onIsPlayingChanged(eventTime, isPlaying);
      }

      @Override
      public void onTimelineChanged(EventTime eventTime, int reason) {
        Log.e("tag","timeLine"+"onTimelineChanged");
        AnalyticsListener.super.onTimelineChanged(eventTime, reason);

      }

      @Override
      public void onTracksChanged(EventTime eventTime, Tracks tracks) {
        Log.e("tag","timeLine"+"onTracksChanged");
        AnalyticsListener.super.onTracksChanged(eventTime, tracks);
      }

      @Override
      public void onLoadStarted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        Log.e("tag","timeLine"+"onLoadStarted");
        AnalyticsListener.super.onLoadStarted(eventTime, loadEventInfo, mediaLoadData);
      }

      @Override
      public void onLoadCompleted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        Log.e("tag","timeLine"+"onLoadCompleted");
        AnalyticsListener.super.onLoadCompleted(eventTime, loadEventInfo, mediaLoadData);
      }

      @Override
      public void onLoadCanceled(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        Log.e("tag","timeLine"+"onLoadCanceled");
        AnalyticsListener.super.onLoadCanceled(eventTime, loadEventInfo, mediaLoadData);
      }

    });
    videoPlayer.addListener(new Player.Listener() {
      @Override
      public void onTimelineChanged(Timeline timeline, int reason) {
        Log.e("tag","timeLine"+timeline.getPeriodCount());
      }
    });
    runnable = new Runnable() {
      @Override
      public void run() {
        if(videoPlayer!=null) {
          videoPlayer.seekTo(0);
          videoPlayer.setPlayWhenReady(true);
          handler.postDelayed(runnable, 4000);
        }
      }
    };
  }

  public void playVideo(boolean isEndOfList) {

    int targetPosition;

    if (!isEndOfList) {
      int startPosition = ((LinearLayoutManager) Objects.requireNonNull(
          getLayoutManager())).findFirstVisibleItemPosition();
      int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

      // if there is more than 2 list-items on the screen, set the difference to be 1
      if (endPosition - startPosition > 1) {
        endPosition = startPosition + 1;
      }

      // something is wrong. return.
      if (startPosition < 0 || endPosition < 0) {
        return;
      }

      // if there is more than 1 list-item on the screen
      if (startPosition != endPosition) {
        int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
        int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);

        targetPosition =
            startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
      } else {
        targetPosition = startPosition;
      }
    } else {
      targetPosition = mediaObjects.size() - 1;
    }

    Log.d(TAG, "playVideo: target position: " + targetPosition);

    // video is already playing so return
    if (targetPosition == playPosition) {
      return;
    }

    // set the position of the list-item that is to be played
    playPosition = targetPosition;
    if (videoSurfaceView == null) {
      return;
    }

    // remove any old surface views from previously playing videos
    videoSurfaceView.setVisibility(INVISIBLE);
    removeVideoView(videoSurfaceView);

    int currentPosition =
        targetPosition - ((LinearLayoutManager) Objects.requireNonNull(
            getLayoutManager())).findFirstVisibleItemPosition();

    View child = getChildAt(currentPosition);
    if (child == null) {
      return;
    }

    PlayerViewHolder holder = (PlayerViewHolder) child.getTag();
    if (holder == null) {
      playPosition = -1;
      return;
    }
    mediaCoverImage = holder.mediaCoverImage;
    progressBar = holder.progressBar;
    volumeControl = holder.volumeControl;
    viewHolderParent = holder.itemView;
    requestManager = holder.requestManager;
    mediaContainer = holder.mediaContainer;

    videoSurfaceView.setPlayer(videoPlayer);
    viewHolderParent.setOnClickListener(videoViewClickListener);

    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
        context, Util.getUserAgent(context, AppName));
    DataSource.Factory dataSource = new DefaultDataSource.Factory(context);
    String mediaUrl = RetrofitClient.getVideoUrl(mediaObjects.get(targetPosition).getId(),mediaObjects.get(targetPosition).getOutputFileName());
    //mediaObjects.get(targetPosition).getUrl();
    if (mediaUrl != null) {
      MediaItem mediaItem = MediaItem.fromUri(mediaUrl);
//      MediaSource mediaSource = new ProgressiveMediaSource.Factory(
//              new DefaultDataSource.Factory(context) // <- context
//      )
//              .createMediaSource(mediaItem);
      MediaSource mediaSource =
              new DashMediaSource.Factory(dataSourceFactory)
                      .createMediaSource(mediaItem);
      videoPlayer.setMediaSource(mediaSource,true);
      videoPlayer.prepare();
      videoPlayer.setPlayWhenReady(true);
    }
  }

  /**
   * Returns the visible region of the video surface on the screen.
   * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
   */
  private int getVisibleVideoSurfaceHeight(int playPosition) {
    int at = playPosition - ((LinearLayoutManager) Objects.requireNonNull(
        getLayoutManager())).findFirstVisibleItemPosition();
    Log.d(TAG, "getVisibleVideoSurfaceHeight: at: " + at);

    View child = getChildAt(at);
    if (child == null) {
      return 0;
    }

    int[] location = new int[2];
    child.getLocationInWindow(location);

    if (location[1] < 0) {
      return location[1] + videoSurfaceDefaultHeight;
    } else {
      return screenDefaultHeight - location[1];
    }
  }

  // Remove the old player
  private void removeVideoView(PlayerView videoView) {
    handler.removeCallbacks(runnable);
    ViewGroup parent = (ViewGroup) videoView.getParent();
    if (parent == null) {
      return;
    }

    int index = parent.indexOfChild(videoView);
    if (index >= 0) {
      parent.removeViewAt(index);
      isVideoViewAdded = false;
      viewHolderParent.setOnClickListener(null);
    }
  }

  private void addVideoView() {
    mediaContainer.addView(videoSurfaceView);
    isVideoViewAdded = true;
    videoSurfaceView.requestFocus();
    videoSurfaceView.setVisibility(VISIBLE);
    videoSurfaceView.setAlpha(1);
    mediaCoverImage.setVisibility(GONE);
  }

  private void resetVideoView() {
    if (isVideoViewAdded) {
      removeVideoView(videoSurfaceView);
      playPosition = -1;
      videoSurfaceView.setVisibility(INVISIBLE);
      mediaCoverImage.setVisibility(VISIBLE);
    }
  }

  public void releasePlayer() {

    if (videoPlayer != null) {
      videoPlayer.release();
      handler.removeCallbacks(runnable);
      videoPlayer = null;
    }

    viewHolderParent = null;
  }

  public void onPausePlayer() {
    if (videoPlayer != null) {
      videoPlayer.stop();
    }
  }

  private void toggleVolume() {
    if (videoPlayer != null) {
      if (volumeState == VolumeState.OFF) {
        Log.d(TAG, "togglePlaybackState: enabling volume.");
        setVolumeControl(VolumeState.ON);
      } else if (volumeState == VolumeState.ON) {
        Log.d(TAG, "togglePlaybackState: disabling volume.");
        setVolumeControl(VolumeState.OFF);
      }
    }
  }

  private void setVolumeControl(VolumeState state) {
    volumeState = state;
    if (state == VolumeState.OFF) {
      videoPlayer.setVolume(0f);
      animateVolumeControl();
    } else if (state == VolumeState.ON) {
      videoPlayer.setVolume(1f);
      animateVolumeControl();
    }
  }

  private void animateVolumeControl() {
    if (volumeControl != null) {
      volumeControl.bringToFront();
      if (volumeState == VolumeState.OFF) {
        requestManager.load(R.drawable.ic_volume_off)
            .into(volumeControl);
      } else if (volumeState == VolumeState.ON) {
        requestManager.load(R.drawable.ic_volume_on)
            .into(volumeControl);
      }
      volumeControl.animate().cancel();

      volumeControl.setAlpha(1f);

      volumeControl.animate()
          .alpha(0f)
          .setDuration(600).setStartDelay(1000);
    }
  }

  public void setMediaObjects(ArrayList<MediaObject> mediaObjects) {
    this.mediaObjects = mediaObjects;
  }

  /**
   * Volume ENUM
   */
  private enum VolumeState {
    ON, OFF
  }
}

