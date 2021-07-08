package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText etUsername;
    EditText etPassword;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Find Components
        etUsername = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        //Set up sign uup button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: Sign Up Button Clicked");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signUp(username, password);
            }
        });
    }

    public void signUp(String username, String password) {
        Log.i(TAG, "signUp: Attempting to sign up user " + username);

        // Create the ParseUser
        ParseUser user = new ParseUser();

        // Set core properties
        user.setUsername(username);
        user.setPassword(password);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                //No errors
                if (e == null) {
                    //Log in user and go to Main Activity
                    Log.i(TAG, "done: User signed up");
                    login(username, password);
                    //Closes signup activity after user signs up
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.e(TAG, "done: Error signing up", e);
                }
            }
        });
    }

    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                //If there's an error with the user login
                if(e != null) {
                    Log.e(TAG, "done: Issue with login", e);
                    Toast.makeText(SignupActivity.this, "Wrong username/password", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Else navigate to the main activity if the user has signed in properly
                Intent intent = new Intent(SignupActivity.this, FeedActivity.class);
                startActivity(intent);
            }
        });
    }
}