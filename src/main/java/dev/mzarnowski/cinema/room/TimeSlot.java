package dev.mzarnowski.cinema.room;

import java.time.ZonedDateTime;

record TimeSlot(ZonedDateTime start, ZonedDateTime end) {}
