package com.example.local1.foodvote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class EventVoteActivity extends AppCompatActivity {
    // Variables
    int[] count = new int[5];
    String eventId;
    List<Integer> voteCounts = new ArrayList<Integer>();
    List<String> restaurantNames = new ArrayList<String>();
    List<ParseObject> tempList = new ArrayList<ParseObject>();

    // Widgets
    Button b1, b2, b3, b4, b5, logout;
    TextView c1, c2, c3, c4, c5;

    // TAG
    private static final String TAG = "EventVoteActivity ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "In onStart().");

        listEventVotes();
        listEventRestaurants();
        restaurantClicked();
        logout();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.e(TAG, "In onBackPressed().");

        Intent intent = new Intent(EventVoteActivity.this, EventsListActivity.class);
        startActivity(intent);
        finish();
    }

    private void initializeView() {
        Log.e(TAG, "In initializeView().");

        // Get the view from event_vote.xml.
        setContentView(R.layout.event_vote);

        // Get the name of event to query for data.
        eventId = getIntent().getExtras().getString("eventId");

        // Locate widgets in event_vote.xml
        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        b5 = (Button) findViewById(R.id.button5);
        logout = (Button) findViewById(R.id.logOutButton);

        c1 = (TextView) findViewById(R.id.count1);
        c2 = (TextView) findViewById(R.id.count2);
        c3 = (TextView) findViewById(R.id.count3);
        c4 = (TextView) findViewById(R.id.count4);
        c5 = (TextView) findViewById(R.id.count5);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("objectId", eventId);
        try {
            List<ParseObject> list = query.find();

            voteCounts = list.get(0).getList("votes");
            restaurantNames = list.get(0).getList("restaurants");

            for (int i = 0; i < voteCounts.size(); i++) {
                count[i] = voteCounts.get(i);
            }
        } catch(ParseException e) {
            Log.e(TAG, "Unable to find event.");
        }
    }

    private void listEventVotes() {
        Log.e(TAG, "In listEventVotes().");

        c1.setText(String.valueOf(voteCounts.get(0)));
        c2.setText(String.valueOf(voteCounts.get(1)));
        c3.setText(String.valueOf(voteCounts.get(2)));
        c4.setText(String.valueOf(voteCounts.get(3)));
        c5.setText(String.valueOf(voteCounts.get(4)));
    }

    private void listEventRestaurants() {
        Log.e(TAG, "In listEventRestaurants().");

        b1.setText(restaurantNames.get(0));
        b2.setText(restaurantNames.get(1));
        b3.setText(restaurantNames.get(2));
        b4.setText(restaurantNames.get(3));
        b5.setText(restaurantNames.get(4));
    }

    public void restaurantClicked() {
        Log.e(TAG, "In restaurantClicked().");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.whereEqualTo("objectId", eventId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            tempList = list;
                            tempList.get(0).removeAll("votes", voteCounts);
                            tempList.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    count[0]++;
                                    c1.setText(String.valueOf(count[0]));
                                    Toast.makeText(getApplicationContext(), "Restaurant voted.",
                                            Toast.LENGTH_SHORT).show();
                                    voteCounts.remove(0);
                                    voteCounts.add(0, count[0]);
                                    tempList.get(0).addAll("votes", voteCounts);
                                    tempList.get(0).saveInBackground();
                                }
                            });
                        }
                    }
                });
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.whereEqualTo("objectId", eventId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            tempList = list;
                            tempList.get(0).removeAll("votes", voteCounts);
                            tempList.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    count[1]++;
                                    c2.setText(String.valueOf(count[1]));
                                    Toast.makeText(getApplicationContext(), "Restaurant voted.",
                                            Toast.LENGTH_SHORT).show();
                                    voteCounts.remove(1);
                                    voteCounts.add(1, count[1]);
                                    tempList.get(0).addAll("votes", voteCounts);
                                    tempList.get(0).saveInBackground();
                                }
                            });
                        }
                    }
                });
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.whereEqualTo("objectId", eventId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            tempList = list;
                            tempList.get(0).removeAll("votes", voteCounts);
                            tempList.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    count[2]++;
                                    c3.setText(String.valueOf(count[2]));
                                    Toast.makeText(getApplicationContext(), "Restaurant voted.",
                                            Toast.LENGTH_SHORT).show();
                                    voteCounts.remove(2);
                                    voteCounts.add(2, count[2]);
                                    tempList.get(0).addAll("votes", voteCounts);
                                    tempList.get(0).saveInBackground();
                                }
                            });
                        }
                    }
                });
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.whereEqualTo("objectId", eventId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            tempList = list;
                            tempList.get(0).removeAll("votes", voteCounts);
                            tempList.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    count[3]++;
                                    c4.setText(String.valueOf(count[3]));
                                    Toast.makeText(getApplicationContext(), "Restaurant voted.",
                                            Toast.LENGTH_SHORT).show();
                                    voteCounts.remove(3);
                                    voteCounts.add(3, count[3]);
                                    tempList.get(0).addAll("votes", voteCounts);
                                    tempList.get(0).saveInBackground();
                                }
                            });
                        }
                    }
                });
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.whereEqualTo("objectId", eventId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            tempList = list;
                            tempList.get(0).removeAll("votes", voteCounts);
                            tempList.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    count[4]++;
                                    c5.setText(String.valueOf(count[4]));
                                    Toast.makeText(getApplicationContext(), "Restaurant voted.",
                                            Toast.LENGTH_SHORT).show();
                                    voteCounts.remove(4);
                                    voteCounts.add(4, count[4]);
                                    tempList.get(0).addAll("votes", voteCounts);
                                    tempList.get(0).saveInBackground();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void logout() {
        // logout button click listener.
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout current user.
                ParseUser.logOut();

                // After logout, switch to LoginSignUpActivity.
                Intent intent = new Intent(EventVoteActivity.this,
                        LoginSignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Successfully logged out.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
