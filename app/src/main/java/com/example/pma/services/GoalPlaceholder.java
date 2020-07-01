package com.example.pma.services;

import com.example.pma.model.Goal;
import com.example.pma.model.GoalRequest;
import com.example.pma.model.GoalResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface GoalPlaceholder {
    @POST("goals")
    Call<GoalResponse> addGoal(@Body GoalRequest goal, @Header("Authorization") String token);
    @PATCH("goals/updateGoal")
    Call<GoalResponse> updateGoal(@Body GoalResponse goal,@Header("Authorization") String token);
    @GET("goals")
    Call<ArrayList<GoalResponse>> getGoals(@Header("Authorization") String token);

}
