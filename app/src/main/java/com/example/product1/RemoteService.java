package com.example.product1;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RemoteService {
    public static final String BASE_URL="http://192.168.0.14:8088/product/";
    @GET("list.jsp")
    Call<List<ProductVO>> listProduct(
            @Query("order") String order,
            @Query("query") String query);
    @GET("read.jsp")
    Call<ProductVO> readProduct(@Query("code") String code);

    @Multipart
    @POST("add.jsp")
    Call<ResponseBody> uploadProduct(
            @Part("code") RequestBody strCode,
            @Part("pname") RequestBody strPname,
            @Part("price") RequestBody strPrice,
            @Part MultipartBody.Part image);
}
