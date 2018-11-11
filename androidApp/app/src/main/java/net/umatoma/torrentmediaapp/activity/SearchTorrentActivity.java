package net.umatoma.torrentmediaapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.umatoma.torrentmediaapp.R;
import net.umatoma.torrentmediaapp.adapter.OnClickItemListener;
import net.umatoma.torrentmediaapp.adapter.TorrentItemsAdapter;
import net.umatoma.torrentmediaapp.repository.TorrentChannel;
import net.umatoma.torrentmediaapp.repository.TorrentRepository;

public class SearchTorrentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_torrent);


        final TorrentItemsAdapter torrentItemsAdapter = new TorrentItemsAdapter();
        torrentItemsAdapter.setOnClickItemListener(new OnClickItemListener() {
            @Override
            public void onClickItem(View view, int position) {

            }
        });

        RecyclerView torrentsRecyclerView = findViewById(R.id.torrents_rv);
        torrentsRecyclerView.setAdapter(torrentItemsAdapter);
        torrentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TorrentRepository torrentRepository = new TorrentRepository();
        torrentRepository.searchTorrent(new TorrentRepository.TorrentRepositoryCallback() {
            @Override
            public void onResponse(TorrentChannel torrentChannel) {
                torrentItemsAdapter.setItems(torrentChannel.getItems());
                torrentItemsAdapter.notifyDataSetChanged();
            }
        });
    }
}
