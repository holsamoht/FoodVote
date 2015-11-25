package com.example.local1.foodvote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;

public class EventsListActivity extends AppCompatActivity {
    Button addButton;
    ListView listEvents;
    ParseUser currentUser;
    List<String> userEventIDs = new ArrayList<String>();
    List<String> userEvents = new ArrayList<String>();
    String[] noEvents = {"You currently have no events."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.events_list);

        listEvents = (ListView) findViewById(R.id.eventsList);

        try {
            userEventIDs = currentUser.getCurrentUser().getList("eventsList");

            if (userEventIDs.size() == 0) {
                createEventsList(false);
            } else {
                for (int i = 0; i < userEventIDs.size(); i++) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                    userEvents.add(query.get(userEventIDs.get(i)).getString("eventName"));
                }

                createEventsList(true);
            }
        } catch (NullPointerException e) {
            createEventsList(false);
        } catch (ParseException e) {
            createEventsList(false);
        }

        listEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (userEventIDs.size() > 0) {
                    Intent intent = new Intent(EventsListActivity.this, EventVoteActivity.class);
                    intent.putExtra("eventName", userEvents.get(position));
                    startActivity(intent);
                    finish();
                }
            }
        });

        listEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (userEventIDs.size() > 0) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                    query.whereEqualTo("objectId", userEventIDs.get(position));
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (parseObject == null) {
                                Log.println(Log.ERROR, "EventsListActivity: ", "Unable to find event.");
                            } else {
                                parseObject.deleteInBackground();
                            }
                        }
                    });

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
                }

                return true;
            }
        });

        addButton = (Button)findViewById(R.id.addEvent);
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

    /* helper method to create adapter and list of events
       hasEvents is true if we can list the user's current events, and false if no events were found
     */
    private void createEventsList(boolean hasEvents) {
        ArrayAdapter adapter = hasEvents ? new ArrayAdapter<String> (this, R.layout.listview_events, userEvents) :
                new ArrayAdapter<String> (this, R.layout.listview_events, noEvents);

        ListView listEvents = (ListView) findViewById(R.id.eventsList);
        listEvents.setAdapter(adapter);
    }
}
