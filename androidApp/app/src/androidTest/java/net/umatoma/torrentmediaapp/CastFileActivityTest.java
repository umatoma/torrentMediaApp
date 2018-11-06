package net.umatoma.torrentmediaapp;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.umatoma.torrentmediaapp.activity.CastFileActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class CastFileActivityTest {

    @Rule
    public ActivityTestRule<CastFileActivity> activityTestRule =
            new ActivityTestRule<>(CastFileActivity.class);

    @Before
    public void setUp() {
        Intent intent = new Intent();
        intent.putExtra("fileName", "FILE_A.txt");
        this.activityTestRule.launchActivity(intent);
    }

    @Test
    public void whenStartingTheActivity_ItShouldDisplayFileName() {
        onView(withText("FILE_A.txt")).check(matches(isDisplayed()));
    }

}
