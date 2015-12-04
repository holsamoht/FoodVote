package com.example.local1.foodvote;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.Before;

/** Scenario2
 *  Given   I am a user
 *  And     I enter in a username and password
 *  When    I click the "Login" button
 *  Then    I will be logged into my existing account
 *  When    I press the "+" button, enter in an event name, type of business, and location,
 *          select friends, and then click the "Create" button
 *  Then    An my event will be created
 *  When    I click the "count#" button
 *  Then    A counter will increment to indicate my vote
 */

public class Scenario2 extends ActivityInstrumentationTestCase2<LoginSignUpActivity> {

    private LoginSignUpActivity LSA;
    private FragmentContainer FC;

    private static final String TAG = "Scenario2 ";

    public Scenario2() {
        super(LoginSignUpActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        LSA = getActivity();
    }

    /** Scenario2
     *  Given   I am a user
     *  And     I enter in a username and password
     *  When    I click the "Login" button
     *  Then    I will be logged into my existing account
     *  When    I press the "+" button, enter in an event name, type of business, and location,
     *          select friends, and then click the "Create" button
     *  Then    An my event will be created
     *  When    I click the "count#" button
     *  Then    A counter will increment to indicate my vote
     */
    public void testA(){
        onView(withId(R.id.usernameText))
                .perform(typeText("a"), closeSoftKeyboard());
        onView(withId(R.id.passwordText))
                .perform(typeText("1234"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread.sleep(1000) interrupted.");
        }

        onView(withId(R.id.addEventButton)).perform(click());
        onView(withId(R.id.eventNameText)).perform(typeText("event1"), closeSoftKeyboard());
        onView(withId(R.id.typeOfBusinessText)).perform(typeText("restaurants"), closeSoftKeyboard());
        onView(withId(R.id.locationText)).perform(typeText("La Jolla"), closeSoftKeyboard());
        onData(anything()).inAdapterView(withId(R.id.friendsList)).atPosition(0).perform(click());
        onView(withId(R.id.createEventButton)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread.sleep(1000) interrupted.");
        }

        onView(withId(R.id.count1)).perform(click());
    }

    /*
    public void testB() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread.sleep(1000) interrupted.");
        }

        onView(withId(R.id.viewpager)).perform(swipeLeft());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread.sleep(1000) interrupted.");
        }

        onView(withId(R.id.addFriendText)).perform(typeText("b"), closeSoftKeyboard());
        onView(withId(R.id.addFriendButton)).perform(click());
    }

    public void testC() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread.sleep(1000) interrupted.");
        }

        onView(withId(R.id.addEventButton)).perform(click());
        onView(withId(R.id.eventNameText)).perform(typeText("event1"), closeSoftKeyboard());
        onView(withId(R.id.typeOfBusinessText)).perform(typeText("restaurants"), closeSoftKeyboard());
        onView(withId(R.id.locationText)).perform(typeText("La Jolla"), closeSoftKeyboard());
        onData(anything()).inAdapterView(withId(R.id.friendsList)).atPosition(0).perform(click());
        onView(withId(R.id.createEventButton)).perform(click());
    }
    */
}

