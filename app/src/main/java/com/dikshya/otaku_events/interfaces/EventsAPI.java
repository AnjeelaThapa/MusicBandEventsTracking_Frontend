package com.dikshya.otaku_events.interfaces;

import com.dikshya.otaku_events.model.Events;
import com.dikshya.otaku_events.model.EventsDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventsAPI {
    @POST("events")
    Call<Void> addEvents(@Header("Authorization") String token, @Body Events events);

    @GET("events/all")
    Call<List<EventsDto>> getAllEvents(@Header("Authorization") String token);

    @POST("events/filter")
    Call<List<EventsDto>> getAvailableEvents(@Header("Authorization") String token, @Body EventsDto searchObj);

    @GET("events/{id}")
    Call<Events> getById(@Header("Authorization") String token, @Path("id") String id);
}
