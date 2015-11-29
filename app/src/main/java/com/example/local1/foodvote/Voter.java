package com.example.local1.foodvote;

import java.util.List;

public class Voter {
    RestaurantCandidate restaurantChoice;

    public void vote(RestaurantCandidate choice) {
        if (choice != null) {
            choice.removeVote();
        }
        restaurantChoice = choice;
        choice.vote();
    }

    public RestaurantCandidate getChoice() {
        return restaurantChoice;
    }
}
