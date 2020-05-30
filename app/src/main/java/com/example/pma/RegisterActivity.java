package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}
