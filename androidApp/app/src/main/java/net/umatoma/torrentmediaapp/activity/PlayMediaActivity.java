package net.umatoma.torrentmediaapp.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import net.umatoma.torrentmediaapp.R;
import net.umatoma.torrentmediaapp.adapter.MediaRendererDevicesAdapter;
import net.umatoma.torrentmediaapp.adapter.OnClickItemListener;
import net.umatoma.torrentmediaapp.async.PlayMovieAsyncTask;
import net.umatoma.torrentmediaapp.repository.DownloadedFileRepository;
import net.umatoma.torrentmediaapp.upnp.SsdpClient;
import net.umatoma.torrentmediaapp.upnp.UpnpDevice;

public class PlayMediaActivity extends AppCompatActivity {

    public static final String KEY_FILE_NAME = "fileName";

    private static final int UPDATE_PROGRESS_INTERVAL_MS = 500;

    private String downloadedFileName;

    private VideoView videoView;
    private ConstraintLayout mediaPlayerControlLayout;
    private SeekBar mediaPlayerSeekBar;
    private ImageButton mediaPlayerPlayButton;
    private FloatingActionButton castActionButton;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    private MediaRendererDevicesAdapter mediaRendererDevicesAdapter;

    private Handler handler = new Handler();
    private Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (PlayMediaActivity.this.isDestroyed()) {
                return;
            }

            if (PlayMediaActivity.this.videoView.isPlaying()) {
                int position = PlayMediaActivity.this.videoView.getCurrentPosition();
                PlayMediaActivity.this.mediaPlayerSeekBar.setProgress(position);
            }

            PlayMediaActivity.this.handler.postDelayed(this, UPDATE_PROGRESS_INTERVAL_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_media);


        this.downloadedFileName = getIntent().getStringExtra(KEY_FILE_NAME);
        this.castActionButton = findViewById(R.id.cast_action_fab);
        this.videoView = findViewById(R.id.play_media_vv);
        this.mediaPlayerControlLayout = findViewById(R.id.media_player_control_cl);
        this.mediaPlayerSeekBar = findViewById(R.id.media_player_seek_bar);
        this.mediaPlayerPlayButton = findViewById(R.id.media_player_play_btn);
        this.bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.upnp_devices_cl));


        TextView fileNameTextView = findViewById(R.id.media_player_control_media_name_tv);
        fileNameTextView.setText(this.downloadedFileName);


        this.mediaRendererDevicesAdapter = new MediaRendererDevicesAdapter();
        this.mediaRendererDevicesAdapter.setOnClickItemListener(new OnClickItemListener() {
            @Override
            public void onClickItem(View view, int position) {
                UpnpDevice upnpDevice = PlayMediaActivity.this.mediaRendererDevicesAdapter.getItem(position);
                PlayMediaActivity.this.castMovieToUpnpDevice(upnpDevice);
                PlayMediaActivity.this.hideUpnpDevices();
            }
        });
        RecyclerView devicesRecycleView = findViewById(R.id.upnp_media_renderer_devices_rv);
        devicesRecycleView.setLayoutManager(new LinearLayoutManager(this));
        devicesRecycleView.setAdapter(this.mediaRendererDevicesAdapter);

        this.castActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayMediaActivity.this.showUpnpDevices();
            }
        });


        ViewTreeObserver viewTreeObserver = this.mediaPlayerControlLayout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                PlayMediaActivity.this.adjustPadding();
            }
        });


        this.mediaPlayerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayMediaActivity.this.videoView.isPlaying()) {
                    PlayMediaActivity.this.stopMovie();
                } else {
                    PlayMediaActivity.this.playMovie();
                }
            }
        });
        this.mediaPlayerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    PlayMediaActivity.this.seekMovie(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { /* DO NOTHING */ }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { /* DO NOTHING */ }
        });
        this.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                PlayMediaActivity.this.mediaPlayerSeekBar.setMax(mediaPlayer.getDuration());
                PlayMediaActivity.this.startUpdateProgress();
            }
        });
        this.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                PlayMediaActivity.this.stopMovie();
            }
        });


        this.disableCastActionButton();
        this.hideUpnpDevices();
        this.discoverMediaRendererDevices();
    }

    @Override
    protected void onDestroy() {
        this.stopMovie();

        super.onDestroy();
    }

    private void adjustPadding() {
        videoView.setPadding(
                videoView.getPaddingLeft(),
                videoView.getPaddingTop(),
                videoView.getPaddingRight(),
                this.mediaPlayerControlLayout.getMeasuredHeight());
    }

    private void disableCastActionButton() {
        this.castActionButton.hide();
    }

    private void enableCastActionButton() {
        this.castActionButton.show();
        this.castActionButton.bringToFront();
    }

    private void hideUpnpDevices() {
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void showUpnpDevices() {
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void discoverMediaRendererDevices() {
        new SsdpClient()
                .addDeviceMediaRendererFilter()
                .addExcludeSameUsnFilter()
                .setSsdpDiscoveryListener(new SsdpClient.SsdpDiscoveryListener() {
                    @Override
                    public void onDeviceDiscovered(final UpnpDevice upnpDevice) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PlayMediaActivity.this.mediaRendererDevicesAdapter.addItem(upnpDevice);
                                PlayMediaActivity.this.mediaRendererDevicesAdapter.notifyDataSetChanged();
                                PlayMediaActivity.this.enableCastActionButton();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                })
                .startDiscovery(this);
    }

    private String getMediaFileStreamingUrl() {
        return DownloadedFileRepository.getMediaFileStreamingUrl(this.downloadedFileName);
    }

    private void startUpdateProgress() {
        this.handler.postDelayed(this.updateProgressRunnable, UPDATE_PROGRESS_INTERVAL_MS);
    }

    private void stopUpdateProgress() {
        this.handler.removeCallbacks(this.updateProgressRunnable);
    }

    private void playMovie() {
        Uri videoURI = Uri.parse(this.getMediaFileStreamingUrl());
        this.videoView.setVideoURI(videoURI);
        this.videoView.start();
        this.mediaPlayerPlayButton.setImageResource(R.drawable.ic_stop_black_24dp);
    }

    private void stopMovie() {
        this.stopUpdateProgress();
        this.videoView.stopPlayback();
        this.videoView.seekTo(0);
        this.mediaPlayerSeekBar.setProgress(0);
        this.mediaPlayerPlayButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
    }

    private void seekMovie(int progress) {
        this.videoView.seekTo(progress);
    }

    private void castMovieToUpnpDevice(UpnpDevice upnpDevice) {
        String currentUri = this.getMediaFileStreamingUrl();
        new PlayMovieAsyncTask(upnpDevice, new PlayMovieAsyncTask.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(PlayMediaActivity.this, "Start casting", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(PlayMediaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).execute(currentUri);
    }
}
