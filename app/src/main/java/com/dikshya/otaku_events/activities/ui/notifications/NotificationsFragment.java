package com.dikshya.otaku_events.activities.ui.notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.dikshya.otaku_events.R;
import com.dikshya.otaku_events.activities.LoginActivity;
import com.dikshya.otaku_events.api_util.Base_url;
import com.dikshya.otaku_events.interfaces.UsersAPI;
import com.dikshya.otaku_events.model.User;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class NotificationsFragment extends Fragment {
    private TextInputLayout tetPhone;
    private TextInputLayout tetEmail;
    private TextInputLayout tetUsername;
    private TextInputLayout tetFullName;
    private Button btnUpdateProfile;
    private Button btnLogOut;
    private TextView txtRentedStatus;
    private TextView txtBookedStatus;
    private TextView txtFullName;
    private TextView txtUsername;
    private CircleImageView profile_image;

    private User currentUser = new User();
    private String token;

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        // GET TOKEN FROM SHARED PREFERENCE -----
        SharedPreferences sharedPreferences = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("AuthSP", MODE_PRIVATE);
        }
        this.token = sharedPreferences.getString("token", "");

        this.bindProperties(root);
        this.getActiveUser(token);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.setUsername(tetUsername.getEditText().getText().toString());
                currentUser.setFullName(tetFullName.getEditText().getText().toString());
                currentUser.setEmail(tetEmail.getEditText().getText().toString());
                currentUser.setPhoneNumber(tetPhone.getEditText().getText().toString());

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Base_url.base_url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                UsersAPI usersAPI = retrofit.create(UsersAPI.class);
                final Call<Void> updateRes = usersAPI.updateUser("Bearer " + token, currentUser);

                updateRes.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), response.message() + " Email might already exit!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Log out")
                        .setMessage("Do you really want to Log out of this account?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(Base_url.base_url)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

                                UsersAPI usersAPI = retrofit.create(UsersAPI.class);
                                final Call<Void> logOutRes = usersAPI.logOut("Bearer " + token);

                                try {
                                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                            .permitAll().build();
                                    StrictMode.setThreadPolicy(policy);

                                    Response<Void> response = logOutRes.execute();
                                    if (response.isSuccessful()) {
                                        // Clear shared preferences --
                                        SharedPreferences settings = getContext().getSharedPreferences("AuthSP", MODE_PRIVATE);
                                        settings.edit().clear().apply();

                                        Intent loginActivity = new Intent(getContext(), LoginActivity.class);
                                        startActivity(loginActivity);
                                    } else {
                                        Toast.makeText(getContext(), "Failed to Log Out !!", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (IOException e) {
                                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                    Log.e("", "", e);
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        return root;
    }

    private void bindProperties(View container) {
        tetFullName = container.findViewById(R.id.tetFullName);
        tetPhone = container.findViewById(R.id.tetPhone);
        tetEmail = container.findViewById(R.id.tetEmail);
        tetUsername = container.findViewById(R.id.tetUsername);
        btnUpdateProfile = container.findViewById(R.id.btnUpdateProfile);
        btnLogOut = container.findViewById(R.id.btnLogOut);
        txtRentedStatus = container.findViewById(R.id.txtRentedStatus);
        txtBookedStatus = container.findViewById(R.id.txtBookedStatus);
        txtFullName = container.findViewById(R.id.txtFullName);
        txtUsername = container.findViewById(R.id.txtUsername);
        profile_image = container.findViewById(R.id.profile_image);
    }

    private void setValues(User user) {
        tetFullName.getEditText().setText(user.getFullName());
        tetPhone.getEditText().setText(user.getPhoneNumber());
        tetUsername.getEditText().setText(user.getUsername());
        tetEmail.getEditText().setText(user.getEmail());
        txtFullName.setText(user.getFullName());
        txtUsername.setText(user.getUsername());
        if (user.getBookmarkedEvents().isEmpty()) {
            txtRentedStatus.setText("No");
        } else {
            txtRentedStatus.setText("Yes");
            txtBookedStatus.setText(String.valueOf(user.getBookmarkedEvents().size()));
        }

        // Decode Base64 to image ----
        String imageString = user.getImgString();
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        profile_image.setImageBitmap(decodedImage);
    }

    private void getActiveUser(String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Base_url.base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsersAPI usersAPI = retrofit.create(UsersAPI.class);

        final Call<User> activeUserResponse = usersAPI.getActiveUser("Bearer " + token);

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Response<User> response = activeUserResponse.execute();

            if (response.isSuccessful()) {
                setValues(response.body());
                currentUser = response.body();
            } else {
                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("", "", e);
        }
    }
}