package net.umatoma.torrentmediaapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.umatoma.torrentmediaapp.R;
import net.umatoma.torrentmediaapp.upnp.UpnpDevice;

import java.util.ArrayList;
import java.util.List;

public class MediaRendererDevicesAdapter
        extends RecyclerView.Adapter<MediaRendererDevicesAdapter.MediaRendererDeviceViewHolder> {

    private List<UpnpDevice> mediaRendererDevices;
    private OnClickItemListener onClickItemListener;

    public MediaRendererDevicesAdapter() {
        this.mediaRendererDevices = new ArrayList<>();
    }

    @NonNull
    @Override
    public MediaRendererDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.media_renderer_device_list_item, viewGroup, false);
        return new MediaRendererDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaRendererDeviceViewHolder holder, final int position) {
        holder.bind(this.mediaRendererDevices.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaRendererDevicesAdapter.this.onClickItemListener.onClickItem(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mediaRendererDevices.size();
    }

    public UpnpDevice getItem(int position) {
        return this.mediaRendererDevices.get(position);
    }

    public MediaRendererDevicesAdapter setItems(List<UpnpDevice> upnpDevices) {
        this.mediaRendererDevices.clear();
        this.mediaRendererDevices.addAll(upnpDevices);
        return this;
    }

    public MediaRendererDevicesAdapter addItem(UpnpDevice upnpDevice) {
        this.mediaRendererDevices.add(upnpDevice);
        return this;
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public class MediaRendererDeviceViewHolder extends RecyclerView.ViewHolder {

        TextView deviceDisplayNameTextView;
        TextView deviceManufacturerTextView;
        TextView deviceServerLocationTextView;

        public MediaRendererDeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            this.deviceDisplayNameTextView =
                    itemView.findViewById(R.id.media_renderer_device_friendly_name_tv);
            this.deviceManufacturerTextView =
                    itemView.findViewById(R.id.media_renderer_device_manufacturer_tv);
            this.deviceServerLocationTextView =
                    itemView.findViewById(R.id.media_renderer_server_location_tv);
        }

        public void bind(UpnpDevice upnpDevice) {
            this.deviceDisplayNameTextView.setText(upnpDevice.getFriendlyName());
            this.deviceManufacturerTextView.setText(upnpDevice.getManufacturer());
            this.deviceServerLocationTextView.setText(upnpDevice.getServerLocation());
        }
    }
}
