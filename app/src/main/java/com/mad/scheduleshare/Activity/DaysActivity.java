package com.mad.scheduleshare.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mad.scheduleshare.Controller.DayArrayAdapter;
import com.mad.scheduleshare.R;

import java.util.ArrayList;

/**
 * Show Main Schedule screen for logged in User.
 */
public class DaysActivity extends AppCompatActivity {

    private static final String TAG = "DaysActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ListView mListView;
    private ArrayList<String> DAYS = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);

        // Get current user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Intent accountIntent = getIntent();
        String name = accountIntent.getStringExtra("NAME");

        getSupportActionBar().setTitle(name + "'s Palendar");

        // Add Days of the Week
        DAYS.add("Monday");
        DAYS.add("Tuesday");
        DAYS.add("Wednesday");
        DAYS.add("Thursday");
        DAYS.add("Friday");
        DAYS.add("Saturday");
        DAYS.add("Sunday");

        mListView = (ListView) findViewById(R.id.week_list_view);
        showScheduleWeek();

    }

    /**
     * Show Schedule Week Days.
     */
    private void showScheduleWeek() {

        DayArrayAdapter mAdapter = new DayArrayAdapter(this, DAYS);

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
                Intent intent = new Intent(DaysActivity.this, EventsActivity.class);
                intent.putExtra("DAY_POSITION", itemPosition);
                intent.putExtra("DAY_NAME", itemValue);
                startActivity(intent);

            }

        });
    }

    /**
     * Create menu for application.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handle standard menu options.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // Reminder of current screen
        if (id == R.id.action_schedule) {
            toastMessage("You're already here!");
        }

        // Go to friends list
        if (id == R.id.action_friends_list) {
            Intent friendIntent = new Intent(DaysActivity.this, FriendsActivity.class);
            startActivity(friendIntent);
            Log.d(TAG, "onOptionsItemSelected:friends_screen");
        }

        // Logout of app
        if (id == R.id.action_logout) {
            finish();
            Intent logout = new Intent(DaysActivity.this, LoginActivity.class);
            startActivity(logout);
            toastMessage("You've been logged out!");
            Log.d(TAG, "onOptionsItemSelected:signed_out");
        }

        return super.onOptionsItemSelected(item);

    }

    /**
     * Create text as toast message.
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
