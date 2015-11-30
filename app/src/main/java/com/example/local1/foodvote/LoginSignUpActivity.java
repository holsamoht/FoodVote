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
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginSignUpActivity extends AppCompatActivity {
    // Variables
    String usernameText;
    String passwordText;

    // Widgets
    Button loginButton;
    Button signUpButton;
    EditText password;
    EditText username;

    // Log TAG
    private static final String TAG = "LoginSignUpActivity ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        login();
        signUp();
    }

    /**
     * Initialize view and locate widgets within login_sign_up.xml.
     */
    private void initializeView() {
        // Set the view from login_sign_up.xml.
        setContentView(R.layout.login_sign_up);

        // Locate widgets in login_sign_up.xml
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        username = (EditText) findViewById(R.id.usernameText);
        password = (EditText) findViewById(R.id.passwordText);
    }

    /**
     * On login button press, log in current user by authenticating info. If either username
     * or password was entered incorrectly, display message that login failed or if the current
     * user does not exist within our database, user must sign-up first.
     */
    private void login() {
        // Login button click listener.
        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Retrieve username and password entered in the EditText.
                usernameText = username.getText().toString();
                passwordText = password.getText().toString();

                // If username or password is not entered, display message to enter in info; else,
                // try to log user in.
                if (usernameText.equals("") || passwordText.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter in username and password",
                            Toast.LENGTH_SHORT).show();
                } else {
                    ParseUser.logInInBackground(usernameText, passwordText, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (parseUser == null) {
                                Toast.makeText(getApplicationContext(),
                                        "Username or password is wrong, please try again.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Logged in.",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginSignUpActivity.this,
                                        FragmentContainer.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * On sign-up button press, create a new ParseUser object within Parse and store entered in
     * username and password. Usernames must be unique.
     */
    private void signUp() {
        // Sign-up Button click listener.
        signUpButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Retrieve username and password entered in the EditText.
                usernameText = username.getText().toString();
                passwordText = password.getText().toString();

                // If username or password is not entered, display message to enter in info; else,
                // create a new ParseUser with entered in info and save to Parse.
                if (usernameText.equals("") || passwordText.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the sign-up form.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    ParseUser user = new ParseUser();
                    user.setUsername(usernameText);
                    user.setPassword(passwordText);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(),
                                        "Signed up, please log in.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Username taken, please enter a new one.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}