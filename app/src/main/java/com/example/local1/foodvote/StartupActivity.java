package com.example.local1.foodvote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
import com.parse.Parse;
import com.parse.ParseACL;

public class StartupActivity extends AppCompatActivity {
    // Parse API keys.
    private static final String APPLICATION_ID = "eXtNPVXW7taCtotOcfAWtrDEVyZxS1UksSgVSLvm";
    private static final String CLIENT_KEY = "bffulWPQ7a9WaGOQXN8BfI3oNkkhkLtWZql0MUkQ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Parse using application ID and client key.
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // Remove this line if you want all objects to be private by default.
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        // Check whether the current user is an anonymous user.
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            // If the user is anonymous, send the user to LoginSignUpActivity.class.
            Intent intent = new Intent(StartupActivity.this,
                    LoginSignUpActivity.class);
            startActivity(intent);
            finish();
        } else {
            // If user is not anonymous, get user data from Parse.com
            ParseUser currentUser = ParseUser.getCurrentUser();

            // Check if user data was successfully obtained.
            if (currentUser != null) {
                // Send logged in user to MainActivity.class
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
