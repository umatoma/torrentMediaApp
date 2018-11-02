package net.umatoma.torrentmediaapp;

import net.umatoma.torrentmediaapp.repository.RepositoryConfig;

public class TestTorrentMediaApplication extends TorrentMediaApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        this.inject(new RepositoryConfig() {
            @Override
            public String getServerUrl() {
                return "http://127.0.0.1:8080";
            }
        });
    }
}
