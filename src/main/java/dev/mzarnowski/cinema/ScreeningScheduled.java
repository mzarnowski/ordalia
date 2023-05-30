package dev.mzarnowski.cinema;

import dev.mzarnowski.cinema.room.Room;

import java.time.ZonedDateTime;

public record ScreeningScheduled(Movie movie, Room.Id id, ZonedDateTime start) implements Event {}
