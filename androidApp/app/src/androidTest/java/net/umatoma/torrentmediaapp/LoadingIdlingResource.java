package net.umatoma.torrentmediaapp;

import android.support.test.espresso.IdlingResource;
import android.support.v7.widget.RecyclerView;

public class LoadingIdlingResource implements IdlingResource {

    private RecyclerView recyclerView;
    private ResourceCallback resourceCallback;

    public LoadingIdlingResource(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public String getName() {
        return LoadingIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        RecyclerView.Adapter adapter = this.recyclerView.getAdapter();
        boolean isIdle = (adapter != null) && (adapter.getItemCount() != 0);

        if (isIdle && this.resourceCallback != null) {
            this.resourceCallback.onTransitionToIdle();
        }

        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }

}
