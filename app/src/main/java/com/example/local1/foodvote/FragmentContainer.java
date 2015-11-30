package com.example.local1.foodvote;

import android.content.Intent;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Class holds two fragments, EventsListFrag and FriendsListFrag.
 */
public class FragmentContainer extends AppCompatActivity {

    // Widgets
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    // Log TAG
    private static final String TAG = "FragmentContainer ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeView();
    }

    @Override
    protected void onStart(){
        super.onStart();

        addUserRequests();
        // TODO too fast, so when owner deletes events before participants can save, issues.
        deleteUserRequests();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        ParseUser.getCurrentUser().logOut();

        Intent intent = new Intent(FragmentContainer.this, LoginSignUpActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Create menu from menu.xml.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        menu.getItem(0).setTitle("Logged in: " + ParseUser.getCurrentUser().getUsername());

        return true;
    }

    /**
     * On log out menu item press, log current user out.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            ParseUser.getCurrentUser().logOut();
            Intent intent = new Intent(FragmentContainer.this, LoginSignUpActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * TODO Comment on what this method does.
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new EventsListFrag(), "Events");
        adapter.addFragment(new FriendsListFrag(), "Friends");
        viewPager.setAdapter(adapter);
    }

    /**
     * TODO Comment on what this class does.
     */
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

    /**
     * Initialize activity view and find widgets in activity_fragment_container.xml.
     */
    private void initializeView() {
        // Initialize view.
        setContentView(R.layout.activity_fragment_container);

        // Find widgets.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Add event requests to current user's event list by searching Parse for pending event
     * requests. For every add request, add the event to the current user's event list and change
     * the status of the request from pending to accepted.
     */
    private void addUserRequests() {
        // Query Parse for pending add requests.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("EventRequest");
        query.whereEqualTo("requestTo", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("action", "add");
        query.whereEqualTo("status", "pending");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    // For each request found, add event to user and change status of request.
                    for (int i = 0; i < list.size(); i++) {
                        List<String> tempList = new ArrayList<String>();
                        tempList.add(list.get(i).getString("eventId"));
                        ParseUser.getCurrentUser().addAll("eventsList", tempList);

                        list.get(i).remove("status");
                        list.get(i).put("status", "accepted");
                        list.get(i).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
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
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Delete event requests to current user's event list by searching Parse for pending event
     * requests. For every delete request, delete the event to the current user's event list and
     * change the status of the request from pending to deleted.
     */
    private void deleteUserRequests() {
        // Query Parse for delete request.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("EventRequest");
        query.whereEqualTo("requestTo", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("action", "delete");
        query.whereEqualTo("status", "pending");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    // For each delete request, delete event from user and change status to deleted.
                    for (int i = 0; i < list.size(); i++) {
                        List<String> tempList = new ArrayList<String>();
                        tempList.add(list.get(i).getString("eventId"));
                        ParseUser.getCurrentUser().removeAll("eventsList", tempList);

                        list.get(i).remove("status");
                        list.get(i).put("status", "deleted");
                        list.get(i).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
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
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}