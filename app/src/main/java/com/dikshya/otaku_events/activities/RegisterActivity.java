package com.dikshya.otaku_events.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dikshya.otaku_events.R;
import com.dikshya.otaku_events.api_util.Base_url;
import com.dikshya.otaku_events.interfaces.UsersAPI;
import com.dikshya.otaku_events.model.AuthResponseObj;
import com.dikshya.otaku_events.model.User;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputLayout etUsername, etPassword, etFullName, etEmail, etPhone;
    Button btnBackToLogin, btnSignUp;
    TextView txtLogoName, txtInfo;
    ImageView imgLogo;
    CircleImageView profile_image;

    String imageString = "none";

    private static final float SHAKE_THRESHOLD = 3.25f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 1000;
    private long mLastShakeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Use this for high end devices (Full screen with punch holes on screen)
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        // Getting Sensors ---
        SensorManager mSensorMgr  = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mGyro = mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        SensorEventListener gyroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[1] < 0) {
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        if (mGyro != null) {
            mSensorMgr.registerListener(gyroListener, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "No sensor found!", Toast.LENGTH_SHORT).show();
        }

        Sensor accelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            mSensorMgr.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        long curTime = System.currentTimeMillis();
                        if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {

                            float x = event.values[0];
                            float y = event.values[1];
                            float z = event.values[2];

                            double acceleration = Math.sqrt(Math.pow(x, 2) +
                                    Math.pow(y, 2) +
                                    Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;

                            if (acceleration > SHAKE_THRESHOLD) {
                                mLastShakeTime = curTime;
                                etEmail.getEditText().setText("");
                                etFullName.getEditText().setText("");
                                etPassword.getEditText().setText("");
                                etPhone.getEditText().setText("");
                                etUsername.getEditText().setText("");
                            }
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            }, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Hooks
        imgLogo = findViewById(R.id.imgLogoR);
        profile_image = findViewById(R.id.profile_image);
        txtLogoName = findViewById(R.id.txtLogoNameR);
        txtInfo = findViewById(R.id.txtInfoR);

        etUsername = findViewById(R.id.etUsernameR);
        etPassword = findViewById(R.id.etPasswordR);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etFullName = findViewById(R.id.etFullName);

        btnSignUp = findViewById(R.id.btnSignUpR);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // Set click listeners for buttons
        btnSignUp.setOnClickListener(this);
        btnBackToLogin.setOnClickListener(this);
        profile_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profile_image:
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    startActivityForResult(Intent.createChooser(gallery, "Select profile picture"), 1);
                }
                break;

            case R.id.btnSignUpR:
                this.registerUser(v);
                break;

            case R.id.btnBackToLogin:
                redirectToLogin();
        }
    }

    private void redirectToLogin() {
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    if (data != null) {
                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), data.getData());
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        profile_image.setImageBitmap(bitmap);

                        // Converting to base64--
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        this.imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     Validator Functions
     */

    private Boolean validateName() {
        String val = etFullName.getEditText().getText().toString();

        if (val.isEmpty()) {
            etFullName.setError("Field cannot be empty");
            return false;
        } else {
            etFullName.setError(null);
            etFullName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateUsername() {
        String val = etUsername.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            etUsername.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 15) {
            etUsername.setError("Username too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            etUsername.setError("White Spaces are not allowed");
            return false;
        } else {
            etUsername.setError(null);
            etUsername.setErrorEnabled(false);
            return true;
        }
    }

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

    private Boolean validatePhoneNo() {
        String val = etPhone.getEditText().getText().toString();

        if (val.isEmpty()) {
            etPhone.setError("Field cannot be empty");
            return false;
        } else {
            etPhone.setError(null);
            etPhone.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = etPassword.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&amp;+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            etPassword.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            etPassword.setError("Password is too weak");
            return false;
        } else {
            etPassword.setError(null);
            etPassword.setErrorEnabled(false);
            return true;
        }
    }

    //This function will execute when user click on Register Button
    public void registerUser(View view) {
        if (!validateName() | !validatePassword() | !validatePhoneNo() | !validateEmail() | !validateUsername()) {
            return;
        }
        User user = new User();
        user.setFullName(etFullName.getEditText().getText().toString());
        user.setEmail(etEmail.getEditText().getText().toString());
        user.setImgString(this.imageString);
        user.setPassword(etPassword.getEditText().getText().toString());
        user.setUsername(etUsername.getEditText().getText().toString());
        user.setPhoneNumber(etPhone.getEditText().getText().toString());
        user.setPassResetToken("none");
        user.setType("client");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Base_url.base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsersAPI usersAPI = retrofit.create(UsersAPI.class);

        final Call<AuthResponseObj> authResponse = usersAPI.registerUser(user);

        authResponse.enqueue(new Callback<AuthResponseObj>() {
            @Override
            public void onResponse(Call<AuthResponseObj> call, Response<AuthResponseObj> response) {
                if (response.isSuccessful()) {

                    SharedPreferences sharedPreferences = getSharedPreferences("AuthSP", MODE_PRIVATE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                        sharedPreferences.edit().putString("token", Objects.requireNonNull(response.body()).getToken()).apply();
                        sharedPreferences.edit().putString("user_id", Objects.requireNonNull(response.body()).getUser().get_id()).apply();
                        sharedPreferences.edit().putString("user_type", Objects.requireNonNull(response.body()).getUser().getType()).apply();

                        Toast.makeText(RegisterActivity.this, "SUCCESSFULLY REGISTERED", Toast.LENGTH_SHORT).show();

                        Intent bottomNavIntent = new Intent(RegisterActivity.this, BottonNavActivity.class);
                        startActivity(bottomNavIntent);
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponseObj> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
