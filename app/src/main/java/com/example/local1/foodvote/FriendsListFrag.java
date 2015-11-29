package com.example.local1.foodvote;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class FriendsListFrag extends Fragment {
    List<String> currentFriends = new ArrayList<String>();
    List<String> userFriendIDs = new ArrayList<String>();
    List<String> userFriends = new ArrayList<String>();
    String[] noFriends = {"You currently have no friends."};
    String friendUsername;

    // Widgets
    EditText enteredInUsername;
    FloatingActionButton addFriendButton;
    ListView friendsList;
    Toolbar toolbar;

    // User
    ParseUser currentUser;

    boolean SetUp = false;

    // Log TAG
    private static final String TAG = "FriendsListActivity ";

    public FriendsListFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    public void onStart(){
        super.onStart();

        if(SetUp == false) {
            // Locate widget views in friends_list.xml
            enteredInUsername = (EditText) getActivity().findViewById(R.id.addFriend);
            addFriendButton = (FloatingActionButton) getActivity().findViewById(R.id.addButton);
            friendsList = (ListView) getActivity().findViewById(R.id.friendsList);

            addFriend();
            listUserFriends();
            SetUp = true;
        }
    }


    private void addFriend() {
        Log.e(TAG, "In addFriend().");

        // On FAB click, add specified user to current user's friend list.
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get friend's name from current user input.
                friendUsername = enteredInUsername.getText().toString();

                // If current user entered in a username, search for user in database.
                if (!(friendUsername.equals(""))) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", friendUsername);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser object, ParseException e) {
                            if (object != null) {
                                // Check for current user for friends already added.
                                // If no friends added, add friend.
                                if (currentUser.getCurrentUser().getList("friendsList") != null) {
                                    currentFriends = currentUser.getCurrentUser().getList("friendsList");

                                    // Check if friend is already added, add if not added already.
                                    if (!(currentFriends.contains(object.getObjectId()))) {
                                        currentUser.getCurrentUser().add("friendsList", object.getObjectId());
                                        currentUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Toast.makeText(getActivity().getApplicationContext(), "User added.",
                                                        Toast.LENGTH_LONG).show();

                                                Intent intent = getActivity().getIntent();
                                                getActivity().overridePendingTransition(0, 0);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                getActivity().finish();
                                                getActivity().overridePendingTransition(0, 0);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity().getApplicationContext(), "User is already added.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    currentUser.getCurrentUser().add("friendsList", object.getObjectId());
                                    currentUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Toast.makeText(getActivity().getApplicationContext(), "User added.",
                                                    Toast.LENGTH_LONG).show();

                                            Intent intent = getActivity().getIntent();
                                            getActivity().overridePendingTransition(0, 0);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            getActivity().finish();
                                            getActivity().overridePendingTransition(0, 0);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "User does not exist.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter username.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void listUserFriends() {
        Log.e(TAG, "In listUserFriends().");

        // List current user's friends in a list.
        try {
            userFriendIDs = currentUser.getCurrentUser().getList("friendsList");

            if (userFriendIDs != null) {
                // Convert user IDs to usernames.
                for (int i = 0; i < userFriendIDs.size(); i++) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    userFriends.add(query.get(userFriendIDs.get(i)).getString("username"));
                }

                // Display friends list.
                ArrayAdapter adapter = new ArrayAdapter<String> (getActivity(), R.layout.listview_friends, userFriends);
                friendsList.setAdapter(adapter);
            } else {
                // Display msg that current user has no friends.
                ArrayAdapter adapter = new ArrayAdapter<String> (getActivity(), R.layout.listview_friends, noFriends);
                friendsList.setAdapter(adapter);
            }
        } catch(NullPointerException e) {
            // Display msg that current user has no friends.
            ArrayAdapter adapter = new ArrayAdapter<String> (getActivity(), R.layout.listview_friends, noFriends);
            friendsList = (ListView) getActivity().findViewById(R.id.friendsList);
            friendsList.setAdapter(adapter);
        } catch (ParseException e) {
            Log.e(TAG, "Unable to convert user ID to username.");
        }
    }
}
