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
import android.widget.TextView;
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
    static String eventId;
    List<Integer> voteCounts = new ArrayList<Integer>();
    List<String> restaurantNames = new ArrayList<String>();
    List<ParseObject> tempList = new ArrayList<ParseObject>();

    // Widgets
    Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b10;
    TextView c1, c2, c3, c4, c5, c6, c7, c8, c9, c10;
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

        c1 = (TextView) findViewById(R.id.count1);
        c2 = (TextView) findViewById(R.id.count2);
        c3 = (TextView) findViewById(R.id.count3);
        c4 = (TextView) findViewById(R.id.count4);
        c5 = (TextView) findViewById(R.id.count5);
        c6 = (TextView) findViewById(R.id.count6);
        c7 = (TextView) findViewById(R.id.count7);
        c8 = (TextView) findViewById(R.id.count8);
        c9 = (TextView) findViewById(R.id.count9);
        c10 = (TextView) findViewById(R.id.count10);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        c6.setText(String.valueOf(voteCounts.get(5)));
        c7.setText(String.valueOf(voteCounts.get(6)));
        c8.setText(String.valueOf(voteCounts.get(7)));
        c9.setText(String.valueOf(voteCounts.get(8)));
        c10.setText(String.valueOf(voteCounts.get(9)));
    }

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
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("eventId", eventId);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (parseObject == null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                            query.whereEqualTo("objectId", eventId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        ParseObject vote = new ParseObject("Vote");
                                        vote.put("userId", ParseUser.getCurrentUser().getObjectId());
                                        vote.put("eventId", eventId);
                                        vote.put("restaurant", restaurantNames.get(0));
                                        vote.saveInBackground();
                                        tempList = list;
                                        tempList.get(0).removeAll("votes", voteCounts);
                                        tempList.get(0).saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                count[0]++;
                                                c1.setText(String.valueOf(count[0]));
                                                Toast.makeText(getApplicationContext(),
                                                        "Restaurant voted.",
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
                        } else if (parseObject != null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                            query.whereEqualTo("eventId", eventId);
                            query.whereEqualTo("restaurant", restaurantNames.get(0));
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (parseObject == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "You have already voted.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        parseObject.deleteInBackground();
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
                                                            count[0]--;
                                                            c1.setText(String.valueOf(count[0]));
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Restaurant unvoted.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            voteCounts.remove(0);
                                                            voteCounts.add(0, count[0]);
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
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("eventId", eventId);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (parseObject == null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                            query.whereEqualTo("objectId", eventId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        ParseObject vote = new ParseObject("Vote");
                                        vote.put("userId", ParseUser.getCurrentUser().getObjectId());
                                        vote.put("eventId", eventId);
                                        vote.put("restaurant", restaurantNames.get(1));
                                        vote.saveInBackground();
                                        tempList = list;
                                        tempList.get(0).removeAll("votes", voteCounts);
                                        tempList.get(0).saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                count[1]++;
                                                c2.setText(String.valueOf(count[1]));
                                                Toast.makeText(getApplicationContext(),
                                                        "Restaurant voted.",
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
                        } else if (parseObject != null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                            query.whereEqualTo("eventId", eventId);
                            query.whereEqualTo("restaurant", restaurantNames.get(1));
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (parseObject == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "You have already voted.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        parseObject.deleteInBackground();
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
                                                            count[1]--;
                                                            c2.setText(String.valueOf(count[1]));
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Restaurant unvoted.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            voteCounts.remove(1);
                                                            voteCounts.add(1, count[1]);
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
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("eventId", eventId);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (parseObject == null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                            query.whereEqualTo("objectId", eventId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        ParseObject vote = new ParseObject("Vote");
                                        vote.put("userId", ParseUser.getCurrentUser().getObjectId());
                                        vote.put("eventId", eventId);
                                        vote.put("restaurant", restaurantNames.get(2));
                                        vote.saveInBackground();
                                        tempList = list;
                                        tempList.get(0).removeAll("votes", voteCounts);
                                        tempList.get(0).saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                count[2]++;
                                                c3.setText(String.valueOf(count[2]));
                                                Toast.makeText(getApplicationContext(),
                                                        "Restaurant voted.",
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
                        } else if (parseObject != null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                            query.whereEqualTo("eventId", eventId);
                            query.whereEqualTo("restaurant", restaurantNames.get(2));
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (parseObject == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "You have already voted.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        parseObject.deleteInBackground();
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
                                                            count[2]--;
                                                            c3.setText(String.valueOf(count[2]));
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Restaurant unvoted.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            voteCounts.remove(2);
                                                            voteCounts.add(2, count[2]);
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
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("eventId", eventId);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (parseObject == null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                            query.whereEqualTo("objectId", eventId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        ParseObject vote = new ParseObject("Vote");
                                        vote.put("userId", ParseUser.getCurrentUser().getObjectId());
                                        vote.put("eventId", eventId);
                                        vote.put("restaurant", restaurantNames.get(3));
                                        vote.saveInBackground();
                                        tempList = list;
                                        tempList.get(0).removeAll("votes", voteCounts);
                                        tempList.get(0).saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                count[3]++;
                                                c4.setText(String.valueOf(count[3]));
                                                Toast.makeText(getApplicationContext(),
                                                        "Restaurant voted.",
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
                        } else if (parseObject != null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                            query.whereEqualTo("eventId", eventId);
                            query.whereEqualTo("restaurant", restaurantNames.get(3));
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (parseObject == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "You have already voted.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        parseObject.deleteInBackground();
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
                                                            count[3]--;
                                                            c4.setText(String.valueOf(count[3]));
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Restaurant unvoted.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            voteCounts.remove(3);
                                                            voteCounts.add(3, count[3]);
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
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("eventId", eventId);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (parseObject == null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                            query.whereEqualTo("objectId", eventId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        ParseObject vote = new ParseObject("Vote");
                                        vote.put("userId", ParseUser.getCurrentUser().getObjectId());
                                        vote.put("eventId", eventId);
                                        vote.put("restaurant", restaurantNames.get(4));
                                        vote.saveInBackground();
                                        tempList = list;
                                        tempList.get(0).removeAll("votes", voteCounts);
                                        tempList.get(0).saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                count[4]++;
                                                c5.setText(String.valueOf(count[4]));
                                                Toast.makeText(getApplicationContext(),
                                                        "Restaurant voted.",
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
                        } else if (parseObject != null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                            query.whereEqualTo("eventId", eventId);
                            query.whereEqualTo("restaurant", restaurantNames.get(4));
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (parseObject == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "You have already voted.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        parseObject.deleteInBackground();
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
                                                            count[4]--;
                                                            c5.setText(String.valueOf(count[4]));
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Restaurant unvoted.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            voteCounts.remove(4);
                                                            voteCounts.add(4, count[4]);
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
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("eventId", eventId);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (parseObject == null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                            query.whereEqualTo("objectId", eventId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        ParseObject vote = new ParseObject("Vote");
                                        vote.put("userId", ParseUser.getCurrentUser().getObjectId());
                                        vote.put("eventId", eventId);
                                        vote.put("restaurant", restaurantNames.get(5));
                                        vote.saveInBackground();
                                        tempList = list;
                                        tempList.get(0).removeAll("votes", voteCounts);
                                        tempList.get(0).saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                count[5]++;
                                                c6.setText(String.valueOf(count[5]));
                                                Toast.makeText(getApplicationContext(),
                                                        "Restaurant voted.",
                                                        Toast.LENGTH_SHORT).show();
                                                voteCounts.remove(5);
                                                voteCounts.add(5, count[5]);
                                                tempList.get(0).addAll("votes", voteCounts);
                                                tempList.get(0).saveInBackground();
                                            }
                                        });
                                    }
                                }
                            });
                        } else if (parseObject != null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                            query.whereEqualTo("eventId", eventId);
                            query.whereEqualTo("restaurant", restaurantNames.get(5));
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (parseObject == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "You have already voted.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        parseObject.deleteInBackground();
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
                                                            count[5]--;
                                                            c6.setText(String.valueOf(count[5]));
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Restaurant unvoted.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            voteCounts.remove(5);
                                                            voteCounts.add(5, count[5]);
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
        });

        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("eventId", eventId);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (parseObject == null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                            query.whereEqualTo("objectId", eventId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        ParseObject vote = new ParseObject("Vote");
                                        vote.put("userId", ParseUser.getCurrentUser().getObjectId());
                                        vote.put("eventId", eventId);
                                        vote.put("restaurant", restaurantNames.get(6));
                                        vote.saveInBackground();
                                        tempList = list;
                                        tempList.get(0).removeAll("votes", voteCounts);
                                        tempList.get(0).saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                count[6]++;
                                                c7.setText(String.valueOf(count[6]));
                                                Toast.makeText(getApplicationContext(),
                                                        "Restaurant voted.",
                                                        Toast.LENGTH_SHORT).show();
                                                voteCounts.remove(6);
                                                voteCounts.add(6, count[6]);
                                                tempList.get(0).addAll("votes", voteCounts);
                                                tempList.get(0).saveInBackground();
                                            }
                                        });
                                    }
                                }
                            });
                        } else if (parseObject != null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                            query.whereEqualTo("eventId", eventId);
                            query.whereEqualTo("restaurant", restaurantNames.get(6));
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (parseObject == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "You have already voted.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        parseObject.deleteInBackground();
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
                                                            count[6]--;
                                                            c7.setText(String.valueOf(count[6]));
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Restaurant unvoted.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            voteCounts.remove(6);
                                                            voteCounts.add(6, count[6]);
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
        });

        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("eventId", eventId);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (parseObject == null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                            query.whereEqualTo("objectId", eventId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        ParseObject vote = new ParseObject("Vote");
                                        vote.put("userId", ParseUser.getCurrentUser().getObjectId());
                                        vote.put("eventId", eventId);
                                        vote.put("restaurant", restaurantNames.get(7));
                                        vote.saveInBackground();
                                        tempList = list;
                                        tempList.get(0).removeAll("votes", voteCounts);
                                        tempList.get(0).saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                count[7]++;
                                                c8.setText(String.valueOf(count[7]));
                                                Toast.makeText(getApplicationContext(),
                                                        "Restaurant voted.",
                                                        Toast.LENGTH_SHORT).show();
                                                voteCounts.remove(7);
                                                voteCounts.add(7, count[7]);
                                                tempList.get(0).addAll("votes", voteCounts);
                                                tempList.get(0).saveInBackground();
                                            }
                                        });
                                    }
                                }
                            });
                        } else if (parseObject != null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                            query.whereEqualTo("eventId", eventId);
                            query.whereEqualTo("restaurant", restaurantNames.get(7));
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (parseObject == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "You have already voted.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        parseObject.deleteInBackground();
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
                                                            count[7]--;
                                                            c8.setText(String.valueOf(count[7]));
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Restaurant unvoted.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            voteCounts.remove(7);
                                                            voteCounts.add(7, count[7]);
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
        });

        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("eventId", eventId);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (parseObject == null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                            query.whereEqualTo("objectId", eventId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        ParseObject vote = new ParseObject("Vote");
                                        vote.put("userId", ParseUser.getCurrentUser().getObjectId());
                                        vote.put("eventId", eventId);
                                        vote.put("restaurant", restaurantNames.get(8));
                                        vote.saveInBackground();
                                        tempList = list;
                                        tempList.get(0).removeAll("votes", voteCounts);
                                        tempList.get(0).saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                count[8]++;
                                                c9.setText(String.valueOf(count[8]));
                                                Toast.makeText(getApplicationContext(),
                                                        "Restaurant voted.",
                                                        Toast.LENGTH_SHORT).show();
                                                voteCounts.remove(8);
                                                voteCounts.add(8, count[8]);
                                                tempList.get(0).addAll("votes", voteCounts);
                                                tempList.get(0).saveInBackground();
                                            }
                                        });
                                    }
                                }
                            });
                        } else if (parseObject != null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                            query.whereEqualTo("eventId", eventId);
                            query.whereEqualTo("restaurant", restaurantNames.get(8));
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (parseObject == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "You have already voted.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        parseObject.deleteInBackground();
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
                                                            count[8]--;
                                                            c9.setText(String.valueOf(count[8]));
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Restaurant unvoted.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            voteCounts.remove(8);
                                                            voteCounts.add(8, count[8]);
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
        });

        b10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                query.whereEqualTo("eventId", eventId);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (parseObject == null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                            query.whereEqualTo("objectId", eventId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        ParseObject vote = new ParseObject("Vote");
                                        vote.put("userId", ParseUser.getCurrentUser().getObjectId());
                                        vote.put("eventId", eventId);
                                        vote.put("restaurant", restaurantNames.get(9));
                                        vote.saveInBackground();
                                        tempList = list;
                                        tempList.get(0).removeAll("votes", voteCounts);
                                        tempList.get(0).saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                count[9]++;
                                                c10.setText(String.valueOf(count[9]));
                                                Toast.makeText(getApplicationContext(),
                                                        "Restaurant voted.",
                                                        Toast.LENGTH_SHORT).show();
                                                voteCounts.remove(9);
                                                voteCounts.add(9, count[9]);
                                                tempList.get(0).addAll("votes", voteCounts);
                                                tempList.get(0).saveInBackground();
                                            }
                                        });
                                    }
                                }
                            });
                        } else if (parseObject != null) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Vote");
                            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                            query.whereEqualTo("eventId", eventId);
                            query.whereEqualTo("restaurant", restaurantNames.get(9));
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (parseObject == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "You have already voted.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        parseObject.deleteInBackground();
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
                                                            count[9]--;
                                                            c10.setText(String.valueOf(count[9]));
                                                            Toast.makeText(getApplicationContext(),
                                                                    "Restaurant unvoted.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            voteCounts.remove(9);
                                                            voteCounts.add(9, count[9]);
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
        });

    }
}