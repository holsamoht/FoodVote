package com.example.local1.foodvote;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {
    // Variables
    List<String> currentFriends = new ArrayList<String>();
    List<String> userFriendIDs = new ArrayList<String>();
    List<String> userFriends = new ArrayList<String>();
    String[] noFriends = {"You currently have no friends."};
    String friendUsername;

    // Widgets
    EditText enteredInUsername;
    FloatingActionButton addFriendButton;
    ListView friendsList;

    // User
    ParseUser currentUser;

    // Log TAG
    private static final String TAG = "FriendsListActivity ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "In onCreate().");

        initializeView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "In onStart().");

        addFriend();
        listUserFriends();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.e(TAG, "In onBackPressed");

        Intent intent = new Intent(FriendsListActivity.this, EventCreateActivity.class);
        startActivity(intent);
        finish();
    }

    private void initializeView() {
        Log.e(TAG, "In initializeData().");

        // Set view from friends_list.xml.
        setContentView(R.layout.friends_list);

        // Locate widget views in friends_list.xml
        enteredInUsername = (EditText) findViewById(R.id.addFriend);
        addFriendButton = (FloatingActionButton) findViewById(R.id.addButton);
        friendsList = (ListView) findViewById(R.id.friendsList);
    }

    private void addFriend() {
        Log.e(TAG, "In addFriend().");

        // On FAB click, add specified user to current user's friend list.
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get friend's name from current user input.
                friendUsername = enteredInUsername.getText().toString();

                // If current user entered in a username, search for user in database.
                if (!(friendUsername.equals(""))) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", friendUsername);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser object, ParseException e) {
                            if (object != null) {
                                // Check for current user for friends already added.
                                // If no friends added, add friend.
                                if (currentUser.getCurrentUser().getList("friendsList") != null) {
                                    currentFriends = currentUser.getCurrentUser().getList("friendsList");

                                    // Check if friend is already added, add if not added already.
                                    if (!(currentFriends.contains(object.getObjectId()))) {
                                        currentUser.getCurrentUser().add("friendsList", object.getObjectId());
                                        currentUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Toast.makeText(getApplicationContext(), "User added.",
                                                        Toast.LENGTH_LONG).show();

                                                Intent intent = getIntent();
                                                overridePendingTransition(0, 0);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                finish();
                                                overridePendingTransition(0, 0);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "User is already added.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    currentUser.getCurrentUser().add("friendsList", object.getObjectId());
                                    currentUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Toast.makeText(getApplicationContext(), "User added.",
                                                    Toast.LENGTH_LONG).show();

                                            Intent intent = getIntent();
                                            overridePendingTransition(0, 0);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            finish();
                                            overridePendingTransition(0, 0);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "User does not exist.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter username.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void listUserFriends() {
        Log.e(TAG, "In listUserFriends().");

        // List current user's friends in a list.
        try {
            userFriendIDs = currentUser.getCurrentUser().getList("friendsList");

            if (userFriendIDs != null) {
                // Convert user IDs to usernames.
                for (int i = 0; i < userFriendIDs.size(); i++) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    userFriends.add(query.get(userFriendIDs.get(i)).getString("username"));
                }

                // Display friends list.
                ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_friends, userFriends);
                friendsList.setAdapter(adapter);
            } else {
                // Display msg that current user has no friends.
                ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_friends, noFriends);
                friendsList.setAdapter(adapter);
            }
        } catch(NullPointerException e) {
            // Display msg that current user has no friends.
            ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_friends, noFriends);
            friendsList = (ListView) findViewById(R.id.friendsList);
            friendsList.setAdapter(adapter);
        } catch (ParseException e) {
            Log.e(TAG, "Unable to convert user ID to username.");
        }
    }
}