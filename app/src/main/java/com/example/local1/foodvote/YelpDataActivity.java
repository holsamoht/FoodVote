package com.example.local1.foodvote;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class YelpDataActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Variables
    String mLatitudeText;
    String mLongitudeText;
    String eventId;
    String businessType;
    String loc;


    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    YelpAPI yAPI;

    List<String> eventParticipants = new ArrayList<String>();
    List<String> restaurants = new ArrayList<String>();
    List<String> phoneNumbers = new ArrayList<String>();
    List<String> addresses = new ArrayList<String>();
    List<String> ratings = new ArrayList<String>();
    List<String> typeOfFood = new ArrayList<String>();
    List<String> hours = new ArrayList<String>();


    // Yelp API keys
    private static final String CONSUMER_KEY = "FQFe1MpY3PGvxKy-Aq702g";
    private static final String CONSUMER_SECRET = "u_ifYEaonk6W5sf24SCXiGPKx6I";
    private static final String TOKEN = "6YSX1I448VpE2WQ1rrQv0NJRNJ9E9rOX";
    private static final String TOKEN_SECRET = "_iN4GhZsdgojn1WYZTHOi-q2jzM";

    // Log TAG
    private static final String TAG = "YelpDataActivity ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (loc.toLowerCase().equals("current location") || loc.isEmpty()) {
            // Use if developing on an android phone.
            if (mLastLocation != null) {
                mLatitudeText = String.valueOf(mLastLocation.getLatitude());
                mLongitudeText = String.valueOf(mLastLocation.getLongitude());
                String location = mLatitudeText + ", " + mLongitudeText;
                String YelpJSON = setUpAPIRet(yAPI, location, businessType, "Current");
                parseAndDisplayRestaurantOutput(YelpJSON);
                mGoogleApiClient.disconnect();
            }

            /*
            // Use if developing on an emulator.
            if (mLastLocation == null) {
                mLatitudeText = "32.8810";
                mLongitudeText = "-117.2380";
                String location = mLatitudeText + ", " + mLongitudeText;
                String YelpJSON = setUpAPIRet(yAPI, location, businessType, "Current");
                parseAndDisplayRestaurantOutput(YelpJSON);
                mGoogleApiClient.disconnect();
            }

            */
        } else {
            String location = loc;
            String YelpJSON = setUpAPIRet(yAPI, loc, businessType, "Other");
            parseAndDisplayRestaurantOutput(YelpJSON);
            mGoogleApiClient.disconnect();
        }
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

    private void initializeView() {
        eventId = getIntent().getExtras().getString("eventId");
        businessType = getIntent().getExtras().getString("businessType");
        loc = getIntent().getExtras().getString("location");

        // Set Yelp API keys
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

    // Method gets the API call in JSON format.
    public String setUpAPIRet(YelpAPI yelp, String location, String parameter, String typeOfLocation) {
        Log.e(TAG, "In setUpAPIRet");

        // To return.
        String ret = "";

        try {
            // Get the return from the API call.
            ret = yelp.execute(typeOfLocation, parameter, location).get();
        } catch (Exception e) {
            Log.e(TAG, "Caught exception - setUpAPIRet().");
        }

        Log.e(TAG, "Result = " + ret);
        return ret;
    }

    public void parseAndDisplayRestaurantOutput(final String yelpJSON) {
        Log.e(TAG, "In parseAndDisplayRestaurantOutput");

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
        JSONObject errors = (JSONObject) response.get("error");

        if (errors != null) {
            Toast.makeText(getApplicationContext(), "Location error.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(YelpDataActivity.this, EventCreateActivity.class);
            startActivity(intent);
            finish();
        } else if(businesses.size() == 0){
            Toast.makeText(getApplicationContext(), "Parameter error.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(YelpDataActivity.this, EventCreateActivity.class);
            startActivity(intent);
            finish();
        } else {
            /*List<String> phoneNumbers = new ArrayList<String>();
            List<String> addresses = new ArrayList<String>();
            List<String> ratings = new ArrayList<String>();
            List<String> typeOfFood = new ArrayList<String>();*/


            List<JSONObject> bus = new ArrayList<JSONObject>();

            for(int i = 0; i < businesses.size(); i++){
                JSONObject business = (JSONObject) businesses.get(i);
                restaurants.add(business.get("name").toString());
                if(business.get("display_phone") != null) {
                    phoneNumbers.add(business.get("display_phone").toString());
                }
                else{
                    phoneNumbers.add("No Available Phone Number");
                }

                if(business.get("rating") != null) {
                    ratings.add(business.get("rating").toString());
                }
                else{
                    ratings.add("No Rating Information Available");
                }

                JSONObject locationData = (JSONObject)(business.get("location"));

                if(locationData.get("display_address") != null) {
                    String address = locationData.get("display_address").toString();
                    Log.e("YDA: ", address);
                    addresses.add(address);
                }
                else{
                    addresses.add("No Address Available");
                }

                if(business.get("categories") != null){
                    List<List<String>> category = (List<List<String>>)business.get("categories");
                    String categoryStr = "";
                    for(int j = 0; j < category.size(); j++){
                        categoryStr = categoryStr + category.get(j).get(0).toString();
                        if(j < category.size() - 1) {
                            categoryStr = categoryStr + ", ";
                        }

                    }

                    Log.e("YDA: ", categoryStr);

                    typeOfFood.add(categoryStr);
                }
                else{
                    typeOfFood.add("Unknown Restaurant Type");
                }



            }
            // For now, manually pick the first 5 restaurants.
            /*JSONObject business1 = (JSONObject) businesses.get(0);
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
            restaurants.add(R10);*/

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
            query.whereEqualTo("objectId", eventId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        eventParticipants = list.get(0).getList("eventParticipants");
                        list.get(0).addAll("restaurants", restaurants);
                        list.get(0).addAll("phoneNumbers", phoneNumbers);
                        list.get(0).addAll("addresses", addresses);
                        list.get(0).addAll("ratings", ratings);
                        list.get(0).addAll("typeOfFood", typeOfFood);

                        list.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ParseUser.getCurrentUser().add("eventsList", eventId);

                                    for (int i = 0; i < eventParticipants.size() - 1; i++) {
                                        ParseObject request = new
                                                ParseObject("EventRequest");
                                        request.put("requestFrom",
                                                ParseUser.getCurrentUser().getObjectId());
                                        request.put("requestTo",
                                                eventParticipants.get(i));
                                        request.put("action", "add");
                                        request.put("status", "pending");
                                        request.put("eventId", eventId);
                                        ParseACL newACL = new ParseACL();
                                        newACL.setPublicReadAccess(true);
                                        newACL.setPublicWriteAccess(true);
                                        request.setACL(newACL);
                                        request.saveInBackground();
                                    }

                                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Intent intent = new Intent(YelpDataActivity.this,
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
