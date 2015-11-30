package com.example.local1.foodvote;

import android.app.Application;

import com.parse.Parse;

public class InitializeParse extends Application {
    // Parse API keys.
    private static final String APPLICATION_ID = "eXtNPVXW7taCtotOcfAWtrDEVyZxS1UksSgVSLvm";
    private static final String CLIENT_KEY = "bffulWPQ7a9WaGOQXN8BfI3oNkkhkLtWZql0MUkQ";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
    }
}
