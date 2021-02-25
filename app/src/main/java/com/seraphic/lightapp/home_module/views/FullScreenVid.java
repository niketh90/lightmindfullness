package com.seraphic.lightapp.home_module.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.home_module.models.SessionGetter;
import com.seraphic.lightapp.menuprofile.views.StatsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

public class FullScreenVid extends AppCompatActivity {
    boolean isItDailySession=false;
    @BindView(R.id.clVideo)
    ConstraintLayout clVideo;
    @BindView(R.id.mVideoView)
    PlayerView mVideoView;
    SimpleExoPlayer videoPlayer;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.ivThumbnail)
    ImageView ivThumbnail;
    @BindView(R.id.vidControlls)
    PlayerControlView vidControlls;
    SessionGetter sessionGetter;
    private   final int CLOSEINTENT =10 ;
@BindView(R.id.ivFullViewdvid)
ImageView ivFullViewdvid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.full_screen);
        ButterKnife.bind(this);
        ivFullViewdvid.setVisibility(View.GONE);
        if (getIntent()!=null)
        sessionGetter=(SessionGetter) getIntent().getExtras().getSerializable("session");
        isItDailySession=getIntent().getBooleanExtra("dailySession",false);

    }
    @Override
    protected void onResume() {
        super.onResume();

            clVideo.setVisibility(VISIBLE);
            playVid();


    }
    public void playVid() {
        Glide.with(this).load(sessionGetter.sessionThumbNail).placeholder(R.mipmap.placeholder1).into(ivThumbnail);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        videoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        // Bind the player to the view.
        mVideoView.setControllerShowTimeoutMs(0);
        mVideoView.setPlayer(videoPlayer);
        vidControlls.setPlayer(videoPlayer);
        mVideoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, "RecyclerView VideoPlayer"));
        if (sessionGetter.sessionUrl != null) {
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(sessionGetter.sessionUrl));
            if (videoPlayer != null) {
                videoPlayer.prepare(videoSource);
                videoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                videoPlayer.setPlayWhenReady(true);
            }
        }
        videoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {

                    case Player.STATE_BUFFERING:
//                        ivThumbnail.setVisibility(VISIBLE);
                        Log.e("full", "onPlayerStateChanged: Buffering video.");
                        if (progressBar != null) {

                            progressBar.setVisibility(View.VISIBLE);


                        }

                        break;
                    case Player.STATE_ENDED:
                        releasePlayer();

//                        if (isItDailySession){
                            Intent n = new Intent(FullScreenVid.this, StatsFragment.class);
                            n.putExtra("session", sessionGetter);
                            startActivityForResult(n, CLOSEINTENT);
//                        }

                         Log.d("full", "onPlayerStateChanged: Video ended.");
//                        videoPlayer.seekTo(0);

                        break;
                    case Player.STATE_IDLE:

                        break;
                    case Player.STATE_READY:
                        ivThumbnail.setVisibility(View.GONE);

                        Log.e("full", "onPlayerStateChanged: Ready to play.");
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }



                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });


    }
    public void releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
            Log.e("player", "releasedcalled");

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoPlayer != null) {
            videoPlayer.stop();
            videoPlayer.release();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CLOSEINTENT) {
            if (data != null) {
                Intent n = new Intent();
                n.putExtra("finish", true);
                setResult(CLOSEINTENT, n);
                finish();

            }
        }
    }
}
