package dev.mzarnowski.cinema.show;

import dev.mzarnowski.cinema.Event;
import dev.mzarnowski.cinema.Movie;
import dev.mzarnowski.cinema.room.Room;

import java.time.ZonedDateTime;

public record ShowScheduled(Movie movie, Room.Id id, ZonedDateTime start) implements Event {}
