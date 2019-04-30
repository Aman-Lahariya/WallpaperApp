package com.amanlahariya.gameWallpaper.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Wallpaper implements Serializable {

    @Exclude
    public String id;
    @Exclude
    public String category;
    @Exclude
    public boolean isFavourite = false;

    public String name, url;


    public Wallpaper(String id, String name, String url, String category) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.category = category;
    }

    //Overloaded constructor for fav
    public Wallpaper(String id, String url) {
        this.id = id;
        this.url = url;
    }

    //Overloaded for FavFragment
    public Wallpaper(String id, String url, String category) {
        this.id = id;
        this.url = url;
        this.category = category;
    }

}
