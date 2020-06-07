package com.example.pma.services;

import com.example.pma.model.Goal;
import com.example.pma.model.GoalRequest;
import com.example.pma.model.GoalResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GoalPlaceholder {
    @POST("goals")
    Call<GoalResponse> addGoal(@Body GoalRequest goal, @Header("Authorization") String token);


}
