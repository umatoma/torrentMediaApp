package net.umatoma.torrentmediaapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import net.umatoma.torrentmediaapp.R;
import net.umatoma.torrentmediaapp.adapter.DownloadedFilesAdapter;
import net.umatoma.torrentmediaapp.adapter.OnClickItemListener;
import net.umatoma.torrentmediaapp.repository.DownloadedFile;
import net.umatoma.torrentmediaapp.repository.DownloadedFileRepository;

import java.util.List;


public class DownloadedFilesActivity extends AppCompatActivity {

    private DownloadedFilesAdapter downloadedFilesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_files);


        this.downloadedFilesAdapter = new DownloadedFilesAdapter();
        this.downloadedFilesAdapter.setOnClickItemListener(new OnClickItemListener() {
            @Override
            public void onClickItem(View view, int position) {
                DownloadedFile downloadedFile = DownloadedFilesActivity.this.downloadedFilesAdapter.getItem(position);

                Intent startCastFileActivityIntent = new Intent(DownloadedFilesActivity.this, PlayMediaActivity.class);
                startCastFileActivityIntent.putExtra(PlayMediaActivity.KEY_FILE_NAME, downloadedFile.getName());

                startActivity(startCastFileActivityIntent);
            }
        });


        RecyclerView downloadedFilesRecyclerView = findViewById(R.id.downloaded_files_rv);
        downloadedFilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        downloadedFilesRecyclerView.setAdapter(this.downloadedFilesAdapter);


        loadDownloadedFiles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.downloaded_files_activity_menu, menu);

        Drawable icon = menu.findItem(R.id.search_torrent).getIcon();
        icon.mutate();
        icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_torrent:
                Intent startSearchTorrentActivityIntent =
                        new Intent(this, SearchTorrentActivity.class);
                startActivity(startSearchTorrentActivityIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadDownloadedFiles() {
        final DownloadedFilesAdapter adapter = DownloadedFilesActivity.this.downloadedFilesAdapter;

        DownloadedFileRepository downloadedFileRepository = new DownloadedFileRepository();
        downloadedFileRepository.getDownloadedFiles(new DownloadedFileRepository.DownloadedFileRepositoryCallback() {
            @Override
            public void onResponse(List<DownloadedFile> downloadedFiles) {
                adapter.setItems(downloadedFiles);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
