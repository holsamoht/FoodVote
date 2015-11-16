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

            /*
            for (int i = 0; i < userEventIDs.size(); i++) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                // query.whereEqualTo("objectId", userEventIDs.get(i));
                query.getInBackground(userEventIDs.get(i) ,new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            userEvents.add(object.getString("eventName"));
                            Log.println(Log.ERROR, "Main: ", "" + userEvents.get(0));
                        }
                    }
                });
            }
            */

            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_events, userEventIDs);
            ListView listEvents = (ListView) findViewById(R.id.listView);
            listEvents.setAdapter(adapter);
        } catch (NullPointerException e) {
            ArrayAdapter adapter = new ArrayAdapter<String> (this, R.layout.listview_events, noEvents);
            ListView listEvents = (ListView) findViewById(R.id.listView);
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
