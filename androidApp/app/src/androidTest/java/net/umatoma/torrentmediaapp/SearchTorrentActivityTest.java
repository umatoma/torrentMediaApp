package net.umatoma.torrentmediaapp;

import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SearchTorrentActivityTest {

    @Rule
    public ActivityTestRule<SearchTorrentActivity> activityTestRule =
            new ActivityTestRule<>(SearchTorrentActivity.class, true, false);

    private MockWebServer mockWebServer;

    private void startMockWebServer(Dispatcher dispatcher) throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.setDispatcher(dispatcher);
        this.mockWebServer.start(8080);
    }

    private void waitLoadingTorrents() {
        RecyclerView recyclerView = this.activityTestRule.getActivity().findViewById(R.id.torrents_rv);
        LoadingIdlingResource idlingResource = new LoadingIdlingResource(recyclerView);
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @Before
    public void setUp() throws IOException {
        Intents.init();
        TestServerDispatcher dispatcher = new TestServerDispatcher()
                .setSearchTorrentMockResponse(new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/rss+xml; charset=utf-8")
                        .setBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                                "<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                                "<channel>" +
                                "<item>" +
                                "<category>Raws</category>" +
                                "<title>TEST_TITLE_TEXT</title>" +
                                "<link><![CDATA[magnet:?xt=urn:btih:123456789ABCDEFGHIJKLMNOPQRSTUV]]></link>" +
                                "<description><![CDATA[TEST_DESCRIPTION_TEXT]]></description>" +
                                "<guid><![CDATA[https://example.com/details.php?id=1234567 ]]></guid>" +
                                "<pubDate>Sun, 11 Nov 2018 08:26:21 GMT</pubDate>" +
                                "</item>" +
                                "</channel>" +
                                "</rss>"
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
    public void whenStartingTheActivity_ItShouldDisplayTorrents() {
        this.waitLoadingTorrents();


        onView(withId(R.id.torrents_rv))
                .check(matches(hasDescendant(withText("TEST_TITLE_TEXT"))))
                .check(matches(hasDescendant(withText("Sun, 11 Nov 2018 08:26:21 GMT"))));
    }
}
