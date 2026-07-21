package com.example.truyenmoingay;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("api/login")
    Call<AuthResponse> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("api/register")
    Call<AuthResponse> registerUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("api/truyen-moi")
    Call<ResponseBody> getHome();

    @GET("api/danh-sach/{type}")
    Call<ResponseBody> getList(
            @Path("type") String type,
            @Query("page") int page
    );

    @GET("api/truyen/{slug}")
    Call<ResponseBody> getDetail(@Path("slug") String slug);

    // KHÔNG dùng nữa - endpoint OTruyen tương ứng "/truyen-tranh/{slug}/chuong-{chapter}" không tồn tại.
    // Giữ lại để không vỡ chỗ khác nếu còn tham chiếu, nhưng dùng getChapterContent() bên dưới thay thế.
    @GET("api/chuong/{slug}/{chapter}")
    Call<ResponseBody> getChapter(
            @Path("slug") String slug,
            @Path("chapter") int chapter
    );

    // Proxy cho "chapter_api_data" lấy được từ getDetail() -> data.item.chapters[].server_data[].chapter_api_data
    // url phải là URL đầy đủ (ví dụ "https://sv1.otruyencdn.com/v1/api/chapter/xxxxx")
    @GET("api/chuong-noi-dung")
    Call<ResponseBody> getChapterContent(@Query("url") String chapterApiData);

    @GET("api/tim-kiem")
    Call<ResponseBody> search(
            @Query("q") String keyword,
            @Query("page") int page
    );

    @GET("api/truyen-theo-tag")
    Call<ResponseBody> getComicsByHashtag(@Query("tag") String tag);

    @GET("api/theo-doi")
    Call<ResponseBody> getFollowingComics();
}