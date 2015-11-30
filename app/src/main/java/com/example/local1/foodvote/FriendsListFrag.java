package com.example.local1.foodvote;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
    // Variables
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

    /**
     * TODO Comment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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

    /**
     *  Add friend when button is clicked.
     */
    private void addFriend() {
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendUsername = enteredInUsername.getText().toString();

                if (friendUsername.equals("") ||
                        friendUsername.equals(ParseUser.getCurrentUser().getUsername())) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please enter a different username.",
                            Toast.LENGTH_LONG).show();
                } else {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", friendUsername);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser parseObject, ParseException e) {
                            if (parseObject == null) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "User does not exist.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                if (ParseUser.getCurrentUser().getList("friendsList") == null) {
                                    ParseUser.getCurrentUser().add("friendsList",
                                            parseObject.getObjectId());
                                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Toast.makeText(getActivity().getApplicationContext(),
                                                    "User added.",
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
                                    currentFriends =
                                            ParseUser.getCurrentUser().getList("friendsList");

                                    if (currentFriends.contains(parseObject.getObjectId())) {
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                "User is already added.",
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        ParseUser.getCurrentUser().add("friendsList",
                                                parseObject.getObjectId());
                                        ParseUser.getCurrentUser().saveInBackground(
                                                new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Toast.makeText(getActivity().getApplicationContext(),
                                                        "User added.",
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
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void listUserFriends() {
        try {
            userFriendIDs = ParseUser.getCurrentUser().getList("friendsList");

            if (userFriendIDs == null) {
                ArrayAdapter adapter = new ArrayAdapter<String> (getActivity(),
                        R.layout.listview_friends, noFriends);
                friendsList.setAdapter(adapter);
            } else {
                for (int i = 0; i < userFriendIDs.size(); i++) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    userFriends.add(query.get(userFriendIDs.get(i)).getString("username"));
                }

                ArrayAdapter adapter = new ArrayAdapter<String> (getActivity(),
                        R.layout.listview_friends, userFriends);
                friendsList.setAdapter(adapter);
            }
        } catch(NullPointerException e) {
            ArrayAdapter adapter = new ArrayAdapter<String> (getActivity(),
                    R.layout.listview_friends, noFriends);
            friendsList.setAdapter(adapter);
        } catch (ParseException e) {
            Log.e(TAG, "Unable to convert user ID to username.");
        }
    }
}
