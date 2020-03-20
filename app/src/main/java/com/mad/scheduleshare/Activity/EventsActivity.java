package com.mad.scheduleshare.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.scheduleshare.Model.Event;
import com.mad.scheduleshare.R;

/**
 * Show the user's events for a specific day.
 */
public class EventsActivity extends AppCompatActivity {

    private static final String TAG = "EventsActivity";

    private RecyclerView mEventsList;
    private DatabaseReference mEventsDatabase;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId, mDayName;

    private TextView mEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get current user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserId = mUser.getUid();

        // Get Day Intent:
        Intent nameIntent = getIntent();
        mDayName = nameIntent.getStringExtra("DAY_NAME");
        getSupportActionBar().setTitle("Events for " + mDayName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Establish Firebase:
        mEventsDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mUserId).child("mSchedule").child(mDayName);
        mLayoutManager = new LinearLayoutManager(this);

        mEventsList = (RecyclerView) findViewById(R.id.firebase_events_list);
        mEventsList.setHasFixedSize(true);
        mEventsList.setLayoutManager(mLayoutManager);

        mEmptyList = findViewById(R.id.empty_events_list);

        // Add New Event to Firebase:
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventsActivity.this, AddEventActivity.class);
                intent.putExtra("EVENT_DAY", mDayName);
                startActivity(intent);
            }
        });
    }

    /**
     * Setup FirebaseRecylcerAdapter to display User's event data.
     */
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Event, EventsActivity.EventsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, EventsActivity.EventsViewHolder>(

                Event.class,
                R.layout.event_item,
                EventsActivity.EventsViewHolder.class,
                mEventsDatabase

        ) {

            @Override
            protected void populateViewHolder(EventsViewHolder eventsViewHolder, Event event, int position) {

                eventsViewHolder.setDisplayName(event.getName());
                eventsViewHolder.setDispayTime(event.getStartTime() + " - " + event.getEndTime());
                eventsViewHolder.setDispayLocation(event.getLocation());

                // Debug: show event data
                Log.d(TAG, "populateViewHolder: name: " + event.getName());
                Log.d(TAG, "populateViewHolder: time: " + event.getStartTime() + " - " + event.getEndTime());
                Log.d(TAG, "populateViewHolder: location: " + event.getLocation());

                Intent dayIntent = getIntent();
                final String event_day = dayIntent.getStringExtra("DAY_NAME");

                final String event_id = getRef(position).getKey();
                final String event_name = event.getName();
                final String event_start_time = event.getStartTime();
                final String event_end_time = event.getEndTime();
                final String event_location = event.getLocation();

                // Handle empty event list
                if (getItemCount() != 0) {
                    mEmptyList.setVisibility(View.GONE);
                }

                // Go to Edit Event Screen upon click
                eventsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent editIntent = new Intent(EventsActivity.this, EditEventActivity.class);
                        editIntent.putExtra("EVENT_ID", event_id);
                        editIntent.putExtra("EVENT_NAME", event_name);
                        editIntent.putExtra("EVENT_START_TIME", event_start_time);
                        editIntent.putExtra("EVENT_END_TIME", event_end_time);
                        editIntent.putExtra("EVENT_DAY", event_day);
                        editIntent.putExtra("EVENT_LOCATION", event_location);
                        startActivity(editIntent);

                    }
                });

            }
        };
        mEventsList.setAdapter(firebaseRecyclerAdapter);
    }

    /**
     * Setup View for Event Item.
     */
    public static class EventsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public EventsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDisplayName(String name) {
            TextView eventNameView = (TextView) mView.findViewById(R.id.event_item_name);
            eventNameView.setText(name);
        }

        public void setDispayTime(String time) {
            TextView eventTimeView = (TextView) mView.findViewById(R.id.event_item_time);
            eventTimeView.setText(time);
        }

        public void setDispayLocation(String location) {
            TextView eventLocationView = (TextView) mView.findViewById(R.id.event_item_location);
            eventLocationView.setText(location);
        }

    }

    /**
     * Menu Manager for Events.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Back to previous screen
            case android.R.id.home:
                finish();
                Log.d(TAG, "onOptionsItemSelected:schedule_screen");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
