package com.evandyce.pettinder;

import android.content.Context;

import com.evandyce.pettinder.cards.Animal;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String userID;
    private String name;
    private String email;
    private List<Animal> liked;

    private static int counter = 0;
    private static int likedCounter = 0;

    public User (String userID, String name, String email, int count, List<Animal> liked) {
        this.userID = userID;
        this.name = name;
        this.email = email;
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

    public static int getCounter() {
        return counter;
    }

    public static void incrementCounter() {
        counter++;
    }

    public static int getLikedCounter() {
        return likedCounter;
    }

    public static void incrementLikedCounter() {
        likedCounter++;
    }
}
