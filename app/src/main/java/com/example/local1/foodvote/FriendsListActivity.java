package com.example.local1.foodvote;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseUser;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.FindCallback;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {
    ParseUser currentUser;
    EditText enteredInUsername;
    String friendUsername;
    ListView friendsList;
    List<String> userFriendIDs = new ArrayList<String>();
    List<String> userFriends = new ArrayList<String>();
    String[] noFriends = {"You currently have no friends."};

    private ParseQuery<ParseUser> query = ParseUser.getQuery();
    //query.selectKeys((Arrays.asList("username")));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friends_list);

        try {
            Log.println(Log.ERROR, "FriendsList: ", "FirstQueryElement = " + query.getFirst());
        }catch(com.parse.ParseException c){
            Log.println(Log.ERROR, "FriendsList: ", "FirstQueryElemnt not found");
        }
        friendsList = (ListView) findViewById(R.id.friendsList);

        enteredInUsername = (EditText) findViewById(R.id.addFriend);
        Log.println(Log.ERROR, "FriendsListActivity: ", "Before fab - " + enteredInUsername.getText().toString());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.println(Log.ERROR, "FriendsListActivity: ", "In fab - " + enteredInUsername.getText().toString());
                friendUsername = enteredInUsername.getText().toString();

                Log.println(Log.ERROR, "FriendsListActivity: ", "In fab - " + friendUsername);

                if (friendUsername.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter username.",
                            Toast.LENGTH_LONG).show();
                } else {
                    //ParseQuery<ParseUser> query = ParseQuery.getQuery("User");
                    Log.println(Log.ERROR, "FriendsList: ", "friendUserName = " + friendUsername);
                    query.whereEqualTo("username", friendUsername);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> userList, ParseException e) {

                            if (e == null) {

                                //boolean foundDupe = false;
                                for (int i = 0; i < userList.size(); i++) {
                                    List<String> listOfFriends;
                                    listOfFriends = currentUser.getCurrentUser().getList("friendsList");
                                    boolean foundDupe = false;
                                    //listOfFriends.size();
                                    /*for (int j = 0; j < listOfFriends.size(); j++) {
                                        String str;
                                        if (listOfFriends.get(j).equals(friendUsername)) {
                                            Toast.makeText(getApplicationContext(), "User is already a friend",
                                                    Toast.LENGTH_LONG).show();
                                            foundDupe = true;
                                            break;
                                        }
                                    }*/

                                    if (foundDupe == false) {
                                        currentUser.getCurrentUser().add("friendsList", userList.get(i).getObjectId());
                                        //currentUser.getCurrentUser().add("friendsList", userList.get(0));
                                        Toast.makeText(getApplicationContext(), userList.get(i).getUsername().toString() +
                                                        " is now your friend!",
                                                Toast.LENGTH_LONG).show();
                                        currentUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {

                                                Intent intent = getIntent();
                                                overridePendingTransition(0, 0);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                finish();
                                                overridePendingTransition(0, 0);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        Log.println(Log.ERROR, "MAIN: ", "FriendsListActivity.java - unable to filter users");
                                    }
                                }
                            }


                            if(userList.size() == 0){
                                Toast.makeText(getApplicationContext(), "User does not exist.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    /*
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (parseObject == null) {
                                Toast.makeText(getApplicationContext(), "User does not exist.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                currentUser.getCurrentUser().add("friendsList",
                                        parseObject.getObjectId());
                                currentUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Intent intent = getIntent();
                                        overridePendingTransition(0, 0);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    });
                    */

                }
            }
        });

        friendsList.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                Intent intent = new Intent(FriendsListActivity.this, EventCreateActivity.class);
                startActivity(intent);
                finish();
            }
        });

        try {
            // friendsList = (ListView) findViewById(R.id.friendsList);
            Log.println(Log.ERROR, "FriendsList: ", "IN TRY!");
            userFriendIDs = currentUser.getCurrentUser().getList("friendsList");
            Log.println(Log.ERROR, "FriendsList: ", "Size = " + userFriendIDs.size());

            for (int i = 0; i < userFriendIDs.size(); i++) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                userFriends.add(query.get(userFriendIDs.get(i)).getString("username"));
            }

            ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_friends, userFriends);
            friendsList.setAdapter(adapter);

        } catch(NullPointerException e) {
            ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_friends, noFriends);
            friendsList = (ListView) findViewById(R.id.friendsList);
            friendsList.setAdapter(adapter);
        } catch (ParseException e) {
            ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_friends, noFriends);
            friendsList = (ListView) findViewById(R.id.friendsList);
            friendsList.setAdapter(adapter);
        }
    }
}

