package com.example.local1.foodvote;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.ViewInteraction.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.*;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.Before;

/** Scenario1
 * Given    I am a user
 * And      I enter a username and password
 * When     I click the "Sign Up" button
 * Then     I will will be registered for an account
 * When     I click the "Login" button
 * Then     I will be logged into my existing account
 * When     I swipe left, enter a friend's username, and click the "+" button
 * Then     My friend will be added to my friends list.
 */

public class Scenario1 extends ActivityInstrumentationTestCase2<LoginSignUpActivity> {

    private LoginSignUpActivity LSA;

    private static final String TAG = "Scenario1 ";

    public Scenario1() {
        super(LoginSignUpActivity.class);
    }

    @Before
        public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        LSA = getActivity();
    }

    private static ViewAction swipeLeft() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_RIGHT,
                GeneralLocation.CENTER_LEFT, Press.FINGER);
    }

    /** Scenario1
     * Given    I am a user
     * And      I enter in username and password
     * When     I click the "Sign Up" button
     * Then     I will will be registered for an account
     * When     I click the "Login" button
     * Then     I will be logged into my existing account
     * When     I swipe left, enter a friend's username, and click the "+" button
     * Then     My friend will be added to my friends list.
     */


    /** Scenario1
     * Given    I have an account
     * And      I am logged in
     * And      I am on the friends List fragment
     * And      I have a friend with a FoodVote account I already added
     * When     I enter the friend's username
     * And      I click the "+" button
     * Then     My friend will not be added to my friends list.
     */
    public void testA() throws Exception{

        String un = "a";
        String pswd = "1234";
        Tester t = new Tester("Output.txt");

        t.login(un, pswd);
        t.checkLogin(un);


        int currentSize = t.addFriend("b");
        t.checkFriendAdded("b", currentSize);


    }

}

