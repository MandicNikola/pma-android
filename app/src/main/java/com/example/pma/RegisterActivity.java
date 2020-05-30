package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.pma.model.User;
import com.example.pma.model.UserResponse;
import com.example.pma.services.AuthPlaceholder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private Retrofit retrofit;
    private AuthPlaceholder service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://pma-app-19.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public void registerUser(View view) {
        service = retrofit.create(AuthPlaceholder.class);
        String userName = ((EditText)findViewById(R.id.firstName)).getText().toString();
        String userLastName = ((EditText)findViewById(R.id.lastName)).getText().toString();
        String  username= ((EditText)findViewById(R.id.username)).getText().toString();
        String  email= ((EditText)findViewById(R.id.email)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        User user = new User(userName,userLastName,username,"123456",email,password);
        Call<UserResponse> call = service.registerUser(user);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()){
                    Log.d(TAG,"Registration successfull "+response.code());
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Log.d(TAG,"Unsuccessfull registration"+response.code());
                }

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.d(TAG,"fail registration");
            }
        });
    }
}
