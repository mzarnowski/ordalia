package dev.mzarnowski.cinema.room;

import java.time.ZonedDateTime;

public record TimeSlot(ZonedDateTime start, ZonedDateTime end) {}
