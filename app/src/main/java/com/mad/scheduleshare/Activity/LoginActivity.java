package com.mad.scheduleshare.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.scheduleshare.Controller.InputValidation;
import com.mad.scheduleshare.Model.User;
import com.mad.scheduleshare.R;

/**
 * Sign in registered user to use the app.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ProgressDialog mLoginProgress;

    private NestedScrollView mNestedScrollView;

    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;

    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;

    private Button mLoginBtn;
    private TextView mRegisterLink;

    private InputValidation mInputValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this);

        mInputValidation = new InputValidation(LoginActivity.this);

        initViews();

        mLoginBtn.setOnClickListener(this);
        mRegisterLink.setOnClickListener(this);

    }

    /**
     * Setup Login Views.
     */
    private void initViews() {
        mNestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        mEmailLayout = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        mEmailView = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        mPasswordView = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mRegisterLink = (TextView) findViewById(R.id.register_link);
    }

    /**
     * Menu Manager for Login and link to Register.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Attempt login on button click.
            case R.id.login_btn:
                attemptLogin();
                break;

            //Go to register page on link click.
            case R.id.register_link:
                Intent intentRegister = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }

    /**
     * Attempt user login with email and password.
     */
    private void attemptLogin() {

        // Authentication and Error message for Empty Email
        if (!mInputValidation.isInputEditTextFilled(mEmailView, mEmailLayout, getString(R.string.error_message_email))) {
            return;
        }
        // Authentication and Error message for Invalid Email Type
        if (!mInputValidation.isInputEditTextEmail(mEmailView, mEmailLayout, getString(R.string.error_message_email))) {
            return;
        }
        // Authentication and Error message for Empty Password
        if (!mInputValidation.isInputEditTextFilled(mPasswordView, mPasswordLayout, getString(R.string.error_message_email))) {
            return;
        }

        // Initiate progress bar loading login
        mLoginProgress.setTitle("Logging In");
        mLoginProgress.setMessage("Please wait while we check your credentials...");
        mLoginProgress.setCanceledOnTouchOutside(false);
        mLoginProgress.show();

        // Sign in with Firebase Authentication
        mAuth.signInWithEmailAndPassword(mEmailView.getText().toString(), mPasswordView.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            mUser = mAuth.getCurrentUser();
                            final String userId = mUser.getUid();

                            Log.d(TAG, "attemptLogin:signed_in:" + userId);

                            // Retrieve logged in User's  name
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference name = ref.child("User").child(userId).child("mName");
                            final User user = new User();

                            name.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String name = dataSnapshot.getValue(String.class);

                                    mLoginProgress.dismiss();

                                    // Reset Input Text;
                                    mEmailView.setText(null);
                                    mPasswordView.setText(null);

                                    // Welcome user message
                                    user.setName(name);
                                    toastMessage("Welcome " + name);

                                    finish();

                                    // Transfer User Information to Main Activity
                                    Intent accountsIntent = new Intent(LoginActivity.this, DaysActivity.class); // MainActivity
                                    accountsIntent.putExtra("ID", userId);
                                    accountsIntent.putExtra("NAME", name);
                                    accountsIntent.putExtra("EMAIL", mAuth.getCurrentUser().getEmail());
                                    startActivity(accountsIntent);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                        } else {

                            mLoginProgress.hide();

                            // Snack Bar to show error message for invalid email or password
                            Snackbar.make(mNestedScrollView, getString(R.string.error_valid_email_password),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Create text as toast message.
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
