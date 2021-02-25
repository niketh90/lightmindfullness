package com.seraphic.lightapp.home_module.views;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.stefanodp91.android.circularseekbar.CircularSeekBar;
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
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.home_module.models.ScalableVideoView;
import com.seraphic.lightapp.home_module.models.SessionGetter;
import com.seraphic.lightapp.menuprofile.views.MenuFragment;
import com.seraphic.lightapp.menuprofile.views.StatsFragment;
import com.seraphic.lightapp.utilities.Constants;
import com.seraphic.lightapp.utilities.Utility;

import java.io.IOException;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hiennguyen.me.circleseekbar.CircleSeekBar;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
import static android.view.View.VISIBLE;

public class MediaPlayFragment extends AppCompatActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, PlayerControlView.ProgressUpdateListener {
    private final int CLOSEINTENT = 10;
    PlayerControlView.ProgressUpdateListener progressUpdateListener;
    private static final String TAG = "Media";
    int max = 2000000;
    @BindView(R.id.clAudio)
    ConstraintLayout clAudio;
    @BindView(R.id.pcAudioController)
    PlayerControlView pcAudioController;
    @BindView(R.id.clVideo)
    ConstraintLayout clVideo;
    @BindView(R.id.mVideoView)
    PlayerView mVideoView;
    //    @BindView(R.id.prAudio)
//    CircularSeekBar prAudio;
    boolean isItDailySession = false;
    @BindView(R.id.mAudioView)
    PlayerView mAudioView;
    MediaPlayer mediaPlayer;
    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    AudioManager audioManager;
    SimpleExoPlayer videoPlayer;
    @BindView(R.id.tvDescription)
    TextView tvDescription;
    private Handler myHandler = new Handler();
    @BindView(R.id.circular)
    com.seraphic.lightapp.home_module.models.CircleSeekBar songSeekBar;

    //    @BindView(R.id.exo_progress)
//    DefaultTimeBar exo_progress;
    @OnClick(R.id.ivBack)
    public void gob() {
        finish();
//        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }


    @BindView(R.id.tvAuthorName)
    TextView tvAuthorName;
    @BindView(R.id.tvHeading)
    TextView tvHeading;
    Context mContext;
    AudioAttributes playbackAttributes;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.ivThumbnail)
    ImageView ivThumbnail;

    //    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        mContext = context;
//    }
    @OnClick(R.id.ivFullViewdvid)
    void fullscreen() {
        Intent n = new Intent(this, FullScreenVid.class);
        n.putExtra("session", sessionGetter);
        n.putExtra("dailySession", isItDailySession);

        startActivityForResult(n, CLOSEINTENT);
    }

    @BindView(R.id.vidControlls)
    PlayerControlView vidControlls;
    int i = 10;

    @OnClick(R.id.ivSetting)
    public void goset() {

//        songSeekBar.setProgress(50);

        Intent n = new Intent(this, MenuFragment.class);
        startActivityForResult(n, CLOSEINTENT);
    }

    SessionGetter sessionGetter;
    @BindView(R.id.audio_progrees)
    ProgressBar audio_progrees;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utility.makeStatusBarTransparent(this);
        setContentView(R.layout.media_fragment);
        ButterKnife.bind(this);
        mContext = this;
        init();
    }

    void init() {
        if (getIntent().getExtras() != null) {
            sessionGetter = (SessionGetter) getIntent().getExtras().getSerializable("session");
            isItDailySession = getIntent().getBooleanExtra("dailySession", false);

            if (sessionGetter != null) {
                tvAuthorName.setText(sessionGetter.sessionAuthor.authorName);
                tvDescription.setText(sessionGetter.sessionDescription);
                tvHeading.setText(sessionGetter.sessionName);
                Log.e("url", "" + sessionGetter.sessionUrl);
            }
        }
        if (sessionGetter.sessionType == 2) {
            clAudio.setVisibility(VISIBLE);
            clVideo.setVisibility(View.GONE);

            playAud(sessionGetter);
//            setupAudio(sessionGetter.sessionUrl);
        } else {
            clAudio.setVisibility(View.GONE);
            clVideo.setVisibility(VISIBLE);
            playVid(sessionGetter);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void playAud(SessionGetter sessionGetter) {
        songSeekBar.setProgressDisplay(100);
        songSeekBar.invalidate();

songSeekBar.setSeekBarChangeListener(new com.seraphic.lightapp.home_module.models.CircleSeekBar.OnSeekBarChangedListener() {
    @Override
    public void onPointsChanged(com.seraphic.lightapp.home_module.models.CircleSeekBar circleSeekBar, int points, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(com.seraphic.lightapp.home_module.models.CircleSeekBar circleSeekBar) {

    }

    @Override
    public void onStopTrackingTouch(com.seraphic.lightapp.home_module.models.CircleSeekBar circleSeekBar) {
        Log.e("seek",    " -" + circleSeekBar.getCurrentProgress());
        int points=(int)circleSeekBar.getCurrentProgress();
        float mDUraito = videoPlayer.getDuration() / 1000;
        float mtime = (points * mDUraito) / 2000000;
        mtime = Math.round(mtime);
        long tt = (long) mtime * 1000;

        videoPlayer.seekTo(tt);
    }
});


//        Glide.with(mContext).load(sessionGetter.sessionThumbNail).placeholder(R.mipmap.placeholder1).into(ivThumbnail);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        videoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        // Bind the player to the view.
        mAudioView.setControllerShowTimeoutMs(0);
        mVideoView.setPlayer(videoPlayer);
        pcAudioController.setPlayer(videoPlayer);
        int du = (int) videoPlayer.getDuration() / 1000;
//        float mStep=100/du;
        Log.e("msteppp", du + "");
        //        songSeekBar.setStep(0.09199632f);
//        mVideoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                mContext, Util.getUserAgent(mContext, "RecyclerView VideoPlayer"));
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
                Log.d(TAG, timeline.getPeriodCount() + "onPlayerStateChanged: Video." + reason);

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
                        Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                        if (audio_progrees != null) {

                            audio_progrees.setVisibility(View.VISIBLE);


                        }

                        break;
                    case Player.STATE_ENDED:
//                        releasePlayer();
                        ivThumbnail.setVisibility(VISIBLE);

                        Log.e("ddddd", "" + isItDailySession);
//                        if (isItDailySession){

                        videoPlayer.seekTo(0);

                        pauseSong();

                        Intent n = new Intent(MediaPlayFragment.this, StatsFragment.class);
                        n.putExtra("session", sessionGetter);
                        startActivityForResult(n, CLOSEINTENT);
//                        }

                        Log.d(TAG, "onPlayerStateChanged: Video ended.");
//                        videoPlayer.seekTo(0);

                        break;
                    case Player.STATE_IDLE:

                        break;
                    case Player.STATE_READY:
                        ivThumbnail.setVisibility(View.GONE);

                        myHandler.postDelayed(UpdateSongTime, 100);

                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");
                        if (audio_progrees != null) {
                            audio_progrees.setVisibility(View.GONE);
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
                Log.e(TAG, "33onPlayerStateChanged: Buffering video.");

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.e(TAG, "44onPlayerStateChanged: Buffering video.");

            }

            @Override
            public void onSeekProcessed() {
                Log.e(TAG, "onPlayerseed: seekprocedd video.");

            }
        });


    }

    public void playVid(SessionGetter sessionGetter) {
        Glide.with(mContext).load(sessionGetter.sessionThumbNail).placeholder(R.mipmap.placeholder1).into(ivThumbnail);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        videoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        // Bind the player to the view.
        mVideoView.setControllerShowTimeoutMs(0);
        mVideoView.setPlayer(videoPlayer);
        vidControlls.setPlayer(videoPlayer);
        vidControlls.setProgressUpdateListener(new PlayerControlView.ProgressUpdateListener() {
            @Override
            public void onProgressUpdate(long position, long bufferedPosition) {
                Log.e("@@", position + "" + bufferedPosition);
            }
        });
        mVideoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                mContext, Util.getUserAgent(mContext, "RecyclerView VideoPlayer"));
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
                Log.d(TAG, timeline.getPeriodCount() + "onPlayerStateChanged: Video." + reason);

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
                        Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                        if (progressBar != null) {

                            progressBar.setVisibility(View.VISIBLE);

                        }

                        break;
                    case Player.STATE_ENDED:
//                        releasePlayer();
                        ivThumbnail.setVisibility(VISIBLE);

//                        if (isItDailySession) {
                        videoPlayer.seekTo(0);

                        pauseSong();
                        Intent n = new Intent(MediaPlayFragment.this, StatsFragment.class);
                        n.putExtra("session", sessionGetter);
                        startActivityForResult(n, CLOSEINTENT);
//                        }
                        Log.d(TAG, "onPlayerStateChanged: Video ended.");
//                        videoPlayer.seekTo(0);

                        break;
                    case Player.STATE_IDLE:

                        break;
                    case Player.STATE_READY:
                        ivThumbnail.setVisibility(View.GONE);
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");
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
                Log.e(TAG, "33onPlayerStateChanged: Buffering video.");

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.e(TAG, "44onPlayerStateChanged: Buffering video.");

            }

            @Override
            public void onSeekProcessed() {
                Log.e(TAG, "onPlayerseed: seekprocedd video.");

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
            videoPlayer.setPlayWhenReady(false);
         }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoPlayer != null) {
            videoPlayer.stop();
            videoPlayer.release();
        }
        if (mediaPlayer != null) {
            stopPlay();
        }
        if (myHandler != null)
            myHandler.removeCallbacks(UpdateSongTime);

    }

    void releaseAudioFocusForMyApp(final Context context) {
        audioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }

    public void setupAudio(String url) {
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        mediaPlayer = new MediaPlayer();
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        final Object mFocusLock = new Object();
        boolean mPlaybackDelayed = false;
        boolean mPlaybackNowAuthorized = false;
//        initAudioFocus();

//        playSong(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releasePlayer();

    }

    public void initAudioFocus() {
        playbackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                Log.e("**home", "audio focus Orio " + focusChange);
                //Handle Focus Change
                if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                    Log.e("**home", "audio focus O<<<<Oriorio LOSSTRANSIT");
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            pauseSong();
//                            releaseAudioFocusForMyApp(context);
                        }
                    }

// Pause playback
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    // Resume playback
                    Log.e("**home", "audio focus <<< GAAIn");
//                    if (isCallPause) {
//                        updateMusicPlayer(1);
//                        isCallPause = false;
//                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    Log.e("**home", "audio focus <<<< AUDIOFOCUS_LOSS");
//                    pauseSong(false);

                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            updateMusicPlayer(1);
                            releaseAudioFocusForMyApp(mContext);
                        }

                    }


                } else if (focusChange == AUDIOFOCUS_REQUEST_GRANTED) {
                    Log.e("**home", "audio focus <<<< AUDIOFOCUS_REQUEST_GRANTED");
//                    pauseSong(false);
//                             audioManager.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
//                             audioManager.abandonAudioFocus(this::onAudioFocusChange);
                    // Stop playback
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Handler handler = new Handler();
            audioManager.requestAudioFocus(new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_GAME)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                    .build()
                    )
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(mOnAudioFocusChangeListener).build()
            );
        } else {
            audioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);


        }
    }

    private void pauseSong() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();

            }
        }


    }

    public void updateMusicPlayer(int mcase) {

        switch (mcase) {

            case 1:
                //play
                if (mediaPlayer.isPlaying()) {
                    pauseSong();
//                    mNotificationAction = Constants.ACTION.PAUSE_ACTION_UPDATE;

                } else {
                    if (requestAudioFocusForMyApp(mContext)) {


                        mediaPlayer.start();
//                                    finalTime = mediaPlayer.getDuration();
//                                    startTime = mediaPlayer.getCurrentPosition();
//                                    songSeekBar.setMax((int) finalTime);
//                                    myHandler.postDelayed(UpdateSongTime, 100);


//                            }


                    }
                }
                break;
            case 2:
                //stop
                if (mediaPlayer.isPlaying()) {
                    stopPlay();

                }
                break;


        }


    }


    private void stopPlay() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        Log.e("mediaPlayer", "stopped" + mediaPlayer.isPlaying());

//        pla.setImageResource(android.R.drawable.ic_media_play);
//        handler.removeCallbacks(updatePositionRunnable);
//        songSeekBar.setProgress(0);

    }

    private void playSong(String mediaFile) {

//        currentPlayedSong = songList.get(currentlyPlayingId).name;
        if (mediaPlayer != null) {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(mediaFile);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean requestAudioFocusForMyApp(final Context context) {
//        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        // Request audio focus for playback
        int result = audioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e("**home", "Audio focus received");
            return true;
        } else {
            Log.e("**home", "Audio focus NOT received");
            return false;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CLOSEINTENT) {
            if (data != null) {
                finish();

            }
        }
    }

    @Override
    public void onProgressUpdate(long position, long bufferedPosition) {
        Log.e("prrrrr", position + "" + bufferedPosition);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
//            Log.e("stttt", "t= started");
            if (videoPlayer != null) {
                long startTime = videoPlayer.getCurrentPosition();
                float mTime = startTime / 1000;
                float mDUraito = videoPlayer.getDuration() / 1000;
                float hunderof = (mTime / mDUraito) * 2000000;
                songSeekBar.setProgressDisplay((int) hunderof);
                songSeekBar.invalidate();
                if (hunderof == videoPlayer.getDuration()) {
                    Log.e("stttt", mTime + "t=" + mDUraito + " = " + (int) hunderof);

                }
                myHandler.postDelayed(this, 100);
            }

        }
    };

}


