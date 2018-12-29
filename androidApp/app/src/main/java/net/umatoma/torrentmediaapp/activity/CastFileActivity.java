package net.umatoma.torrentmediaapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import net.umatoma.torrentmediaapp.R;
import net.umatoma.torrentmediaapp.adapter.MediaRendererDevicesAdapter;
import net.umatoma.torrentmediaapp.adapter.OnClickItemListener;
import net.umatoma.torrentmediaapp.upnp.SsdpClient;
import net.umatoma.torrentmediaapp.upnp.UpnpDevice;

public class CastFileActivity extends AppCompatActivity {

    public static final String KEY_FILE_NAME = "fileName";

    private MediaRendererDevicesAdapter mediaRendererDevicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_file);

        Intent intent = getIntent();
        String fileName = intent.getStringExtra(KEY_FILE_NAME);

        TextView fileNameTextView = findViewById(R.id.cast_file_name_tv);
        fileNameTextView.setText(fileName);


        this.mediaRendererDevicesAdapter = new MediaRendererDevicesAdapter();
        this.mediaRendererDevicesAdapter.setOnClickItemListener(new OnClickItemListener() {
            @Override
            public void onClickItem(View view, int position) {
                UpnpDevice upnpDevice = CastFileActivity.this.mediaRendererDevicesAdapter.getItem(position);
                Toast.makeText(CastFileActivity.this, upnpDevice.getFriendlyName(), Toast.LENGTH_SHORT).show();
            }
        });


        final RecyclerView devicesRecycleView = findViewById(R.id.media_renderer_devices_rv);
        devicesRecycleView.setLayoutManager(new LinearLayoutManager(this));
        devicesRecycleView.setAdapter(this.mediaRendererDevicesAdapter);


        final ConstraintLayout constraintLayout = findViewById(R.id.cast_file_footer_cl);
        ViewTreeObserver viewTreeObserver = constraintLayout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                devicesRecycleView.setPadding(
                        devicesRecycleView.getPaddingLeft(),
                        devicesRecycleView.getPaddingTop(),
                        devicesRecycleView.getPaddingRight(),
                        constraintLayout.getMeasuredHeight());
            }
        });

        discoverMediaRendererDevices();
    }

    private void discoverMediaRendererDevices() {
        new SsdpClient()
                .addServerWebOSFilter()
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
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {}
                })
                .startDiscovery(this);
    }
}
