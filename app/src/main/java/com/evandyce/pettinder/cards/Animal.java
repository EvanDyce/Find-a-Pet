package com.evandyce.pettinder.cards;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Animal {
    private String email;
    private String petfinderURL;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    @SerializedName("age")
    @Expose
    private String age;


    public Animal(){}

    public Animal(String name, String location, String email, String age, String imageUrl, String petfinderURL) {
        this.name = name;
        this.location = location;
        this.email = email;
        this.age = age;
        this.imageUrl = imageUrl;
        this.petfinderURL = petfinderURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPetfinderURL() {
        return petfinderURL;
    }

    public void setPetfinderURL(String petfinderURL) {
        this.petfinderURL = petfinderURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
