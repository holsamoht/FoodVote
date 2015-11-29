package com.example.local1.foodvote;

public class RestaurantCandidate {
    int votes;
    String name;

    public RestaurantCandidate(String nmae) {
        votes = 0;
        name = nmae;
    }

    public void vote() {
        votes++;
    }

    public void removeVote() {
        votes--;
    }

    public int getVotes() {
        return votes;
    }

    public String getName() {
        return name;
    }
}
