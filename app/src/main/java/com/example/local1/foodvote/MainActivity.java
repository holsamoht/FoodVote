package com.example.local1.foodvote;

import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import android.location.Location;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /*
     * Update OAuth credentials below from the Yelp Developers API site:
     * http://www.yelp.com/developers/getting_started/api_access
     */
    private static final String CONSUMER_KEY = "FQFe1MpY3PGvxKy-Aq702g";
    private static final String CONSUMER_SECRET = "u_ifYEaonk6W5sf24SCXiGPKx6I";
    private static final String TOKEN = "6YSX1I448VpE2WQ1rrQv0NJRNJ9E9rOX";
    private static final String TOKEN_SECRET = "_iN4GhZsdgojn1WYZTHOi-q2jzM";

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String mLatitudeText;
    String mLongitudeText;
    YelpAPI yAPI;
    Button logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the view from main.xml
        setContentView(R.layout.main);

        // Check availability of play services.
        if (checkPlayServices()) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Locate Button in main.xml.
        logout = (Button) findViewById(R.id.logout);

        // Logout Button click listener.
        logout.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                // Logout current user.
                ParseUser.logOut();

                Intent intent = new Intent(
                        MainActivity.this,
                        LoginSignUpActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                Toast.makeText(getApplicationContext(),
                        "Successfully logged out.",
                        Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.println(Log.ERROR, "MAIN:", "In OnStart");

        yAPI = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        String location = "";

        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle bundle){
        Log.println(Log.ERROR, "MAIN:", "In onConnected");

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        // Use if developing on an android phone.
        if (mLastLocation != null) {
            mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            mLongitudeText = String.valueOf(mLastLocation.getLongitude());
            String location = mLatitudeText + ", " + mLongitudeText;
            String YelpJSON = setUpAPIRet(yAPI, location);
            ParseAndDisplayRestaurantOutput(YelpJSON);
            mGoogleApiClient.disconnect();
        }
        
        /*
        // Use if developing on an emulator.
        if (mLastLocation == null) {
            mLatitudeText = "32.8810";
            mLongitudeText = "-117.2380";
            String location = mLatitudeText + ", " + mLongitudeText;
            String YelpJSON = setUpAPIRet(yAPI, location);
            ParseAndDisplayRestaurantOutput(YelpJSON);
            mGoogleApiClient.disconnect();
        }
        */
    }


    @Override
    public void onConnectionSuspended(int x){
        Log.println(Log.ERROR, "MAIN:", "Connection Suspended");

        Toast.makeText(getApplicationContext(), "Connection Suspended.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionFailed(ConnectionResult c){
        Log.println(Log.ERROR, "MAIN:", "Connection Failed");

        Toast.makeText(getApplicationContext(), "Connection Failed.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
     * Method gets the API call in JSON format.
     */
    public String setUpAPIRet(YelpAPI Y, String Location){
        // To return.
        String Ret = "";

        try {
            // Get the return from the API call.
            Ret = Y.execute(Location).get();
        }
        catch(Exception e){
            System.out.print("Hi");
        }

        Log.println(Log.ERROR, "MAIN:", "result = " + Ret);
        return Ret;
    }

    public void ParseAndDisplayRestaurantOutput(String YelpJSON){
        JSONParser parser = new JSONParser();
        JSONObject response = null;
        try {
            response = (JSONObject) parser.parse(YelpJSON);
        } catch (ParseException pe) {
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

        // GET EACH TEXT VIEW BOX.
        Button B1 = (Button)findViewById(R.id.Button1);
        Button B2 = (Button)findViewById(R.id.Button2);
        Button B3 = (Button)findViewById(R.id.Button3);
        Button B4 = (Button)findViewById(R.id.Button4);
        Button B5 = (Button)findViewById(R.id.Button5);

        // SET EACH EXT VIEW BOX TO THE RESTAURANT NAME.
        B1.setText(R1);
        B2.setText(R2);
        B3.setText(R3);
        B4.setText(R4);
        B5.setText(R5);
    }


    public void RestaurantClicked(View v){
        Context context = getApplicationContext();
        CharSequence text = "Restaurant Clicked";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    /*
     * Method to verify google play services on the device.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        1000).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }
}
