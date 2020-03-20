package com.mad.scheduleshare.Activity;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.scheduleshare.Controller.InputValidation;
import com.mad.scheduleshare.Model.User;
import com.mad.scheduleshare.R;

import java.util.HashMap;

/**
 * Sign up with new account for new mUser.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DaysActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mUser;

    private ProgressDialog mRegProgress;

    private NestedScrollView mNestedScrollView;

    private TextInputLayout mNameLayout;
    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;
    private TextInputLayout mConfirmPasswordLayout;

    private TextInputEditText mNameView;
    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;
    private TextInputEditText mConfirmPasswordView;

    private Button mRegisterBtn;
    private TextView mLoginLink;

    private InputValidation mInputValidation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        mInputValidation = new InputValidation(RegisterActivity.this);
        mRegProgress = new ProgressDialog(this);

        initViews();

        mRegisterBtn.setOnClickListener(this);
        mLoginLink.setOnClickListener(this);

    }

    /**
     * Retrieve all views for register screen.
     */
    private void initViews() {
        mNestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        mNameLayout = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        mEmailLayout = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        mConfirmPasswordLayout = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);

        mNameView = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        mEmailView = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        mPasswordView = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        mConfirmPasswordView = (TextInputEditText) findViewById(R.id.textInputEditTextConfirmPassword);

        mRegisterBtn = (Button) findViewById(R.id.register_btn);
        mLoginLink = (TextView) findViewById(R.id.login_link);
    }


    /**
     * Menu Manager for Register and link to Login.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Attempt register on button click.
            case R.id.register_btn:
                registerUser();
                break;

            // Go to login page on link click.
            case R.id.login_link:
                finish();
                break;
        }
    }

    /**
     * Create new mUser account with email and password.
     */
    private void registerUser() {

        // Authentication and Error message for empty name field
        if (!mInputValidation.isInputEditTextFilled(mNameView, mNameLayout, getString(R.string.error_message_name))) {
            mRegProgress.hide();
            return;
        }

        // Authentication and Error message for empty email field
        if (!mInputValidation.isInputEditTextFilled(mEmailView, mEmailLayout, getString(R.string.error_message_email))) {
            mRegProgress.hide();
            return;
        }

        // Authentication and Error message for invalid email field
        if (!mInputValidation.isInputEditTextEmail(mEmailView, mEmailLayout, getString(R.string.error_message_email))) {
            mRegProgress.hide();
            return;
        }

        // Authentication and Error message for empty password field
        if (!mInputValidation.isInputEditTextFilled(mPasswordView, mPasswordLayout, getString(R.string.error_message_password))) {
            return;
        }

        // Authentication and Error message for matching password fields
        if (!mInputValidation.isInputEditTextMatches(mPasswordView, mConfirmPasswordView, mConfirmPasswordLayout,
                getString(R.string.error_password_match))) {
            mRegProgress.hide();
            return;
        }

        // Authentication and Error message for weak password
        if (!mInputValidation.isInputEditTextWeak(mPasswordView, mConfirmPasswordLayout, getString(R.string.error_weak_password))) {
            mRegProgress.hide();
            return;
        }

        // Initiate progress bar
        mRegProgress.setTitle("Registering User");
        mRegProgress.setMessage("Please wait while we create your account...");
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.show();

        // Sign up with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(mEmailView.getText().toString(), mPasswordView.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //authenticate if email exists

                            toastMessage("New account for " + mEmailView.getText() + " has been registered!");

                            // Debug: show registered user data
                            Log.d(TAG, "registerUser: name: " + mNameView.getText().toString());
                            Log.d(TAG, "registerUser: email: " + mEmailView.getText().toString());
                            Log.d(TAG, "registerUser: password: " + mPasswordView.getText().toString());

                            mRegProgress.dismiss();

                            // Get ID of registered User
                            mUser = mAuth.getCurrentUser();
                            String userId = mUser.getUid();

                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(userId);

                            // Add Name and Email values to user in Firebase Database
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("mName", mNameView.getText().toString());
                            userMap.put("mEmail", mEmailView.getText().toString());

                            // Add User Hashmap to database
                            mUserDatabase.setValue(userMap);

                            // Reset input text
                            mNameView.setText(null);
                            mEmailView.setText(null);
                            mPasswordView.setText(null);
                            mConfirmPasswordView.setText(null);

                        } else {

                            mRegProgress.hide();

                            // Snack Bar to show error message that record already exists
                            Snackbar.make(mNestedScrollView, getString(R.string.error_email_exists),
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
