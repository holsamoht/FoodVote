package com.example.local1.foodvote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginSignUpActivity extends Activity {
    Button loginButton;
    Button signUpButton;
    EditText password;
    EditText username;
    String usernameText;
    String passwordText;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the view from login_sign_up.xml.
        setContentView(R.layout.login_sign_up);

        // Locate EditTexts in login_sign_up.xml.
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        // Locate Buttons in login_sign_up.xml.
        loginButton = (Button) findViewById(R.id.login);
        signUpButton = (Button) findViewById(R.id.signup);

        // Login Button click listener.
        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText.
                usernameText = username.getText().toString();
                passwordText = password.getText().toString();

                // Send data to Parse.com for verification.
                ParseUser.logInInBackground(usernameText, passwordText,
                        new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    // If user exists and authenticated, send user to MainActivity.class.
                                    Intent intent = new Intent(
                                            LoginSignUpActivity.this,
                                            MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),
                                            "Successfully logged in.",
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "No such user exist, please sign-up.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // Sign up Button click listener.
        signUpButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText.
                usernameText = username.getText().toString();
                passwordText = password.getText().toString();

                // Force user to fill out the sign up form.
                if (usernameText.equals("") && passwordText.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the sign-up form.",
                            Toast.LENGTH_LONG).show();
                } else {
                    // Save new user data into Parse.com data storage.
                    ParseUser user = new ParseUser();
                    user.setUsername(usernameText);
                    user.setPassword(passwordText);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Show a message upon successful registration.
                                Toast.makeText(getApplicationContext(),
                                        "Successfully signed up, please log in.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Sign-up error.", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                }

            }
        });

    }
}
