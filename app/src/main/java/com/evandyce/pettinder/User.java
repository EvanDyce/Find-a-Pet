package com.evandyce.pettinder;

import com.evandyce.pettinder.cards.Dog;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String signup_date;
    private int number_of_swipes;
    private List<Dog> liked_animals;

    public User(){}

    public User(String name, String signup_date) {
        this.name = name;
        this.signup_date = signup_date;
        this.number_of_swipes = 0;
        this.liked_animals = new ArrayList<>();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignup_date() {
        return signup_date;
    }

    public void setSignup_date(String signup_date) {
        this.signup_date = signup_date;
    }

    public int getNumber_of_swipes() {
        return number_of_swipes;
    }

    public void setNumber_of_swipes(int number_of_swipes) {
        this.number_of_swipes = number_of_swipes;
    }

    public List<Dog> getLiked_animals() {
        return liked_animals;
    }

    public void setLiked_animals(List<Dog> liked_animals) {
        this.liked_animals = liked_animals;
    }
}
