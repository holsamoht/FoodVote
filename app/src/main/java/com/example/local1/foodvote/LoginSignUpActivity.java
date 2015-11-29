package com.example.local1.foodvote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginSignUpActivity extends AppCompatActivity {
    // Variables
    String usernameText;
    String passwordText;

    // Widgets
    EditText password;
    EditText username;
    Button loginButton;
    Button signUpButton;

    // Log TAG
    private static final String TAG = "LoginSignUpActivity ";

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

        login();
        signUp();
    }

    private void initializeView() {
        Log.e(TAG, "In initializeData().");

        // Set the view from login_sign_up.xml.
        setContentView(R.layout.login_sign_up);

        // Locate EditTexts in login_sign_up.xml.
        username = (EditText) findViewById(R.id.usernameText);
        password = (EditText) findViewById(R.id.passwordText);

        // Locate Buttons in login_sign_up.xml.
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
    }

    private void login() {
        Log.e(TAG, "In login().");

        // Login Button click listener.
        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Retrieve the text entered from the EditText.
                usernameText = username.getText().toString();
                passwordText = password.getText().toString();

                // Send data to Parse.com for verification.
                ParseUser.logInInBackground(usernameText, passwordText, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        // If user exists and authenticated, send user to EventsListActivity.class.
                        if (parseUser != null) {
                            Intent intent = new Intent(LoginSignUpActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Successfully logged in.",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "No user exists, please sign-up.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void signUp() {
        Log.e(TAG, "In signUp().");

        // Sign up Button click listener.
        signUpButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Retrieve the text entered from the EditText.
                usernameText = username.getText().toString();
                passwordText = password.getText().toString();

                // Force user to fill out the sign up form.
                if (!(usernameText.equals("")) && !(passwordText.equals(""))) {
                    // Save new user data into Parse.com data storage.
                    ParseUser user = new ParseUser();
                    user.setUsername(usernameText);
                    user.setPassword(passwordText);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(),
                                        "Successfully signed up, please log in.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Username taken, please enter a new one.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the sign-up form.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}