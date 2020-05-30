package com.example.pma.services;

import com.example.pma.model.LoginRequest;
import com.example.pma.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthPlaceholder {
   // @POST("users")
 //   Call<UserResponse> registerUser(@Body User user);
    @POST("users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest user);

}
