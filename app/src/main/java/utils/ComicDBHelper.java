package com.example.truyenmoingay.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.truyenmoingay.models.Comic;
import java.util.ArrayList;
import java.util.List;

public class ComicDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "TruyenMoiNgay.db";
    private static final int DB_VERSION = 2; // TĂNG PHIÊN BẢN LÊN 2 ĐỂ TẠO BẢNG MỚI

    public static final String TABLE_FAV = "Favorites"; // YÊU THÍCH
    public static final String TABLE_DISLIKE = "Dislikes"; // KHÔNG THÍCH (MỚI)
    public static final String TABLE_FOLLOW = "Follows"; // THEO DÕI (MỚI)
    public static final String TABLE_HIS = "History"; // LỊCH SỬ

    public static final String COL_COMIC_ID = "comic_id";
    public static final String COL_TITLE = "title";
    public static final String COL_AUTHOR = "author";
    public static final String COL_CHAPTER_COUNT = "chapter_count";
    public static final String COL_RATING = "rating";
    public static final String COL_HIS_TIME = "last_read_time";

    private static ComicDBHelper instance;
    public static synchronized ComicDBHelper getInstance(Context context) {
        if (instance == null) instance = new ComicDBHelper(context.getApplicationContext());
        return instance;
    }

    private ComicDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createFav = "CREATE TABLE " + TABLE_FAV + " (" + COL_COMIC_ID + " INTEGER PRIMARY KEY, " + COL_TITLE + " TEXT, " + COL_AUTHOR + " TEXT, " + COL_CHAPTER_COUNT + " INTEGER, " + COL_RATING + " REAL)";
        String createDis = "CREATE TABLE " + TABLE_DISLIKE + " (" + COL_COMIC_ID + " INTEGER PRIMARY KEY, " + COL_TITLE + " TEXT, " + COL_AUTHOR + " TEXT, " + COL_CHAPTER_COUNT + " INTEGER, " + COL_RATING + " REAL)";
        String createFol = "CREATE TABLE " + TABLE_FOLLOW + " (" + COL_COMIC_ID + " INTEGER PRIMARY KEY, " + COL_TITLE + " TEXT, " + COL_AUTHOR + " TEXT, " + COL_CHAPTER_COUNT + " INTEGER, " + COL_RATING + " REAL)";
        String createHis = "CREATE TABLE " + TABLE_HIS + " (" + COL_COMIC_ID + " INTEGER PRIMARY KEY, " + COL_TITLE + " TEXT, " + COL_AUTHOR + " TEXT, " + COL_CHAPTER_COUNT + " INTEGER, " + COL_RATING + " REAL, " + COL_HIS_TIME + " INTEGER)";

        db.execSQL(createFav);
        db.execSQL(createDis);
        db.execSQL(createFol);
        db.execSQL(createHis);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISLIKE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLLOW);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIS);
        onCreate(db);
    }

    // ── HÀM ĐÓNG GÓI DỮ LIỆU CHUNG ──
    private ContentValues comicToValues(Comic comic) {
        ContentValues cv = new ContentValues();
        cv.put(COL_COMIC_ID, comic.id);
        cv.put(COL_TITLE, comic.title);
        cv.put(COL_AUTHOR, comic.author);
        cv.put(COL_CHAPTER_COUNT, comic.chapterCount);
        cv.put(COL_RATING, comic.rating);
        return cv;
    }

    // ── METHODS CHO YÊU THÍCH (FAVORITES) ──
    public void addFavorite(Comic comic) { SQLiteDatabase db = getWritableDatabase(); db.insertWithOnConflict(TABLE_FAV, null, comicToValues(comic), SQLiteDatabase.CONFLICT_REPLACE); }
    public void removeFavorite(int comicId) { SQLiteDatabase db = getWritableDatabase(); db.delete(TABLE_FAV, COL_COMIC_ID + " = ?", new String[]{String.valueOf(comicId)}); }
    public boolean isFavorite(int comicId) { return exists(TABLE_FAV, comicId); }
    public List<Comic> getAllFavorites() { return getAll(TABLE_FAV, null); }

    // ── METHODS CHO KHÔNG THÍCH (DISLIKES) ──
    public void addDislike(Comic comic) { SQLiteDatabase db = getWritableDatabase(); db.insertWithOnConflict(TABLE_DISLIKE, null, comicToValues(comic), SQLiteDatabase.CONFLICT_REPLACE); }
    public void removeDislike(int comicId) { SQLiteDatabase db = getWritableDatabase(); db.delete(TABLE_DISLIKE, COL_COMIC_ID + " = ?", new String[]{String.valueOf(comicId)}); }
    public boolean isDisliked(int comicId) { return exists(TABLE_DISLIKE, comicId); }
    public List<Comic> getAllDislikes() { return getAll(TABLE_DISLIKE, null); }

    // ── METHODS CHO THEO DÕI (FOLLOWS) ──
    public void addFollow(Comic comic) { SQLiteDatabase db = getWritableDatabase(); db.insertWithOnConflict(TABLE_FOLLOW, null, comicToValues(comic), SQLiteDatabase.CONFLICT_REPLACE); }
    public void removeFollow(int comicId) { SQLiteDatabase db = getWritableDatabase(); db.delete(TABLE_FOLLOW, COL_COMIC_ID + " = ?", new String[]{String.valueOf(comicId)}); }
    public boolean isFollowed(int comicId) { return exists(TABLE_FOLLOW, comicId); }
    public List<Comic> getAllFollows() { return getAll(TABLE_FOLLOW, null); }

    // ── METHODS CHO LỊCH SỬ (HISTORY) ──
    public void addOrUpdateHistory(Comic comic) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = comicToValues(comic);
        cv.put(COL_HIS_TIME, System.currentTimeMillis());
        db.insertWithOnConflict(TABLE_HIS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public List<Comic> getAllHistory() { return getAll(TABLE_HIS, COL_HIS_TIME + " DESC"); }

    // ── HÀM HỖ TRỢ TRUY VẤN ──
    private boolean exists(String table, int comicId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(table, null, COL_COMIC_ID + " = ?", new String[]{String.valueOf(comicId)}, null, null, null);
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }

    private List<Comic> getAll(String table, String orderBy) {
        List<Comic> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(table, null, null, null, null, null, orderBy);
        if (c.moveToFirst()) {
            do {
                Comic comic = new Comic(
                        c.getInt(c.getColumnIndexOrThrow(COL_COMIC_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_TITLE)),
                        c.getString(c.getColumnIndexOrThrow(COL_AUTHOR)),
                        c.getInt(c.getColumnIndexOrThrow(COL_CHAPTER_COUNT)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_RATING))
                );
                list.add(comic);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }
}