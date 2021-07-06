package com.example.instagram;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Ulv6ULmIlZFjbfN3Wp6UCTBnOIfYp8AfESm2sCNk")
                .clientKey("5WufdEYWguww2yosuasO8TWxXydgSfuWzyQCXLyn")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
