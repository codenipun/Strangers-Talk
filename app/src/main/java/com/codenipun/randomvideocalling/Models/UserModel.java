package com.codenipun.randomvideocalling.Models;

public class UserModel {
    private String Uid, name , profile, city;


    long coins;


    public UserModel(){}

    public UserModel(String uid, String name, String profile, String city, long coins) {
        Uid = uid;
        this.name = name;
        this.profile = profile;
        this.city = city;
        this.coins = coins;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

}
