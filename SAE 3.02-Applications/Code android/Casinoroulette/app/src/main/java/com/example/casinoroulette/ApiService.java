package com.example.casinoroulette;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Query;

public interface ApiService {
    @POST("login.php")
    Call<ApiResponse> loginUser(@Body User user);

    @POST("register.php")
    Call<ApiResponse> registerUser(@Body User user);

    @POST("update_solde.php")
    Call<ApiResponse> updateSolde(@Body User user);

    @GET("messages.php")
    Call<ApiResponse> getMessages();

    @POST("update_history.php")
    @FormUrlEncoded
    Call<ApiResponse> updateHistory(@Field("winning_number") int number);

    @GET("get_history.php")
    Call<ApiResponse> getHistory();






}
