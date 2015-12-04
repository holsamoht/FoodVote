package com.example.local1.foodvote;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class EventsListFrag extends Fragment {
    // Variables
    int pos;
    List<String> userEventIDs = new ArrayList<String>();
    List<String> userEvents = new ArrayList<String>();
    String[] noEvents = {"You currently have no events."};

    // Widgets
    ListView listEvents;
    FloatingActionButton addButton;

    // Boolean checks weather data has already been pulled
    boolean setUp = false;

    // Log TAG
    private static final String TAG = "EventsListFrag ";

    public EventsListFrag(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Create fragment view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_events_list, container, false);
    }

    /**
     * Set up fragment view and locate widgets.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //If data has not been pulled
        if(setUp == false) {
            // Locate widgets.
            listEvents = (ListView) getView().findViewById(R.id.eventsList);
            addButton = (FloatingActionButton) getView().findViewById(R.id.addEventButton);

            listUserEvents();
            addEvent();

            setUp = true;
       }
    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * List current user events and allow navigation to event or deletion of event on either
     * item click or item long click.
     */
    private void listUserEvents() {
        if (setUp == false) {
            // Get current user's list of events.
            userEventIDs = ParseUser.getCurrentUser().getList("eventsList");

            try {
                // if user has no events, show the noEvents string
                if (userEventIDs == null || userEventIDs.size() == 0) {
                    ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                            R.layout.listview_events, noEvents);
                    listEvents.setAdapter(adapter);
                } 
                // otherwise list out the events' names
                else {
                    // query for event names
                    for (int i = 0; i < userEventIDs.size(); i++) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                        try {
                            userEvents.add(query.get(userEventIDs.get(i)).getString("eventName"));
                        } catch (ParseException e) {
                            Log.e(TAG, "Unable to convert event IDs to event names.");
                        }
                    }

                    // Display event names.
                    ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                            R.layout.listview_events, userEvents);
                    listEvents.setAdapter(adapter);
                }
            } catch (NullPointerException e) {
                ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.listview_events, noEvents);
                listEvents.setAdapter(adapter);
            }
        }

        // On event name click, switch to EventVoteActivity.java for that event.
        listEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // protective check in case there are no event ids, should never execute
                if (userEventIDs == null) {
                    Intent intent = new Intent(getActivity(), EventCreateActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } 
                // otherwise go to the event's EventVoteActivity
                else {
                    Intent intent = new Intent(getActivity(), EventVoteActivity.class);
                    intent.putExtra("eventId", userEventIDs.get(position));
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        // On event name long click, delete/remove event from user's event list.
        listEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // protective check in case there are no event ids, should never execute 
                if (userEventIDs == null) {
                    Toast.makeText(getActivity().getApplicationContext(), "No event to delete.",
                            Toast.LENGTH_SHORT).show();
                } 
                else {
                    pos = position;

                    // Search Parse for event and delete its EventRequest object
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                    query.whereEqualTo("objectId", userEventIDs.get(position));
                    query.whereEqualTo("eventOwner", ParseUser.getCurrentUser().getObjectId());
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            // toast error message in case the event was not found
                            if (parseObject == null) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Deleting event.",
                                        Toast.LENGTH_SHORT).show();
                            } 
                            // otherwise delete the event
                            else {
                                // find the EventRequest object for that event and delete it
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("EventRequest");
                                query.whereEqualTo("eventId", userEventIDs.get(pos));
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> list, ParseException e) {
                                        if (e == null) {
                                            for (int i = 0; i < list.size(); i++) {
                                                list.get(i).deleteInBackground();
                                            }
                                        }
                                    }
                                });

                                /*
                                List<String> eventParticipants = new ArrayList<String>();
                                eventParticipants = parseObject.getList("eventParticipants");

                                for (int i = 0; i < parseObject.getList("eventParticipants").size() - 1; i++) {
                                    ParseObject request = new ParseObject("EventRequest");
                                    request.put("requestFrom", ParseUser.getCurrentUser().getObjectId());
                                    request.put("requestTo", eventParticipants.get(i));
                                    request.put("action", "delete");
                                    request.put("status", "pending");
                                    request.put("eventId", parseObject.getObjectId());
                                    ParseACL newACL = new ParseACL();
                                    newACL.setPublicReadAccess(true);
                                    newACL.setPublicWriteAccess(true);
                                    request.setACL(newACL);
                                    request.saveInBackground();
                                }
                                */

                                parseObject.deleteInBackground();
                            }
                        }
                    });

                    // Delete the event from current user's list of events and refresh activity.
                    ParseQuery<ParseObject> tempQuery = ParseQuery.getQuery("Event");
                    tempQuery.whereEqualTo("objectId", userEventIDs.get(position));
                    tempQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            // error console log in case the event could not be found
                            if (parseObject == null) {
                                Log.e(TAG, "Unable to delete event.");
                            } 
                            // otherwise go on to delete the event from the user
                            else {
                                // remove the current user from the event's list of participants
                                List<String> list = new ArrayList<String>();
                                list.add(ParseUser.getCurrentUser().getObjectId());
                                parseObject.removeAll("eventParticipants", list);
                                parseObject.saveInBackground();

                                // remove the event from the user's list of events
                                List<String> tempList = new ArrayList<String>();
                                tempList.add(userEventIDs.get(pos));
                                ParseUser.getCurrentUser().removeAll("eventsList", tempList);
                                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        // once this is saved without issues, refresh the activity
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
                            }
                        }
                    });

                    return true;
                }

                return true;
            }
        });
    }

    /**
     * Add button listener, allows current user to create a new event in Parse.
     */
    private void addEvent() {
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EventCreateActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}
