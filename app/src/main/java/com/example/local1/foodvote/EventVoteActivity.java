package com.example.local1.foodvote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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

    int count1, count2, count3, count4, count5 = 0;
    String nameOfEvent;
    List<String> restaurantNames = new ArrayList<String>();
    List<Integer> voteCounts = new ArrayList<Integer>();
    List<ParseObject> tempList = new ArrayList<ParseObject>();
    Button B1, B2, B3, B4, B5, logout;
    TextView C1, C2, C3, C4, C5;
    RelativeLayout switchActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the view from event_vote.xml.
        setContentView(R.layout.event_vote);

        // Get the name of event to query for data.
        nameOfEvent = getIntent().getExtras().getString("eventName");

        // Locate logout button in event_vote.xml.
        logout = (Button) findViewById(R.id.logout);

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

        switchActivity = (RelativeLayout) findViewById(R.id.entireScreen);
        switchActivity.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                Intent intent = new Intent(EventVoteActivity.this,
                        EventsListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /*
        B1 = (Button) findViewById(R.id.Button1);
        B1.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                Intent intent = new Intent(EventVoteActivity.this, EventsListActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onSwipeRight() {
                Intent intent = new Intent(EventVoteActivity.this, FriendsListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        B2 = (Button) findViewById(R.id.Button2);
        B2.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                Intent intent = new Intent(EventVoteActivity.this, EventsListActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onSwipeRight() {
                Intent intent = new Intent(EventVoteActivity.this, FriendsListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        B3 = (Button) findViewById(R.id.Button3);
        B3.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                Intent intent = new Intent(EventVoteActivity.this, EventsListActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onSwipeRight() {
                Intent intent = new Intent(EventVoteActivity.this, FriendsListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        B4 = (Button) findViewById(R.id.Button4);
        B4.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                Intent intent = new Intent(EventVoteActivity.this, EventsListActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onSwipeRight() {
                Intent intent = new Intent(EventVoteActivity.this, FriendsListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        B5 = (Button) findViewById(R.id.Button5);
        B5.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                Intent intent = new Intent(EventVoteActivity.this, EventsListActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onSwipeRight() {
                Intent intent = new Intent(EventVoteActivity.this, FriendsListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        */

        // Get each view for the restaurant buttons.
        B1 = (Button)findViewById(R.id.Button1);
        B2 = (Button)findViewById(R.id.Button2);
        B3 = (Button)findViewById(R.id.Button3);
        B4 = (Button)findViewById(R.id.Button4);
        B5 = (Button)findViewById(R.id.Button5);

        // Get each view for the vote count.
        C1 = (TextView)findViewById(R.id.textView1);
        C2 = (TextView)findViewById(R.id.textView2);
        C3 = (TextView)findViewById(R.id.textView3);
        C4 = (TextView)findViewById(R.id.textView4);
        C5 = (TextView)findViewById(R.id.textView5);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("eventName", nameOfEvent);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    restaurantNames = list.get(0).getList("restaurants");

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                    query.whereEqualTo("eventName", nameOfEvent);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {
                                voteCounts = list.get(0).getList("votes");

                                count1 = voteCounts.get(0);
                                count2 = voteCounts.get(1);
                                count3 = voteCounts.get(2);
                                count4 = voteCounts.get(3);
                                count5 = voteCounts.get(4);

                                // Set the buttons and text views to the appropriate data.
                                B1.setText(restaurantNames.get(0));
                                C1.setText(String.valueOf(voteCounts.get(0)));
                                B2.setText(restaurantNames.get(1));
                                C2.setText(String.valueOf(voteCounts.get(1)));
                                B3.setText(restaurantNames.get(2));
                                C3.setText(String.valueOf(voteCounts.get(2)));
                                B4.setText(restaurantNames.get(3));
                                C4.setText(String.valueOf(voteCounts.get(3)));
                                B5.setText(restaurantNames.get(4));
                                C5.setText(String.valueOf(voteCounts.get(4)));
                            }
                        }
                    });
                }
            }
        });
    }

    public void RestaurantClicked(View v){
        B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.whereEqualTo("eventName", nameOfEvent);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            tempList = list;
                            tempList.get(0).removeAll("votes", voteCounts);
                            tempList.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    count1++;
                                    C1.setText(String.valueOf(count1));
                                    Toast.makeText(getApplicationContext(), "Restaurant voted.", Toast.LENGTH_SHORT).show();
                                    voteCounts.remove(0);
                                    voteCounts.add(0, count1);
                                    tempList.get(0).addAll("votes", voteCounts);
                                    tempList.get(0).saveInBackground();
                                }
                            });
                        }
                    }
                });
            }
        });

        B2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.whereEqualTo("eventName", nameOfEvent);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            tempList = list;
                            tempList.get(0).removeAll("votes", voteCounts);
                            tempList.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    count2++;
                                    C2.setText(String.valueOf(count2));
                                    Toast.makeText(getApplicationContext(), "Restaurant voted.", Toast.LENGTH_SHORT).show();
                                    voteCounts.remove(1);
                                    voteCounts.add(1, count2);
                                    tempList.get(0).addAll("votes", voteCounts);
                                    tempList.get(0).saveInBackground();
                                }
                            });
                        }
                    }
                });
            }
        });

        B3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.whereEqualTo("eventName", nameOfEvent);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            tempList = list;
                            tempList.get(0).removeAll("votes", voteCounts);
                            tempList.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    count3++;
                                    C3.setText(String.valueOf(count3));
                                    Toast.makeText(getApplicationContext(), "Restaurant voted.", Toast.LENGTH_SHORT).show();
                                    voteCounts.remove(2);
                                    voteCounts.add(2, count3);
                                    tempList.get(0).addAll("votes", voteCounts);
                                    tempList.get(0).saveInBackground();
                                }
                            });
                        }
                    }
                });
            }
        });

        B4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.whereEqualTo("eventName", nameOfEvent);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            tempList = list;
                            tempList.get(0).removeAll("votes", voteCounts);
                            tempList.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    count4++;
                                    C4.setText(String.valueOf(count4));
                                    Toast.makeText(getApplicationContext(), "Restaurant voted.", Toast.LENGTH_SHORT).show();
                                    voteCounts.remove(3);
                                    voteCounts.add(3, count4);
                                    tempList.get(0).addAll("votes", voteCounts);
                                    tempList.get(0).saveInBackground();
                                }
                            });
                        }
                    }
                });
            }
        });

        B5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.whereEqualTo("eventName", nameOfEvent);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            tempList = list;
                            tempList.get(0).removeAll("votes", voteCounts);
                            tempList.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    count5++;
                                    C5.setText(String.valueOf(count5));
                                    Toast.makeText(getApplicationContext(), "Restaurant voted.", Toast.LENGTH_SHORT).show();
                                    voteCounts.remove(4);
                                    voteCounts.add(4, count5);
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
}
