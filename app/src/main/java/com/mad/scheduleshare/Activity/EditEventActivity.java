package com.mad.scheduleshare.Activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.scheduleshare.Model.Event;
import com.mad.scheduleshare.Model.User;
import com.mad.scheduleshare.R;

import java.util.Calendar;

/**
 * Edit or Delete event from schedule.
 */
public class EditEventActivity extends AppCompatActivity {

    private static final String TAG = "EditEventActivity";
    private static final int REQUEST_CODE = 100;

    private DatabaseReference mEventsDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String mUserId, mEventId, mActivity;

    private EditText mEditName, mEditLocation;
    private TextView mEditStartTime, mEditEndTime, mInviteFriends;
    private Spinner mEditDayOfWeek;

    private String mOldEventName, mOldEventStartTime, mOldEventEndTime, mOldEventDay, mOldEventLocation, mOldFriendList;
    private String mNewEventName, mNewEventStartTime, mNewEventEndTime, mNewEventDay, mNewEventLocation, mNewFriendList;

    private Intent mFriendIntent;
    private Calendar mCalendar;
    private TimePickerDialog mTimePickerDialog;

    private int mCurrentHour, mCurrentMinute;
    private String mTimeUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        getSupportActionBar().setTitle("Edit Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mActivity = "EditEventActivity";

        // Get current user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserId = mUser.getUid();

        // Establish Firebase:
        mEventsDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mUserId).child("mSchedule");

        // Replace 'Invite Friends' text with Array String List of people's names
        mInviteFriends = (TextView) findViewById(R.id.event_invite_friends);


        mInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editFriends = new Intent(EditEventActivity.this, InviteFriendActivity.class);
                editFriends.putExtra("PREVIOUS_ACTIVITY", mActivity);
                startActivityForResult(editFriends, REQUEST_CODE);
            }
        }); // If no one is invited, set text to 'Invite Friends'

        getEventData();
    }

    /**
     * Retrieve Event data of selected event.
     */
    private void getEventData() {

        // Retreive Intents
        mEventId = getIntent().getStringExtra("EVENT_ID");

        mOldEventName = getIntent().getStringExtra("EVENT_NAME");
        mOldEventStartTime = getIntent().getStringExtra("EVENT_START_TIME");
        mOldEventEndTime = getIntent().getStringExtra("EVENT_END_TIME");
        mOldEventDay = getIntent().getStringExtra("EVENT_DAY");
        mOldEventLocation = getIntent().getStringExtra("EVENT_LOCATION");

        // Debug: old event data
        Log.d(TAG, "getEventData: name: " + getIntent().getStringExtra("EVENT_NAME"));
        Log.d(TAG, "getEventData: startTime: " + getIntent().getStringExtra("EVENT_START_TIME"));
        Log.d(TAG, "getEventData: endTime: " + getIntent().getStringExtra("EVENT_END_TIME"));
        Log.d(TAG, "getEventData: day: " + getIntent().getStringExtra("EVENT_DAY"));
        Log.d(TAG, "getEventData: location: " + getIntent().getStringExtra("EVENT_LOCATION"));

        // Retrieve User's friendlist
        DatabaseReference friends = mEventsDatabase.child(mOldEventDay).child(mEventId).child("mFriends");
        friends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String friendList = dataSnapshot.getValue(String.class);

                // Reset Invite Friends Text if List is empty
                if (!mInviteFriends.getText().equals("")) {
                    mInviteFriends.setText("Invite Friends");
                } else {
                    mInviteFriends.setText(friendList);
                    Log.d(TAG, "getEventData: friends: " + friendList);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Set Old Event Data
        mEditName = (EditText) findViewById(R.id.new_event_name);
        mEditName.setText(mOldEventName);

        mEditStartTime = (TextView) findViewById(R.id.event_start_time);
        mEditStartTime.setText(mOldEventStartTime);
        mEditStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTimeWidget(mEditStartTime);
                mTimePickerDialog.show();
            }
        });

        mEditEndTime = (TextView) findViewById(R.id.event_end_time);
        mEditEndTime.setText(mOldEventEndTime);
        mEditEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTimeWidget(mEditEndTime);
                mTimePickerDialog.show();
            }
        });

        mEditDayOfWeek = (Spinner) findViewById(R.id.new_event_day);
        if (mOldEventDay.equals("Monday")) {
            mEditDayOfWeek.setSelection(0);
        } else if (mOldEventDay.equals("Tuesday")) {
            mEditDayOfWeek.setSelection(1);
        } else if (mOldEventDay.equals("Wednesday")) {
            mEditDayOfWeek.setSelection(2);
        } else if (mOldEventDay.equals("Thursday")) {
            mEditDayOfWeek.setSelection(3);
        } else if (mOldEventDay.equals("Friday")) {
            mEditDayOfWeek.setSelection(4);
        } else if (mOldEventDay.equals("Saturday")) {
            mEditDayOfWeek.setSelection(5);
        } else {
            mEditDayOfWeek.setSelection(6);
        }

        mEditLocation = (EditText) findViewById(R.id.event_location);
        mEditLocation.setText(mOldEventLocation);

    }

    /**
     * Load TimePickerDialog Widget for time input.
     */
    private void loadTimeWidget(final TextView time) {
        mCalendar = Calendar.getInstance();
        mCurrentHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mCurrentMinute = mCalendar.get(Calendar.MINUTE);

        mTimePickerDialog = new TimePickerDialog(EditEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
     * Edit details of selected event.
     */
    private void editEvent() {

        if (!mOldEventName.equals("") && !mOldEventStartTime.equals("")
                && !mOldEventLocation.equals("")) {

            // Debug: new event data
            Log.d(TAG, "editEvent: name: " + mEditName.getText().toString());
            Log.d(TAG, "editEvent: startTime: " + mEditStartTime.getText().toString());
            Log.d(TAG, "editEvent: endTime: " + mEditEndTime.getText().toString());
            Log.d(TAG, "editEvent: day: " + mEditDayOfWeek.getSelectedItem().toString());
            Log.d(TAG, "editEvent: location: " + mEditLocation.getText().toString());
            Log.d(TAG, "editEvent: friends: " + mInviteFriends.getText().toString());

            // Retrieve Updated Event details:
            mNewEventName = mEditName.getText().toString();
            mNewEventStartTime = mEditStartTime.getText().toString();
            mNewEventEndTime = mEditEndTime.getText().toString();
            mNewEventDay = mEditDayOfWeek.getSelectedItem().toString();
            mNewEventLocation = mEditLocation.getText().toString();
            mNewFriendList = mInviteFriends.getText().toString();

            // Set Updated event
            Event event = new Event();
            event.setName(mNewEventName);
            event.setStartTime(mNewEventStartTime);
            event.setEndTime(mNewEventEndTime);
            event.setDayOfWeek(mNewEventDay);
            event.setLocation(mNewEventLocation);
            event.setFriendList(mNewFriendList);

            // Update Event values:
            DatabaseReference name = mEventsDatabase.child(mOldEventDay).child(mEventId).child("mName");
            name.setValue(mNewEventName);

            DatabaseReference startTime = mEventsDatabase.child(mOldEventDay).child(mEventId).child("mStartTime");
            startTime.setValue(mNewEventStartTime);

            DatabaseReference endTime = mEventsDatabase.child(mOldEventDay).child(mEventId).child("mEndTime");
            endTime.setValue(mNewEventEndTime);

            DatabaseReference day = mEventsDatabase.child(mOldEventDay).child(mEventId).child("mDay");
            day.setValue(mNewEventDay);

            DatabaseReference location = mEventsDatabase.child(mOldEventDay).child(mEventId).child("mLocation");
            location.setValue(mNewEventLocation);

            DatabaseReference friends = mEventsDatabase.child(mOldEventDay).child(mEventId).child("mFriends");
            friends.setValue(mNewFriendList);

            finish();
            toastMessage("Event updated!");

        } else {
            toastMessage("You must complete all fields!");
        }
    }

    /**
     * Delete event from schedule.
     */
    private void deleteEvent() {

        // Alert to Confirm Delete Event
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this event?");
        builder.setCancelable(true);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int whichButton) {
                dialogInterface.cancel();

                // Remove event from database
                DatabaseReference event = mEventsDatabase.child(mOldEventDay).child(mEventId);
                event.removeValue();
                finish();
                toastMessage("Event deleted!");
                Log.d(TAG, "deleteEvent:event_deleted: " + mEventId);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Log.d(TAG, "deleteEvent:delete_cancelled");
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

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
     * Create Menu for Edit/Delete Event.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_delete, menu);
        return true;
    }

    /**
     * Menu Manager for Edit Event.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Cancel Activity (Back to previous Screen)
            case android.R.id.home:
                finish();
                Log.d(TAG, "onOptionsItemSelected:edit_canceled");
                return true;

            // Confirm (Edit Event)
            case R.id.action_confirm:
                editEvent();
                Log.d(TAG, "onOptionsItemSelected:event_edited");
                return true;

            // Delete (Delete event)
            case R.id.action_delete:
                deleteEvent();
                Log.d(TAG, "onOptionsItemSelected:event_deleted");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
