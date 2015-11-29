package com.example.local1.foodvote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;

public class StartupActivity extends AppCompatActivity {
    // Parse API keys.
    private static final String APPLICATION_ID = "eXtNPVXW7taCtotOcfAWtrDEVyZxS1UksSgVSLvm";
    private static final String CLIENT_KEY = "bffulWPQ7a9WaGOQXN8BfI3oNkkhkLtWZql0MUkQ";

    // Log TAG
    private static final String TAG = "StartupActivity ";

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

        checkIfLoggedIn();
    }

    private void initializeView() {
        Log.e(TAG, "In initializeData().");

        // Initialize Parse using application ID and client key.
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

        // Initialize automatic user.
        ParseUser.enableAutomaticUser();

        // Initialize Parse public access.
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

    private void checkIfLoggedIn() {
        Log.e(TAG, "In checkIfLoggedIn().");

        // Check whether the current user is anonymous (not logged in).
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            // If the user is anonymous (not logged in), send the user to LoginSignUpActivity.class.
            Intent intent = new Intent(StartupActivity.this,
                    LoginSignUpActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Else, user is not anonymous (is logged in), get user data from Parse.com
            ParseUser currentUser = ParseUser.getCurrentUser();

            // Check if user data was successfully obtained.
            if (currentUser != null) {
                // Send logged in user to EventsListActivity.class
                Intent intent = new Intent(StartupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // User data was not successfully obtained, send user to LoginSignUpActivity.class
                Intent intent = new Intent(StartupActivity.this,
                        LoginSignUpActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}