package com.mad.scheduleshare.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.mad.scheduleshare.R;

/**
 * Show profile of specific user.
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mFriendId, mFriendName, mFriendEmail;

    private TextView mName;

    private ListView mListView;
    private final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get current user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Intent friendIntent = getIntent();
        mFriendName = friendIntent.getStringExtra("USER_NAME");
        mFriendEmail = friendIntent.getStringExtra("USER_EMAIL");
        mFriendId = friendIntent.getStringExtra("USER_ID");

        getSupportActionBar().setTitle(mFriendName + "'s Schedule");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mName = (TextView) findViewById(R.id.friend_profile_name);
        mName.setText("Hi! My name " + mFriendName + ".");

        mListView = (ListView) findViewById(R.id.friend_week_list_view);

        Log.d(TAG, "onCreate:user_profile: " + mFriendName);

        showUsersSchedule();
    }

    /**
     * Show weekly schedule for user.
     */
    private void showUsersSchedule() {
        // Define Adapter & Filter database for specific user id and events
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, DAYS);

        // Assign adapter to ListView
        mListView.setAdapter(mAdapter);

        // ListView Item Click Listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) mListView.getItemAtPosition(position);

                // View events for that day
                Intent intent = new Intent(ProfileActivity.this, FriendEventsActivity.class);
                intent.putExtra("DAY_POSITION", itemPosition);
                intent.putExtra("DAY_NAME", itemValue);
                intent.putExtra("FRIEND_ID", mFriendId);
                intent.putExtra("FRIEND_NAME", mFriendName);
                startActivity(intent);

            }

        });
    }

    /**
     * Menu Manager for Friend Profile.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Back to Previous Screen (Friends List)
            case android.R.id.home:
                finish();
                Log.d(TAG, "onOptionsItemSelected:friends_screen");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
