package com.example.local1.foodvote;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventCreateActivity extends AppCompatActivity {
    EditText eventName;
    Button createButton;
    Button cancelButton;
    ParseUser currentUser;
    Event createdEvent;
    String nameOfEvent;
    ListView listFriends;
    List<String> userFriendIDs = new ArrayList<String>();
    List<String> userFriends = new ArrayList<String>();
    List<String> eventParticipants = new ArrayList<String>();
    List<Boolean> clicked = new ArrayList<Boolean>();
    String[] noFriends = {"You currently have no friends."};
    String[] friends = {"we9f8efe", "wqvsfewr39"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_create);

        eventName = (EditText)findViewById(R.id.eventName);

        createButton = (Button)findViewById(R.id.createButton);
        cancelButton = (Button)findViewById(R.id.cancelButton);

        try {
            listFriends = (ListView) findViewById(R.id.friendsList);

            userFriendIDs = currentUser.getCurrentUser().getList("friendsList");

            for (int i = 0; i < userFriendIDs.size(); i++) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
                userFriends.add(query.get(userFriendIDs.get(i)).getString("username"));
            }

            ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_friends, userFriends);
            listFriends.setAdapter(adapter);

            for (int i = 0; i < userFriends.size(); i++) {
                clicked.add(false);
            }
        } catch (NullPointerException e) {
            ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_friends, noFriends);
            listFriends = (ListView) findViewById(R.id.friendsList);
            listFriends.setAdapter(adapter);
        } catch (ParseException e) {
            ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_friends, noFriends);
            listFriends = (ListView) findViewById(R.id.friendsList);
            listFriends.setAdapter(adapter);
        }

        listFriends.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeRight() {
                Intent intent = new Intent(EventCreateActivity.this, FriendsListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        listFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (userFriends.size() == 0) {
                    Intent intent = new Intent(EventCreateActivity.this, FriendsListActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (clicked.get(position) == true) {
                    eventParticipants.remove(friends[position]);
                    listFriends.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                    clicked.set(position, false);
                }
                else {
                    eventParticipants.add(friends[position]);
                    listFriends.getChildAt(position).setBackgroundColor(Color.GREEN);
                    clicked.set(position, true);
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameOfEvent = eventName.getText().toString();

                if (nameOfEvent.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter name of event.",
                            Toast.LENGTH_LONG).show();
                } else {
                    ParseObject event = new ParseObject("Event");
                    event.put("eventName", nameOfEvent);
                    event.put("eventOwner", currentUser.getCurrentUser().getObjectId());
                    event.addAll("eventParticipants", eventParticipants);
                    event.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                                query.whereEqualTo("eventName", nameOfEvent);
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> list, ParseException e) {
                                        if (e == null) {
                                            currentUser.getCurrentUser().add("eventsList",
                                                    list.get(0).getObjectId());
                                            currentUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Intent intent = new Intent(EventCreateActivity.this,
                                                                MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventCreateActivity.this, EventsListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
