package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.pma.model.LoginRequest;
import com.example.pma.model.LoginResponse;
import com.example.pma.model.UserResponse;
import com.example.pma.services.AuthPlaceholder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private SharedPreferences preferences;
    Retrofit retrofit;
    private AuthPlaceholder service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://pma-app-19.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.d(TAG,"My activity");
                //startActivity(new Intent(MainActivity.this, LoginActivity.class));
                // your code to start second activity. Will wait for 3 seconds before calling this method
                if(preferences.contains("token") ) {
                    Log.d(TAG,"Token is there");
                    String token = preferences.getString("token",null);
                    Log.d(TAG,"Token  is "+token);
                    service = retrofit.create(AuthPlaceholder.class);

                    Call<UserResponse> call = service.getLoggedUser("Bearer "+token);
                    call.enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            Log.d(TAG,"Code is "+response.code());
                            if(response.code() == 403){
                                Log.d(TAG,"Token is expired "+response.code());
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                            if(response.code() == 200){
                                Log.d(TAG,"Token is active "+response.code());
                                startActivity(new Intent(MainActivity.this, RouteActivity.class));
                                Log.d(TAG,"User  is "+response.body().getFirstname());
                            }
                        }
                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            Log.d(TAG,"Unsuccessfull");
                        }
                    });
                }else{
                    //nobody is logged
                    Log.d(TAG,"Token is not there");
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                }
        }, 2000);
    }
}
