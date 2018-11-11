package net.umatoma.torrentmediaapp;

import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.umatoma.torrentmediaapp.activity.CastFileActivity;
import net.umatoma.torrentmediaapp.activity.DownloadedFilesActivity;
import net.umatoma.torrentmediaapp.activity.SearchTorrentActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class DownloadedFilesActivityTest {
    @Rule
    public ActivityTestRule<DownloadedFilesActivity> activityTestRule =
            new ActivityTestRule<>(DownloadedFilesActivity.class, true, false);

    private MockWebServer mockWebServer = null;

    private void startMockWebServer(Dispatcher dispatcher) throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.setDispatcher(dispatcher);
        this.mockWebServer.start(8080);
    }

    @Before
    public void setUp() throws IOException {
        Intents.init();

        TestServerDispatcher dispatcher = new TestServerDispatcher()
                .setGetFilesMockResponse(new MockResponse()
                        .setResponseCode(200)
                        .setBody("[" +
                                "{\"name\": \"FILE_A.txt\", \"type\": \"file\"}," +
                                "{\"name\": \"DIRECTORY_X\", \"type\": \"directory\"}" +
                                "]"
                        ));
        startMockWebServer(dispatcher);


        this.activityTestRule.launchActivity(new Intent());
    }

    @After
    public void tearDown() throws IOException {
        if (this.mockWebServer != null) {
            this.mockWebServer.shutdown();
        }
        Intents.release();
    }

    @Test
    public void whenStartingTheActivity_ItShouldDisplayDownloadedFiles() {
        onView(withId(R.id.downloaded_files_rv))
                .check(matches(hasDescendant(withText("FILE_A.txt"))))
                .check(matches(hasDescendant(withText("file"))))
                .check(matches(hasDescendant(withText("DIRECTORY_X"))))
                .check(matches(hasDescendant(withText("directory"))));
    }

    @Test
    public void whenClickingTheDownloadedFileItem_ItShouldStartCastFileActivityWithFileName() {
        onView(withId(R.id.downloaded_files_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));


        intended(hasComponent(CastFileActivity.class.getName()));
        intended(hasExtra("fileName", "FILE_A.txt"));
    }

    @Test
    public void whenSelectingTheSearchTorrentMenu_ItShouldStartSearchTorrentActivity() {
        onView(withContentDescription("Search Torrent")).perform(click());


        intended(hasComponent(SearchTorrentActivity.class.getName()));
    }
}
