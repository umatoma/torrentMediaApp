package net.umatoma.torrentmediaapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.umatoma.torrentmediaapp.R;
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
        this.downloadedFilesAdapter.setOnClickItemListener(new DownloadedFilesAdapter.OnClickItemListener() {
            @Override
            public void onClickItem(View view, int position) {
                Intent startCastFileActivityIntent = new Intent(DownloadedFilesActivity.this, CastFileActivity.class);
                startActivity(startCastFileActivityIntent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());

        RecyclerView downloadedFilesRecyclerView = findViewById(R.id.downloaded_files_recycler_view);
        downloadedFilesRecyclerView.setLayoutManager(layoutManager);
        downloadedFilesRecyclerView.addItemDecoration(itemDecoration);
        downloadedFilesRecyclerView.setAdapter(this.downloadedFilesAdapter);

        loadDownloadedFiles();
    }

    private void loadDownloadedFiles() {
        final DownloadedFilesAdapter adapter = DownloadedFilesActivity.this.downloadedFilesAdapter;

        DownloadedFileRepository downloadedFileRepository = new DownloadedFileRepository();
        downloadedFileRepository.getDownloadedFiles(new DownloadedFileRepository.DownloadedFileRepositoryCallback() {
            @Override
            public void onResponse(List<DownloadedFile> downloadedFiles) {
                adapter.setDownloadedFiles(downloadedFiles);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
