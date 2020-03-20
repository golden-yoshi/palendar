package com.mad.scheduleshare.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.scheduleshare.Model.User;
import com.mad.scheduleshare.R;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Invite friends to an event.
 */
public class InviteFriendActivity extends AppCompatActivity {

    private static final String TAG = "InviteFriendActivity";

    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private TextView mFriendsInvited;

    private HashSet<String> mInviteFriendSet;
    private String[] mInviteFriendArray;
    private String mInviteFriendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        getSupportActionBar().setTitle("Invite Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get current user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        mLayoutManager = new LinearLayoutManager(this);

        mFriendsInvited = (TextView) findViewById(R.id.friends_invited_title);

        mUsersList = (RecyclerView) findViewById(R.id.invite_friends_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

        // HashSet to Store list of friends without duplicates
        mInviteFriendSet = new HashSet<String>();

    }

    /**
     * Send list of friends invited to Add or Edit Event Activity.
     */
    private void sendInviteList() {
        String previousActivity = getIntent().getStringExtra("PREVIOUS_ACTIVITY");

        if (previousActivity.equals("AddEventActivity")) {
            Intent inviteListIntent = new Intent(InviteFriendActivity.this, AddEventActivity.class);
            inviteListIntent.putExtra("INVITE_LIST", mInviteFriendList);
            setResult(RESULT_OK, inviteListIntent);
            finish();
        } else {
            Intent inviteListIntent = new Intent(InviteFriendActivity.this, EditEventActivity.class);
            inviteListIntent.putExtra("INVITE_LIST", mInviteFriendList);
            setResult(RESULT_OK, inviteListIntent);
            finish();
        }
    }

    /**
     * Setup FirebaseRecylcerAdapter to display Friend User Data.
     */
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, InviteFriendActivity.UsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<User, InviteFriendActivity.UsersViewHolder>(

                User.class,
                R.layout.user_item,
                InviteFriendActivity.UsersViewHolder.class,
                mUsersDatabase

        ) {

            @Override
            protected void populateViewHolder(InviteFriendActivity.UsersViewHolder usersViewHolder, User user, int position) {

                usersViewHolder.setDisplayName(user.getmName());
                usersViewHolder.setUserEmail(user.getmEmail());
                usersViewHolder.setCheckBox(false);

                final int userPosition = usersViewHolder.getAdapterPosition();
                final String user_id = getRef(position).getKey();

                final User friend = user;

                // Add to Invite List upon click
                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Add friends to Hashet
                        mInviteFriendSet.add(friend.getmName());
                        // Convert to String Array
                        mInviteFriendArray = mInviteFriendSet.toArray(new String[mInviteFriendSet.size()]);
                        // Convert to String
                        mInviteFriendList = Arrays.toString(mInviteFriendArray).replaceAll("\\[|\\]", "");

                        mFriendsInvited.setText("Friends Invited: " + mInviteFriendList);

                    }
                });

            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    /**
     * Setup View for Friend User Item.
     */
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDisplayName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_item_name);
            userNameView.setText(name);
        }

        public void setUserEmail(String email) {
            TextView userEmailView = (TextView) mView.findViewById(R.id.user_item_email);
            userEmailView.setText(email);
        }

        public void setCheckBox(Boolean checked) {
            CheckBox userInviteBoxView = (CheckBox) mView.findViewById(R.id.user_item_checkbox);
            userInviteBoxView.setChecked(checked);
        }

    }

    /**
     * Create Menu for Invite Friends.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    /**
     * Menu Manager for Invite Friends.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Cancel Activity (Back to Previous Screen)
            case android.R.id.home:
                finish();
                return true;

            // Confirm (Return Invite List to previous Activity)
            case R.id.action_confirm:
                sendInviteList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
