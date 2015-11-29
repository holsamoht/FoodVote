package com.example.local1.foodvote;


import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


/*Fragment inside Main Activity that displays all events associated with the current user*/
public class EventsListFrag extends Fragment {
    // Variables
    List<String> userEventIDs = new ArrayList<String>();
    List<String> userEvents = new ArrayList<String>();
    String[] noEvents = {"You currently have no events."};

    // Widgets
    ListView listEvents;
    FloatingActionButton addButton;

    //Boolean checks weather data has already been pulled
    boolean SetUp = false;

    // User
    ParseUser currentUser;

    /*Default Constructor*/
    public EventsListFrag(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.println(Log.ERROR, "EventsListActivity: ", "In OnCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.println(Log.ERROR, "EventsListFrag: ", "In onCreateView");
        return inflater.inflate(R.layout.fragment_events_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        Log.println(Log.ERROR, "EventsListActivity: ", "In onActivityCreated");

        //If data has not been pulled
        if(SetUp == false) {
            //Locate the List View in fragment_events_list.xml
            listEvents = (ListView) getView().findViewById(R.id.eventsList);
            // Locate FAB in fragment_events_list.xml
            addButton = (FloatingActionButton) getView().findViewById(R.id.fab);

            //Get the Event data
            listUserEvents();
            //Set up button functionality
            addEvent();
            //Set boolean to true
            SetUp = true;
        }

    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.println(Log.ERROR, "EventsListFrag: ", "In onResume");
    }

    /*I don't think this does anything*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /*Method to pull event data from parse*/
    private void listUserEvents() {

        //Check that data has not already been pulled
        if (SetUp == false) {

            // Get current user's list of events.
            userEventIDs = currentUser.getCurrentUser().getList("eventsList");

            // Display current user's list of events.
            // If user has events, display them,
             try{   // else display msg that user has no events.
                if (userEventIDs != null) {
                    // Convert event IDs into event names.
                    for (int i = 0; i < userEventIDs.size(); i++) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                        //Get events that exist in database. Ignore deleted events
                        try {
                            userEvents.add(query.get(userEventIDs.get(i)).getString("eventName"));
                        }catch (ParseException e) {
                            Log.e("EventsFrag: ", "Parse Exception");
                        }
                    }
                    // Display event names.
                    ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.listview_events, userEvents);
                    listEvents.setAdapter(adapter);
                } else {
                    Log.e("EventsFrag: ", "No Events");
                    // Display msg that user has not events.
                    ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.listview_events, noEvents);
                    listEvents.setAdapter(adapter);
                }
            } catch (NullPointerException e) {
                Log.e("EventsFrag: ", "Null Pointer");
                // Display msg that user has not events.
                ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.listview_events, noEvents);
                listEvents.setAdapter(adapter);
            }
        }
        // On event name click, switch to EventVoteActivity.java for that event.
        listEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (userEventIDs != null) {
                    Intent intent = new Intent(getActivity(), EventVoteActivity.class);
                    intent.putExtra("eventId", userEventIDs.get(position));
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Intent intent = new Intent(getActivity(), EventCreateActivity.class);
                    startActivity(intent);
                    getActivity().finish();
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
                                Toast.makeText(getActivity().getApplicationContext(),
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
                                Intent intent = getActivity().getIntent();
                                getActivity().overridePendingTransition(0, 0);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                getActivity().finish();
                                getActivity().overridePendingTransition(0, 0);
                                startActivity(intent);
                            }
                        }
                    });

                    return true;
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No event to delete.",
                            Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    /*Set up button functionality*/
    private void addEvent() {
        //Listener
        addButton.setOnClickListener(new OnClickListener() {
            //Start an event create activity
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        EventCreateActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}
