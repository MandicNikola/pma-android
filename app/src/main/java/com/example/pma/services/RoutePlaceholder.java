package com.example.pma.services;

import com.example.pma.model.RouteRequest;
import com.example.pma.model.RouteResponse;
import com.example.pma.model.User;
import com.example.pma.model.UserResponse;
import com.example.pma.model.UserSettings;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RoutePlaceholder {
    @POST("routes")
    Call<RouteResponse> saveRoute(@Body RouteRequest route, @Header("Authorization") String token);
    @GET("routes")
    Call<ArrayList<RouteResponse>> getRoutes(@Header("Authorization") String token);
}
