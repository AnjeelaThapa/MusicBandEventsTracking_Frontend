package com.dikshya.otaku_events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsDto {
    private String name;
    private String _id;
    private String description;
    private String imgString;
    private String schedule;
    private String locationCoordinates;
}
