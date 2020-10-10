package com.dikshya.otaku_events.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dikshya.otaku_events.R;
import com.dikshya.otaku_events.api_util.Base_url;
import com.dikshya.otaku_events.interfaces.UsersAPI;
import com.dikshya.otaku_events.interfaces.EventsAPI;
import com.dikshya.otaku_events.model.Events;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddEventsActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap map;
    ImageView imgEvent;
    EditText etName, etDesc, etSchedule, etLocationCoordinates;
    Button btnAdd, btnLogOut;

    private String imageString = "none";
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        // Use this for high end devices (Full screen with punch holes on screen)
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // GET TOKEN FROM SHARED PREFERENCE -----
        SharedPreferences sharedPreferences = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            sharedPreferences = Objects.requireNonNull(getSharedPreferences("AuthSP", MODE_PRIVATE));
        }
        this.token = sharedPreferences.getString("token", "");

        this.bindProperties();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Events events = new Events();
                events.setName(etName.getText().toString());
                events.setDescription(etDesc.getText().toString());
                events.setLocationCoordinates(etLocationCoordinates.getText().toString());
                events.setImgString(imageString);
                events.setSchedule(etSchedule.getText().toString());

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Base_url.base_url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                EventsAPI eventsAPI = retrofit.create(EventsAPI.class);

                final Call<Void> eventsSaveRes = eventsAPI.addEvents("Bearer " + token, events);

                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    Response<Void> response = eventsSaveRes.execute();

                    if (response.isSuccessful()) {
                        Toast.makeText(AddEventsActivity.this, "SUCCESSFULLY ADDED EVENT", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddEventsActivity.this, "FAILED TO ADD Event", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    Toast.makeText(AddEventsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("", "", e);
                }
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddEventsActivity.this)
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
                                        SharedPreferences settings = AddEventsActivity.this.getSharedPreferences("AuthSP", MODE_PRIVATE);
                                        settings.edit().clear().apply();

                                        Intent loginActivity = new Intent(AddEventsActivity.this, LoginActivity.class);
                                        startActivity(loginActivity);
                                    } else {
                                        Toast.makeText(AddEventsActivity.this, "Failed to Log Out !!", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (IOException e) {
                                    Toast.makeText(AddEventsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    Log.e("", "", e);
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        imgEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    startActivityForResult(Intent.createChooser(gallery, "Select profile picture"), 1);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng Nepal = new LatLng(27.666921, 85.350011);
        map.addMarker(new MarkerOptions().position(Nepal).title("Starting point!"));
        map.moveCamera(CameraUpdateFactory.newLatLng(Nepal));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Nepal, 16.0f));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String selectedLatLng = String.valueOf(latLng.latitude).concat(",").concat(String.valueOf(latLng.longitude));
                etLocationCoordinates.setText(selectedLatLng);
                map.clear();
                map.addMarker(new MarkerOptions().position(latLng).title("Pick up location"));
            }
        });
    }

    private void bindProperties() {
        etName = findViewById(R.id.etName);
        etDesc = findViewById(R.id.etDesc);
        etSchedule = findViewById(R.id.etSchedule);
        etLocationCoordinates = findViewById(R.id.etLocationCoordinates);
        imgEvent = findViewById(R.id.imgEvent);
        btnAdd = findViewById(R.id.btnAdd);
        btnLogOut = findViewById(R.id.btnLogOut);
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
                        imgEvent.setImageBitmap(bitmap);

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
}
