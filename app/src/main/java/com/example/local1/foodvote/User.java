package com.example.local1.foodvote;

import java.util.ArrayList;
import java.util.List;

public class User {
    String usernameText;
    String passwordText;
    Boolean vote;
    List<String> userEvents = new ArrayList<String>();

    public User(String username, String password) {
        this.usernameText = username;
        this.passwordText = password;
        this.vote = false;
    }

    public String getUsernameText() {
        return this.usernameText;
    }

    public String getPasswordText() {
        return this.passwordText;
    }

    public Boolean getVote() {
        return this.vote;
    }

    void setUserEvents(List<String> userEvents) {
        this.userEvents = userEvents;
    }
}
