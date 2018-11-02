package net.umatoma.torrentmediaapp;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

public class TorrentMediaAndroidTestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        return super.newApplication(cl, TestTorrentMediaApplication.class.getName(), context);
    }
}
