package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.pma.database.DatabaseManagerGoal;
import com.example.pma.database.DatabaseManagerProfile;
import com.example.pma.dialogues.MessageDialogue;
import com.example.pma.model.Profile;
import com.example.pma.model.ProfileDB;
import com.example.pma.model.UserResponse;
import com.example.pma.services.AuthPlaceholder;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {
    private Spinner spinnerGender;
    private Spinner spinnerUnits;
    String[] gender_array = { "MALE", "FEMALE"};
    EditText editName;
    EditText editSurname;
    EditText editEmail;
    EditText editHeight;
    EditText editWeight;
    private DatabaseManagerProfile dbManager;
    private SharedPreferences preferences;
    Retrofit retrofit;
    private AuthPlaceholder service;
    private static final String TAG = "ProfileActivity";
    private String token = "";
    private Integer userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        spinnerGender = (Spinner) findViewById(R.id.gender_spinner);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://pma-app-19.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
         editName = (EditText)findViewById(R.id.name_data);
         editSurname = (EditText)findViewById(R.id.surname_data);
         editEmail = (EditText)findViewById(R.id.email_data);
         editHeight = (EditText)findViewById(R.id.height_data);
         editWeight = (EditText)findViewById(R.id.weight_data);
         dbManager = new DatabaseManagerProfile(this);
         dbManager.open();

        preferences = getSharedPreferences("user_detail", MODE_PRIVATE);

        ArrayAdapter adapterGender= new ArrayAdapter(this,android.R.layout.simple_spinner_item, gender_array);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapterGender);

        if(preferences.contains("token") ) {
            token = preferences.getString("token",null);
            service = retrofit.create(AuthPlaceholder.class);
            Call<UserResponse> callLoggedUser = service.getLoggedUser("Bearer "+token);
            callLoggedUser.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if(response.code() == 200) {
                        userId = response.body().getId();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {

                }
            });
            Call<Profile> call = service.getProfile("Bearer "+token);
            call.enqueue(new Callback<Profile>() {
                @Override
                public void onResponse(Call<Profile> call, Response<Profile> response) {
                    Log.d(TAG,"Code is "+response.code());
                    if (response.isSuccessful()) {
                            if(response.code() == 200){
                                editName.setText(response.body().getFirstname());
                                editSurname.setText(response.body().getLastname());
                                editEmail.setText(response.body().getEmail());
                                editHeight.setText(String.valueOf(response.body().getHeight()));
                                editWeight.setText(String.valueOf(response.body().getWeight()));
                                int spinnerPosition = adapterGender.getPosition(response.body().getGender());
                                spinnerGender.setSelection(spinnerPosition);
                            }
                    }
                }
                @Override
                public void onFailure(Call<Profile> call, Throwable t) {
                    Log.d(TAG,"Unsuccessfull");
                  }
            });
        }else{
            Log.d(TAG,"Token is not there");
        }
    }
    public void checkData(View view) {
        boolean valid = true;
        if(isEmpty(editName.getText().toString())){
            valid = false;
        }
        if(isEmpty(editSurname.getText().toString())) {
            valid = false;
        }
        if(isEmpty(editEmail.getText().toString()) || isEmail(editEmail.getText().toString()) == false) {
            valid = false;
        }
        if(valid){
            if(!token.isEmpty()){
                Profile userProfile = new Profile();
                userProfile.setFirstname(editName.getText().toString());
                userProfile.setLastname(editSurname.getText().toString());
                userProfile.setEmail(editEmail.getText().toString());
                userProfile.setHeight(Double.parseDouble(editHeight.getText().toString()));
                userProfile.setWeight(Double.parseDouble(editWeight.getText().toString()));
                userProfile.setGender(spinnerGender.getSelectedItem().toString());
                Call<HashMap<String, String>> update = service.updateProfile(userProfile,"Bearer "+token);
                update.enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                        if(response.code() == 200){

                            ProfileDB profileDB = dbManager.getProfileByUserId(userId);
                            Log.d("Profile Activity","informacije "+profileDB.getUser_id());
                            dbManager.update(Double.parseDouble(editHeight.getText().toString()),Double.parseDouble(editWeight.getText().toString()),profileDB.getUser_id());
                            MessageDialogue dialog = new MessageDialogue("Profile is updated", "Notification");
                            dialog.show(getSupportFragmentManager(), "Profile");

                        }else{
                            MessageDialogue dialog = new MessageDialogue("There was a problem with updating, please try again", "Notification");
                            dialog.show(getSupportFragmentManager(), "Profile");
                        }
                    }

                    @Override
                    public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                        MessageDialogue dialog = new MessageDialogue("There was a problem with updating, please try again", "Notification");
                        dialog.show(getSupportFragmentManager(), "Profile");
                    }
                });
            }
        }
    }
    public boolean isEmpty(String text){
        return TextUtils.isEmpty(text);
    }
    public boolean isEmail(String text) {
        return (!TextUtils.isEmpty(text) && Patterns.EMAIL_ADDRESS.matcher(text).matches());
    }

}
