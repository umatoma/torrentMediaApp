package net.umatoma.torrentmediaapp.repository;

import android.util.Log;

import net.umatoma.torrentmediaapp.BuildConfig;

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
        this.downloadedFilesService.getDownloadedFiles().enqueue(new Callback<List<DownloadedFile>>() {
            @Override
            public void onResponse(Call<List<DownloadedFile>> call, Response<List<DownloadedFile>> response) {
                List<net.umatoma.torrentmediaapp.repository.DownloadedFile> downloadedFiles = response.body();

                callback.onResponse(downloadedFiles);
            }

            @Override
            public void onFailure(Call<List<DownloadedFile>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public interface DownloadedFilesService {
        @GET("/files")
        Call<List<DownloadedFile>> getDownloadedFiles();
    }

    public interface DownloadedFileRepositoryCallback {
        void onResponse(List<net.umatoma.torrentmediaapp.repository.DownloadedFile> downloadedFiles);
    }
}
