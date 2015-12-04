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

/**
 *  Given   I am a user
 *  And     I enter in a username and password
 *  When    I click the "Login" button
 *  Then    I will be logged into my existing account
 *  When    I click an event from the list of events
 *  Then    It will take me to an instance of that event
 *  When    I click the name of a restaurant
 *  Then    It will take me to an activity that displays the restaurant's information
 */

public class Scenario3 extends ActivityInstrumentationTestCase2<LoginSignUpActivity> {

    private LoginSignUpActivity LSA;

    private static final String TAG = "Scenario3 ";

    public Scenario3() {
        super(LoginSignUpActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        LSA = getActivity();
    }

    /**
     *  Given   I am a user
     *  And     I enter in a username and password
     *  When    I click the "Login" button
     *  Then    I will be logged into my existing account
     *  When    I click an event from the list of events
     *  Then    It will take me to an instance of that event
     *  When    I click the name of a restaurant
     *  Then    It will take me to an activity that displays the restaurant's information
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

        onData(anything()).inAdapterView(withId(R.id.eventsList)).atPosition(0).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread.sleep(1000) interrupted.");
        }

        onView(withId(R.id.button1)).perform(click());
    }

    /*
    public void testB() {
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

    public void testC() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread.sleep(1000) interrupted.");
        }


        onView(withId(R.id.count1)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread.sleep(1000) interrupted.");
        }
    }
    */
}

