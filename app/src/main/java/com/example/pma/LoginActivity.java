package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pma.dialogues.MessageDialogue;

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
    public void checkData(View view){
        String username = ((EditText)findViewById(R.id.username)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        boolean correct = true;
            if(TextUtils.isEmpty(username)){
                correct = false;
                Log.d(TAG,"Username is empty");

            }
            if(TextUtils.isEmpty(password)){
                correct = false;
                Log.d(TAG,"Password is empty");
            }
            if(correct){
                LoginRequest request = new LoginRequest(username, password);
                navigateRoutes(request);
            }

    }
    public void navigateRoutes(LoginRequest request){
        service = retrofit.create(AuthPlaceholder.class);

        Call<LoginResponse> call = service.loginUser(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    if(response.code() == 200){
                        Log.d(TAG,"Successfull logged "+response.code());

                        MessageDialogue dialog = new MessageDialogue("You have successfully logged in", "Notification");
                        dialog.show(getSupportFragmentManager(), "logging dialog");
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // your code to start second activity. Will wait for 3 seconds before calling this method
                                Intent intent = new Intent(LoginActivity.this, RouteActivity.class);
                                startActivity(intent);
                            }
                        }, 2000);

                    }
                 }else{
                    if(response.code() == 406){
                        CharSequence mess = "Login usuccessfull "+response.code();
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, mess, duration);
                        toast.show();
                    }else {
                        Log.d(TAG, "Unsuccessfull logging" + response.code());
                        MessageDialogue dialog = new MessageDialogue("Sign in unsuccessfully, please try again.", "Notification");
                        dialog.show(getSupportFragmentManager(), "logging dialog");
                    }
                    Log.d(TAG,"Unsuccessfull logged "+response.code());
                }


            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG,"failed "+t.getMessage());

            }
        });
  }

    public void navigateRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
