package com.evandyce.pettinder;

import android.content.Context;

import com.evandyce.pettinder.cards.Animal;

import java.util.ArrayList;
import java.util.List;

public class User {
    private static User instance;
    private static Context context;

    private String userID;
    private String name;
    private String email;
    private List<Animal> favorites;
    private int swipeCounter;

    private User (Context ctx, String userID, String name, String email) {
        context = ctx;
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.favorites = new ArrayList<>();
        this.swipeCounter = 0;
    }

    public static synchronized User getInstance(Context context, String userID, String name, String email) {
        if (instance == null) {
            instance = new User(context, userID, name, email);
        }
        return instance;
    }

    public void addAnimal(Animal animal) {
        this.favorites.add(animal);
    }

    public List<Animal> getLiked() {
        return this.favorites;
    }

    public void incrementCounter() {
        this.swipeCounter++;
    }

    public int getSwipeCounter() {
        return this.swipeCounter;
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
