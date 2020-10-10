package com.dikshya.otaku_events.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dikshya.otaku_events.R;
import com.dikshya.otaku_events.api_util.Base_url;
import com.dikshya.otaku_events.interfaces.UsersAPI;
import com.dikshya.otaku_events.model.AuthResponseObj;
import com.dikshya.otaku_events.model.User;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputLayout etEmail, etPassword;
    Button btnSignUp, btnSignIn, btnForgotPassword;
    ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Use this for high end devices (Full screen with punch holes on screen)
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Getting Sensors ---
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        SensorEventListener gyroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[1] > 0) {
                    redirectToRegister();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        if (mGyro != null) {
            sensorManager.registerListener(gyroListener, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "No sensor found!", Toast.LENGTH_SHORT).show();
        }

        // Hooks
        imgLogo = findViewById(R.id.imgLogo);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        // Set click listeners for buttons
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                this.loginUser(v);
                break;

            case R.id.btnSignUp:
                redirectToRegister();
                break;

            case R.id.btnForgotPassword:
                Intent forgotPasswordActivity = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordActivity);


        }
    }

    private void redirectToRegister() {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    /*
     Validator Functions
     */

    private Boolean validateEmail() {
        String val = etEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            etEmail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            etEmail.setError("Invalid email address");
            return false;
        } else {
            etEmail.setError(null);
            etEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = etPassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            etPassword.setError("Field cannot be empty");
            return false;
        } else {
            etPassword.setError(null);
            etPassword.setErrorEnabled(false);
            return true;
        }
    }

    //This function will execute when user click on Login Button
    public void loginUser(View view) {
        if (!validatePassword() | !validateEmail()) {
            return;
        }
        User user = new User();
        user.setEmail(etEmail.getEditText().getText().toString());
        user.setPassword(etPassword.getEditText().getText().toString());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Base_url.base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsersAPI usersAPI = retrofit.create(UsersAPI.class);

        final Call<AuthResponseObj> authResponse = usersAPI.loginUser(user);

        authResponse.enqueue(new Callback<AuthResponseObj>() {
            @Override
            public void onResponse(Call<AuthResponseObj> call, Response<AuthResponseObj> response) {
                if (response.isSuccessful()) {

                    SharedPreferences sharedPreferences = getSharedPreferences("AuthSP", MODE_PRIVATE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                        sharedPreferences.edit().putString("token", Objects.requireNonNull(response.body()).getToken()).apply();
                        sharedPreferences.edit().putString("user_id", Objects.requireNonNull(response.body()).getUser().get_id()).apply();
                        sharedPreferences.edit().putString("user_type", Objects.requireNonNull(response.body()).getUser().getType()).apply();

                        Toast.makeText(LoginActivity.this, "SUCCESSFULLY LOGGED IN", Toast.LENGTH_SHORT).show();

                        if (response.body().getUser().getType().equals("admin")) {
                            Intent adminIntent = new Intent(LoginActivity.this, AddEventsActivity.class);
                            startActivity(adminIntent);
                        } else {
                            Intent bottomNavIntent = new Intent(LoginActivity.this, BottonNavActivity.class);
                            startActivity(bottomNavIntent);
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponseObj> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
