package com.example.local1.foodvote;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.util.Log;

import junit.framework.Assert;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.Espresso.onData;

import java.util.List;

import static org.hamcrest.Matchers.anything;

public class Tester {

    public Tester(){

    }

    private static ViewAction swipeLeft() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_RIGHT,
                GeneralLocation.CENTER_LEFT, Press.FINGER);
    }

    public void login(String userName, String password){
        onView(withId(R.id.usernameText))
                .perform(typeText(userName), closeSoftKeyboard());
        onView(withId(R.id.passwordText))
                .perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());
    }

    public void checkLogin(String userName) throws Exception {
        Assert.assertTrue("FAILED: Login", ParseUser.getCurrentUser().getUsername().equals(userName));
    }

    public void addFriend(String friend) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e("TESTER: ", "Thread.sleep(1000) interrupted.");
        }

        onView(withId(R.id.viewpager)).perform(swipeLeft());
        onView(withId(R.id.addFriendText)).perform(typeText(friend), closeSoftKeyboard());
        onView(withId(R.id.addFriendButton)).perform(click());
    }

    public void checkDupFriendAdded(String friend) {
        List<String> friends = ParseUser.getCurrentUser().getList("friendsList");

        Assert.assertTrue("Duplicate friend has been added.", friends.size() == 1);
    }

    public void createEvent(String eventName, String business, String location) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e("TESTER", "Thread.sleep(1000) interrupted.");
        }

        onView(withId(R.id.addEventButton)).perform(click());
        onView(withId(R.id.eventNameText)).perform(typeText(eventName), closeSoftKeyboard());
        onView(withId(R.id.typeOfBusinessText)).perform(typeText(business), closeSoftKeyboard());
        onView(withId(R.id.locationText)).perform(typeText(location), closeSoftKeyboard());
        onData(anything()).inAdapterView(withId(R.id.friendsList)).atPosition(0).perform(click());
        onView(withId(R.id.createEventButton)).perform(click());
    }

    public void voteForRestaurant() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e("TESTER", "Thread.sleep(1000) interrupted.");
        }

        onView(withId(R.id.count1)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e("TESTER", "Thread.sleep(1000) interrupted.");
        }
    }

    public void checkEvent(String eventName) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e("TESTER", "Thread.sleep(1000) interrupted.");
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query = query.addDescendingOrder("createdAt");
        query.whereEqualTo("eventName", eventName);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                Assert.assertTrue("Event not found.", parseObject != null);
                List<Integer> list = parseObject.getList("votes");
                Assert.assertTrue("Vote not saved.", list.get(0).intValue() == 1);
            }
        });
    }

    public void checkAddFriendNotInDatabase() {
        List<String> friends = ParseUser.getCurrentUser().getList("friendsList");

        Assert.assertTrue("Nonexistent user has been added.", friends.size() == 1);
    }
}
