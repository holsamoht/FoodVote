package com.example.local1.foodvote;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;

/** Scenario2
 *  Given   I am logged in
 *  And     I am on the EventCreateActivity
 *  When    I enter an event name
 *  And     I enter a business
 *  And     I enter a location
 *  And     I select a friend
 *  And     I click the "Create" button
 *  Then    Then an event created
 *  When    I click the vote button
 *  Then    The counter is incremented
 */

public class Scenario2 extends ActivityInstrumentationTestCase2<LoginSignUpActivity> {

    private LoginSignUpActivity LSA;

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
     *  Given   I am logged in
     *  And     I am on the EventCreateActivity
     *  When    I enter an event name
     *  And     I enter a business
     *  And     I enter a location
     *  And     I select a friend
     *  And     I click the "Create" button
     *  Then    Then an event created
     *  When    I click the vote button
     *  Then    The counter is incremented
     */
    public void testA() throws Exception {
        String username = "a";
        String password = "1234";
        String eventName = "event1";
        String business = "restaurants";
        String location = "La Jolla";
        Tester t = new Tester();

        t.login(username, password);
        t.checkLogin(username);
        t.createEvent(eventName, business, location);
        t.voteForRestaurant();
        t.checkEvent(eventName);
    }
}

