package com.example.pma.services;

import com.example.pma.model.GoalRequest;
import com.example.pma.model.GoalResponse;
import com.example.pma.model.LoginRequest;
import com.example.pma.model.LoginResponse;
import com.example.pma.model.Profile;
import com.example.pma.model.User;
import com.example.pma.model.UserResponse;
import com.example.pma.model.UserSettings;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthPlaceholder {
    @POST("users")
   Call<UserResponse> registerUser(@Body User user);
    @POST("users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest user);
    @GET("users/getLogged")
    Call<UserResponse> getLoggedUser(@Header("Authorization") String token);
    @GET("users/profile")
    Call<Profile> getProfile(@Header("Authorization") String token);
    @PATCH("users/updateProfile")
    Call<HashMap<String, String>> updateProfile(@Body Profile profile, @Header("Authorization") String token);
    @PATCH("users/updateReminder")
    Call<HashMap<String, String>> updateReminder(@Body UserSettings settings, @Header("Authorization") String token);

}
