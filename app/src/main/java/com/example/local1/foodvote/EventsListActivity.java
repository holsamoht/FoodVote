package com.example.local1.foodvote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.parse.SignUpCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;

public class EventsListActivity extends AppCompatActivity {
    Button addButton;
    Button removeButton;
    ParseUser currentUser;
    List<String> userEventIDs = new ArrayList<String>();
    List<String> userEvents = new ArrayList<String>();
    String[] noEvents = {"You currently have no events."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_list);

        try {
            userEventIDs = currentUser.getCurrentUser().getList("eventsList");
            Log.println(Log.ERROR, "EventsListActivity: ", "" + userEventIDs.get(0));

            for (int i = 0; i < userEventIDs.size(); i++) {
                Log.println(Log.ERROR, "EventsListActivity: ", "In ParseQuery");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                userEvents.add(query.get(userEventIDs.get(i)).getString("eventName"));
            }
            Log.println(Log.ERROR, "EventsListActivity: ", "Out ParseQuery");

            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_events, userEvents);
            ListView listEvents = (ListView) findViewById(R.id.eventsList);
            listEvents.setAdapter(adapter);
        } catch (NullPointerException e) {
            ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_events, noEvents);
            ListView listEvents = (ListView) findViewById(R.id.eventsList);
            listEvents.setAdapter(adapter);
        } catch (ParseException e) {
            ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_events, noEvents);
            ListView listEvents = (ListView) findViewById(R.id.eventsList);
            listEvents.setAdapter(adapter);
        }

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
}
