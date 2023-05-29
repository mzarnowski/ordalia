package dev.mzarnowski.cinema;

import java.time.LocalTime;
import java.time.ZonedDateTime;

public record OperatingHours(LocalTime since, LocalTime until) {
    public boolean contains(ZonedDateTime time) {
        return !time.toLocalTime().isBefore(since) && !time.toLocalTime().isAfter(until);
    }
}
