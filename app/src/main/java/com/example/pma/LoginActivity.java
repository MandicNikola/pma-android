package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void navigateRoutes(View view){
        Intent intent = new Intent(LoginActivity.this, RouteActivity.class);
        startActivity(intent);
    }
}
