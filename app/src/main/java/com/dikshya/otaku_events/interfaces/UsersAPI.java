package com.dikshya.otaku_events.interfaces;

import com.dikshya.otaku_events.model.AuthResponseObj;
import com.dikshya.otaku_events.model.User;
import com.dikshya.otaku_events.model.UserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsersAPI {
    @POST("users")
    Call<AuthResponseObj> registerUser(@Body User user);

    @POST("users/login")
    Call<AuthResponseObj> loginUser(@Body User user);

    @GET("users/me")
    Call<User> getActiveUser(@Header("Authorization") String token);

    @PUT("users/update")
    Call<Void> updateUser(@Header("Authorization") String token, @Body User user);

    @POST("users/me/logoutall")
    Call<Void> logOut(@Header("Authorization") String token);

    @GET("users/reset-password/{email}")
    Call<Void> sendResetToken(@Path("email") String email);

    @POST("users/reset-password")
    Call<Void> resetPassword(@Body UserDto resetUserObj);

    @PATCH("users/{id}")
    Call<Void> bookMarkEvents(@Header("Authorization") String token, @Path("id") String id, @Body User user);
}
