package com.example.truyenmoingay.models;

public class Comic {
    public int id;
    public String title;
    public String author;
    public String coverUrl;
    public int chapterCount;
    public float rating;

    public Comic(int id, String title, String author, int chapterCount, float rating) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.chapterCount = chapterCount;
        this.rating = rating;
        this.coverUrl = ""; // để trống, Glide sẽ hiện placeholder
    }
}

