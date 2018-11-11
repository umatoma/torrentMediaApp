package net.umatoma.torrentmediaapp.repository;

import android.util.Log;

import net.umatoma.torrentmediaapp.BuildConfig;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;

public class TorrentRepository {

    private static final String TAG = "TorrentRepository";

    private TorrentService torrentService;

    public TorrentRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.TORRENT_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        this.torrentService = retrofit.create(TorrentRepository.TorrentService.class);
    }

    public void searchTorrent(final TorrentRepositoryCallback callback) {
        this.torrentService.searchTorrent().enqueue(new Callback<TorrentRss>() {
            @Override
            public void onResponse(Call<TorrentRss> call, Response<TorrentRss> response) {
                TorrentRss torrentRss = response.body();
                callback.onResponse(torrentRss.getChannel());
            }

            @Override
            public void onFailure(Call<TorrentRss> call, Throwable t) {
                Log.d(TAG, "onFailure: ", t);
            }
        });
    }

    public interface TorrentService {
        @GET("/rss.php")
        Call<TorrentRss> searchTorrent();
    }

    public interface TorrentRepositoryCallback {
        void onResponse(TorrentChannel torrentChannel);
    }

}
