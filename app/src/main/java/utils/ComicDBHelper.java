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
    private static final int DB_VERSION = 3; // TĂNG PHIÊN BẢN LÊN 3 ĐỂ CẬP NHẬT BẢNG MỚI

    public static final String TABLE_FAV = "Favorites";
    public static final String TABLE_DISLIKE = "Dislikes";
    public static final String TABLE_FOLLOW = "Follows";
    public static final String TABLE_HIS = "History";

    public static final String COL_COMIC_ID = "comic_id";
    public static final String COL_TITLE = "title";
    public static final String COL_AUTHOR = "author";
    public static final String COL_CHAPTER_COUNT = "chapter_count";
    public static final String COL_RATING = "rating";
    public static final String COL_HIS_TIME = "last_read_time";
    public static final String COL_LAST_CHAP_ID = "last_chapter_id";     // THÊM MỚI
    public static final String COL_LAST_CHAP_TITLE = "last_chapter_title"; // THÊM MỚI

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

        // THÊM 2 CỘT MỚI VÀO LỊCH SỬ
        String createHis = "CREATE TABLE " + TABLE_HIS + " (" +
                COL_COMIC_ID + " INTEGER PRIMARY KEY, " +
                COL_TITLE + " TEXT, " +
                COL_AUTHOR + " TEXT, " +
                COL_CHAPTER_COUNT + " INTEGER, " +
                COL_RATING + " REAL, " +
                COL_HIS_TIME + " INTEGER, " +
                COL_LAST_CHAP_ID + " INTEGER, " +
                COL_LAST_CHAP_TITLE + " TEXT)";

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

    private ContentValues comicToValues(Comic comic) {
        ContentValues cv = new ContentValues();
        cv.put(COL_COMIC_ID, comic.id);
        cv.put(COL_TITLE, comic.title);
        cv.put(COL_AUTHOR, comic.author);
        cv.put(COL_CHAPTER_COUNT, comic.chapterCount);
        cv.put(COL_RATING, comic.rating);
        return cv;
    }

    public void addFavorite(Comic comic) { SQLiteDatabase db = getWritableDatabase(); db.insertWithOnConflict(TABLE_FAV, null, comicToValues(comic), SQLiteDatabase.CONFLICT_REPLACE); }
    public void removeFavorite(int comicId) { SQLiteDatabase db = getWritableDatabase(); db.delete(TABLE_FAV, COL_COMIC_ID + " = ?", new String[]{String.valueOf(comicId)}); }
    public boolean isFavorite(int comicId) { return exists(TABLE_FAV, comicId); }
    public List<Comic> getAllFavorites() { return getAll(TABLE_FAV, null); }

    public void addDislike(Comic comic) { SQLiteDatabase db = getWritableDatabase(); db.insertWithOnConflict(TABLE_DISLIKE, null, comicToValues(comic), SQLiteDatabase.CONFLICT_REPLACE); }
    public void removeDislike(int comicId) { SQLiteDatabase db = getWritableDatabase(); db.delete(TABLE_DISLIKE, COL_COMIC_ID + " = ?", new String[]{String.valueOf(comicId)}); }
    public boolean isDisliked(int comicId) { return exists(TABLE_DISLIKE, comicId); }
    public List<Comic> getAllDislikes() { return getAll(TABLE_DISLIKE, null); }

    public void addFollow(Comic comic) { SQLiteDatabase db = getWritableDatabase(); db.insertWithOnConflict(TABLE_FOLLOW, null, comicToValues(comic), SQLiteDatabase.CONFLICT_REPLACE); }
    public void removeFollow(int comicId) { SQLiteDatabase db = getWritableDatabase(); db.delete(TABLE_FOLLOW, COL_COMIC_ID + " = ?", new String[]{String.valueOf(comicId)}); }
    public boolean isFollowed(int comicId) { return exists(TABLE_FOLLOW, comicId); }
    public List<Comic> getAllFollows() { return getAll(TABLE_FOLLOW, null); }

    // SỬA HÀM LƯU LỊCH SỬ: THÊM CHƯƠNG ĐANG ĐỌC VÀO
    public void addOrUpdateHistory(Comic comic, int chapterId, String chapterTitle) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = comicToValues(comic);
        cv.put(COL_HIS_TIME, System.currentTimeMillis());
        cv.put(COL_LAST_CHAP_ID, chapterId);         // LƯU ID CHƯƠNG
        cv.put(COL_LAST_CHAP_TITLE, chapterTitle);   // LƯU TÊN CHƯƠNG
        db.insertWithOnConflict(TABLE_HIS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<Comic> getAllHistory() {
        List<Comic> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_HIS, null, null, null, null, null, COL_HIS_TIME + " DESC");
        if (c.moveToFirst()) {
            do {
                Comic comic = new Comic(
                        c.getInt(c.getColumnIndexOrThrow(COL_COMIC_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_TITLE)),
                        c.getString(c.getColumnIndexOrThrow(COL_AUTHOR)),
                        c.getInt(c.getColumnIndexOrThrow(COL_CHAPTER_COUNT)),
                        c.getFloat(c.getColumnIndexOrThrow(COL_RATING))
                );
                // LẤY THÔNG TIN CHƯƠNG ĐÃ ĐỌC GẮN VÀO OBJECT COMIC
                comic.lastChapterId = c.getInt(c.getColumnIndexOrThrow(COL_LAST_CHAP_ID));
                comic.lastChapterTitle = c.getString(c.getColumnIndexOrThrow(COL_LAST_CHAP_TITLE));
                list.add(comic);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

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