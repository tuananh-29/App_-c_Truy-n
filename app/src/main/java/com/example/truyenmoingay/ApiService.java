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

    @GET("api/chuong/{slug}/{chapter}")
    Call<ResponseBody> getChapter(
            @Path("slug") String slug,
            @Path("chapter") int chapter
    );

    @GET("api/tim-kiem")
    Call<ResponseBody> search(
            @Query("q") String keyword,
            @Query("page") int page
    );
}