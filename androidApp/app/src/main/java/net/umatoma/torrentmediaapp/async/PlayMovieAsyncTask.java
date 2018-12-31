package net.umatoma.torrentmediaapp.async;

import android.os.AsyncTask;

import net.umatoma.torrentmediaapp.upnp.UpnpAVTransportPlayer;
import net.umatoma.torrentmediaapp.upnp.UpnpDevice;

public class PlayMovieAsyncTask extends AsyncTask<String, Void, Void> {
    private Exception exception;
    private UpnpDevice upnpDevice;
    private Callback callback;

    public interface Callback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public PlayMovieAsyncTask(UpnpDevice upnpDevice, Callback callback) {
        this.upnpDevice = upnpDevice;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... currentUris) {
        String currentUri = currentUris[0];
        UpnpAVTransportPlayer avTransportPlayer = new UpnpAVTransportPlayer(this.upnpDevice);

        try {
            avTransportPlayer.stop();
            avTransportPlayer.setAVTransportURI(currentUri);
            avTransportPlayer.play();
        } catch (Exception e) {
            this.exception = e;
            this.cancel(true);
        }

        return null;
    }

    @Override
    protected void onCancelled() {
        if (this.exception != null) {
            this.callback.onFailure(this.exception);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        this.callback.onSuccess();
    }
}
