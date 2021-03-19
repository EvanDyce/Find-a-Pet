package com.evandyce.pettinder.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Dog {
//    private String email;

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
    private Integer age;

//    public Dog(String name, String location, String email, String imageUrl, String age) {
//        this.name = name;
//        this.location = location;
//        this.email = email;
//        this.imageUrl = imageUrl;
//        this.age = age;
//    }

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

//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
