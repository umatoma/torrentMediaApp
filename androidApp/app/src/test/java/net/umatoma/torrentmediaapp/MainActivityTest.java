package net.umatoma.torrentmediaapp;

import android.widget.TextView;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {
    @Test
    public void mainActivity_shouldHaveHelloWorldTextView() {
        MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);


        TextView viewHelloWorld = mainActivity.findViewById(R.id.hello_world);
        Assert.assertEquals(viewHelloWorld.getText().toString(), "Hello World!");
    }
}
