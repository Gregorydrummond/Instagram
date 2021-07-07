package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

//Login Screen
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    TextView tvSignUp;
    TextView tvSignUpMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Go to main activity if user is already logged in
        if(ParseUser.getCurrentUser() != null) {
            goToFeedActivity();
        }

        //Find Components
        etUsername = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUpMessage = findViewById(R.id.tvSignUpMessage);

        //Setup login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: Login Button Clicked");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });

        //Setup Sign Up Here Click
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: Sign Up Button Clicked");
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    //Login user
    private void loginUser(String username, String password) {
        Log.i(TAG, "loginUser: Attempting to login user " + username);
        //Login user
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                //If there's an error with the user login
                if(e != null) {
                    Log.e(TAG, "done: Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Wrong username/password", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Else navigate to the feed activity if the user has signed in properly
                goToFeedActivity();
                Toast.makeText(LoginActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Start main activity
    private void goToFeedActivity() {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
        //Closes login activity after user logs in
        finish();
    }
}