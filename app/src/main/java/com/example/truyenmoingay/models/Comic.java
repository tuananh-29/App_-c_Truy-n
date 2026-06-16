package com.example.truyenmoingay.models;

public class Comic {
    public int id;
    public String title;
    public String author;
    public String coverUrl;
    public int chapterCount;
    public float rating;

    // Biên dùng cho Lịch sử (Lưu chương đang đọc) - ĐÃ SỬA CHỮ STRING VIẾT HOA
    public int lastChapterId = 1;
    public String lastChapterTitle = "Chương 1: Khởi đầu";

    public Comic(int id, String title, String author, int chapterCount, float rating) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.chapterCount = chapterCount;
        this.rating = rating;
        this.coverUrl = "";
    }
}