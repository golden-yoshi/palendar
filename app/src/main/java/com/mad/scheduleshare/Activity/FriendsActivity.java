package com.mad.scheduleshare.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.scheduleshare.Model.User;
import com.mad.scheduleshare.R;

/**
 * Show all users of the app.
 */
public class FriendsActivity extends AppCompatActivity {

    private static final String TAG = "FriendsActivity";

    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        getSupportActionBar().setTitle("Friends List");

        // Get current user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        mLayoutManager = new LinearLayoutManager(this);

        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

    }

    /**
     * Setup FirebaseRecylcerAdapter to display Friend User Data.
     */
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(

                User.class,
                R.layout.user_item,
                UsersViewHolder.class,
                mUsersDatabase

        ) {

            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, User user, int position) {

                usersViewHolder.setDisplayName(user.getmName());
                usersViewHolder.setUserEmail(user.getmEmail());

                // Debug: show user data
                Log.d(TAG, "populateViewHolder: name: " + user.getmName());
                Log.d(TAG, "populateViewHolder: email: " + user.getmEmail());

                final String user_id = getRef(position).getKey();
                final String user_name = user.getmName();
                final String user_email = user.getmEmail();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Only go to profiles of users that aren't the logged in user
                        if (user_email.equals(mUser.getEmail())) {
                            toastMessage("This is you!");
                        } else {
                            Intent profileIntent = new Intent(FriendsActivity.this, ProfileActivity.class);
                            profileIntent.putExtra("USER_ID", user_id);
                            profileIntent.putExtra("USER_NAME", user_name);
                            profileIntent.putExtra("USER_EMAIL", user_email);
                            startActivity(profileIntent);
                        }

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
        if (id == R.id.action_friends_list) {
            toastMessage("You're already here!");
        }

        // Go to Schedule screen
        if (id == R.id.action_schedule) {
            Log.d(TAG, "onOptionsItemSelected:schedule_screen");
            finish();
        }

        // Logout of app
        if (id == R.id.action_logout) {
            finish();
            Intent logout = new Intent(FriendsActivity.this, LoginActivity.class);
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
