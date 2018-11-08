package net.umatoma.torrentmediaapp.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
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

import java.util.ArrayList;

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


        startUpnpService();
    }

    private void startUpnpService() {
        Intent androidUpnpServiceIntent = new Intent(this, CustomAndroidUpnpService.class);

        final DefaultRegistryListener registryListener = new DefaultRegistryListener() {
            @Override
            public void deviceAdded(Registry registry, final Device device) {
                if (new MediaRendererDeviceType().equals(device.getType())) {
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
        };

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AndroidUpnpService androidUpnpService = (AndroidUpnpService) service;

                Registry serviceRegistry = androidUpnpService.getRegistry();
                ArrayList<Device> devices = new ArrayList<>(serviceRegistry.getDevices(new MediaRendererDeviceType()));
                CastFileActivity.this.mediaRendererDevicesAdapter.setItems(devices);
                CastFileActivity.this.mediaRendererDevicesAdapter.notifyDataSetChanged();

                serviceRegistry.addListener(registryListener);
                androidUpnpService.getControlPoint().search();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {}
        };

        getApplicationContext().bindService(
                androidUpnpServiceIntent,
                serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    public class MediaRendererDeviceType extends DeviceType {
        public MediaRendererDeviceType() {
            super("schemas-upnp-org", "MediaRenderer", 1);
        }
    }
}
