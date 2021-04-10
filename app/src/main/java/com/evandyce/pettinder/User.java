package com.evandyce.pettinder;

import android.content.Context;

import com.evandyce.pettinder.cards.Animal;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String userID;
    private String name;
    private String email;
    private int counter;
    private List<Animal> liked;

    public User (String userID, String name, String email, int count, List<Animal> liked) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.counter = count;
        this.liked = liked;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
