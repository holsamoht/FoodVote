package com.example.local1.foodvote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ExtraYelpInformationActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView restaurantName;
    TextView rating;
    TextView type;
    TextView address;
    TextView phone;

    int position;

    boolean setUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(setUp == false) {
            displayInfo();
            setUp = true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(ExtraYelpInformationActivity.this, EventVoteActivity.class);
        intent.putExtra("eventId", getIntent().getExtras().getString("eventId"));
        startActivity(intent);
        finish();
    }

    /* set Logged In menu option */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        menu.getItem(0).setTitle("Logged in: " + ParseUser.getCurrentUser().getUsername());

        return true;
    }

    /* when user clicks Log Out in menu options, log the user out and go to LoginSignUpActivity */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            ParseUser.getCurrentUser().logOut();
            Intent intent = new Intent(ExtraYelpInformationActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeView(){
        setContentView(R.layout.extra_yelp_information);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        restaurantName = (TextView)findViewById(R.id.restaurantName);
        rating = (TextView)findViewById(R.id.rating);
        type = (TextView)findViewById(R.id.type);
        address = (TextView)findViewById(R.id.address);
        phone = (TextView)findViewById(R.id.phone);
    }

    private void displayInfo(){
        String eventId = getIntent().getExtras().getString("eventId");
        position = getIntent().getExtras().getInt("position");

        ParseQuery<ParseObject>query = ParseQuery.getQuery("Event");
        query.whereEqualTo("objectId", eventId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null){
                    ParseObject obj = list.get(0);
                    restaurantName.setText((String)obj.getList("restaurants").get(position));
                    rating.setText((String)obj.getList("ratings").get(position));
                    type.setText((String)obj.getList("typeOfFood").get(position));
                    address.setText((String) obj.getList("addresses").get(position));
                    phone.setText((String)obj.getList("phoneNumbers").get(position));
                }
            }
        });
    }
}
