package com.dikshya.otaku_events.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dikshya.otaku_events.R;
import com.dikshya.otaku_events.api_util.Base_url;
import com.dikshya.otaku_events.interfaces.EventsAPI;
import com.dikshya.otaku_events.interfaces.UsersAPI;
import com.dikshya.otaku_events.model.Events;
import com.dikshya.otaku_events.model.EventsDto;
import com.dikshya.otaku_events.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final String CHANNEL_ID = "ALERT_NOTIFICATIONS";
    private final int NOTIFICATION_ID = 001;
    TextView txtName, txtSchedule, txtDescription;
    ImageView imgActiveEvent;
    Button btnBookmark;
    GoogleMap map;
    String active_event_id;
    Boolean isBookMarked;
    Events activeEvents = new Events();
    User activeUser = new User();
    private String token;
    private String activeUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sharedPreferences = Objects.requireNonNull(getSharedPreferences("AuthSP", MODE_PRIVATE));
        }
        this.token = sharedPreferences.getString("token", "");
        this.activeUserId = sharedPreferences.getString("user_id", "");

        getActiveUser(this.activeUserId);

        this.bindProperties();

        // Receiving intent data---
        Intent i = getIntent();
        active_event_id = i.getExtras().getString("Event_id");
        isBookMarked = i.getExtras().getBoolean("bookMarked");

        if (isBookMarked) {
            btnBookmark.setText("Remove");
        }

        this.getActiveEvent(active_event_id);

        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Base_url.base_url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                if (isBookMarked) {
                    int i = 0;
                    for (EventsDto events: activeUser.getBookmarkedEvents())
                    {
                        if (events.get_id().equals(active_event_id)) {
                            activeUser.getBookmarkedEvents().remove(i);
                        }
                        i++;
                    }
                } else {
                    EventsDto eventsDto = new EventsDto();
                    eventsDto.set_id(active_event_id);
                    activeUser.getBookmarkedEvents().add(eventsDto);
                }

                UsersAPI usersAPI= retrofit.create(UsersAPI.class);

                final Call<Void> reserveRes = usersAPI.bookMarkEvents("Bearer " + token, activeUserId, activeUser);

                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    Response<Void> response = reserveRes.execute();
                    if (response.isSuccessful()) {
                        displayNotifications();
                        Toast.makeText(EventDetailsActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                            Intent reservedIntent = new Intent(EventDetailsActivity.this, BottonNavActivity.class);
                            startActivity(reservedIntent);
                    } else {
                        Toast.makeText(EventDetailsActivity.this, "Failed to perform action!", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    Toast.makeText(EventDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("", "", e);
                }
            }
        });
    }

    private void bindProperties() {
        txtName = findViewById(R.id.txtName);
        txtSchedule = findViewById(R.id.txtSchedule);
        txtDescription = findViewById(R.id.txtDescription);
        imgActiveEvent = findViewById(R.id.imgActiveEvent);
        btnBookmark = findViewById(R.id.btnBookmark);
    }

    private void setValues(Events events) {
        txtName.setText(events.getName());
        txtDescription.setText(events.getDescription());
        txtSchedule.setText(events.getSchedule());

        // Decode Base64 to image ----
        String imageString = events.getImgString();
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imgActiveEvent.setImageBitmap(decodedImage);
    }

    private void displayNotifications() {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_directions_car_black_24dp);
        builder.setContentTitle("You just bookmarked an event!");
        builder.setContentText("Name:" + activeEvents.getName() + "| Events schedule:"
                + activeEvents.getSchedule() + "| Desc:" + activeEvents.getDescription());
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ALERT_NOTIFICATIONS";
            String description = "Includes all the notifications for alerting register";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void getActiveEvent(String event_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Base_url.base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EventsAPI eventsAPI = retrofit.create(EventsAPI.class);

        final Call<Events> activeEventResponse = eventsAPI.getById("Bearer " + token, event_id);

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Response<Events> response = activeEventResponse.execute();

            if (response.isSuccessful()) {
                activeEvents = response.body();
                setValues(activeEvents);

            } else {
                Toast.makeText(EventDetailsActivity.this, "Failed to fetch event!", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(EventDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("", "", e);
        }
    }

    private void getActiveUser(String user_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Base_url.base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsersAPI usersAPI = retrofit.create(UsersAPI.class);

        final Call<User> activeEventResponse = usersAPI.getActiveUser("Bearer " + token);

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Response<User> response = activeEventResponse.execute();

            if (response.isSuccessful()) {
                activeUser = response.body();

            } else {
                Toast.makeText(EventDetailsActivity.this, "Failed to fetch user!", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(EventDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("", "", e);
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Base_url.base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EventsAPI eventsAPI = retrofit.create(EventsAPI.class);

        final Call<Events> activeEventResponse = eventsAPI.getById("Bearer " + token, active_event_id);

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Response<Events> response = activeEventResponse.execute();

            if (response.isSuccessful()) {
                // Set map coordinates ---
                String[] latLongCharArray = response.body().getLocationCoordinates().split(",");
                double latitude = Double.parseDouble(latLongCharArray[0]);
                double longitude = Double.parseDouble(latLongCharArray[1]);

                LatLng pickUpLocation = new LatLng(latitude, longitude);
                map.addMarker(new MarkerOptions().position(pickUpLocation).title("Pick me up here :D"));
                map.moveCamera(CameraUpdateFactory.newLatLng(pickUpLocation));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLocation, 16.0f));

            } else {
                Toast.makeText(EventDetailsActivity.this, "Failed to fetch event!", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(EventDetailsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("", "", e);
        }
    }
}
