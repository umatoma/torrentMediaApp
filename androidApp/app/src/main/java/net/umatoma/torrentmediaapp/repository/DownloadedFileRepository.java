package net.umatoma.torrentmediaapp.repository;

import android.content.Context;
import android.util.Log;

import net.umatoma.torrentmediaapp.BuildConfig;
import net.umatoma.torrentmediaapp.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class DownloadedFileRepository {

    private static final String TAG = "DownloadedFileRepository";

    private DownloadedFilesService downloadedFilesService;

    public DownloadedFileRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.downloadedFilesService = retrofit.create(DownloadedFilesService.class);
    }

    public void getDownloadedFiles(final DownloadedFileRepositoryCallback callback) {
        this.downloadedFilesService.getDownloadedFiles().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                List<String> body = response.body();
                String[] downloadedFiles = body.toArray(new String[body.size()]);

                callback.onResponse(downloadedFiles);
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public interface DownloadedFilesService {
        @GET("/files")
        Call<List<String>> getDownloadedFiles();
    }

    public interface DownloadedFileRepositoryCallback {
        void onResponse(String[] downloadedFiles);
    }

}
