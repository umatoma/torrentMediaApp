package net.umatoma.torrentmediaapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.umatoma.torrentmediaapp.R;
import net.umatoma.torrentmediaapp.repository.TorrentItem;

import java.util.ArrayList;
import java.util.List;

public class TorrentItemsAdapter
        extends RecyclerView.Adapter<TorrentItemsAdapter.TorrentItemViewHolder> {

    private List<TorrentItem> torrentItems;
    private OnClickItemListener onClickItemListener;

    public TorrentItemsAdapter() {
        this.torrentItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public TorrentItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.torrent_item_list_item, viewGroup, false);
        return new TorrentItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TorrentItemViewHolder holder, final int position) {
        holder.bind(this.torrentItems.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TorrentItemsAdapter.this.onClickItemListener.onClickItem(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.torrentItems.size();
    }

    public TorrentItemsAdapter setItems(List<TorrentItem> torrentItems) {
        this.torrentItems = torrentItems;
        return this;
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public class TorrentItemViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView pubDateTextView;

        public TorrentItemViewHolder(@NonNull View itemView) {
            super(itemView);

            this.titleTextView = itemView.findViewById(R.id.torrent_item_title_tv);
            this.pubDateTextView = itemView.findViewById(R.id.torrent_item_pub_date_tv);
        }

        public void bind(TorrentItem torrentItem) {
            this.titleTextView.setText(torrentItem.getTitle());
            this.pubDateTextView.setText(torrentItem.getPubDate());
        }

    }

}
