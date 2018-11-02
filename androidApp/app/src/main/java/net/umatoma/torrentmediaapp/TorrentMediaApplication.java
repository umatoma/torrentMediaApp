package net.umatoma.torrentmediaapp;

import android.app.Application;

import net.umatoma.torrentmediaapp.repository.RepositoryConfig;

public class TorrentMediaApplication extends Application {

    private RepositoryConfig repositoryConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        this.repositoryConfig = new RepositoryConfig() {
            @Override
            public String getServerUrl() {
                return "http://10.0.2.2:8080";
            }
        };
    }

    public RepositoryConfig getRepositoryConfig() {
        return this.repositoryConfig;
    }

    public void inject(RepositoryConfig repositoryConfig) {
        this.repositoryConfig = repositoryConfig;
    }
}
