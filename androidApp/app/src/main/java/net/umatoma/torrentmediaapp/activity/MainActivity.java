package net.umatoma.torrentmediaapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import net.umatoma.torrentmediaapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button showDownloadingFilesButton = findViewById(R.id.show_downloading_files_btn);
        showDownloadingFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startDownloadedFilesActivityIntent =
                        new Intent(MainActivity.this, DownloadedFilesActivity.class);
                startActivity(startDownloadedFilesActivityIntent);
            }
        });
    }
}
