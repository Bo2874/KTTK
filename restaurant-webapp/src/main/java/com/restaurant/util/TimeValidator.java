package com.restaurant.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class TimeValidator {

    private static final int BOOKING_DURATION_HOURS = 2;

    public boolean isValidBookingTime(String date, String time) {
        try {
            LocalDate bookingDate = LocalDate.parse(date);
            LocalTime bookingTime = LocalTime.parse(time);
            return isValidBookingTime(bookingDate, bookingTime);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isValidBookingTime(LocalDate date, LocalTime time) {
        LocalDateTime bookingDateTime = LocalDateTime.of(date, time);
        LocalDateTime now = LocalDateTime.now();

        LocalTime opening = LocalTime.of(8, 0);
        LocalTime closing = LocalTime.of(23, 0);
        LocalTime endTime = time.plusHours(BOOKING_DURATION_HOURS);

        boolean inServiceHours = !time.isBefore(opening) && !endTime.isAfter(closing);
        return !bookingDateTime.isBefore(now) && inServiceHours;
    }
}