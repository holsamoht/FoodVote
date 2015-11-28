package com.example.local1.foodvote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class EventsListActivity extends AppCompatActivity {
    // Variables
    List<String> userEventIDs = new ArrayList<String>();
    List<String> userEvents = new ArrayList<String>();
    String[] noEvents = {"You currently have no events."};

    List<String> requestedEventIDs = new ArrayList<String>();
    List<String> requestedEvents = new ArrayList<String>();
    String[] noRequests = {"You currently have no pending requests."};

    // Widgets
    ListView listEvents;
    ListView listRequests;
    Button addButton;
    Toolbar toolbar;

    // User
    ParseUser currentUser;

    // Log TAG
    private static final String TAG = "EventsListActivity ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "In onCreate");

        initializeView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "In onStart().");

        listUserEvents();
        listUserRequests();
        addEvent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.e(TAG, "In onBackPressed().");

        currentUser.getCurrentUser().logOut();

        Intent intent = new Intent(EventsListActivity.this, LoginSignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            ParseUser.getCurrentUser().logOut();
            Intent intent = new Intent(EventsListActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeView() {
        Log.e(TAG, "In initializeData().");

        // Set view from events_list.xml.
        setContentView(R.layout.events_list);

        // Locate ListViews in events_list.xml.
        listEvents = (ListView) findViewById(R.id.eventsList);
        listRequests = (ListView) findViewById(R.id.requestsList);

        // Locate Button in events_list.xml
        addButton = (Button) findViewById(R.id.addEvent);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void listUserEvents() {
        Log.e(TAG, "In listUserEvents().");

        try {
            // Get current user's list of events.
            userEventIDs = currentUser.getCurrentUser().getList("eventsList");

            // Display current user's list of events.
            // If user has events, display them,
            // else display msg that user has no events.
            if (userEventIDs != null) {
                // Convert event IDs into event names.
                for (int i = 0; i < userEventIDs.size(); i++) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                    userEvents.add(query.get(userEventIDs.get(i)).getString("eventName"));
                }
                // Display event names.
                ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_events, userEvents);
                listEvents.setAdapter(adapter);
            } else {
                // Display msg that user has not events.
                ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_events, noEvents);
                listEvents.setAdapter(adapter);
            }
        } catch (NullPointerException e) {
            // Display msg that user has not events.
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_events, noEvents);
            listEvents.setAdapter(adapter);
        } catch (ParseException e) {
            // Display msg that user has not events.
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_events, noEvents);
            listEvents.setAdapter(adapter);
        }

        // On event name click, switch to EventVoteActivity.java for that event.
        listEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (userEventIDs != null) {
                    Intent intent = new Intent(EventsListActivity.this, EventVoteActivity.class);
                    intent.putExtra("eventId", userEventIDs.get(position));
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(EventsListActivity.this, EventCreateActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // On event name long click, delete/remove event from user's event list.
        listEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (userEventIDs != null) {
                    // Search Parse for event and delete it.
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                    query.whereEqualTo("objectId", userEventIDs.get(position));
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (parseObject != null) {
                                parseObject.deleteInBackground();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Event does not exist in our database.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // Delete the event from current user's list of events and refresh activity.
                    List<String> tempList = new ArrayList<String>();
                    tempList.add(userEventIDs.get(position));
                    currentUser.getCurrentUser().removeAll("eventsList", tempList);
                    currentUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent intent = getIntent();
                                overridePendingTransition(0, 0);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(intent);
                            }
                        }
                    });

                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), "No event to delete.",
                            Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    private void listUserRequests() {
        Log.e(TAG, "In listUserRequest().");

        // Search Parse for current user's list of pending event requests.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("EventRequest");
        query.whereEqualTo("requestTo", currentUser.getCurrentUser().getObjectId());
        query.whereEqualTo("status", "pending");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (!list.isEmpty() && e == null) {
                    // Gather list of event IDs from current user's requested events.
                    for (int i = 0; i < list.size(); i++) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                        query.whereEqualTo("objectId", list.get(i).get("eventId"));

                        try {
                            ParseObject tempObject = query.getFirst();
                            requestedEventIDs.add(tempObject.getObjectId());
                        } catch (ParseException ee) {
                            // Display msg that current user has no requested events.
                            ArrayAdapter adapter = new ArrayAdapter<String>(EventsListActivity.this,
                                    R.layout.listview_requests_events, noRequests);
                            listRequests.setAdapter(adapter);
                        }
                    }

                    // Convert current user's requested event IDs to event names and display them.
                    try {
                        for (int i = 0; i < list.size(); i++) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                            requestedEvents.add(query.get(requestedEventIDs.get(i)).getString("eventName"));
                        }
                    } catch (ParseException ee) {
                        // Display msg that current user has no requested events.
                        ArrayAdapter adapter = new ArrayAdapter<String>(EventsListActivity.this,
                                R.layout.listview_requests_events, noRequests);
                        listRequests.setAdapter(adapter);
                    }

                    // Display list of current user's requested events.
                    ArrayAdapter adapter = new ArrayAdapter<String>(EventsListActivity.this,
                            R.layout.listview_requests_events, requestedEvents);
                    listRequests.setAdapter(adapter);
                } else {
                    // Display msg that current user has no requested events.
                    ArrayAdapter adapter = new ArrayAdapter<String>(EventsListActivity.this,
                            R.layout.listview_requests_events, noRequests);
                    listRequests.setAdapter(adapter);
                }
            }
        });


        // On event request click, add event to the current user's list of events.
        listRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!requestedEventIDs.isEmpty()) {
                    // Add requested event to current user's list of events.
                    currentUser.getCurrentUser().add("eventsList", requestedEventIDs.get(position));

                    // In Parse, change the status of the request to accepted.
                    ParseQuery<ParseObject> tempQuery = ParseQuery.getQuery("EventRequest");
                    tempQuery.whereEqualTo("eventId", requestedEventIDs.get(position));
                    tempQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                parseObject.remove("status");
                                parseObject.put("status", "accepted");
                                parseObject.saveInBackground();
                            }
                        }
                    });

                    currentUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent intent = getIntent();
                                overridePendingTransition(0, 0);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

    private void addEvent() {
        Log.e(TAG, "In addEvent().");

        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsListActivity.this,
                        EventCreateActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
