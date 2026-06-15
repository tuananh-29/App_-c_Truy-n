package com.example.truyenmoingay.models;

public class Chapter {
    private int id;
    private String title;
    private boolean isLocked;
    private int coinCost;
    private String date;

    public Chapter() {}

    public Chapter(int id, String title, boolean isLocked, int coinCost, String date) {
        this.id = id;
        this.title = title;
        this.isLocked = isLocked;
        this.coinCost = coinCost;
        this.date = date;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public boolean isLocked() { return isLocked; }
    public int getCoinCost() { return coinCost; }
    public String getDate() { return date; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setLocked(boolean locked) { isLocked = locked; }
    public void setCoinCost(int coinCost) { this.coinCost = coinCost; }
    public void setDate(String date) { this.date = date; }
}