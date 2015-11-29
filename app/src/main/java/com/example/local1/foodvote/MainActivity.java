package com.example.local1.foodvote;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/*Container for events list frag and friends list frag*/
public class MainActivity extends AppCompatActivity {
    //Variables
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    // User
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Add all the requested events automatically in the background
        AddUserRequests();

        //Set up display
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new EventsListFrag(), "Events");
        adapter.addFragment(new FriendsListFrag(), "Friends");
        Log.println(Log.ERROR, "EventFS: ", "Added Frag");
        viewPager.setAdapter(adapter);


    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void onStart(){
        super.onStart();
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
            Intent intent = new Intent(MainActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*Adds events user has been invited to automatically*/
    private void AddUserRequests() {

        // Search Parse for current user's list of pending event requests.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("EventRequest");
        query.whereEqualTo("requestTo", currentUser.getCurrentUser().getObjectId());
        query.whereEqualTo("status", "pending");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (!list.isEmpty() && e == null) {
                    // Gather list of event IDs from current user's requested events.
                    for (int i = 0; i < list.size(); i++) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                        query.whereEqualTo("objectId", list.get(i).get("eventId"));

                        try {
                            ParseObject tempObject = query.getFirst();
                            // Add requested event to current user's list of events.
                            currentUser.getCurrentUser().add("eventsList", tempObject.getObjectId());

                            //Set the EventRequest field to accepted
                            ParseQuery<ParseObject> tempQuery = ParseQuery.getQuery("EventRequest");
                            tempQuery.whereEqualTo("eventId", tempObject.getObjectId());
                            tempQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    if (e == null) {
                                        parseObject.remove("status");
                                        parseObject.put("status", "accepted");
                                        parseObject.saveInBackground();
                                    }
                                }
                            });

                            //Save the update
                            currentUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Intent intent = getIntent();
                                        overridePendingTransition(0, 0);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(intent);
                                    }
                                }
                            });
                        } catch (ParseException ee) {

                        }
                    }

                }
            }
        });
    }
}