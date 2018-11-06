package net.umatoma.torrentmediaapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.umatoma.torrentmediaapp.activity.DownloadedFilesActivity;
import net.umatoma.torrentmediaapp.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class, false, false);

    @Before
    public void setUp() {
        Intents.init();
        this.activityTestRule.launchActivity(new Intent());
    }

    @After
    public void tearDown() {
        Intents.release();
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
    public void whenClickingCastButton_ItShouldStartDownloadedFilesActivity() {
        onView(withText("Show Downloaded Files")).perform(click());


        intended(hasComponent(DownloadedFilesActivity.class.getName()));
    }
}
