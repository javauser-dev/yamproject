package com.yam.shop.reserve.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalendarEventController {

    @GetMapping("/events")
    public List<Event> getEvents() {
        // 여기서는 간단한 하드코딩된 이벤트 목록을 반환합니다.
        List<Event> events = new ArrayList<>();
        events.add(new Event("Meeting", "2025-03-05"));
        events.add(new Event("Conference", "2025-03-10"));
        return events;
    }
}

class Event {
    private String title; 
    private String startDate;

    // 생성자, getter, setter
    public Event(String title, String startDate) {
        this.title = title;
        this.startDate = startDate;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }
}
