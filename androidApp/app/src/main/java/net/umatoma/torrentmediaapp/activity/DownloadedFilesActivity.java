package net.umatoma.torrentmediaapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

                Intent startCastFileActivityIntent = new Intent(DownloadedFilesActivity.this, CastFileActivity.class);
                startCastFileActivityIntent.putExtra(CastFileActivity.KEY_FILE_NAME, downloadedFile.getName());

                startActivity(startCastFileActivityIntent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());

        RecyclerView downloadedFilesRecyclerView = findViewById(R.id.downloaded_files_rv);
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
                adapter.setItems(downloadedFiles);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
