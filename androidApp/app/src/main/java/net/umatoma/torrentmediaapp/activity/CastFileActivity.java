package net.umatoma.torrentmediaapp.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.VideoView;

import net.umatoma.torrentmediaapp.R;
import net.umatoma.torrentmediaapp.adapter.MediaRendererDevicesAdapter;
import net.umatoma.torrentmediaapp.adapter.OnClickItemListener;
import net.umatoma.torrentmediaapp.repository.DownloadedFileRepository;
import net.umatoma.torrentmediaapp.upnp.SsdpClient;
import net.umatoma.torrentmediaapp.upnp.UpnpAVTransportPlayer;
import net.umatoma.torrentmediaapp.upnp.UpnpDevice;

import static net.umatoma.torrentmediaapp.upnp.UpnpAVTransportPlayer.CommandCallback;

public class CastFileActivity extends AppCompatActivity {

    public static final String KEY_FILE_NAME = "fileName";

    private String downloadedFileName;

    private VideoView videoView;
    private ConstraintLayout footerConstraintLayout;
    private FloatingActionButton castActionButton;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    private MediaRendererDevicesAdapter mediaRendererDevicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_file);


        this.downloadedFileName = getIntent().getStringExtra(KEY_FILE_NAME);
        this.castActionButton = findViewById(R.id.cast_action_fab);
        this.videoView = findViewById(R.id.cast_file_vv);
        this.footerConstraintLayout = findViewById(R.id.cast_file_footer_cl);
        this.bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));


        TextView fileNameTextView = findViewById(R.id.cast_file_name_tv);
        fileNameTextView.setText(this.downloadedFileName);


        this.mediaRendererDevicesAdapter = new MediaRendererDevicesAdapter();
        this.mediaRendererDevicesAdapter.setOnClickItemListener(new OnClickItemListener() {
            @Override
            public void onClickItem(View view, int position) {
                UpnpDevice upnpDevice = CastFileActivity.this.mediaRendererDevicesAdapter.getItem(position);
                CastFileActivity.this.playMovie(upnpDevice);
                CastFileActivity.this.hideUpnpDevices();
            }
        });
        RecyclerView devicesRecycleView = findViewById(R.id.media_renderer_devices_rv);
        devicesRecycleView.setLayoutManager(new LinearLayoutManager(this));
        devicesRecycleView.setAdapter(this.mediaRendererDevicesAdapter);


        ViewTreeObserver viewTreeObserver = this.footerConstraintLayout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CastFileActivity.this.adjustPadding();
            }
        });


        this.castActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CastFileActivity.this.showUpnpDevices();
            }
        });


        this.disableCastActionButton();
        this.hideUpnpDevices();
        this.discoverMediaRendererDevices();
    }

    private void adjustPadding() {
        videoView.setPadding(
                videoView.getPaddingLeft(),
                videoView.getPaddingTop(),
                videoView.getPaddingRight(),
                this.footerConstraintLayout.getMeasuredHeight());
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
                                CastFileActivity.this.mediaRendererDevicesAdapter.addItem(upnpDevice);
                                CastFileActivity.this.mediaRendererDevicesAdapter.notifyDataSetChanged();
                                CastFileActivity.this.enableCastActionButton();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                })
                .startDiscovery(this);
    }

    private void playMovie(UpnpDevice upnpDevice) {
        String currentUri = DownloadedFileRepository.getMediaFileStreamingUrl(this.downloadedFileName);

        final UpnpAVTransportPlayer avTransportPlayer = new UpnpAVTransportPlayer(upnpDevice);
        avTransportPlayer.setAVTransportURI(currentUri, new CommandCallback() {
            @Override
            public void onCommandFailure(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onCommandSuccess() {
                avTransportPlayer.play(new CommandCallback() {
                    @Override
                    public void onCommandFailure(Exception e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onCommandSuccess() {
                        avTransportPlayer.seek(new CommandCallback() {
                            @Override
                            public void onCommandFailure(Exception e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onCommandSuccess() {
                            }
                        });
                    }
                });
            }
        });
    }
}
