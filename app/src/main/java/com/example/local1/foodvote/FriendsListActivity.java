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
    List<String> currentFriends = new ArrayList<String>();
    List<String> userFriendIDs = new ArrayList<String>();
    List<String> userFriends = new ArrayList<String>();
    String[] noFriends = {"You currently have no friends."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friends_list);

        friendsList = (ListView) findViewById(R.id.friendsList);

        enteredInUsername = (EditText) findViewById(R.id.addFriend);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendUsername = enteredInUsername.getText().toString();

                if (friendUsername.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter username.",
                            Toast.LENGTH_LONG).show();
                } else {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", friendUsername);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser object, ParseException e) {
                            if (object == null) {
                                Toast.makeText(getApplicationContext(), "User does not exist.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                if (currentUser.getCurrentUser().getList("friendsList") == null) {
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
                                    currentFriends = currentUser.getCurrentUser().getList("friendsList");
                                    if (currentFriends.contains(object.getObjectId())) {
                                        Toast.makeText(getApplicationContext(), "User is already added.",
                                                Toast.LENGTH_LONG).show();
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
                                }
                            }
                        }
                    });
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
            userFriendIDs = currentUser.getCurrentUser().getList("friendsList");

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

