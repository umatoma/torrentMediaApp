package net.umatoma.torrentmediaapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.umatoma.torrentmediaapp.R;

import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.List;

public class MediaRendererDevicesAdapter
        extends RecyclerView.Adapter<MediaRendererDevicesAdapter.MediaRendererDeviceViewHolder> {

    private List<Device> mediaRendererDevices;
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

    public Device getItem(int position) {
        return this.mediaRendererDevices.get(position);
    }

    public MediaRendererDevicesAdapter setItems(List<Device> devices) {
        this.mediaRendererDevices.clear();
        this.mediaRendererDevices.addAll(devices);
        return this;
    }

    public MediaRendererDevicesAdapter addItem(Device device) {
        this.mediaRendererDevices.add(device);
        return this;
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public class MediaRendererDeviceViewHolder extends RecyclerView.ViewHolder {

        TextView deviceDisplayNameTextView;
        TextView deviceFriendlyNameTextView;

        public MediaRendererDeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            this.deviceDisplayNameTextView =
                    itemView.findViewById(R.id.media_renderer_device_display_name_tv);
            this.deviceFriendlyNameTextView =
                    itemView.findViewById(R.id.media_renderer_device_friendly_name_tv);
        }

        public void bind(Device device) {
            this.deviceDisplayNameTextView.setText(device.getDisplayString());
            this.deviceFriendlyNameTextView.setText(device.getDetails().getFriendlyName());
        }
    }
}
