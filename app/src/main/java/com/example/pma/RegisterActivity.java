package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pma.database.DatabaseManagerGoal;
import com.example.pma.database.DatabaseManagerProfile;
import com.example.pma.database.DatabaseManagerRoute;
import com.example.pma.dialogues.MessageDialogue;
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
    //za testiranje baze

    private DatabaseManagerProfile dbManagerProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dbManagerProfile = new DatabaseManagerProfile(this);
        dbManagerProfile.open();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://pma-app-19.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("name")){
                ((EditText)findViewById(R.id.firstName)).setText(savedInstanceState.getString("name"));
            }
            if(savedInstanceState.containsKey("surname")){
                ((EditText)findViewById(R.id.lastName)).setText(savedInstanceState.getString("surname"));
            }
            if(savedInstanceState.containsKey("email")){
                ((EditText)findViewById(R.id.email)).setText(savedInstanceState.getString("email"));
            }
            if(savedInstanceState.containsKey("username")){
                ((EditText)findViewById(R.id.username)).setText(savedInstanceState.getString("username"));
            }
            if(savedInstanceState.containsKey("password")){
                ((EditText)findViewById(R.id.password)).setText(savedInstanceState.getString("password"));
            }
        }


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
                    if(response.code() == 200) {
                        MessageDialogue dialog = new MessageDialogue("You have successfully registered", "Notification");
                        dialog.show(getSupportFragmentManager(), "example dialog");
                        int id = response.body().getId();
                        dbManagerProfile.insert(170,60, id,0);
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // your code to start second activity. Will wait for 3 seconds before calling this method
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        }, 2000);
                    }
                }else{
                    if(response.code() == 406){
                        CharSequence mess = "Registration usuccessfull "+response.code();
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, mess, duration);
                        toast.show();
                    }else {
                        MessageDialogue dialog = new MessageDialogue("You are not successfully registered" + response.code(), "Notification");
                        dialog.show(getSupportFragmentManager(), "example dialog");
                    }
                }

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.d(TAG,"Registration failed");
            }
        });
    }

    public void checkData(View view) {
        boolean checkFlag = false;
        if(isEmpty((EditText)findViewById(R.id.firstName))){
            ((EditText) findViewById(R.id.firstName)).setError("First name is required.");
            checkFlag = true;
        }
        if(isEmpty((EditText)findViewById(R.id.lastName))) {
            ((EditText) findViewById(R.id.lastName)).setError("Last name is required.");
            checkFlag = true;
        }
        if(isEmpty((EditText)findViewById(R.id.username))) {
            ((EditText) findViewById(R.id.username)).setError("Username is required.");
            checkFlag = true;
        }
        if(isEmpty((EditText)findViewById(R.id.email))) {
            ((EditText) findViewById(R.id.email)).setError("Email is required.");
            checkFlag = true;
        }else{
            if(!isEmail((EditText)findViewById(R.id.email))){
                ((EditText) findViewById(R.id.email)).setError("Email is not valid.");
                checkFlag = true;
            }
        }
        if(isEmpty((EditText)findViewById(R.id.password))) {
            ((EditText) findViewById(R.id.password)).setError("Password is required.");
            checkFlag = true;
        }else if(isNotPassword((EditText)findViewById(R.id.password))){
            ((EditText) findViewById(R.id.password)).setError("Password must be at least 8 characters long.");
            checkFlag = true;

        }
        if(!checkFlag){
            registerUser(view);
        }

    }
    public boolean isEmpty(EditText text){
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
    public boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    public boolean isNotPassword(EditText text){
        CharSequence password = text.getText().toString();
        if(password.toString().length() < 8 ){
          return true;
        }
        return  false;
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.containsKey("name")){
            ((EditText)findViewById(R.id.firstName)).setText(savedInstanceState.getString("name"));
        }
        if(savedInstanceState.containsKey("surname")){
            ((EditText)findViewById(R.id.lastName)).setText(savedInstanceState.getString("surname"));
        }
        if(savedInstanceState.containsKey("email")){
            ((EditText)findViewById(R.id.email)).setText(savedInstanceState.getString("email"));
        }
        if(savedInstanceState.containsKey("username")){
            ((EditText)findViewById(R.id.username)).setText(savedInstanceState.getString("username"));
        }
        if(savedInstanceState.containsKey("password")){
            ((EditText)findViewById(R.id.password)).setText(savedInstanceState.getString("password"));
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        String name = ((EditText)findViewById(R.id.firstName)).getText().toString();
        String surname = ((EditText)findViewById(R.id.lastName)).getText().toString();
        String email =  ((EditText)findViewById(R.id.email)).getText().toString();
        String username = ((EditText)findViewById(R.id.username)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        savedInstanceState.putString("name", name);
        savedInstanceState.putString("surname", surname);
        savedInstanceState.putString("email", email);
        savedInstanceState.putString("username", username);
        savedInstanceState.putString("password", password);

        super.onSaveInstanceState(savedInstanceState);
    }

}
