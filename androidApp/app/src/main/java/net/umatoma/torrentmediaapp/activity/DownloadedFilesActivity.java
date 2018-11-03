package net.umatoma.torrentmediaapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.umatoma.torrentmediaapp.R;
import net.umatoma.torrentmediaapp.repository.DownloadedFileRepository;

import java.util.ArrayList;

public class DownloadedFilesActivity extends AppCompatActivity {

    private ArrayAdapter<String> downloadedFilesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_files);

        this.downloadedFilesAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

        ListView downloadedFilesListView = findViewById(R.id.downloaded_files_list_view);
        downloadedFilesListView.setAdapter(this.downloadedFilesAdapter);

        loadDownloadedFiles();
    }

    private void loadDownloadedFiles() {
        final ArrayAdapter<String> adapter = DownloadedFilesActivity.this.downloadedFilesAdapter;

        DownloadedFileRepository downloadedFileRepository = new DownloadedFileRepository();
        downloadedFileRepository.getDownloadedFiles(new DownloadedFileRepository.DownloadedFileRepositoryCallback() {
            @Override
            public void onResponse(String[] downloadedFiles) {
                adapter.clear();
                adapter.addAll(downloadedFiles);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
