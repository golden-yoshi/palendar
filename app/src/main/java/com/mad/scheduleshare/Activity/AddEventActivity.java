package com.mad.scheduleshare.Activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.scheduleshare.Model.Event;
import com.mad.scheduleshare.R;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Add new event to schedule.
 */
public class AddEventActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;
    private static final String TAG = "AddEventActivity";

    private DatabaseReference mEventsDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId, mActivity;

    private EditText mName, mLocation;
    private Spinner mDayOfWeek;
    private TextView mStartTime, mEndTime, mInviteFriends;

    private Intent mFriendIntent;
    private Calendar mCalendar;
    private TimePickerDialog mTimePickerDialog;

    private int mCurrentHour, mCurrentMinute;
    private String mTimeUnit, mDayIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        getSupportActionBar().setTitle("Create Event");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = "AddEventActivity";

        // Get current user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserId = mUser.getUid();

        // Views
        mName = (EditText) findViewById(R.id.new_event_name);
        mStartTime = (TextView) findViewById(R.id.event_start_time);
        mStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTimeWidget(mStartTime);
                mTimePickerDialog.show();
            }
        });

        mEndTime = (TextView) findViewById(R.id.event_end_time);
        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTimeWidget(mEndTime);
                mTimePickerDialog.show();
            }
        });

        // Set initial day based on previous screen
        mDayOfWeek = (Spinner) findViewById(R.id.new_event_day);
        mDayIntent = getIntent().getStringExtra("EVENT_DAY");
        if (mDayIntent.equals("Monday")) {
            mDayOfWeek.setSelection(0);
        } else if (mDayIntent.equals("Tuesday")) {
            mDayOfWeek.setSelection(1);
        } else if (mDayIntent.equals("Wednesday")) {
            mDayOfWeek.setSelection(2);
        } else if (mDayIntent.equals("Thursday")) {
            mDayOfWeek.setSelection(3);
        } else if (mDayIntent.equals("Friday")) {
            mDayOfWeek.setSelection(4);
        } else if (mDayIntent.equals("Saturday")) {
            mDayOfWeek.setSelection(5);
        } else {
            mDayOfWeek.setSelection(6);
        }

        mLocation = (EditText) findViewById(R.id.event_location);

        // Replace 'Invite Friends' text with Array String List of people's names
        mInviteFriends = (TextView) findViewById(R.id.event_invite_friends);
        mInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addFriends = new Intent(AddEventActivity.this, InviteFriendActivity.class);
                addFriends.putExtra("PREVIOUS_ACTIVITY", mActivity);
                startActivityForResult(addFriends, REQUEST_CODE);
            }
        });

        // Grab friendList intent
        mFriendIntent = getIntent();
        final String friendList = mFriendIntent.getStringExtra("INVITE_LIST");

        // Set text to friend invite list. If no one is invited, set text to 'Invite Friends'.
        if (!mInviteFriends.getText().equals("")) {
            mInviteFriends.setText("Invite Friends");
        } else {
            mInviteFriends.setText(friendList);
        }

    }

    /**
     * Load TimePickerDialog Widget for time input.
     */
    private void loadTimeWidget(final TextView time) {
        mCalendar = Calendar.getInstance();
        mCurrentHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mCurrentMinute = mCalendar.get(Calendar.MINUTE);

        mTimePickerDialog = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                if (hourOfDay > 12) {
                    hourOfDay -= 12;
                    mTimeUnit = "PM";
                } else if (hourOfDay == 0) {
                    hourOfDay += 12;
                    mTimeUnit = "AM";
                } else if (hourOfDay == 12) {
                    mTimeUnit = "PM";
                } else {
                    mTimeUnit = "AM";
                }
                time.setText(String.format("%02d:%02d", hourOfDay, minutes) + mTimeUnit);
            }
        }, mCurrentHour, mCurrentMinute, false);

    }


    /**
     * Add event data to database.
     */
    public void addEventData(String name, String startTime, String endTime, String day, String location, String friends) {

        Event event = new Event();

        if (!name.equals("") && !startTime.equals("") && !endTime.equals("") && !location.equals("")) {

            // Debug: new event data
            Log.d(TAG, "addEventData: name: " + name);
            Log.d(TAG, "addEventData: startTime: " + startTime);
            Log.d(TAG, "addEventData: endTime: " + endTime);
            Log.d(TAG, "addEventData: day: " + day);
            Log.d(TAG, "addEventData: location: " + location);
            Log.d(TAG, "addEventData: friends: " + friends);

            // Set to model instance
            event.setName(name);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setDayOfWeek(day);
            event.setLocation(location);
            event.setFriendList(friends);

            // Add Name and Email values to Firebase User
            HashMap<String, String> eventMap = new HashMap<>();
            eventMap.put("mName", name);
            eventMap.put("mStartTime", startTime);
            eventMap.put("mEndTime", endTime);
            eventMap.put("mLocation", location);
            eventMap.put("mDay", day);
            eventMap.put("mFriends", friends);

            // Generate Event ID
            String eventId = FirebaseDatabase.getInstance().getReference().child("User").child(mUserId).child("mSchedule").child(day).push().getKey();

            // Add Event Hashmap to database
            mEventsDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mUserId).child("mSchedule").child(day).child(eventId);
            mEventsDatabase.setValue(eventMap);

            toastMessage("Event Added!");

            finish();

        } else {
            toastMessage("Please fill in missing field(s)!");
        }

    }

    /**
     * Create text as toast message.
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns feedback for Invited Friends List.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == InviteFriendActivity.RESULT_OK) {
                String message = data.getStringExtra("INVITE_LIST");
                mInviteFriends.setText(message);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Create Menu for Add Event.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    /**
     * Menu Manager for Add Event.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Cancel (Back to Previous Scren)
            case android.R.id.home:
                finish();
                Log.d(TAG, "onOptionsItemSelected:add_event_cancelled");
                return true;

            // Confirm (Add New Event)
            case R.id.action_confirm:
                addEventData(mName.getText().toString(), mStartTime.getText().toString(),
                        mEndTime.getText().toString(), mDayOfWeek.getSelectedItem().toString(),
                        mLocation.getText().toString(), mInviteFriends.getText().toString());
                Log.d(TAG, "onOptionsItemSelected:event_added");
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
