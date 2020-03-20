package com.mad.scheduleshare.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.scheduleshare.Model.Event;
import com.mad.scheduleshare.R;

/**
 * Show friend's events for a specific day.
 */
public class FriendEventsActivity extends AppCompatActivity {

    private static final String TAG = "FriendEventsActivity";

    private RecyclerView mEventsList;
    private DatabaseReference mEventsDatabase;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mFriendId, mFriendName;

    private TextView mEmptyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get current user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mEmptyList = findViewById(R.id.empty_friend_events_list);

        // Get Day Intent:
        Intent friendIntent = getIntent();
        String dayName = friendIntent.getStringExtra("DAY_NAME");
        mFriendId = friendIntent.getStringExtra("FRIEND_ID");
        mFriendName = friendIntent.getStringExtra("FRIEND_NAME");

        getSupportActionBar().setTitle(mFriendName + "'s Events for " + dayName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Establish Firebase:
        mEventsDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mFriendId).child("mSchedule").child(dayName);
        mLayoutManager = new LinearLayoutManager(this);

        mEventsList = (RecyclerView) findViewById(R.id.friend_events_list);
        mEventsList.setHasFixedSize(true);
        mEventsList.setLayoutManager(mLayoutManager);

    }

    /**
     * Setup FirebaseRecylcerAdapter to display Friend's Event Data.
     */
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Event, FriendEventsActivity.EventsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, FriendEventsActivity.EventsViewHolder>(

                Event.class,
                R.layout.event_item,
                FriendEventsActivity.EventsViewHolder.class,
                mEventsDatabase

        ) {

            @Override
            protected void populateViewHolder(FriendEventsActivity.EventsViewHolder eventsViewHolder, Event event, int position) {

                eventsViewHolder.setDisplayName(event.getName());
                eventsViewHolder.setDispayTime(event.getStartTime() + " - " + event.getEndTime());
                eventsViewHolder.setDispayLocation(event.getLocation());

                // Debug: show event data
                Log.d(TAG, "populateViewHolder: name: " + event.getName());
                Log.d(TAG, "populateViewHolder: time: " + event.getStartTime() + " - " + event.getEndTime());
                Log.d(TAG, "populateViewHolder: location: " + event.getLocation());

                // Handle empty event list
                if (getItemCount() != 0) {
                    mEmptyList.setVisibility(View.GONE);
                }


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
     * Menu Manager for Friends Events.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Back to previous screen
            case android.R.id.home:
                finish();
                Log.d(TAG, "onOptionsItemSelected:profile_screen");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
