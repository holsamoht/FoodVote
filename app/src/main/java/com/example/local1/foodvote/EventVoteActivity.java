package com.example.local1.foodvote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class EventVoteActivity extends AppCompatActivity {
    // Variables
    int[] count = new int[10];
    int index;
    static String eventId;
    List<Integer> voteCounts = new ArrayList<Integer>();
    List<String> restaurantNames = new ArrayList<String>();
    List<ParseObject> tempList = new ArrayList<ParseObject>();

    // Widgets
    Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b10;
    Button c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, cn;
    Toolbar toolbar;

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
    }

    /* go back to FragmentContainer on back button */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.e(TAG, "In onBackPressed().");

        Intent intent = new Intent(EventVoteActivity.this, FragmentContainer.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        menu.getItem(0).setTitle("Logged in: " + ParseUser.getCurrentUser().getUsername());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            ParseUser.getCurrentUser().logOut();
            Intent intent = new Intent(EventVoteActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
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
        b6 = (Button) findViewById(R.id.button6);
        b7 = (Button) findViewById(R.id.button7);
        b8 = (Button) findViewById(R.id.button8);
        b9 = (Button) findViewById(R.id.button9);
        b10 = (Button) findViewById(R.id.button10);

        c1 = (Button) findViewById(R.id.count1);
        c2 = (Button) findViewById(R.id.count2);
        c3 = (Button) findViewById(R.id.count3);
        c4 = (Button) findViewById(R.id.count4);
        c5 = (Button) findViewById(R.id.count5);
        c6 = (Button) findViewById(R.id.count6);
        c7 = (Button) findViewById(R.id.count7);
        c8 = (Button) findViewById(R.id.count8);
        c9 = (Button) findViewById(R.id.count9);
        c10 = (Button) findViewById(R.id.count10);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // find the event in Parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("objectId", eventId);
        try {
            List<ParseObject> list = query.find();

            // get the list of votes and the list of restaurants
            voteCounts = list.get(0).getList("votes");
            restaurantNames = list.get(0).getList("restaurants");


            for (int i = 0; i < voteCounts.size(); i++) {
                count[i] = voteCounts.get(i);
            }
        } catch (ParseException e) {
            Log.e(TAG, "Unable to find event.");
        }
    }

    // update the view with the votes currently stored in Parse
    private void listEventVotes() {
        Log.e(TAG, "In listEventVotes().");

        c1.setText(String.valueOf(voteCounts.get(0)));
        c2.setText(String.valueOf(voteCounts.get(1)));
        c3.setText(String.valueOf(voteCounts.get(2)));
        c4.setText(String.valueOf(voteCounts.get(3)));
        c5.setText(String.valueOf(voteCounts.get(4)));
        c6.setText(String.valueOf(voteCounts.get(5)));
        c7.setText(String.valueOf(voteCounts.get(6)));
        c8.setText(String.valueOf(voteCounts.get(7)));
        c9.setText(String.valueOf(voteCounts.get(8)));
        c10.setText(String.valueOf(voteCounts.get(9)));
    }

    // update the view with the restaurant names stored in Parse
    private void listEventRestaurants() {
        Log.e(TAG, "In listEventRestaurants().");

        b1.setText(restaurantNames.get(0));
        b2.setText(restaurantNames.get(1));
        b3.setText(restaurantNames.get(2));
        b4.setText(restaurantNames.get(3));
        b5.setText(restaurantNames.get(4));
        b6.setText(restaurantNames.get(5));
        b7.setText(restaurantNames.get(6));
        b8.setText(restaurantNames.get(7));
        b9.setText(restaurantNames.get(8));
        b10.setText(restaurantNames.get(9));
    }

    public void restaurantClicked() {
        /* when the user clicks on a voting button, handle the vote actions for that button */
        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cn = c1;
                index = 0;
                handleVote();
            }
        });

        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cn = c2;
                index = 1;
                handleVote();
            }
        });

        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cn = c3;
                index = 2;
                handleVote();
            }
        });

        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cn = c4;
                index = 3;
                handleVote();
            }
        });

        c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cn = c5;
                index = 4;
                handleVote();
            }
        });

        c6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cn = c6;
                index = 5;
                handleVote();
            }
        });

        c7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cn = c7;
                index = 6;
                handleVote();
            }
        });

        c8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cn = c8;
                index = 7;
                handleVote();
            }
        });

        c9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cn = c9;
                index = 8;
                handleVote();
            }
        });

        c10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cn = c10;
                index = 9;
                handleVote();
            }
        });

        /* when a user clicks on a restaurant's name, show ExtraYelpInformationActivity with that
        respective restaurant's info */
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExtraYelpInformationActivity(eventId, 0);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExtraYelpInformationActivity(eventId, 1);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExtraYelpInformationActivity(eventId, 2);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExtraYelpInformationActivity(eventId, 3);
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExtraYelpInformationActivity(eventId, 4);
            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExtraYelpInformationActivity(eventId, 5);
            }
        });

        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExtraYelpInformationActivity(eventId, 6);
            }
        });

        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExtraYelpInformationActivity(eventId, 7);
            }
        });

        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExtraYelpInformationActivity(eventId, 8);
            }
        });

        b10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExtraYelpInformationActivity(eventId, 9);
            }
        });

    }

    /* handles a user voting for the first time, voting for a different restaurant while they currently
     * have a vote stored, and unvoting */
    public void handleVote() {
        // find the first Vote object with the user's id and the event id
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("eventId", eventId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                // if such a Vote object doesn't exist yet, that means the user has not voted on
                // this Event yet
                if (parseObject == null) {
                    // get the event
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                    query.whereEqualTo("objectId", eventId);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {
                                // create a new Vote object and save the userId, eventId, and
                                // list of restaurants
                                ParseObject vote = new ParseObject("Vote");
                                vote.put("userId", ParseUser.getCurrentUser().getObjectId());
                                vote.put("eventId", eventId);
                                vote.put("restaurant", restaurantNames.get(index));
                                vote.saveInBackground();
                                // now update the votes array in the event
                                // first remove all the current votes
                                tempList = list;
                                tempList.get(0).removeAll("votes", voteCounts);
                                tempList.get(0).saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        count[index]++;
                                        cn.setText(String.valueOf(count[index]));
                                        Toast.makeText(getApplicationContext(),
                                                "Restaurant voted.",
                                                Toast.LENGTH_SHORT).show();
                                        // now update the vote number for the respective restaurant
                                        voteCounts.remove(index);
                                        voteCounts.add(index, count[index]);
                                        // now save it back into the Event object
                                        tempList.get(0).addAll("votes", voteCounts);
                                        tempList.get(0).saveInBackground();
                                    }
                                });
                            }
                        }
                    });
                }
                // if such a Vote object exists, the user has already voted, so either delete
                // their vote or change their vote
                else if (parseObject != null) {
                    // get the Vote object that matches the userId, eventId, and restaurant
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                    query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                    query.whereEqualTo("eventId", eventId);
                    query.whereEqualTo("restaurant", restaurantNames.get(index));
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            // if such a Vote object was not found, then the user's vote must be
                            // currently registered for a different restaurant
                            if (parseObject == null) {
                                Toast.makeText(getApplicationContext(),
                                        "You have already voted.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // otherwise their vote is registered for the same restaurant whose
                            // count button they clicked on, so undo the vote
                            else {
                                // unvote
                                parseObject.deleteInBackground();
                                // now decrement the number of votes for that restaurant in the
                                // event's votes array
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
                                                    // decrementing vote
                                                    count[index]--;
                                                    cn.setText(String.valueOf(count[index]));
                                                    Toast.makeText(getApplicationContext(),
                                                            "Restaurant unvoted.",
                                                            Toast.LENGTH_SHORT).show();
                                                    voteCounts.remove(index);
                                                    voteCounts.add(index, count[index]);
                                                    tempList.get(0).addAll("votes",
                                                            voteCounts);
                                                    tempList.get(0).saveInBackground();
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
        });
    }

    /* start the ExtraYelpInformationActivity intent after passing some variables */
    public void goToExtraYelpInformationActivity(String eventIdParam, int position) {
        Intent intent = new Intent(EventVoteActivity.this, ExtraYelpInformationActivity.class);
        intent.putExtra("eventId", eventIdParam);
        intent.putExtra("position", position);
        startActivity(intent);
        finish();
    }
}