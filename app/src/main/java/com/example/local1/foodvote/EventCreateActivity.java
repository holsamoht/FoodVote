package com.example.local1.foodvote;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventCreateActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    EditText eventName;
    Button createButton;
    Button cancelButton;
    ParseUser currentUser;
    String nameOfEvent;
    ListView listFriends;
    List<String> userFriendIDs = new ArrayList<String>();
    List<String> userFriends = new ArrayList<String>();
    List<String> eventParticipants = new ArrayList<String>();
    List<Boolean> clicked = new ArrayList<Boolean>();
    String[] noFriends = {"You currently have no friends."};

    List<String> restaurants = new ArrayList<String>();
    List<Integer> votes = new ArrayList<Integer>();

    private static final String CONSUMER_KEY = "FQFe1MpY3PGvxKy-Aq702g";
    private static final String CONSUMER_SECRET = "u_ifYEaonk6W5sf24SCXiGPKx6I";
    private static final String TOKEN = "6YSX1I448VpE2WQ1rrQv0NJRNJ9E9rOX";
    private static final String TOKEN_SECRET = "_iN4GhZsdgojn1WYZTHOi-q2jzM";

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String mLatitudeText;
    String mLongitudeText;
    YelpAPI yAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_create);

        eventName = (EditText) findViewById(R.id.eventName);

        createButton = (Button) findViewById(R.id.createButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        if (checkPlayServices()) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void onStart() {
        super.onStart();

        yAPI = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        String location = "";

        mGoogleApiClient.connect();
    }

    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        /*
        // Use if developing on an android phone.
        if (mLastLocation != null) {
            mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            mLongitudeText = String.valueOf(mLastLocation.getLongitude());
            String location = mLatitudeText + ", " + mLongitudeText;
            String YelpJSON = setUpAPIRet(yAPI, location);
            ParseAndDisplayRestaurantOutput(YelpJSON);
            mGoogleApiClient.disconnect();
        }
        */

        // Use if developing on an emulator.
        if (mLastLocation == null) {
            mLatitudeText = "32.8810";
            mLongitudeText = "-117.2380";
            String location = mLatitudeText + ", " + mLongitudeText;
            String YelpJSON = setUpAPIRet(yAPI, location);
            ParseAndDisplayRestaurantOutput(YelpJSON);
            mGoogleApiClient.disconnect();
        }

        try {
            listFriends = (ListView) findViewById(R.id.friendsList);

            userFriendIDs = currentUser.getCurrentUser().getList("friendsList");

            if (userFriendIDs.size() == 0) {
                ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_friends, noFriends);
                listFriends = (ListView) findViewById(R.id.friendsList);
                listFriends.setAdapter(adapter);
            } else {
                for (int i = 0; i < userFriendIDs.size(); i++) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    userFriends.add(query.get(userFriendIDs.get(i)).getString("username"));
                }

                ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_friends, userFriends);
                listFriends.setAdapter(adapter);

                for (int i = 0; i < userFriendIDs.size(); i++) {
                    clicked.add(false);
                }
            }
        } catch (NullPointerException e) {
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_friends, noFriends);
            listFriends = (ListView) findViewById(R.id.friendsList);
            listFriends.setAdapter(adapter);
        } catch (ParseException e) {
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_friends, noFriends);
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
                if (userFriendIDs.size() == 0) {
                    Intent intent = new Intent(EventCreateActivity.this, FriendsListActivity.class);
                    startActivity(intent);
                    finish();
                }

                if (clicked.get(position) == true) {
                    eventParticipants.remove(userFriendIDs.get(position));
                    listFriends.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                    clicked.set(position, false);
                } else {
                    eventParticipants.add(userFriendIDs.get(position));
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
                    event.addAll("restaurants", restaurants);

                    for (int i = 0; i < eventParticipants.size(); i++) {
                        ParseObject request = new ParseObject("EventRequest");
                        request.put("requestFrom", currentUser.getCurrentUser().getObjectId());
                        request.put("requestTo", eventParticipants.get(i));
                        request.put("status", "pending");
                        request.put("eventName", nameOfEvent);
                        request.saveInBackground();
                    }

                    for (int i = 0; i < 5; i++) {
                        votes.add(0);
                    }
                    event.addAll("votes", votes);
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
                                                                EventVoteActivity.class);
                                                        intent.putExtra("eventName", nameOfEvent);
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


    @Override
    public void onConnectionSuspended(int x) {
        Log.println(Log.ERROR, "EventCreateActivity: ", "Connection Suspended");

        Toast.makeText(getApplicationContext(), "Connection Suspended.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionFailed(ConnectionResult c) {
        Log.println(Log.ERROR, "EventCreateActivity: ", "Connection Failed");

        Toast.makeText(getApplicationContext(), "Connection Failed.", Toast.LENGTH_SHORT).show();
    }

    /*
     * Method gets the API call in JSON format.
     */
    public String setUpAPIRet(YelpAPI Y, String Location) {
        // To return.
        String Ret = "";

        try {
            // Get the return from the API call.
            Ret = Y.execute(Location).get();
        } catch (Exception e) {
            System.out.print("EventCreateActivity: Caught Exception - setUpAPIRet().");
        }

        Log.println(Log.ERROR, "EventCreateActivity: ", "result = " + Ret);
        return Ret;
    }


    public void ParseAndDisplayRestaurantOutput(final String YelpJSON) {
        JSONParser parser = new JSONParser();
        JSONObject response = null;

        try {
            response = (JSONObject) parser.parse(YelpJSON);
        } catch (org.json.simple.parser.ParseException pe) {
            System.out.println("Error: could not parse JSON response:");
            System.out.println(YelpJSON);
            System.exit(1);
        }

        // Each buisness is now represented as an array entry.
        JSONArray businesses = (JSONArray) response.get("businesses");

        // FOR NOW MANUALLY PICK THE 1st 5 RESTAURANTS.
        JSONObject Buisness1 = (JSONObject) businesses.get(0);
        JSONObject Buisness2 = (JSONObject) businesses.get(1);
        JSONObject Buisness3 = (JSONObject) businesses.get(2);
        JSONObject Buisness4 = (JSONObject) businesses.get(3);
        JSONObject Buisness5 = (JSONObject) businesses.get(4);

        // GET THE NAME OF EACH RESTAURANT.
        String R1 = Buisness1.get("name").toString();
        String R2 = Buisness2.get("name").toString();
        String R3 = Buisness3.get("name").toString();
        String R4 = Buisness4.get("name").toString();
        String R5 = Buisness5.get("name").toString();

        // List<String> Restaurants = new ArrayList<>();
        restaurants.add(R1);
        restaurants.add(R2);
        restaurants.add(R3);
        restaurants.add(R4);
        restaurants.add(R5);
    }


    // Method to verify google play services on the device.
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        1000).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }
}
