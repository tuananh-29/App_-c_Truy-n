package com.example.truyenmoingay.models;

public class Chapter {
    public int id;
    public String title;
    public boolean isLocked;
    public int coinCost;
    public String date;

    public Chapter(int id, String title, boolean isLocked, int coinCost, String date) {
        this.id = id;
        this.title = title;
        this.isLocked = isLocked;
        this.coinCost = coinCost;
        this.date = date;
    }
}