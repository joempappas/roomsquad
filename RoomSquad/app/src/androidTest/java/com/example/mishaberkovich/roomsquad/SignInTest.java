package com.example.mishaberkovich.roomsquad;

import junit.framework.TestSuite;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

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
public class SignInTest
        extends ActivityInstrumentationTestCase2<LoginActivity>
{
    public SignInTest() {
        super(LoginActivity.class);
    }

    String testEmail = "hello@example.com";
    String testPassword = "pastasalad";


    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    private LoginActivity mActivity;

    @Override
    protected void setUp() throws Exception {
        // Espresso does not start the Activity for you we need to do this manually here.
        mActivity = getActivity();
    }

    @Test
    public void testPreconditions() {
        assertThat(mActivity, notNullValue());
    }

    @Test
    public void testEmailEntry() {
        onView(withId(R.id.email)).perform(typeText(testEmail));
        onView(withId(R.id.email)).check(matches(withText(testEmail)));
    }

    @Test
    public void testPasswordEntry() {
        onView(withId(R.id.password)).perform(typeText(testPassword));
        onView(withId(R.id.password)).check(matches(withText(testPassword)));
    }

    @Test
    public void triggerSignInIntentTest() {
        // check that the button is there
        onView(withId(R.id.login_bottom_bar)).check(matches(notNullValue()));
        onView(withId(R.id.login_bottom_bar)).check(matches(withText("Login")));
        onView(withId(R.id.login_bottom_bar)).perform(click());
        intended(toPackage("com.example.mishaberkovich.roomsquad"));
    }

    @Test
    public void triggerRegisterIntentTest() {
        // check that the button is there
        onView(withId(R.id.email)).perform(typeText(testEmail));
        onView(withId(R.id.password)).perform(typeText(testPassword));
        onView(withId(R.id.email_register_button)).perform(click());
    }

}