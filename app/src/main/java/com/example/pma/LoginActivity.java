package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.pma.model.LoginRequest;
import com.example.pma.model.LoginResponse;
import com.example.pma.services.AuthPlaceholder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    Retrofit retrofit;
    private AuthPlaceholder service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://pma-app-19.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }
    public void navigateRoutes(View view){
        service = retrofit.create(AuthPlaceholder.class);
        String username = ((EditText)findViewById(R.id.username)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        LoginRequest request = new LoginRequest(username, password);
        Call<LoginResponse> call = service.loginUser(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    Log.d(TAG,"Successfull logged "+response.code());
                }else{
                    Log.d(TAG,"Unsuccessfull logged "+response.code());
                }
                Log.d(TAG,"Message code "+response.code());
                if(response.body()==null){
                    Log.d(TAG,"body is null");
                }else{
                    Log.d(TAG,"body is not null"+response.body().getAccessToken());
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG,"failed "+t.getMessage());

            }
        });
        Intent intent = new Intent(LoginActivity.this, RouteActivity.class);
        startActivity(intent);}

    public void navigateRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
