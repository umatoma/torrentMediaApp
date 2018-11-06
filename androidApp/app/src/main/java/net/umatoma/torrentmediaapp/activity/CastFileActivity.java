package net.umatoma.torrentmediaapp.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.umatoma.torrentmediaapp.R;
import net.umatoma.torrentmediaapp.adapter.MediaRendererDevicesAdapter;
import net.umatoma.torrentmediaapp.adapter.OnClickItemListener;
import net.umatoma.torrentmediaapp.service.CustomAndroidUpnpService;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

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
                Device device = CastFileActivity.this.mediaRendererDevicesAdapter.getItem(position);
                Toast.makeText(CastFileActivity.this, device.getDisplayString(), Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());

        RecyclerView devicesRecycleView = findViewById(R.id.media_renderer_devices_rv);
        devicesRecycleView.setLayoutManager(layoutManager);
        devicesRecycleView.addItemDecoration(itemDecoration);
        devicesRecycleView.setAdapter(this.mediaRendererDevicesAdapter);


        Intent androidUpnpServiceIntent = new Intent(this, CustomAndroidUpnpService.class);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AndroidUpnpService androidUpnpService = (AndroidUpnpService) service;
                androidUpnpService.getRegistry().addListener(new CustomRegistryListener());

                androidUpnpService.getControlPoint().search();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        getApplicationContext().bindService(
                androidUpnpServiceIntent,
                serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    public class CustomRegistryListener extends DefaultRegistryListener {
        @Override
        public void deviceAdded(Registry registry, final Device device) {
            DeviceType deviceType = device.getType();

            if (deviceType.getType().equals("MediaRenderer")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CastFileActivity.this
                                .mediaRendererDevicesAdapter
                                .addItem(device)
                                .notifyDataSetChanged();
                    }
                });
            }
        }
    }
}
