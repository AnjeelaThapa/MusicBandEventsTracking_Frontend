package com.dikshya.otaku_events.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dikshya.otaku_events.R;
import com.dikshya.otaku_events.api_util.Base_url;
import com.dikshya.otaku_events.interfaces.UsersAPI;
import com.dikshya.otaku_events.model.UserDto;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgotPasswordActivity extends AppCompatActivity {
    LinearLayout initialPhase, laterPhase;
    TextInputLayout tetResetEmail, tetResetToken, tetNewPassword;
    Button btnSendEmail, btnReset;

    String activeEmail;

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Base_url.base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    UsersAPI usersAPI = retrofit.create(UsersAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Use this for high end devices (Full screen with punch holes on screen)
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        this.bindProperties();

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    activeEmail = Objects.requireNonNull(tetResetEmail.getEditText()).getText().toString();
                }
                final Call<Void> emailResponse = usersAPI.sendResetToken(activeEmail);

                emailResponse.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            laterPhase.setVisibility(View.VISIBLE);
                            initialPhase.setVisibility(View.GONE);
                            Toast.makeText(ForgotPasswordActivity.this, "SUCCESSFUL! Please check your Email!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "FAILED TO SEND TOKEN! Email does not exist!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ForgotPasswordActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserDto resetUserObj = new UserDto();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String passResetToken= Objects.requireNonNull(tetResetToken.getEditText()).getText().toString();
                    String password = Objects.requireNonNull(tetNewPassword.getEditText()).getText().toString();
                    resetUserObj.setEmail(activeEmail);
                    resetUserObj.setPassResetToken(passResetToken);
                    resetUserObj.setPassword(password);
                }

                final Call<Void> resetRes = usersAPI.resetPassword(resetUserObj);

                resetRes.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            startActivity(i);
                            Toast.makeText(ForgotPasswordActivity.this, "SUCCESSFUL! Please Login with your new password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ForgotPasswordActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void bindProperties() {
        initialPhase = findViewById(R.id.initialPhase);
        laterPhase = findViewById(R.id.laterPhase);
        tetResetEmail = findViewById(R.id.tetResetEmail);
        tetResetToken = findViewById(R.id.tetResetToken);
        tetNewPassword = findViewById(R.id.tetNewPassword);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        btnReset = findViewById(R.id.btnReset);
    }
}
