package com.example.local1.foodvote;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;

/** Scenario1
 * Given    I have an account
 * And      I am logged in
 * And      I am on the friends list fragment
 * And      The user I try to add is in the database and is already my friend.
 * When     I enter the username
 * And      I click the "+" button
 * Then     My friend will not be added to my friends list.
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

    /** Scenario1
     * Given    I have an account
     * And      I am logged in
     * And      I am on the friends list fragment
     * And      The user I try to add is in the database and is already my friend.
     * When     I enter the username
     * And      I click the "+" button
     * Then     My friend will not be added to my friends list.
     */
    public void testA() throws Exception{

        String username = "a";
        String password = "1234";
        String friendUsername = "b";
        Tester t = new Tester();

        t.login(username, password);
        t.checkLogin(username);
        t.addFriend(friendUsername);
        t.checkDupFriendAdded(friendUsername);
    }
}

