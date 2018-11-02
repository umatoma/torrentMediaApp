package net.umatoma.torrentmediaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button showDownloadingFilesButton = findViewById(R.id.show_downloading_files_button);
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
