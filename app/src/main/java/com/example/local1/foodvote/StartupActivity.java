package com.example.local1.foodvote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;

public class StartupActivity extends AppCompatActivity {
    // Log TAG
    private static final String TAG = "StartupActivity ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkIfLoggedIn();
    }

    /**
     * Initializes Parse with ID/key and sets up an automatic ParseUser to check authentication.
     */
    private void initializeView() {
        // Initialize automatic user.
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

    /**
     * Checks if current user is an anonymous user, if the current user is anonymous, send current
     * user to login/sign-up activity; else if the current user is not anonymous, send  current user
     * to events list activity (aka FragmentContainer).
     */
    private void checkIfLoggedIn() {
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            Intent intent = new Intent(StartupActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Check if user data was successfully obtained. If not, user -> login/sign-up.
            if (ParseUser.getCurrentUser() == null) {
                Intent intent = new Intent(StartupActivity.this, LoginSignUpActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(StartupActivity.this, FragmentContainer.class);
                startActivity(intent);
                finish();
            }
        }
    }
}