package com.example.local1.foodvote;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    // Variables
    int pos;
    List<String> currentFriends = new ArrayList<String>();
    List<String> userFriendIDs = new ArrayList<String>();
    List<String> userFriends = new ArrayList<String>();
    String[] noFriends = {"You currently have no friends."};
    String friendUsername;

    // Widgets
    EditText enteredInUsername;
    FloatingActionButton addFriendButton;
    ListView friendsList;

    boolean SetUp = false;

    // Log TAG
    private static final String TAG = "FriendsListActivity ";

    public FriendsListFrag() { }

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
            enteredInUsername = (EditText) getActivity().findViewById(R.id.addFriendText);
            addFriendButton = (FloatingActionButton) getActivity().findViewById(R.id.addFriendButton);
            friendsList = (ListView) getActivity().findViewById(R.id.friendsList);

            addFriend();
            deleteFriend();
            listUserFriends();
            SetUp = true;
        }
    }

    /**
     *  Add friend when button is clicked.
     *  Called from onStart()
     */
    private void addFriend() {
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendUsername = enteredInUsername.getText().toString();

                // if they tried to search for an empty username, prompt the user to enter a username 
                if (friendUsername.equals("") ||
                        friendUsername.equals(ParseUser.getCurrentUser().getUsername())) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please enter a different username.",
                            Toast.LENGTH_LONG).show();
                }
                // otherwise search for a user who matches the entered username
                else {
                    // usernames should all be unique, so we will only look for the first matching
                    // user since that should be the only matching user
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", friendUsername);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser parseObject, ParseException e) {
                            // show a toast if no matching user could be found
                            if (parseObject == null) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "User does not exist.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                // initialize a new friendsList if the current user doesn't have one
                                if (ParseUser.getCurrentUser().getList("friendsList") == null) {
                                    ParseUser.getCurrentUser().add("friendsList",
                                            parseObject.getObjectId());
                                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {

                                        @Override
                                        public void done(ParseException e) {
                                            // display a toast once the newly initialized friendlist is saved
                                            Toast.makeText(getActivity().getApplicationContext(),
                                                    "User added.",
                                                    Toast.LENGTH_LONG).show();

                                            // refresh the activity
                                            Intent intent = getActivity().getIntent();
                                            getActivity().overridePendingTransition(0, 0);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            getActivity().finish();
                                            getActivity().overridePendingTransition(0, 0);
                                            startActivity(intent);
                                        }
                                    });
                                } 
                                else {
                                    currentFriends =
                                            ParseUser.getCurrentUser().getList("friendsList");

                                    // if the matching user is already a friend, show a toast and do 
                                    // nothing else
                                    if (currentFriends.contains(parseObject.getObjectId())) {
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                "User is already added.",
                                                Toast.LENGTH_LONG).show();
                                    } 
                                    // otherwise append to the existing friendsList
                                    else {
                                        ParseUser.getCurrentUser().add("friendsList",
                                                parseObject.getObjectId());
                                        ParseUser.getCurrentUser().saveInBackground(
                                                new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        // display a toast once the new data is saved
                                                        Toast.makeText(getActivity().getApplicationContext(),
                                                                "User added.",
                                                                Toast.LENGTH_LONG).show();

                                                        // refresh the activity
                                                        Intent intent = getActivity().getIntent();
                                                        getActivity().overridePendingTransition(0, 0);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                        getActivity().finish();
                                                        getActivity().overridePendingTransition(0, 0);
                                                        startActivity(intent);
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void deleteFriend() {
        friendsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // protective check in case the user has no friends
                // this should never execute because if the user has no friends, there would have been
                // nothing to long click on
                if (userFriendIDs == null) {
                    Toast.makeText(getActivity().getApplicationContext(), "No friend to delete",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // remove the friend's id from the current user's friendsList and save
                    pos = position;

                    List<String> tempList = new ArrayList<String>();
                    tempList.add(userFriendIDs.get(pos));
                    ParseUser.getCurrentUser().removeAll("friendsList", tempList);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        // once save completes, refresh the activity
                        @Override
                        public void done(ParseException e) {
                            Intent intent = getActivity().getIntent();
                            getActivity().overridePendingTransition(0, 0);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            getActivity().finish();
                            getActivity().overridePendingTransition(0, 0);
                            startActivity(intent);
                        }
                    });
                }

                return true;
            }
        });
    }

    private void listUserFriends() {
        try {
            userFriendIDs = ParseUser.getCurrentUser().getList("friendsList");

            // display the noFriends message if the user has no friends
            if (userFriendIDs == null || userFriendIDs.size() == 0) {
                ArrayAdapter adapter = new ArrayAdapter<String> (getActivity(),
                        R.layout.listview_friends, noFriends);
                friendsList.setAdapter(adapter);
            } else {
                // get the list of names of the user's friends
                for (int i = 0; i < userFriendIDs.size(); i++) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    userFriends.add(query.get(userFriendIDs.get(i)).getString("username"));
                }

                // display the names of the user's friends
                ArrayAdapter adapter = new ArrayAdapter<String> (getActivity(),
                        R.layout.listview_friends, userFriends);
                friendsList.setAdapter(adapter);
            }
        } catch(NullPointerException e) {
            // NullPointerException catch simply displays noFriends message to user
            ArrayAdapter adapter = new ArrayAdapter<String> (getActivity(),
                    R.layout.listview_friends, noFriends);
            friendsList.setAdapter(adapter);
        } catch (ParseException e) {
            Log.e(TAG, "Unable to convert user ID to username.");
        }
    }
}
