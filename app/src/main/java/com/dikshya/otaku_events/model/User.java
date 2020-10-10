package com.dikshya.otaku_events.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String fullName;
    private String _id;
    private String imgString;
    private String username;
    private String phoneNumber;
    private String email;
    private String type;
    private List<Object> tokens;
    private String password;
    private List<EventsDto> bookmarkedEvents;
    private String passResetToken;
}
