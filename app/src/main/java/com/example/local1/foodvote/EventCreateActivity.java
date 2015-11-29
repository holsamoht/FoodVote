package com.example.local1.foodvote;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class EventCreateActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Variables
    String nameOfEvent;
    String[] noFriends = {"You currently have no friends."};
    String mLatitudeText;
    String mLongitudeText;
    String eventId;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    YelpAPI yAPI;

    List<Boolean> clicked = new ArrayList<Boolean>();
    List<Integer> votes = new ArrayList<Integer>();
    List<Integer> choiceIndex = new ArrayList<Integer>();
    List<String> userFriendIDs = new ArrayList<String>();
    List<String> userFriends = new ArrayList<String>();
    List<String> eventParticipants = new ArrayList<String>();
    List<String> restaurants = new ArrayList<String>();

    // Widgets
    Button createButton;
    Button cancelButton;
    EditText eventName;
    ListView listFriends;
    Toolbar toolbar;

    // User
    ParseUser currentUser;

    //RunVariable
    boolean isSetUp = false;

    // YELP API keys
    private static final String CONSUMER_KEY = "FQFe1MpY3PGvxKy-Aq702g";
    private static final String CONSUMER_SECRET = "u_ifYEaonk6W5sf24SCXiGPKx6I";
    private static final String TOKEN = "6YSX1I448VpE2WQ1rrQv0NJRNJ9E9rOX";
    private static final String TOKEN_SECRET = "_iN4GhZsdgojn1WYZTHOi-q2jzM";

    // TAG
    private static final String TAG = "EventCreateActivity ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "In onCreate().");

        initializeView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "In onStart().");

        if(isSetUp == false) {
            selectUserFriends();
            createEvent();
            cancelEvent();

            isSetUp = true;
            mGoogleApiClient.connect();
        }
    }

    public void onConnected(Bundle bundle) {
        Log.e(TAG, "In onConnected().");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        // Use if developing on an android phone.
        if (mLastLocation != null) {
            mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            mLongitudeText = String.valueOf(mLastLocation.getLongitude());
            String location = mLatitudeText + ", " + mLongitudeText;
            String YelpJSON = setUpAPIRet(yAPI, location);
            parseAndDisplayRestaurantOutput(YelpJSON);
            mGoogleApiClient.disconnect();
        }

        // Use if developing on an emulator.
        /*if (mLastLocation == null) {
            mLatitudeText = "32.8810";
            mLongitudeText = "-117.2380";
            String location = mLatitudeText + ", " + mLongitudeText;
            String YelpJSON = setUpAPIRet(yAPI, location);
            parseAndDisplayRestaurantOutput(YelpJSON);
            mGoogleApiClient.disconnect();
        }*/
    }

    @Override
    public void onConnectionSuspended(int x) {
        Log.e(TAG, "In onConnectionSuspended().");
        Log.e(TAG, "Connection suspended.");

        Toast.makeText(getApplicationContext(), "Connection suspended.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult c) {
        Log.e(TAG, "In onConnectionFailed().");
        Log.e(TAG, "Connection failed.");

        Toast.makeText(getApplicationContext(), "Connection failed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.e(TAG, "In onBackPressed");

        Intent intent = new Intent(EventCreateActivity.this, MainActivity.class);
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
            Intent intent = new Intent(EventCreateActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeView() {
        Log.e(TAG, "In initializeView().");

        // Set view from event_create.xml.
        setContentView(R.layout.event_create);

        // Find widgets from event_create.xml.
        createButton = (Button) findViewById(R.id.createButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        eventName = (EditText) findViewById(R.id.eventName);
        listFriends = (ListView) findViewById(R.id.friendsList);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set yelpAPI keys.
        yAPI = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);

        // Build googleAPI client.
        if (checkPlayServices()) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void selectUserFriends() {
        Log.e(TAG, "In selectUserFriends().");

        try {
            // Get a list of user IDs from current user's friends list.
            userFriendIDs = currentUser.getCurrentUser().getList("friendsList");

            // If user IDs were found, convert to usernames else print msg saying no friends.
            if (userFriendIDs != null) {
                // Convert the user IDs to usernames and add to another list.
                for (int i = 0; i < userFriendIDs.size(); i++) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    userFriends.add(query.get(userFriendIDs.get(i)).getString("username"));
                }

                // Print out current user's friends.
                ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_friends, userFriends);
                listFriends.setAdapter(adapter);

                // Set clicked (selected) flag to false.
                for (int i = 0; i < userFriendIDs.size(); i++) {
                    clicked.add(false);
                }
            } else {
                // Display msg that current user has no friends.
                ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_friends, noFriends);
                listFriends.setAdapter(adapter);
            }
        } catch (NullPointerException e) {
            // Display msg that current user has no friends.
            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_friends, noFriends);
            listFriends.setAdapter(adapter);
        } catch (ParseException e) {
            Log.e(TAG, "Unable to convert userIDs to usernames.");
        }

        // TODO remove after fragments work.
        listFriends.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeRight() {
                Intent intent = new Intent(EventCreateActivity.this, FriendsListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // On item click, highlight friend and add them to list of event participants.
        listFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (userFriendIDs != null) {
                    // If false, highlight friends and add to event, else do the opposite.
                    if (clicked.get(position) != true) {
                        eventParticipants.add(userFriendIDs.get(position));
                        listFriends.getChildAt(position).setBackgroundColor(Color.GREEN);
                        clicked.set(position, true);
                    } else {
                        eventParticipants.remove(userFriendIDs.get(position));
                        listFriends.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                        clicked.set(position, false);
                    }
                } else {
                    Intent intent = new Intent(EventCreateActivity.this, FriendsListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void createEvent() {
        Log.e(TAG, "In createEvent().");

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameOfEvent = eventName.getText().toString();

                if (nameOfEvent.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter name of event.",
                            Toast.LENGTH_LONG).show();
                } else {
                    // Create the event/set all parameters for the event.
                    ParseObject event = new ParseObject("Event");
                    event.put("eventName", nameOfEvent);
                    event.put("eventOwner", currentUser.getCurrentUser().getObjectId());

                    event.addAll("eventParticipants", eventParticipants);
                    event.add("eventParticipants", currentUser.getCurrentUser().getObjectId());

                    event.addAll("restaurants", restaurants);

                    for (int i = 0; i < 10; i++) {
                        votes.add(0);
                    }
                    event.addAll("votes", votes);

                    for (int i = 0; i < eventParticipants.size() + 1; i++) {
                        choiceIndex.add(-1);
                    }
                    event.addAll("choiceIndex", choiceIndex);

                    ParseACL newACL = new ParseACL();
                    newACL.setPublicReadAccess(true);
                    newACL.setPublicWriteAccess(true);
                    event.setACL(newACL);
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
                                            // Add event to current user.
                                            currentUser.getCurrentUser().add("eventsList",
                                                    list.get(0).getObjectId());

                                            eventId = list.get(0).getObjectId();

                                            // Send a request to each added participant.
                                            for (int i = 0; i < eventParticipants.size(); i++) {
                                                ParseObject request = new ParseObject("EventRequest");
                                                request.put("requestFrom", currentUser.getCurrentUser().getObjectId());
                                                request.put("requestTo", eventParticipants.get(i));
                                                request.put("status", "pending");
                                                request.put("eventId", eventId);
                                                ParseACL newACL = new ParseACL();
                                                newACL.setPublicReadAccess(true);
                                                newACL.setPublicWriteAccess(true);
                                                // newACL.setWriteAccess(eventParticipants.get(i), true);
                                                request.setACL(newACL);
                                                request.saveInBackground();
                                            }

                                            currentUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Intent intent = new Intent(EventCreateActivity.this,
                                                                EventVoteActivity.class);
                                                        intent.putExtra("eventId", eventId);
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
    }

    private void cancelEvent() {
        Log.e(TAG, "In cancelEvent().");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventCreateActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Method gets the API call in JSON format.
    public String setUpAPIRet(YelpAPI yelp, String location) {
        // To return.
        String ret = "";

        try {
            // Get the return from the API call.
            ret = yelp.execute(location).get();
        } catch (Exception e) {
            Log.e(TAG, "Caught exception - setUpAPIRet().");
        }

        Log.e(TAG, "Result = " + ret);
        return ret;
    }


    public void parseAndDisplayRestaurantOutput(final String yelpJSON) {
        JSONParser parser = new JSONParser();
        JSONObject response = null;

        try {
            response = (JSONObject) parser.parse(yelpJSON);
        } catch (org.json.simple.parser.ParseException e) {
            Log.e(TAG, "Error - Could not parse JSON response" + yelpJSON);
            System.exit(1);
        }

        // Each business is now represented as an array entry.
        JSONArray businesses = (JSONArray) response.get("businesses");

        // For now, manually pick the first 5 restaurants.
        JSONObject business1 = (JSONObject) businesses.get(0);
        JSONObject business2 = (JSONObject) businesses.get(1);
        JSONObject business3 = (JSONObject) businesses.get(2);
        JSONObject business4 = (JSONObject) businesses.get(3);
        JSONObject business5 = (JSONObject) businesses.get(4);
        JSONObject business6 = (JSONObject) businesses.get(5);
        JSONObject business7 = (JSONObject) businesses.get(6);
        JSONObject business8 = (JSONObject) businesses.get(7);
        JSONObject business9 = (JSONObject) businesses.get(8);
        JSONObject business10 = (JSONObject) businesses.get(9);

        // Get the name of each restaurant.
        String R1 = business1.get("name").toString();
        String R2 = business2.get("name").toString();
        String R3 = business3.get("name").toString();
        String R4 = business4.get("name").toString();
        String R5 = business5.get("name").toString();
        String R6 = business6.get("name").toString();
        String R7 = business7.get("name").toString();
        String R8 = business8.get("name").toString();
        String R9 = business9.get("name").toString();
        String R10 = business10.get("name").toString();

        // List<String> Restaurants = new ArrayList<>();
        restaurants.add(R1);
        restaurants.add(R2);
        restaurants.add(R3);
        restaurants.add(R4);
        restaurants.add(R5);
        restaurants.add(R6);
        restaurants.add(R7);
        restaurants.add(R8);
        restaurants.add(R9);
        restaurants.add(R10);
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
