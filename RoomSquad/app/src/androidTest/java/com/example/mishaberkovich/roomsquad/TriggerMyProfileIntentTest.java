package com.example.mishaberkovich.roomsquad;

import junit.framework.TestSuite;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.firebase.client.Firebase;

import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * JUnit3 Ui Tests for {@link LoginActivity} using the {@link AndroidJUnit4}. This class
 * uses the Junit3 syntax for tests.
 *
 * <p> With the new AndroidJUnit runner you can run both JUnit3 and JUnit4 tests in a single test
 * test suite. The {@link android.support.test.internal.runner.junit4.AndroidJUnit4ClassRunner} which extends JUnit's {@link
 * AllDefaultPossibilitiesBuilder} will create a single {@link TestSuite} from all tests and run
 * them. </p>
 */
@LargeTest
public class TriggerMyProfileIntentTest
        extends ActivityInstrumentationTestCase2<MainMenuActivity>
{
    public TriggerMyProfileIntentTest() {
        super(MainMenuActivity.class);
    }

    String testEmail = "hello@example.com";
    String testPassword = "pastasalad";


    @Rule
    public ActivityTestRule<MainMenuActivity> mActivityRule =
            new ActivityTestRule<>(MainMenuActivity.class);

    private MainMenuActivity mActivity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Espresso does not start the Activity for you we need to do this manually here.
        if (mActivity == null) {
            mActivity = getActivity();
            Firebase.setAndroidContext(mActivity);
            long start = System.currentTimeMillis();
            while (!mActivity.isInitialized){
                Thread.sleep(300);  //wait until FireBase is totally initialized
                if ( (System.currentTimeMillis() - start ) >= 1000 )
                    throw new TimeoutException(this.getClass().getName() +"Setup timeOut");
            }
        }
    }

    @Test
    public void testPreconditions() {
        assertThat(mActivity, notNullValue());
    }

    @Test
    public void triggerMyProfileIntentTest() {
        // check that the button is there
        onView(withId(R.id.menu_to_profile_button)).check(matches(notNullValue()));
        onView(withId(R.id.menu_to_profile_button)).perform(click());
        intended(toPackage("com.example.mishaberkovich.roomsquad"));
    }

}