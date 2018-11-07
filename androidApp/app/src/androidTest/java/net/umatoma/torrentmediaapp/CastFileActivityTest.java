package net.umatoma.torrentmediaapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.umatoma.torrentmediaapp.activity.CastFileActivity;
import net.umatoma.torrentmediaapp.service.CustomAndroidUpnpService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;

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

    @Test
    public void whenStartingTheActivity_ItShouldStartUPnPService() {
        assertThat(isServiceRunning(CustomAndroidUpnpService.class.getName()), is(true));
    }

    public boolean isServiceRunning(String serviceClassName) {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }

        return false;
    }
}
