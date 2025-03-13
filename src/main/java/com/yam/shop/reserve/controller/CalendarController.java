package com.yam.shop.reserve.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalendarController {

    @GetMapping("/today")
    public String getToday() {
        LocalDate today = LocalDate.now();
        return "Today's date is: " + today;
    }
}