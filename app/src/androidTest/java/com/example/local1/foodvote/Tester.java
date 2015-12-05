package com.example.local1.foodvote;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.util.Log;
import junit.framework.Assert;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import java.io.*;
import java.util.List;

/**
 * Created by Krushi on 12/4/15.
 */
public class Tester {
    String FileName;
    FileWriter fstream;
    BufferedWriter out;

    FileInputStream in;

    public Tester(String FileName){
        this.FileName = FileName;

        /*try {
            fstream = new FileWriter(FileName);
            out = new BufferedWriter(fstream);
            Log.e("Tester: ", "PASSED FILE OPEN");

        }catch(Exception e){
            Log.e("Tester: ", "PROBLEM OPENING FILE");
        }*/

    }


    public void login(String userName, String password){
        onView(withId(R.id.usernameText))
                .perform(typeText(userName), closeSoftKeyboard());
        onView(withId(R.id.passwordText))
                .perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());
    }

    public void checkLogin(String userName) throws Exception{
        /*if(ParseUser.getCurrentUser().getUsername().equals(userName)){
            out.write("PASSED: Login");
        }

        else{
            out.write("FAILED: Login");
        }*/

        Assert.assertTrue("FAILED: Login", ParseUser.getCurrentUser().getUsername().equals(userName));
    }

    public int addFriend(String friend){
        int currentSize = 5467;
        List<String> friends = ParseUser.getCurrentUser().getList("friendsList");

        //if(friends.contains(friend))

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", friend);

        try{
            List<ParseUser> list = query.find();
            currentSize = list.size();
            Log.e("TESTER: ", currentSize + "");
            //String friendId = list.get(0).getObjectId();
            //Assert.assertTrue("FAILED: Adding Friend", friends.contains(friendId));
        }catch(ParseException e){

        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e("TESTER: ", "Thread.sleep(1000) interrupted.");
        }

        onView(withId(R.id.viewpager)).perform(swipeLeft());
        onView(withId(R.id.addFriendText)).perform(typeText(friend), closeSoftKeyboard());
        onView(withId(R.id.addFriendButton)).perform(click());

        return currentSize;
    }

    public void checkFriendAdded(String friend, int previousSize){
        Log.e("TESTER: ", previousSize + "");
        List<String> friends = ParseUser.getCurrentUser().getList("friendsList");

        //if(friends.contains(friend))

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", friend);
        try{
            List<ParseUser> list = query.find();
            int currentSize = list.size();
            //String friendId = list.get(0).getObjectId();
            Assert.assertTrue("FAILED: Adding Friend", currentSize == previousSize);
        }catch(ParseException e){

        }
    }

    private static ViewAction swipeLeft() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_RIGHT,
                GeneralLocation.CENTER_LEFT, Press.FINGER);
    }
}
