package com.example.local1.foodvote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.List;

public class EventCreateActivity extends AppCompatActivity {
    EditText eventName;
    Button createButton;
    Button cancelButton;
    String nameOfEvent;
    ParseUser currentUser;
    Event createdEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_create);

        eventName = (EditText)findViewById(R.id.eventName);

        createButton = (Button)findViewById(R.id.createButton);
        cancelButton = (Button)findViewById(R.id.cancelButton);

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
                    event.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.println(Log.ERROR, "Main:", "Save finishes");
                            Intent intent = new Intent(EventCreateActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    /*
                    Calendar c = Calendar.getInstance();
                    int seconds = c.get(Calendar.SECOND);
                    final String sec = seconds + "";
                    event.put("privateId", nameOfEvent + sec);
                    event.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseQuery query = new ParseQuery("Event");
                                query.whereEqualTo("privateId", nameOfEvent + sec);
                                query.getFirstInBackground(new GetCallback() {
                                    @Override
                                    public void done(ParseObject parseObject, ParseException e) {
                                        if (e == null) {
                                            currentUser.getCurrentUser().add("eventsList", parseObject.getObjectId());
                                            Intent intent = new Intent(EventCreateActivity.this,
                                                    MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void done(Object o, Throwable throwable) {
                                        Log.println(Log.ERROR, "Main:", "In throwable");
                                    }
                                });
                            }
                        }
                    });
                    */
                }
            }
        });
    }
}
