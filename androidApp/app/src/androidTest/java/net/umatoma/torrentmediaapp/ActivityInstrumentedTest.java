package net.umatoma.torrentmediaapp;

import android.content.Context;

import org.hamcrest.core.IsAnything;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private MockWebServer mockWebServer = null;

    private void startMockWebServer() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                String requestPath = request.getPath();
                if (requestPath.equals("/files")) {
                    return new MockResponse().setResponseCode(200).setBody("[\"AAA\",\"BBB\"]");
                }
                return new MockResponse().setResponseCode(400);
            }
        });
        this.mockWebServer.start(8080);
    }

    @After
    public void tearDown() throws IOException {
        if (this.mockWebServer != null) {
            this.mockWebServer.shutdown();
        }
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getContext();

        assertEquals("net.umatoma.torrentmediaapp.test", appContext.getPackageName());
    }

    @Test
    public void whenThereIsMainActivity_ItShouldHaveHelloWorldText() {
        onView(withText("Hello World!")).check(matches(isDisplayed()));
    }

    @Test
    public void whenThereIsMainActivity_ItShouldHaveShowDownloadedFilesButton() {
        onView(withText("Show Downloaded Files")).check(matches(isDisplayed()));
    }

    @Test
    public void whenClickingCastButton_ItShouldStartDownloadedFilesActivity() throws IOException {
        startMockWebServer();


        onView(withText("Show Downloaded Files")).perform(click());


        onData(IsAnything.anything())
                .inAdapterView(withId(R.id.downloaded_files_list_view))
                .atPosition(0)
                .check(matches(withText("AAA")));
        onData(IsAnything.anything())
                .inAdapterView(withId(R.id.downloaded_files_list_view))
                .atPosition(1)
                .check(matches(withText("BBB")));
    }
}
